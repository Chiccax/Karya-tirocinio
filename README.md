# Karya Reports

Un'app per la segnalazione degli incendi sviluppata da [Mariarosaria Esposito](https://www.github.com/Chiccax) e [Francesca Perillo](https://www.github.com/francesca-perillo) per il tirocionio svolto all'Università degli Studi di Salerno.

---

## Indice
- [Obiettivo](#Obiettivi)
- [Struttura del progetto](#Struttura-del-progetto)
- [Prerequisiti](#Prerequisiti)
- [Come eseguire il progetto](#Come-eseguire-il-progetto)

## Obiettivo <a name="Obiettivi"></a>

L'obiettivo di Karya è quello di rendere più semplice l’individuazione di incendi unendo tecnologia e società al fine di migliorare le modalità di segnalazione.

## Struttura del progetto <a name="Struttura-del-progetto"></a>

La repository è così strutturata:
- 📂**bot**: Contiene il codice necessario per far eseguire il bot telegram. Tramite il bot è possibile segnalare gli incendi in maniera rapida e semplice.
- 📂**database-middleware**: Contiene il server GraphQL implementato tramite la libreria Apollo Server. E' necessario che il server sia attivo affinché il bot e l'app riescano a comunicare con il database.
- 📑**database-schema.sql**: Contiene la struttura del database MySQL
- 📂**MappaIncendi**: E' una pagina web sul quale è possibile visualizzare gli incendi segnalati presenti sul database.
- 📂**Tirocinio**: Contiene il codice sorgente dell'app Android

## Prerequisiti <a name="Prerequisiti"></a>

Affinché si possano provare tutte le funzionalità è necessario aver installato
- NodeJS
- npm
- Android Studio per compilare il codice sorgente dell'app

## Come eseguire il progetto <a name="Come-eseguire-il-progetto"></a>

### Configurazioni iniziali

#### 📂 bot/database/db.js
Nel file *db.js* presente nella cartella *bot/database* è necessario apportare le seguenti modifiche
```js
...

const sequelize = new Sequelize(
    'INSERT_DB_NAME_HERE', // Il nome del database
    'INSERT_DB_USERNAME_HERE', // Username del database
    'INSERT_DB_PASSWORD_HERE', // Password del database
    {
        host: 'INSERT_HOST_HERE', // URL dove è hostato il database
        port: 'INSERT_PORT_HERE', // Porta sulla quale è attivo MySQL
        ...
    }

...
```

#### 📂 bot/bot.js
Nel file *bot.js* presente nella cartella *bot* è necessario inserire il codice del TOKEN Telegram. E' possibile generarne uno grazie al BotFather. Per ulteriori informazioni, [seguire questa guida](https://core.telegram.org/bots#6-botfather).
```js
...

//Token bot
const bot = new Telegraf("YOUR_TOKEN") // Token Telegram

...
```

#### 📂 database-middleware/database/db.js
Nel file *db.js* presente nella cartella *bot/database* è necessario apportare le seguenti modifiche
```js
...

const sequelize = new Sequelize(
    'INSERT_DB_NAME_HERE', // Il nome del database
    'INSERT_DB_USERNAME_HERE', // Username del database
    'INSERT_DB_PASSWORD_HERE', // Password del database
    {
        host: 'INSERT_HOST_HERE', // URL dove è hostato il database
        port: 'INSERT_PORT_HERE', // Porta sulla quale è attivo MySQL
        ...
    }

...
```

#### 📂 MappaIncendi/script.js
Nel file *script.js* presente nella cartella *MappaIncendi* è necessario inserire le API del servizio HERE MAP.
```js
...

const platform = new H.service.Platform({
    'apikey': 'YOUR_APIKEY' // HERE MAP API Key
});

...
```

#### 📂 Tirocinio/app/src/main/java/com/example/tirocinio/Config.java
Nel file *Config.java* presente nella cartella *Tirocinio/app/src/main/java/com/example/tirocinio* è necessario inserire l'indirizzo IP sul quale è eseguito il server GraphQL.
```java
...

//connection to database - change with your ipv4 id:
public static final String SERVER_URL = "http://YOUR_IPV4_ID:4000/graphql"; // IPV4 del server GraphQL

...
```

Inoltre, è necessario inserire l'email (e la relativa password) dell'indirizzo email dal quale far arrivare i messaggi di servizio.

```java
...

//to send email - change with your email data:
public static final String SENDER_EMAIL = "YOUR_EMAIL"; // Email (@gmail.com)
public static final String SENDER_PASSWORD = "YOUR_PASSWORD"; // Password dell'email

...
```

### Installare le dipendenze

Per installare le dipendeze necessarie, recarsi prima nella cartella *bot* e digitale il seguente comando:
```shell
npm install
```
Allo stesso modo, recarsi nella cartella *database-middleware* e digitare il comando:
```shell
npm install
```