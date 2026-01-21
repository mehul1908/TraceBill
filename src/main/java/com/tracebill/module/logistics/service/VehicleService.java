package com.tracebill.module.logistics.service;

import com.tracebill.module.logistics.dto.VehicleRegisterModel;


public interface VehicleService {

	String createVehicle(VehicleRegisterModel model);

	boolean existById(Long vehicleId);

}
