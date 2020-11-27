const db = require('./database/db');

//Richiedi l'utilizzo della libreria
const { Telegraf, Markup } = require('telegraf')

const NodeGeocoder = require('node-geocoder');
const axios = require('axios');
const fs = require('fs');

const options = {
    provider: 'openstreetmap',
    language: 'it',
    formatter: null // 'gpx', 'string', ...
};

const geocoder = NodeGeocoder(options);

//Sessione
const session = {};

//Token bot
const bot = new Telegraf("YOUR_TOKEN")

bot.start((ctx) => {
    const keyboard = Markup.inlineKeyboard([
        [
            Markup.callbackButton("Si", "utenteRegistrato")
        ],
        [
            Markup.callbackButton("No", "utenteNonRegistrato")
        ]
    ]).extra();
    ctx.reply("Benvenuto, sei iscritto alla nostra app?", keyboard)
})

bot.action('utenteRegistrato', (ctx) => {
    ctx.reply("Per iniziare digita /me seguito dall'email utilizzata in fase di registrazione all'app. \n\nPer esempio, se ti sei registrato alla nostra app con la email email@dominio.com, digita:\n/me email@dominio.com\n\n")
})

bot.action('utenteNonRegistrato', (ctx) => {
    const keyboard = Markup.inlineKeyboard([
        [
            Markup.urlButton("Play Store", "https://play.google.com/store?hl=it&gl=US")
        ]
    ]).extra();
    ctx.reply("Scarica la nostra app attraverso il pulsante sottostante, registrati e dopodichè digita /me seguito dall'email utilizzata in fase di registrazione all'app. \n\nPer esempio, se ti sei registrato alla nostra app con la email email@dominio.com, digita:\n/me email@dominio.com\n\n", keyboard);
})

//Comando iniziale me email
bot.command('me', async (ctx) => {
    const telegramId = ctx.message.from.id;
    if (await authorize(ctx.message)) {
        const user = await db.user.findOne({
            where: {
                telegramId: telegramId
            }
        })

        ctx.reply("Bentornato  " + user.name + ". \n- Digita /iniziamo per avviare una segnalazione. \n- Digita /fine per terminare.");
        return;
    }

    const email = ctx.message.text.split("/me")[1].trim();

    if (!email || email == "")
        ctx.reply("Per favore riesegui il comando inserendo un'email valida.");
    else {
        const user = await db.user.findOne({
            where: {
                email: email
            }
        })

        if (user != null) {
            db.user.update({ telegramId: telegramId }, {
                where: {
                    email: email
                }
            })
            ctx.reply("Benvenuto " + user.name + `. \n- Digita /iniziamo per avviare una segnalazione. \n- Digita /fine per terminare.`);
        } else {
            ctx.reply("Registrati alla nostra app 'Karya Reports' per continuare.");
        }
    }
})

//Comando iniziamo
bot.command('iniziamo', (ctx) => {
    session[ctx.message.from.id] = { Step: 0 };
    ctx.reply("Invia la tua posizione attuale.\n\nN.B. Attivare il GPS.");
})

bot.on('location', async (ctx) => {
    if (session[ctx.message.from.id]) {
        if (session[ctx.message.from.id].Step == 0) {
            var location = ctx.message.location;
            var lat = location.latitude;
            var lng = location.longitude;
            const res = await geocoder.reverse({ lat: lat, lon: lng });
            const address = res[0].formattedAddress;
            session[ctx.message.from.id].address = address;
            session[ctx.message.from.id].Step = 1;
            ctx.reply("Invia la posizione attuale dell'incendio.\n\nN.B. Spostarsi sulla mappa della voce 'posizione' degli allegati di telegram.");
        } else if (session[ctx.message.from.id].Step == 1) {
            const keyboard = Markup.inlineKeyboard([
                [
                    Markup.callbackButton("Avanti", "avanti")
                ]
            ]).extra();
            var location = ctx.message.location;
            var lat = location.latitude;
            var lng = location.longitude;
            const res = await geocoder.reverse({ lat: lat, lon: lng });
            const addressFire = res[0].formattedAddress;
            session[ctx.message.from.id].addressFire = addressFire;
            session[ctx.message.from.id].selectedInfo = [false, false, false, false, false, false];
            session[ctx.message.from.id].Step = 2;
            ctx.reply("Carica una foto dell'incendio.\n\nSe non è possibile premi su 'avanti'", keyboard).catch(e => ctx.reply("Errore"));
        } else {
            ctx.reply("Si è verificato un errore.\nDigita /iniziamo per ricominciare altrimenti /fine per terminare.")
            delete session[ctx.message.from.id];          
        }
    } else {
        ctx.reply("Si è verificato un errore.\nDigita /iniziamo per ricominciare altrimenti /fine per terminare.")
        delete session[ctx.message.from.id];
    }
})

