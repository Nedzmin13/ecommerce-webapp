package com.myshop.ecommerce.service.impl; // o com.myshop.ecommerce.paypal

// Assumiamo che PayPalAccessTokenResponse sia un DTO separato o static nested accessibile
import com.myshop.ecommerce.dto.PayPalAccessTokenResponseDto; // Esempio se spostato in DTO

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;         // <-- IMPORT MANCANTE
import org.springframework.http.HttpHeaders;       // <-- IMPORT MANCANTE
import org.springframework.http.HttpStatus;        // <-- IMPORT MANCANTE
import org.springframework.http.MediaType;         // <-- IMPORT MANCANTE
import org.springframework.http.ResponseEntity;    // <-- IMPORT MANCANTE
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException; // <-- IMPORT MANCANTE
import org.springframework.web.client.RestTemplate;             // <-- IMPORT MANCANTE

@Component
public class PayPalAuthService {
    private static final Logger log = LoggerFactory.getLogger(PayPalAuthService.class);

    @Value("${paypal.client.id}")
    private String clientId;
    @Value("${paypal.client.secret}")
    private String clientSecret;
    @Value("${paypal.mode}")
    private String mode;

    private String accessToken;
    private long tokenExpiryTime;

    private String getPayPalApiBaseUrl() {
        return "sandbox".equalsIgnoreCase(mode) ? "https://api-m.sandbox.paypal.com" : "https://api-m.paypal.com";
    }

    public synchronized String getAccessToken() {
        if (accessToken == null || System.currentTimeMillis() >= tokenExpiryTime) {
            log.info("Richiesta nuovo token di accesso PayPal...");
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(clientId, clientSecret);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            try {
                ResponseEntity<PayPalAccessTokenResponseDto> response = restTemplate.postForEntity(
                        getPayPalApiBaseUrl() + "/v1/oauth2/token",
                        request,
                        PayPalAccessTokenResponseDto.class // Usa il DTO corretto
                );

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    PayPalAccessTokenResponseDto tokenResponse = response.getBody();
                    // ...
                    this.accessToken = tokenResponse.getAccessToken(); // Usa il getter se usi Lombok e @JsonProperty
                    this.tokenExpiryTime = System.currentTimeMillis() + (tokenResponse.getExpiresIn() - 10) * 1000L;
                    log.info("Nuovo token di accesso PayPal ottenuto. Scade tra circa {} secondi.", tokenResponse.getExpiresIn());
                } else {
                    log.error("Errore durante l'ottenimento del token PayPal. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                    throw new RuntimeException("Impossibile ottenere il token di accesso da PayPal.");
                }
            } catch (HttpClientErrorException e) {
                log.error("HttpClientErrorException durante l'ottenimento del token PayPal: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
                throw new RuntimeException("Impossibile ottenere il token di accesso da PayPal: " + e.getResponseBodyAsString());
            }
        }
        return accessToken;
    }
}