package me.oreos.iam.Dtos;

import javax.validation.constraints.Email;

public class LoginDto {
    @Email(message = "Email should be valid")
    public String emailAddress;
    public String password;
}
