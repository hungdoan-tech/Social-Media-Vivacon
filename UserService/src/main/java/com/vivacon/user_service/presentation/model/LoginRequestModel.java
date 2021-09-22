package com.vivacon.user_service.presentation.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class LoginRequestModel {

	@NotNull(message = "Email cannot be null")
	@Email
	private String email;

	@NotNull(message = "Password can not be null")
	@Size(min = 8, max = 16, message = "Password must not be less be than eight characters or larger than sixteen characters")
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
