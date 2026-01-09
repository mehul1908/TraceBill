package com.tracebill.module.party.dto;

import com.tracebill.module.party.enums.PartyType;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingEntityRegisterModel {

	@NotBlank(message = "Email Id cannot be blank")
	@Email(message = "Email must be in correct format")
	private String email;

	private Long parentPartyId;

	@Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$", message = "Invalid GST number format")
	private String gstNo;

	@NotBlank(message = "Legal name can not be blank")
	private String legalName;

	@NotBlank(message = "Phone Number can not be blank")
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be 10 digit long and without country code.")
	private String phoneNumber;

	@NotBlank(message = "Address can not be blank")
	private String address;

	@NotNull(message = "Registered can not be blank")
	private Boolean registered;
	
	@NotNull(message = "PartyType can not be null")
	private PartyType partyType;

	@AssertTrue(message = "GST number is required only when party is registered")
	private boolean isGstValid() {

		if (registered == null) {
			return true;
		}

		if (registered) {
			return gstNo != null && !gstNo.isBlank();
		} else {
			return gstNo == null || gstNo.isBlank();
		}
	}
}
