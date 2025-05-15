package com.myshop.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UserProfileDto {



    @NotBlank(message = "Nome obbligatorio")
    @Size(min = 2, max = 50, message = "Nome deve essere tra 2 e 50 caratteri")
    private String firstName;

    @NotBlank(message = "Cognome obbligatorio")
    @Size(min = 2, max = 50, message = "Cognome deve essere tra 2 e 50 caratteri")
    private String lastName;

    public UserProfileDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}