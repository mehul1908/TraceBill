package com.tracebill.module.logistics.domain;

import java.util.ArrayList;
import java.util.List;

import com.tracebill.module.logistics.entity.Shipment;
import com.tracebill.module.logistics.entity.ShipmentItem;

public class ShipmentAggregate {

	private final Shipment shipment;
	private final List<ShipmentItem> items = new ArrayList<>();
	
	public ShipmentAggregate(Shipment shipment) {
		this.shipment = shipment;
	}
	
	public Shipment getShipment() {
		shipment.setItems(items);
		return shipment;
	}
	
	public void addShipmentItem(ShipmentItem item) {
		items.add(item);
	}
	
}
