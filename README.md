# MyShop E-commerce - Applicazione Web Java Spring Boot

Questa è un'applicazione web e-commerce completa sviluppata in Java utilizzando il framework Spring Boot. Il progetto simula le funzionalità principali di una piattaforma di shopping online, includendo un frontend per i clienti e un backend per l'amministrazione.

## Descrizione del Progetto

MyShop E-commerce è progettata per offrire un'esperienza di acquisto online intuitiva e una gestione efficiente del negozio per gli amministratori. Gli utenti possono registrarsi, navigare nel catalogo prodotti, aggiungerli al carrello, effettuare il checkout e visualizzare la cronologia dei loro ordini. Gli amministratori hanno accesso a una dashboard per gestire prodotti, categorie e ordini.
![Screenshot (899)](https://github.com/user-attachments/assets/0eab8ecf-1238-4454-97d7-7885bf2e79af)

![Screenshot (901)](https://github.com/user-attachments/assets/5d19d628-8bb7-4dfa-9b9a-cae78e8106e5)




## Tecnologie Utilizzate

### Backend
*   **Linguaggio:** Java 11 
*   **Framework Principale:** Spring Boot 2.7.x
    *   **Spring MVC:** Per la gestione delle richieste web e il pattern Model-View-Controller.
    *   **Spring Data JPA & Hibernate:** Per l'Object-Relational Mapping (ORM) e l'interazione con il database.
    *   **Spring Security:** Per l'autenticazione (login standard, login social) e l'autorizzazione (gestione ruoli).
    *   **Spring OAuth2 Client:** Per l'integrazione del login con Google (e placeholder per Facebook).
    *   **Spring Mail:** Per l'invio di email transazionali (conferma registrazione, conferma ordine, aggiornamento stato ordine).
    *   **Spring RESTful Web Services:** Struttura predisposta per future API (es. per app mobile o integrazioni esterne).
*   **Database:** MySQL 
*   **Build Tool:** Apache Maven
*   **Server:** Tomcat Embedded (fornito da Spring Boot)

### Frontend
*   **View Technology:** JSP (JavaServer Pages) con JSTL (JSP Standard Tag Library)
*   **Styling & Layout:** Bootstrap 4 (via CDN)
*   **Interattività (Base):** jQuery 3 (via CDN), HTML5, CSS3

### Strumenti di Sviluppo e Altro
*   **IDE:** IntelliJ IDEA
*   **Testing Email (Sviluppo):** Mailtrap.io
*   **Controllo Versione:** Git & GitHub

## Funzionalità Implementate

### 1. Autenticazione e Gestione Utenti
*   Registrazione cliente (username, email, password, nome, cognome).
*   Login cliente (username/email + password).
*   Login social con Google.
*   Login separato per l'Amministratore.
*   Distinzione dei ruoli: `ROLE_CUSTOMER`, `ROLE_ADMIN`.

  
![Screenshot (912)](https://github.com/user-attachments/assets/355f38ee-2cd6-47ee-856b-dd6ae02221bf)

![Screenshot (910)](https://github.com/user-attachments/assets/b648e997-b4ff-4706-9c7a-4af9a35ab9ff)


### 2. Area Cliente (Frontend)
*   **Catalogo Prodotti:**
    *   Visualizzazione lista prodotti con paginazione.
    *   Filtri per categoria, keyword (nome/descrizione), e range di prezzo.
    *   Ordinamento dei prodotti (per nome, prezzo, novità).
    *   Visualizzazione pagina dettaglio prodotto con immagini e descrizione completa.
*   **Carrello:**
    *   Aggiunta prodotti al carrello.
    *   Visualizzazione e modifica del carrello (quantità, rimozione articoli).
    *   Carrello gestito in sessione HTTP.
*   **Checkout:**
    *   Processo di checkout multi-step.
    *   Raccolta indirizzo di spedizione.
    *   Creazione dell'ordine nel database con stato iniziale.
    *   Integrazione con **PayPal Checkout** per la gestione dei pagamenti (flusso Sandbox).
    *   Cattura del pagamento e aggiornamento dello stato dell'ordine.
    *   Pagina di conferma ordine post-pagamento.
*   **Dashboard Cliente:**
    *   Visualizzazione del profilo utente (dati anagrafici).
    *   Modifica del profilo (nome, cognome).
    *   Cronologia degli ordini effettuati con visualizzazione dei dettagli di ogni ordine.
*   **Notifiche Email:**
    *   Email di conferma alla registrazione.
    *   Email di conferma ordine dopo il pagamento.
    *   Email di notifica all'aggiornamento dello stato dell'ordine da parte dell'admin.
 
  ![Screenshot (908)](https://github.com/user-attachments/assets/413723cd-7945-4f9d-a5ef-72e305a03219)


### 3. Area Amministratore (Backend)
*   **Dashboard Admin:** Pagina di benvenuto con accesso rapido alle sezioni di gestione.
*   **Gestione Prodotti (CRUD):**
    *   Creazione, lettura, aggiornamento ed eliminazione dei prodotti.
    *   Campi gestiti: nome, descrizione, prezzo, stock, categoria, **upload immagine prodotto**, disponibilità.
*   **Gestione Categorie Prodotto (CRUD):**
    *   Creazione, lettura, aggiornamento ed eliminazione delle categorie.
*   **Gestione Ordini:**
    *   Visualizzazione di tutti gli ordini dei clienti con paginazione e ordinamento.
    *   Visualizzazione dei dettagli completi di un singolo ordine.
    *   Aggiornamento dello stato dell'ordine (es. Da "In Elaborazione" a "Spedito").



![Screenshot (896)](https://github.com/user-attachments/assets/59fbb783-8403-414f-ae51-ad5204c95635)


![Screenshot (913)](https://github.com/user-attachments/assets/aa25df2b-a97a-4692-bbfc-64205d04cc69)



![Screenshot (914)](https://github.com/user-attachments/assets/a9b09e73-7a8f-4341-bd6a-e7eb5153c1ba)


![Screenshot (915)](https://github.com/user-attachments/assets/64637319-a96a-4855-84d6-b33ce2f76612)



![Screenshot (916)](https://github.com/user-attachments/assets/35d79b6a-f102-4e0f-b6dd-299db8b43698)

![Screenshot (918)](https://github.com/user-attachments/assets/4ab5c02c-880a-4034-8c72-0a85e5d343cf)


![Screenshot (905)](https://github.com/user-attachments/assets/6bb31af1-830c-46aa-82b7-6b043a48abd8)


![Screenshot (907)](https://github.com/user-attachments/assets/b661073d-7045-48c4-bd28-9b82d576fe6e)

![Screenshot (906)](https://github.com/user-attachments/assets/3b6bb84c-4bb2-411e-88fa-bef566a354c9)


### 4. Sicurezza
*   Protezione delle pagine riservate (cliente e admin) tramite Spring Security.
*   Autorizzazione basata sui ruoli.
*   Protezione CSRF abilitata per i form.

## Struttura del Database (Tabelle Principali)
*   `users`
*   `roles`
*   `users_roles` (tabella di join)
*   `products`
*   `categories`
*   `orders`
*   `order_items`
*   `shippings`
*   `payments`

![Screenshot (919)](https://github.com/user-attachments/assets/4f37d302-1380-4411-9129-f5893c4083c8)


## Setup e Avvio del Progetto

1.  **Prerequisiti:**
    *   JDK 11
    *   Apache Maven 3.6+.
    *   Un server MySQL in esecuzione.
    *   Un account Mailtrap.io (per test email in sviluppo).
    *   Credenziali API Sandbox da Google Developer Console (per il login con Google).
    *   Credenziali API Sandbox da PayPal Developer (per i pagamenti).

2.  **Configurazione Database MySQL:**
    *   Crea un database MySQL (es. `myshop_db`).
    *   Aggiorna le credenziali del database (`spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`) nel file `src/main/resources/application.properties`.

3.  **Configurazione Servizi Esterni (`application.properties`):**
    *   Inserisci le tue credenziali Mailtrap per `spring.mail.*`.
    *   Inserisci il tuo Client ID e Client Secret di Google per `spring.security.oauth2.client.registration.google.*`.
    *   Inserisci il tuo Client ID di PayPal (Sandbox) per `paypal.client.id`.
    *   (Opzionale) Inserisci il tuo Client Secret di PayPal (Sandbox) per `paypal.client.secret` (necessario se si implementano API PayPal server-to-server in modo diverso da quanto fatto).

4.  **Build del Progetto (Maven):**
    Apri un terminale nella directory principale del progetto ed esegui:
    ```bash
    mvn clean package
    ```
    Questo compilerà il codice e creerà un file WAR (o JAR se configurato diversamente) nella cartella `target/`.

5.  **Avvio dell'Applicazione:**
    *   **Da IDE:** Esegui la classe principale `EcommerceApplication.java`.
    *   **Da Riga di Comando (se pacchettizzato come JAR eseguibile):**
        ```bash
        java -jar target/ecommerce-webapp-0.0.1-SNAPSHOT.jar
        ```
    *   **Deploy WAR su Tomcat Esterno:** Copia il file WAR generato da Maven nella directory `webapps` del tuo server Tomcat.

6.  **Accesso all'Applicazione:**
    Apri il browser e vai a `http://localhost:8080` (o `http://localhost:8080/TUO_CONTEXT_PATH` se ne hai configurato uno).

7.  **Dati Iniziali:**
    L'applicazione utilizza un file `src/main/resources/data.sql` per popolare il database con dati iniziali (ruoli, utente admin, categorie, prodotti) all'avvio (se `spring.sql.init.mode=always` e `spring.jpa.hibernate.ddl-auto` è `update` o `create`).
    *   **Utente Admin di Default:** username `admin`, password `adminpassword` (cambiala e ricodificala in `data.sql`!).

## Struttura del Progetto

Il progetto segue la struttura standard di un'applicazione Maven/Spring Boot:

*   `src/main/java`: Codice sorgente Java
    *   `com.myshop.ecommerce`: Package radice
        *   `config`: Classi di configurazione Spring (Security, PayPal, ecc.).
        *   `controller`: Controller Spring MVC (divisi in `admin`, `api`, `customer`, `front`).
        *   `dto`: Data Transfer Objects.
        *   `entity`: Entità JPA che mappano le tabelle del database.
        *   `enums`: Enumerazioni (es. `OrderStatus`, `AuthProvider`).
        *   `exception`: Classi per eccezioni custom.
        *   `model`: Classi modello non-JPA (es. `Cart`, `CartItem`).
        *   `repository`: Interfacce Spring Data JPA.
        *   `security`: Classi relative a Spring Security (es. `CustomOAuth2User`).
        *   `service`: Interfacce e implementazioni dei servizi di business.
        *   `specification`: Specifiche JPA per query complesse.
        *   `EcommerceApplication.java`: Classe principale Spring Boot.
*   `src/main/resources`: File di risorse
    *   `static`: Risorse statiche (CSS, JS, immagini).
        *   `images/products`: Dove vengono salvate le immagini dei prodotti caricate.
    *   `application.properties`: File di configurazione principale.
    *   `data.sql`: Script SQL per dati iniziali.
*   `src/main/webapp/WEB-INF/views`: File JSP.
    *   `admin`, `auth`, `customer`, `error`, `front`, `partials`.
*   `pom.xml`: File di configurazione Maven.

## Possibili Miglioramenti Futuri
*   Login con Facebook.
*   Modifica password e email per l'utente.
*   Email di reset password.
*   Template HTML per le email.
*   Test unitari e di integrazione.
*   Miglioramenti UI/UX.
*   Recensioni prodotti.
*   Wishlist.
*   Gestione indirizzi multipli.
*   Internazionalizzazione (i18n).
*   Deployment in un ambiente di produzione.



