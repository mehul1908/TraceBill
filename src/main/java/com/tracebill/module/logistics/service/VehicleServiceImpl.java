package com.tracebill.module.logistics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.tracebill.module.audit.enums.AuditAction;
import com.tracebill.module.audit.service.AuditLogService;
import com.tracebill.module.auth.service.AuthenticatedUserProvider;
import com.tracebill.module.logistics.dto.VehicleRegisterModel;
import com.tracebill.module.logistics.entity.Vehicle;
import com.tracebill.module.logistics.repo.VehicleRepo;

@Service
public class VehicleServiceImpl implements VehicleService {

	@Autowired
	private VehicleRepo vehicleRepo;
	
	@Autowired
	private AuthenticatedUserProvider authenticatedUser;
	
	@Autowired
	private AuditLogService auditService;
	
	@Override
	@PreAuthorize("hasRole('TRANSPORTER')")
	public String createVehicle(VehicleRegisterModel model) {
		Long transporterPartyId = authenticatedUser.getAuthenticatedParty();
		Vehicle vehicle = Vehicle.builder()
				.vehicleNo(model.getVehicleNo())
				.tranporterId(transporterPartyId)
				.capacity(model.getCapacity())
				.build();
		Vehicle savedVehicle = vehicleRepo.save(vehicle);
		auditService.create(AuditAction.CREATED, "Vehicle Created : " + savedVehicle.getVehicleNo());
		return savedVehicle.getVehicleNo();
	}

	@Override
	public boolean existById(Long vehicleId) {
		return vehicleRepo.existsById(vehicleId);
	}

}
