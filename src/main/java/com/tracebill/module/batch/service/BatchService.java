package com.tracebill.module.batch.service;

import com.tracebill.module.batch.dto.BatchRegisterModel;
import com.tracebill.module.batch.entity.Batch;

public interface BatchService {

	String createBatch(BatchRegisterModel model);

	Batch getBatchByBatchId(Long batchId);

	String getBatchHashById(Long batchId);
	
}
