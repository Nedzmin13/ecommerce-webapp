package com.myshop.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

// Potremmo aggiungere un'annotazione custom per validare la corrispondenza tra newPassword e confirmNewPassword
// import com.myshop.ecommerce.validation.FieldMatch; // Esempio

// @FieldMatch.List({ // Esempio di validazione custom per corrispondenza campi
//    @FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "La nuova password e la conferma non corrispondono")
// })
@Data
public class ChangePasswordDto {

    @NotBlank(message = "Password attuale obbligatoria")
    private String currentPassword;

    @NotBlank(message = "Nuova password obbligatoria")
    @Size(min = 8, max = 128, message = "La nuova password deve essere tra 8 e 128 caratteri")
    private String newPassword;

    @NotBlank(message = "Conferma nuova password obbligatoria")
    private String confirmNewPassword;
}