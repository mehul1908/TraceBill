package com.tracebill.module.logistics.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.tracebill.exception.ResourceNotFoundException;
import com.tracebill.exception.UnauthorizedUserException;
import com.tracebill.module.audit.enums.AuditAction;
import com.tracebill.module.audit.service.AuditLogService;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.inventory.service.InventoryApplicationService;
import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.entity.InvoiceItem;
import com.tracebill.module.invoice.enums.InvoiceStatus;
import com.tracebill.module.invoice.service.InvoiceService;
import com.tracebill.module.logistics.domain.ShipmentAggregate;
import com.tracebill.module.logistics.dto.ShipmentRegisterModel;
import com.tracebill.module.logistics.entity.Shipment;
import com.tracebill.module.logistics.entity.ShipmentItem;
import com.tracebill.module.logistics.enums.ShipmentStatus;
import com.tracebill.module.logistics.record.InventoryTransferEvent;
import com.tracebill.module.logistics.record.ShipmentDispatchedEvent;
import com.tracebill.module.logistics.repo.ShipmentRepo;
import com.tracebill.module.party.service.BillingEntityService;
import com.tracebill.module.party.service.PartyService;

import jakarta.transaction.Transactional;

@Service
public class ShipmentServiceImpl implements ShipmentService {

	@Autowired
	private ShipmentRepo shipmentRepo;

	@Autowired
	private AuthenticatedUserProvider authenticatedUser;

	@Autowired
	private PartyService partyService;

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private BillingEntityService billingEntityService;

	@Autowired
	private VehicleService vehicleService;
	
	@Autowired
	private InventoryApplicationService inventoryApplicationService;
	
	@Autowired
	private AuditLogService auditService;
	
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Override
	@Transactional
	public Long createShipment(ShipmentRegisterModel model) {

		Long fromPartyId = authenticatedUser.getAuthenticatedParty();
		Long toPartyId = model.getToPartyId();
		if (!partyService.existById(toPartyId)) {
			throw new IllegalArgumentException("To Party is invalid");
		}

		if (!vehicleService.existById(model.getVehicleId())) {
			throw new IllegalArgumentException("Vehicle Id is invalid");
		}

		Long fromBillingEntityId = billingEntityService.getBillingEntityByPartyId(fromPartyId).getBillingEntityId();
		Long toBillingEntityId = billingEntityService.getBillingEntityByPartyId(toPartyId).getBillingEntityId();

		Shipment shipment = Shipment.builder().toPartyId(model.getToPartyId()).fromPartyId(fromPartyId)
				.vehicleId(model.getVehicleId()).status(ShipmentStatus.CREATED).build();
		ShipmentAggregate shipmentAggregate = new ShipmentAggregate(shipment);

		List<Invoice> invoices = new ArrayList<>();
		for (Long invoiceId : model.getInvoiceIds()) {
			Invoice invoice = invoiceService.getInvoiceWithItems(invoiceId);

			if (!(invoice.getToBillingEntityId().equals(toBillingEntityId)
					&& invoice.getFromBillingEntityId().equals(fromBillingEntityId))) {
				throw new UnauthorizedUserException("You are not authorized to create shipment for these invoices");
			}

			if (invoice.getStatus() != InvoiceStatus.SAVED) {
				throw new IllegalStateException("Invoice not eligible for shipment: " + invoice.getInvoiceNo());
			}

			for (InvoiceItem invoiceItem : invoice.getItems()) {
				ShipmentItem shipmentItem = ShipmentItem.builder().shipment(shipment)
						.invoiceItemId(invoiceItem.getInvoiceItemId()).batchId(invoiceItem.getBatchId())
						.qty(invoiceItem.getQty()).productId(invoiceItem.getProductId()).build();

				shipmentAggregate.addShipmentItem(shipmentItem);
			}
			invoices.add(invoice);
		}

		Shipment savedShipment = shipmentRepo.save(shipmentAggregate.getShipment());

		invoiceService.addShipment(invoices, savedShipment.getShipmentId());
		auditService.create(AuditAction.SHIPMENT_CREATED, "Shipment Created : " + savedShipment.getShipmentId());
		return savedShipment.getShipmentId();
	}

