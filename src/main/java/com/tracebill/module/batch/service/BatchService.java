package com.tracebill.module.batch.service;

import com.tracebill.module.batch.dto.BatchRegisterModel;

public interface BatchService {

	String createBatch(BatchRegisterModel model);
	
}
