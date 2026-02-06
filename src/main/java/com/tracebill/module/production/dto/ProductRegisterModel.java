package com.tracebill.module.production.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegisterModel {

    @NotBlank(message = "Product name cannot be blank")
    private String prodName;
    
    @NotBlank(message = "HSN Code cannot be blank")
    private String hsnCode;

    @NotNull(message = "MRP cannot be null")
    @Positive(message = "MRP must be greater than zero")
    private BigDecimal mrp;

    @NotNull(message = "Default rate cannot be null")
    @Size(min = 3, max = 3, message = "Default rate must contain exactly 3 values")
    private List<@NotNull @Positive BigDecimal> defaultRate;

    @NotNull(message = "GST rate cannot be null")
    @Min(value = 0, message = "GST rate cannot be negative")
    private BigDecimal gstRate;

    @NotNull(message = "Cess rate cannot be null")
    @Min(value = 0, message = "Cess rate cannot be negative")
    private BigDecimal cessRate;
    
    
}