	@Override
	@Transactional
	public Long dispatchShipment(Long shipmentId) {

		Shipment shipment = findShipmentById(shipmentId);
		Long partyId = authenticatedUser.getAuthenticatedParty();

		if (shipment.getStatus() != ShipmentStatus.CREATED && shipment.getStatus() != ShipmentStatus.DISPATCH_PENDING) {
			throw new IllegalStateException("Shipment is not in dispatchable state: " + shipment.getStatus());
		}

		if (!Objects.equals(shipment.getFromPartyId(), partyId)) {
			throw new UnauthorizedUserException("You are not authorized to dispatch it");
		}

		shipment.setDispatchTime(LocalDateTime.now());
		shipment.setStatus(ShipmentStatus.DISPATCH_PENDING);
		shipmentRepo.save(shipment);
		shipment.getItems().size();
		eventPublisher.publishEvent(new ShipmentDispatchedEvent(shipment.getShipmentId()));
		
		
		
		
		List<Invoice> invoices = invoiceService.getInvoiceByShipmentId(shipmentId);

		invoiceService.markDispatched(invoices);
		auditService.create(AuditAction.SHIPMENT_DISPATCHED, "Shipment Dispatched : " + shipment.getShipmentId());
		return shipment.getShipmentId();
	}

	@Override
	public Shipment findShipmentById(Long shipmentId) {
		return shipmentRepo.findById(shipmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Shipment with given id is not found : " + shipmentId));
	}

	@Override
	@Transactional
	public Long receiveShipment(Long shipmentId) {
		Long toPartyId = authenticatedUser.getAuthenticatedParty();
		Shipment shipment = this.findShipmentById(shipmentId);

		if (shipment.getStatus() != ShipmentStatus.DISPATCHED) {
			throw new IllegalStateException("Shipment is not in received state: " + shipment.getStatus());
		}

		if (!Objects.equals(shipment.getToPartyId(), toPartyId)) {
			throw new UnauthorizedUserException("You are not authorized to receive it");
		}
		
		List<ShipmentItem> items = shipment.getItems();
		
		inventoryApplicationService.addFromInvoice(items);
		
		List<Invoice> invoices = invoiceService.getInvoiceByShipmentWithItems(shipmentId);
		invoiceService.markReceived(invoices);
		eventPublisher.publishEvent(new InventoryTransferEvent(invoices));
		shipment.setStatus(ShipmentStatus.RECEIVED);
		Shipment savedShipment = shipmentRepo.save(shipment);
		auditService.create(AuditAction.SHIPMENT_RECEIVED, "shipment Received: " + savedShipment.getShipmentId());
		return savedShipment.getShipmentId();
	}

	@Override
	public Long cancelShipment(Long shipmentId) {
		Long fromPartyId = authenticatedUser.getAuthenticatedParty();
		Shipment shipment = this.findShipmentById(shipmentId);

		if (shipment.getStatus() != ShipmentStatus.CREATED && shipment.getStatus() != ShipmentStatus.DISPATCH_PENDING) {
			throw new IllegalStateException("Shipment is not in cancelled state: " + shipment.getStatus());
		}

		if (!Objects.equals(shipment.getFromPartyId(), fromPartyId)) {
			throw new UnauthorizedUserException("You are not authorized to cancel it");
		}
						
		List<Invoice> invoices = invoiceService.getInvoiceByShipmentId(shipmentId);
		invoiceService.markSaved(invoices);
		
		shipment.setStatus(ShipmentStatus.CANCELLED);
		Shipment savedShipment = shipmentRepo.save(shipment);
		auditService.create(AuditAction.SHIPMENT_CANCELLED, "Shipment Cancelled : " + savedShipment.getShipmentId());
		return savedShipment.getShipmentId();
	}

	@Override
	public void save(Shipment shipment) {
		shipmentRepo.save(shipment);
	}

}
