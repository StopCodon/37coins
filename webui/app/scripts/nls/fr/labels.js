define({
    "email" : {
        "ResetSubject" : "Mise à jour du mot de passe",
        "Reset" : "SVP, cliquez sur le lien pour rétablir votre mot de passe: {0}",
        "RegisterSubject" : "vérification d''email",
        "Register" : "SVP, cliquez sur le lien pour v\u00E9trifier votre email: {0}",
        "GatewayAlertSubject" : "Gateway offline", 
        "GatewayAlert" : "Votre passerelle 37coins s'est déconnecté, il ya quelques minutes. S'il vous plaît les opérations de restauration. Nous allons informer les clients après un arrêt prolongé.",
        "Byebye" : "Salutations cordiales :), au revoir,\r\n{0}"
    },
    "commands" : {
        "SignupCmd" : ["signupFR"],
        "HelpCmd" : ["aide","hlp"],
        "DepositReqCmd" : ["compte"],
        "BalanceCmd" : ["solde"],
        "TransactionsCmd" : ["actes"],
        "WithdrawalReqCmd" : ["envoyer"],
        "VoiceCmd" : ["voice"],
        "ChargeCmd" : ["charger"],
        "ProductCmd" : ["produit"],
        "PayCmd" : ["payer"],
        "PriceCmd" : ["prix"],
        "SellCmd" : ["vendre"],
        "BuyCmd" : ["acheter"]
    },
    "sms" : {
        "Signup" : "Bienvenue! Votre portefeuille mondial / Bitcoin via SMS. Save this number, send commands to this gateway. Reply HELP for more info {0}",
        "DepositReq" : "{0}",
        "Charge" : "Payable through \"pay {0}\"",
        "Product" : "Payable through \"pay {0}\"",
        "DepositNotify" : "{0}{2}{1} on the way to your wallet. Spendable in about 10 min, notify when complete.",
        "DepositConf" : "Vous avez reçu {0}{2}{1}",
        "DepositConfSndr" : "Vous avez reçu {0}{3}{1} from {2}",
        "DepositConfSndrMsg" : "Vous avez reçu {0}{4}{1} from {2} for \"{3}\"",
        "Balance" : "Solde disponible: {0}{3}{1} {2}",
        "BalanceInst" : "\r\nReply ADDR, for Bitcoin address",
        "WithdrawalReq" : "Envoyer {0}{4}{1} à {2}?",
        "WithdrawalReqHelp" : "Please use SEND <amount> <phone no>\r\nEx: SEND 1.42USD 5558675309\r\n<amount>use XXUSD for US value, or just XX to indicate mili-bitcoin",
        "WithdrawalReqPay" : "Pay {0}{4}{1} to {2} for {3}?",
        "WithdrawalReqIstr" : "Pour confirmer, répondez \"oui {0}\".",
        "WithdrawalConf" : "Transféré {0}{3}{1} à {2}.",
        "Help" : "ENVOYER limite: 10EUR before PIN setup\r\nADDR - Bitcoin deposit address\r\nBAL - see available balance\r\nPIN - setup PIN\r\n{0}",
        "Buy" : "Votre numéro a été ajouté en tant qu''acheteur.",
        "FormatError" : "Votre commande n''a pas été correctement interprétée. Merci d''essayer de nouveau avec un format correct.",
        "InsufficientFunds" : "Votre solde est insuffisant:\r\nVous avez {0}{4}{1},\r\{2}{4}{3} sont nécessaires pour terminer la transaction.",
        "UnknownCommand" : "Vous avez envoyé une commande inconnue. Répondre AIDE ou {0}",
        "Timeout" : "Aucune confirmation recue dans le temps imparti. Transaction annulée.",
        "TransactionFailed" : "La transaction a échoué pour des raisons inconnues.",
        "TransactionCanceled" : "Transaction annulée.", // Mathan - 20140618 21:08:09 - I used google translate for this. 
        "Unavailable" : "{0} is currently unavailable, please try again in 1 hour.\r\nSorry for the inconvenience.",
        "DestinationUnreachable" : "Nous n'avons trouvé aucune passerelle fiable dans le pays que vous essayez d'envoyer un message.",
        "BelowFee" : "Cette transaction ne sera pas envoyée. Double-check the amount.",
        "AccountBlocked" : "Le compte est bloqué.",
        "Overuse" : "Votre commande est redondante et n''est donc pas prise en considération pour vous éviter des frais inutiles.",
        "Voice" : "Security PIN activated successfully"
    },
    "voice" : {
        "VoiceHello" : "37 Coins, votre portefeuille mondial.",
        "VoiceSetup" : "Pour sécuriser vos transactions, merci de créer un code PIN à 4 chiffres.",
        "VoiceCreate" : "Merci d''entrer un nouveau code PIN à 4 chiffres suivi de la touche dièse (#).",
        "VoiceConfirm" : "Merci de répéter votre nouveau code PIN à 4 chiffres suivi de la touche dièse (#).",
        "VoiceMerchantConfirm" : "Please enter the 4-digit number, followed by the hash key.",
        "VoiceMismatch" : "Mauvais code PIN. Merci d''essayer une nouvelle fois.",
        "VoiceSuccess" : "Merci de vous souvenir de votre code PIN pour vos prochaines transactions.",
        "VoiceEnter" : "Merci de composer votre code PIN à 4 chiffres suivi de la touche dièse (#).",
        "VoiceOk" : "Correct. La transaction est en cours d''éxécution.",
        "VoiceFail" : "Le code PIN est incorrect. Le compte sera bloqué après 3 tentatives infructueuses.",
        "VoiceRegister" : "Bonjour de la part de 37coins. Votre code de vérification est $ {payload}. Merci de renvoyer $ {payload} pour terminer la vérification."
    }
});