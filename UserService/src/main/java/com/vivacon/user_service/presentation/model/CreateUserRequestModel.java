package com.vivacon.user_service.presentation.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUserRequestModel {

	@NotNull(message = "First name can not be null")
	@Size(min = 2, message = "First name must not be less be than two characters")
	private String firstName;

	@NotNull(message = "Last name can not be null")
	@Size(min = 2, message = "Last name must not be less be than two characters")
	private String lastName;

	@NotNull(message = "Password can not be null")
	@Size(min = 8, max = 16, message = "Password must not be less be than eight characters or larger than sixteen characters")
	private String password;

	@NotNull(message = "Email cannot be null")
	@Email
	private String email;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
