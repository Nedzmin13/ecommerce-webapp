package com.myshop.ecommerce.dto;

import lombok.Data; // Lombok per @Getter, @Setter, @ToString, @EqualsAndHashCode, @RequiredArgsConstructor
import javax.validation.constraints.*; // Importa le annotazioni di validazione standard

// Potremmo aggiungere una validazione custom per confrontare le password
// import com.myshop.ecommerce.validation.PasswordMatches; // Esempio

@Data // Genera getter, setter, toString, equals, hashCode
// @PasswordMatches // Annotazione custom (da creare) per validare corrispondenza password
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
    // Potremmo aggiungere requisiti più specifici sulla password con @Pattern se necessario
    private String password;

    @NotBlank(message = "Conferma password obbligatoria")
    private String confirmPassword; // Questo campo non sarà mappato sull'entità User

    @NotBlank(message = "Nome obbligatorio")
    @Size(min = 2, max = 50, message = "Nome deve essere tra 2 e 50 caratteri")
    private String firstName;

    @NotBlank(message = "Cognome obbligatorio")
    @Size(min = 2, max = 50, message = "Cognome deve essere tra 2 e 50 caratteri")
    private String lastName;

    // Aggiungeremo la validazione per password==confirmPassword tra poco
    // Per ora, ci concentriamo sui campi base.

}