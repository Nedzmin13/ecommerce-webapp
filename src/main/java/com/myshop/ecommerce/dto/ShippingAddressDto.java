package com.myshop.ecommerce.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ShippingAddressDto {

    @NotBlank(message = "Indirizzo (Via/Numero) obbligatorio")
    @Size(max = 255, message = "Indirizzo troppo lungo")
    private String addressLine1;

    @Size(max = 255, message = "Indirizzo (Linea 2) troppo lungo")
    private String addressLine2; // Opzionale

    @NotBlank(message = "Città obbligatoria")
    @Size(max = 100, message = "Nome città troppo lungo")
    private String city;

    @NotBlank(message = "Provincia/Regione obbligatoria")
    @Size(max = 100, message = "Nome provincia/regione troppo lungo")
    private String state; // Provincia/Regione/Stato

    @NotBlank(message = "CAP obbligatorio")
    @Size(min = 2, max = 20, message = "CAP non valido") // Adattare min/max al formato CAP
    private String postalCode;

    @NotBlank(message = "Paese obbligatorio")
    @Size(max = 50, message = "Nome paese troppo lungo")
    private String country;

    @Size(max = 20, message = "Numero di telefono troppo lungo")
    // Potremmo aggiungere @Pattern per validare il formato del telefono se necessario
    private String phone; // Opzionale, ma spesso utile
}