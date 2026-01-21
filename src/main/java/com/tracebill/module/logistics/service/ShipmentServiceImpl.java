package com.tracebill.module.logistics.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracebill.exception.UnauthorizedUserException;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.invoice.entity.Invoice;
import com.tracebill.module.invoice.entity.InvoiceItem;
import com.tracebill.module.invoice.enums.InvoiceStatus;
import com.tracebill.module.invoice.service.InvoiceService;
import com.tracebill.module.logistics.domain.ShipmentAggregate;
import com.tracebill.module.logistics.dto.ShipmentRegisterModel;
import com.tracebill.module.logistics.entity.Shipment;
import com.tracebill.module.logistics.entity.ShipmentItem;
import com.tracebill.module.logistics.enums.ShipmentStatus;
import com.tracebill.module.logistics.repo.ShipmentRepo;
import com.tracebill.module.party.service.BillingEntityService;
import com.tracebill.module.party.service.PartyService;

import jakarta.transaction.Transactional;

@Service
public class ShipmentServiceImpl implements ShipmentService{
	
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

	@Override
	@Transactional
	public Long createShipment(ShipmentRegisterModel model) {
		
		Long fromPartyId = authenticatedUser.getAuthenticatedParty();
		Long toPartyId = model.getToPartyId();
		if(!partyService.existById(toPartyId)) {
			throw new IllegalArgumentException("To Party is invalid");
		}
		
		if(!vehicleService.existById(model.getVehicleId())) {
			throw new IllegalArgumentException("Vehicle Id is invalid");
		}
		
		Long fromBillingEntityId = billingEntityService.getBillingEntityByPartyId(fromPartyId).getBillingEntityId();
		Long toBillingEntityId = billingEntityService.getBillingEntityByPartyId(toPartyId).getBillingEntityId();
		
		
		Shipment shipment = Shipment.builder()
				.toPartyId(model.getToPartyId())
				.fromPartyId(fromPartyId)
				.vehicleId(model.getVehicleId())
				.status(ShipmentStatus.CREATED)
				.build();
		ShipmentAggregate shipmentAggregate = new ShipmentAggregate(shipment);
		
		List<Invoice> invoices = new ArrayList<>();
		for(Long invoiceId : model.getInvoiceIds()) {
			Invoice invoice = invoiceService.getInvoiceWithItems(invoiceId);
			
			if(! (invoice.getToBillingEntityId().equals(toBillingEntityId) && invoice.getFromBillingEntityId().equals(fromBillingEntityId))) {
				throw new UnauthorizedUserException("You are not authorized to create shipment for these invoices");
			}
			
			if (invoice.getStatus() != InvoiceStatus.SAVED) {
			    throw new IllegalStateException(
			        "Invoice not eligible for shipment: " + invoice.getInvoiceNo()
			    );
			}

			
			for(InvoiceItem invoiceItem : invoice.getItems()) {
				ShipmentItem shipmentItem = ShipmentItem.builder()
						.shipment(shipment)
						.invoiceItemId(invoiceItem.getInvoiceItemId())
						.batchId(invoiceItem.getBatchId())
						.qty(invoiceItem.getQty())
						.build();
				
				shipmentAggregate.addShipmentItem(shipmentItem);
			}
			invoices.add(invoice);
		}
		
		Shipment savedShipment = shipmentRepo.save(shipmentAggregate.getShipment());
		
		invoiceService.addShipment(invoices , savedShipment.getShipmentId());
		
		return savedShipment.getShipmentId();
	}

}
