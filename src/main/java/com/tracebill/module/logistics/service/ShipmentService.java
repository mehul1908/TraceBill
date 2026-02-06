package com.tracebill.module.logistics.service;

import com.tracebill.module.logistics.dto.ShipmentRegisterModel;
import com.tracebill.module.logistics.entity.Shipment;

public interface ShipmentService {

	Long createShipment(ShipmentRegisterModel model);

	Long dispatchShipment(Long shipmentId);

	Shipment findShipmentById(Long shipmentId);

	Long receiveShipment(Long shipmentId);

	Long cancelShipment(Long shipmentId);

	void save(Shipment shipment);

}
