package com.tracebill.module.production.service;

import com.tracebill.module.production.dto.FactoryRegisterModel;

import jakarta.validation.Valid;

public interface FactoryService {

	Long createFactory(@Valid FactoryRegisterModel model);

}
