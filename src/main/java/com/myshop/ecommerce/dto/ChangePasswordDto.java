package com.myshop.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


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