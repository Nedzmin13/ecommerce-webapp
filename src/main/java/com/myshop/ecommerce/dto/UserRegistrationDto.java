package com.myshop.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.*;



@Data
public class UserRegistrationDto {

    @NotBlank(message = "Username obbligatorio")
    @Size(min = 3, max = 50, message = "Username deve essere tra 3 e 50 caratteri")
    private String username;

    @NotBlank(message = "Email obbligatoria")
    @Email(message = "Inserire un indirizzo email valido")
    @Size(max = 100, message = "Email non può superare i 100 caratteri")
    private String email;

    @NotBlank(message = "Password obbligatoria")
    @Size(min = 8, max = 128, message = "La password deve essere tra 8 e 128 caratteri")
    private String password;

    @NotBlank(message = "Conferma password obbligatoria")
    private String confirmPassword;

    @NotBlank(message = "Nome obbligatorio")
    @Size(min = 2, max = 50, message = "Nome deve essere tra 2 e 50 caratteri")
    private String firstName;

    @NotBlank(message = "Cognome obbligatorio")
    @Size(min = 2, max = 50, message = "Cognome deve essere tra 2 e 50 caratteri")
    private String lastName;



}