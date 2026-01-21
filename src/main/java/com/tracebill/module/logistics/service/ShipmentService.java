package com.tracebill.module.logistics.service;

import com.tracebill.module.logistics.dto.ShipmentRegisterModel;

public interface ShipmentService {

	Long createShipment(ShipmentRegisterModel model);

}
