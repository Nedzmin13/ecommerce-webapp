package com.myshop.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // Utile per mappare i nomi JSON
import lombok.Data;

@Data
public class PayPalAccessTokenResponseDto {
    private String scope;
    @JsonProperty("access_token") // Mappa da snake_case JSON a camelCase Java
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("app_id")
    private String appId;
    @JsonProperty("expires_in")
    private int expiresIn;
    private String nonce;
}