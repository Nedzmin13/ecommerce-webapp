package com.myshop.ecommerce.model; // o com.myshop.ecommerce.ui

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BreadcrumbItem {
    private String label; // Testo visualizzato
    private String url;   // URL del link (può essere null per l'ultimo elemento non cliccabile)
}