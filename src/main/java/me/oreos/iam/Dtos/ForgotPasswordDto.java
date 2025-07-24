package me.oreos.iam.Dtos;

import javax.validation.constraints.Email;

public class ForgotPasswordDto {
    @Email(message = "Email should be valid")
    public String emailAddress;
}
