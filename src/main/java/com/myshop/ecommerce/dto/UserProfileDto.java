package com.myshop.ecommerce.dto;

import lombok.Data;
import lombok.NoArgsConstructor; // Aggiungi se vuoi un costruttore senza argomenti
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor // Utile per quando Spring crea l'oggetto dal form
public class UserProfileDto {

    // Non includiamo username o email qui perché tipicamente non permettiamo la loro modifica
    // diretta da questo form semplice (l'email richiederebbe verifica).

    @NotBlank(message = "Nome obbligatorio")
    @Size(min = 2, max = 50, message = "Nome deve essere tra 2 e 50 caratteri")
    private String firstName;

    @NotBlank(message = "Cognome obbligatorio")
    @Size(min = 2, max = 50, message = "Cognome deve essere tra 2 e 50 caratteri")
    private String lastName;

    // Costruttore per inizializzare facilmente dal User entity
    public UserProfileDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}