const getInfoKeyboard = (sessionId) => {
    const { selectedInfo } = session[sessionId];
    const keyboard = Markup.inlineKeyboard([
        [
            selectedInfo[0] ? Markup.callbackButton("✔️ Zona industriale", "info-deselect-0") : Markup.callbackButton("Zona industriale", "info-select-0"),
            selectedInfo[1] ? Markup.callbackButton("✔️ Discariche", "info-deselect-1") : Markup.callbackButton("Discariche", "info-select-1")
        ],
        [
            selectedInfo[2] ? Markup.callbackButton("✔️ Centro abitato", "info-deselect-2") : Markup.callbackButton("Centro abitato", "info-select-2"),
            selectedInfo[3] ? Markup.callbackButton("✔️ Tralicci di alta tensione", "info-deselect-3") : Markup.callbackButton("Tralicci di alta tensione", "info-select-3")
        ],
        [
            selectedInfo[4] ? Markup.callbackButton("✔️ Gasdotto", "info-deselect-4") : Markup.callbackButton("Gasdotto", "info-select-4"),
            selectedInfo[5] ? Markup.callbackButton("✔️ Campanili", "info-deselect-5") : Markup.callbackButton("Campanili", "info-select-5")
        ],
        [
            Markup.callbackButton("Conferma", "submit")
        ]
    ]).extra();
    return keyboard;
}

bot.action('avanti', (ctx) => {
    session[ctx.callbackQuery.from.id].Step = 3;
    ctx.reply("Seleziona le opzioni di seguito elencate di cui sei a conoscenza della loro presenza nei pressi dell'incendio: ", getInfoKeyboard(ctx.callbackQuery.from.id))
})

bot.action(/^info-.*-\d$/, ctx => {
    const [info, action, i] = ctx.callbackQuery.data.split("-");
    const index = parseInt(i);
    session[ctx.callbackQuery.from.id].selectedInfo[index] = action === "select";

    ctx.editMessageText("Seleziona le opzioni di seguito elencate di cui sei a conoscenza della loro presenza nei pressi dell'incendio: ", getInfoKeyboard(ctx.callbackQuery.from.id));
})

bot.on('photo', async (ctx) => {
    if (session[ctx.message.from.id] && session[ctx.message.from.id].Step == 2) {
        var fileId = ctx.message.photo[0].file_id;
        var photo = await ctx.telegram.getFileLink(fileId).then(url => {
            return new Promise((resolve, reject) => {
                axios({ url, responseType: 'stream' }).then(response => {
                    const path = `./temp/images/profiles/${ctx.update.message.from.id}.jpg`;
                    response.data.pipe(fs.createWriteStream(path))
                        .on('finish', () => {
                            fs.readFile(path, (err, data) => {
                                if (err)
                                    reject(err);
                                const toResolve = new Buffer(data, "binary").toString("base64");
                                fs.unlink(path, err => reject(err));
                                resolve(toResolve);
                            })
                        })
                        .on('error', e => reject(e))
                })
            })
        })
        session[ctx.message.from.id].photo = photo;
        session[ctx.message.from.id].Step = 3;
        ctx.reply("Seleziona le opzioni di seguito elencate di cui sei a conoscenza della loro presenza nei pressi dell'incendio: ", getInfoKeyboard(ctx.message.from.id))
    } else {
        ctx.reply("Si è verificato un errore.\nDigita /iniziamo per ricominciare altrimenti /fine per terminare.")
        if (session[ctx.message.from.id])
            delete session[ctx.message.from.id];       
        return
    }
})

bot.command('fine', (ctx) => {
    delete session[ctx.message.from.id];
    ctx.reply("Segnalazione terminata.");
})

bot.action('submit', async (ctx) => {
    const userSession = session[ctx.callbackQuery.from.id];
    const { selectedInfo } = userSession;
    const emailUser = await db.user.findOne({
        where: {
            telegramId: ctx.callbackQuery.from.id
        }
    })

    const currentdate = new Date();
    const datetime = currentdate.getFullYear() + "/"
        + (currentdate.getMonth() + 1) + "/"
        + currentdate.getDate() + " "
        + currentdate.getHours() + ":"
        + currentdate.getMinutes() + ":"
        + currentdate.getSeconds();

    const toSave = {
        actual_location: userSession.address,
        fire_location: userSession.addressFire,
        photo: userSession.photo,
        industrial_area: selectedInfo[0],
        waste_dump: selectedInfo[1],
        city_center: selectedInfo[2],
        trusses: selectedInfo[3],
        gas_pipeline: selectedInfo[4],
        bell_tower: selectedInfo[5],
        more_info: " ",
        dateAndTime: datetime,
        user_email: emailUser.dataValues.email,
    }

    db.fire.create(toSave)
        .then(() => {
            ctx.reply("Le informazioni sono state ricevute e verranno elaborate. ✔️ \nGraz️ie della collaborazione, i nostri colleghi provvederanno al più presto. 🔥\nPer avviare un'altra segnalazione digita /iniziamo altrimenti /fine per terminare.")
            delete session[ctx.callbackQuery.from.id];
        })
        .catch(e => {
            ctx.reply("Impossibile salvare la tua richiesta, riprova più tardi.");
            delete session[ctx.callbackQuery.from.id];
            return;
        });
})

bot.on('text', async (ctx) => {
    ctx.reply('Comando non esistente. \nDigita /start se non sai come proseguire.')
})

const authorize = async message => {
    const chatId = message.from.id;
    const result = await db.user.findOne({
        where: {
            telegramId: chatId
        }
    })

    if (result == null) {
        return false;
    }
    else
        return true;
}

//Esegui il bot 
bot.launch()