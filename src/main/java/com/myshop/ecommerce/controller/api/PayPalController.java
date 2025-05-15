package com.myshop.ecommerce.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference; // Necessario per deserializzare Map da JSON
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myshop.ecommerce.entity.Order;
import com.myshop.ecommerce.entity.Payment;
import com.myshop.ecommerce.enums.OrderStatus;
import com.myshop.ecommerce.enums.PaymentMethod;
import com.myshop.ecommerce.enums.PaymentStatus;
import com.myshop.ecommerce.service.CartService;
import com.myshop.ecommerce.service.OrderService;
import com.myshop.ecommerce.service.impl.PayPalAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.myshop.ecommerce.service.EmailService; // Importa EmailService


import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/paypal")
public class PayPalController {

    private static final Logger log = LoggerFactory.getLogger(PayPalController.class);

    private final EmailService emailService; // Inietta EmailService


    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    private final PayPalAuthService payPalAuthService;
    private final CartService cartService;

    @Value("${app.site.url:http://localhost:8080}")
    private String siteBaseUrl;
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    @Value("${paypal.mode}")
    private String mode;

    @Autowired
    public PayPalController(OrderService orderService, ObjectMapper objectMapper,
                            PayPalAuthService payPalAuthService, CartService cartService, EmailService emailService) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
        this.payPalAuthService = payPalAuthService;
        this.cartService = cartService;
        this.emailService = emailService; // Inizializza

    }

    private String getPayPalApiBaseUrl() {
        return "sandbox".equalsIgnoreCase(mode) ? "https://api-m.sandbox.paypal.com" : "https://api-m.paypal.com";
    }

    // --- DTO interni come static nested class ---
    static class PayPalOrderRequest {
        public String intent;
        public List<PayPalPurchaseUnit> purchase_units;
        public PayPalApplicationContext application_context;
    }

    static class PayPalPurchaseUnit {
        public String reference_id;
        public PayPalAmount amount;
        public List<PayPalItem> items;
        public PayPalShippingDetails shipping;
    }

    static class PayPalAmount {
        public String currency_code;
        public String value;
        public PayPalAmountBreakdown breakdown;
    }

    static class PayPalAmountBreakdown {
        public PayPalMoney item_total;
    }

    static class PayPalItem {
        public String name;
        public String quantity;
        public PayPalMoney unit_amount;
    }

    static class PayPalMoney {
        public String currency_code;
        public String value;
    }

    static class PayPalApplicationContext {
        public String brand_name;
        public String return_url;
        public String cancel_url;
        public String shipping_preference = "SET_PROVIDED_ADDRESS";
    }

    static class PayPalOrderResponse {
        public String id;
        public String status;
        public List<PayPalLink> links;
        public String message;
        public List<PayPalErrorDetail> details;
    }

    static class PayPalErrorDetail {
        public String field;
        public String issue;
        public String description;
    }

    static class PayPalLink {
        public String href;
        public String rel;
        public String method;
    }

    static class PayPalCaptureResponse {
        public String id;
        public String status;
        public List<PayPalPurchaseUnitResponse> purchase_units;
    }

    static class PayPalPurchaseUnitResponse {
        public PayPalPayments payments;
        public String reference_id;
    }

    static class PayPalPayments {
        public List<PayPalCapture> captures;
    }

    static class PayPalCapture {
        public String id;
        public String status;
        public PayPalMoney amount;
        public String create_time;
        public String update_time;
        public List<PayPalLink> links;
    }

    static class PayPalShippingDetails {
        public PayPalName name;
        public PayPalAddressPortable address;
    }

    static class PayPalName {
        public String full_name;
    }

    static class PayPalAddressPortable {
        public String address_line_1;
        public String address_line_2;
        public String admin_area_2;   // Città
        public String admin_area_1;   // Stato/Provincia
        public String postal_code;
        public String country_code;
    }
    // --- Fine DTO interni ---


    @PostMapping("/create-order/{internalOrderId}")
    public ResponseEntity<?> createPayPalOrder(@PathVariable("internalOrderId") Long internalOrderId) {
        this.log.info("Richiesta creazione ordine PayPal (HTTP diretto) per ordine interno ID: {}", internalOrderId);
        com.myshop.ecommerce.entity.Order myShopOrder = this.orderService.findOrderById(internalOrderId).orElse(null);

        if (myShopOrder == null || myShopOrder.getShipping() == null) {
            this.log.error("Ordine interno o indirizzo di spedizione non trovato con ID: {}", internalOrderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Ordine interno o indirizzo di spedizione non trovato."));
        }

        try {
            String accessToken = this.payPalAuthService.getAccessToken();

            PayPalOrderRequest payPalOrderRequest = new PayPalOrderRequest();
            payPalOrderRequest.intent = "CAPTURE";

            PayPalAmount amount = new PayPalAmount();
            amount.currency_code = "EUR";
            amount.value = myShopOrder.getTotalAmount().setScale(2, RoundingMode.HALF_UP).toString();

            PayPalPurchaseUnit purchaseUnit = new PayPalPurchaseUnit();
            purchaseUnit.reference_id = myShopOrder.getOrderNumber();
            purchaseUnit.amount = amount;

            com.myshop.ecommerce.entity.Shipping shippingInfo = myShopOrder.getShipping();
            PayPalShippingDetails shippingDetails = new PayPalShippingDetails();
            PayPalName shippingName = new PayPalName();
            shippingName.full_name = myShopOrder.getUser().getFirstName() + " " + myShopOrder.getUser().getLastName();
            shippingDetails.name = shippingName;
            PayPalAddressPortable shippingAddress = new PayPalAddressPortable();
            shippingAddress.address_line_1 = shippingInfo.getAddressLine1();
            if (shippingInfo.getAddressLine2() != null && !shippingInfo.getAddressLine2().isEmpty()) {
                shippingAddress.address_line_2 = shippingInfo.getAddressLine2();
            }
            shippingAddress.admin_area_2 = shippingInfo.getCity();
            shippingAddress.admin_area_1 = shippingInfo.getState();
            shippingAddress.postal_code = shippingInfo.getPostalCode();
            if ("Italia".equalsIgnoreCase(shippingInfo.getCountry()) || "Italy".equalsIgnoreCase(shippingInfo.getCountry())) {
                shippingAddress.country_code = "IT";
            } else {
                shippingAddress.country_code = shippingInfo.getCountry().toUpperCase().substring(0, Math.min(shippingInfo.getCountry().length(), 2));
                this.log.warn("Codice paese spedizione non mappato, usando: {}", shippingAddress.country_code);
            }
            shippingDetails.address = shippingAddress;
            purchaseUnit.shipping = shippingDetails;

            // (Opzionale) Aggiungere items
            // List<PayPalItem> payPalItems = new ArrayList<>();
            // BigDecimal calculatedItemTotal = BigDecimal.ZERO;
            // for (com.myshop.ecommerce.entity.OrderItem item : myShopOrder.getOrderItems()) {
            //     PayPalMoney itemUnitPrice = new PayPalMoney();
            //     itemUnitPrice.currency_code = "EUR";
            //     itemUnitPrice.value = item.getPricePerUnit().setScale(2, RoundingMode.HALF_UP).toString();
            //     PayPalItem payPalItem = new PayPalItem();
            //     payPalItem.name = item.getProduct().getName().substring(0, Math.min(item.getProduct().getName().length(), 127));
            //     payPalItem.quantity = String.valueOf(item.getQuantity());
            //     payPalItem.unit_amount = itemUnitPrice;
            //     payPalItems.add(payPalItem);
            //     calculatedItemTotal = calculatedItemTotal.add(item.getPricePerUnit().multiply(new BigDecimal(item.getQuantity())));
            // }
            // if (!payPalItems.isEmpty()) {
            //     purchaseUnit.items = payPalItems;
            //     PayPalMoney itemTotalMoney = new PayPalMoney();
            //     itemTotalMoney.currency_code = "EUR";
            //     itemTotalMoney.value = calculatedItemTotal.setScale(2, RoundingMode.HALF_UP).toString();
            //     PayPalAmountBreakdown breakdown = new PayPalAmountBreakdown();
            //     breakdown.item_total = itemTotalMoney;
            //     amount.breakdown = breakdown;
            // }


            payPalOrderRequest.purchase_units = Collections.singletonList(purchaseUnit);

            String redirectContextPath = (this.contextPath == null || this.contextPath.equals("/") || this.contextPath.isEmpty() ? "" : this.contextPath);
            PayPalApplicationContext applicationContext = new PayPalApplicationContext();
            applicationContext.brand_name = "MyShop E-commerce";
            applicationContext.return_url = this.siteBaseUrl + redirectContextPath + "/checkout/paypal/success/" + myShopOrder.getId();
            applicationContext.cancel_url = this.siteBaseUrl + redirectContextPath + "/checkout/paypal/cancel/" + myShopOrder.getId();
            applicationContext.shipping_preference = "SET_PROVIDED_ADDRESS";
            payPalOrderRequest.application_context = applicationContext;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<String> requestEntity = new HttpEntity<>(this.objectMapper.writeValueAsString(payPalOrderRequest), headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    getPayPalApiBaseUrl() + "/v2/checkout/orders",
                    requestEntity,
                    String.class
            );

            if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
                PayPalOrderResponse payPalOrder = this.objectMapper.readValue(responseEntity.getBody(), PayPalOrderResponse.class);
                this.log.info("Ordine PayPal (v2 HTTP) creato con ID PayPal: {}", payPalOrder.id);
                return ResponseEntity.ok(Map.of("id", payPalOrder.id));
            } else {
                this.log.error("Errore creazione ordine PayPal (v2 HTTP) - Status non CREATED. Status: {}, Body: {}", responseEntity.getStatusCode(), responseEntity.getBody());
                String errorMessage = "Errore da PayPal.";
                if (responseEntity.getBody() != null) {
                    try {
                        PayPalOrderResponse errorResponse = this.objectMapper.readValue(responseEntity.getBody(), PayPalOrderResponse.class);
                        if (errorResponse.message != null && !errorResponse.message.isEmpty()) {
                            errorMessage = errorResponse.message;
                        } else if (errorResponse.details != null && !errorResponse.details.isEmpty()) {
                            errorMessage = errorResponse.details.stream().map(d -> d.description).collect(Collectors.joining("; "));
                        }
                    } catch (Exception e) {
                        this.log.warn("Impossibile parsare corpo errore PayPal come PayPalOrderResponse: {}", e.getMessage());
                    }
                }
                return ResponseEntity.status(responseEntity.getStatusCode()).body(Map.of("message", errorMessage, "rawResponse", responseEntity.getBody()));
            }

        } catch (HttpClientErrorException e) {
            this.log.error("HttpClientErrorException PayPal (v2 HTTP): Status {}, Body {}, Headers {}", e.getStatusCode(), e.getResponseBodyAsString(), e.getResponseHeaders(), e);
            String errorMessage = e.getResponseBodyAsString();
            try {
                // Tenta di estrarre un messaggio più pulito dal JSON di errore
                Map<String, Object> errorMap = this.objectMapper.readValue(errorMessage, new TypeReference<Map<String, Object>>() {});
                if (errorMap.containsKey("message")) {
                    errorMessage = (String) errorMap.get("message");
                }
            } catch (Exception parseEx) {
                this.log.warn("Impossibile parsare il messaggio di errore JSON da HttpClientErrorException: {}", parseEx.getMessage());
            }
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", "Errore da PayPal: " + errorMessage));
        } catch (JsonProcessingException e) {
            this.log.error("Errore durante la serializzazione/deserializzazione JSON per PayPal: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Errore interno di sistema (JSON)."));
        } catch (Exception e) {
            this.log.error("Errore imprevisto creazione ordine PayPal (v2 HTTP): {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Errore imprevisto durante la comunicazione con PayPal."));
        }
        // Questo return di fallback non dovrebbe mai essere raggiunto se la logica try/catch è completa.
        // Ma il compilatore lo richiede per garantire che tutti i percorsi abbiano un return.
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Errore server non gestito durante la creazione dell'ordine PayPal."));
    }


    @PostMapping("/capture-order/{paypalOrderId}")
    public ResponseEntity<?> capturePayPalOrder(@PathVariable String paypalOrderId, HttpSession session) {
        this.log.info("Richiesta cattura pagamento per ordine PayPal ID: {}", paypalOrderId);

        Long internalOrderId = (Long) session.getAttribute("pendingOrderId");
        if (internalOrderId == null) {
            this.log.error("ID ordine interno non trovato in sessione per cattura ordine PayPal {}", paypalOrderId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Sessione scaduta o ordine non trovato."));
        }

        com.myshop.ecommerce.entity.Order myShopOrder = this.orderService.findOrderById(internalOrderId).orElse(null);
        if (myShopOrder == null) {
            this.log.error("Ordine interno {} non trovato per cattura ordine PayPal {}", internalOrderId, paypalOrderId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Ordine interno non trovato."));
        }

        if (myShopOrder.getStatus() != OrderStatus.PENDING) {
            this.log.warn("Tentativo cattura ordine PayPal {} per ordine interno {} non PENDING (stato: {})", paypalOrderId, internalOrderId, myShopOrder.getStatus());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Stato ordine non valido per pagamento.", "internalOrderId", internalOrderId));
        }

        try {
            String accessToken = this.payPalAuthService.getAccessToken();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            HttpEntity<String> requestEntity = new HttpEntity<>("{}", headers); // Corpo JSON vuoto per cattura
            String captureUrl = getPayPalApiBaseUrl() + "/v2/checkout/orders/" + paypalOrderId + "/capture";

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(captureUrl, requestEntity, String.class);
            this.log.debug("Risposta grezza cattura PayPal: Status={}, Body={}", responseEntity.getStatusCode(), responseEntity.getBody());

            if (responseEntity.getStatusCode() == HttpStatus.CREATED || responseEntity.getStatusCode() == HttpStatus.OK) {
                PayPalCaptureResponse captureResponse = this.objectMapper.readValue(responseEntity.getBody(), PayPalCaptureResponse.class);

                if ("COMPLETED".equalsIgnoreCase(captureResponse.status)) {
                    String transactionId = captureResponse.id;
                    if (captureResponse.purchase_units != null && !captureResponse.purchase_units.isEmpty() &&
                            captureResponse.purchase_units.get(0).payments != null &&
                            captureResponse.purchase_units.get(0).payments.captures != null && // Aggiunto controllo null per captures
                            !captureResponse.purchase_units.get(0).payments.captures.isEmpty()) {
                        transactionId = captureResponse.purchase_units.get(0).payments.captures.get(0).id;
                    }

                    this.log.info("Pagamento PayPal catturato. Ordine PayPal ID: {}. Transazione PayPal ID: {}", paypalOrderId, transactionId);

                    myShopOrder.setStatus(OrderStatus.PROCESSING);
                    Payment paymentEntity = new Payment();
                    paymentEntity.setOrder(myShopOrder);
                    paymentEntity.setPaymentMethod(PaymentMethod.PAYPAL);
                    paymentEntity.setTransactionId(transactionId);
                    paymentEntity.setStatus(PaymentStatus.COMPLETED);
                    paymentEntity.setAmount(myShopOrder.getTotalAmount());
                    paymentEntity.setPaymentDate(LocalDateTime.now());
                    myShopOrder.setPayment(paymentEntity);

                    this.orderService.saveOrder(myShopOrder);
                    this.cartService.clearCart(session);
                    session.removeAttribute("pendingOrderId");

                    // TODO: Inviare email di conferma ordine
                    // this.emailService.sendOrderConfirmationEmail(myShopOrder);

                    try {
                        this.emailService.sendOrderConfirmationEmail(myShopOrder); // Chiamata al servizio email
                        log.info("Email di conferma ordine inviata per l'ordine ID {}", myShopOrder.getId());
                    } catch (Exception e) {
                        // Logga l'errore ma non far fallire la risposta al client per questo
                        log.error("Fallito invio email di conferma per l'ordine ID {}: {}", myShopOrder.getId(), e.getMessage());
                    }

                    return ResponseEntity.ok(Map.of(
                            "status", "COMPLETED",
                            "transactionId", transactionId,
                            "internalOrderId", myShopOrder.getId()
                    ));
                } else {
                    this.log.warn("Cattura PayPal per ordine {} completata ma con stato inatteso: {}", paypalOrderId, captureResponse.status);
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of("message", "Stato pagamento PayPal inatteso: " + captureResponse.status, "internalOrderId", internalOrderId));
                }
            } else {
                this.log.error("Errore cattura pagamento PayPal. Status: {}, Body: {}", responseEntity.getStatusCode(), responseEntity.getBody());
                return ResponseEntity.status(responseEntity.getStatusCode()).body(Map.of("message", "Errore da PayPal durante cattura pagamento.", "rawResponse", responseEntity.getBody()));
            }
        } catch (HttpClientErrorException e) {
            this.log.error("HttpClientErrorException PayPal (cattura): Status {}, Body {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", "Errore da PayPal: " + e.getResponseBodyAsString()));
        } catch (Exception e) {
            this.log.error("Errore imprevisto cattura pagamento PayPal: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Errore imprevisto durante la finalizzazione del pagamento."));
        }
        // Ritorno di fallback per il metodo capturePayPalOrder
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Errore server non gestito durante la cattura del pagamento PayPal."));

    }
}