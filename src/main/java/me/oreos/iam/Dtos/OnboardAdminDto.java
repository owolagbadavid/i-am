package me.oreos.iam.Dtos;

import javax.validation.constraints.Email;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnboardAdminDto {
    private String username;
    private String password;

    @Email(message = "Email should be valid")
    private String email;
    private String otp;
}
