define({
    "aboutView" : {
        "about" : "About 37coins",
        "desc" : "We are committed to making bitcoin easy, secure to use, and accessible to everyone. With a global focus on social equity, we develop down-teched, secure, scalable solutions that are as appropriate for emerging economies as they are for developed ones.",
        "team" : "Team",
        "role1" : "Social Entrepreneur",
        "role2" : "Jack-of-all-trades Intern",
        "role3" : "Developer",
        "role4" : "Designer"
    },
    "indexLayout" : {
        "mobileTag" : "Money without borders or barriers, as easy as sending an SMS. Send and receive Bitcoin anywhere in the World. No internet or smartphone required.",
        "wallet" : "After setting up your account, you will receive an automated text message from a gateway. To control your account, send any of these commands to that phone number via SMS.",
        "walletInv" : "Become an SMSwallet beta user",
        "walletBtn" : "Open a Wallet",
        "commands" : "Commands are not case-sensitve and are sent to the gateways via SMS/text. To learn more, visit the SMSwallet",
        "link" : "help page",
        "gw1" : "A decentralized, open-source Android app that bridges between SMS and Bitcoin. Designed for low-end Android phones. In other words, you can connect your country to the global economy via Bitcoin (and earn a little for your time and effort, too. ;-).",
        "gw2" : "Gateways are operated by partners who earn transaction fees. To become an SMSgateway partner, the following are required:",
        "gwLi" : ["Dedicated Android phone","Text messaging plan","Internet connection"],
        "gwInv" : "Become an SMSgateway partner",
        "gwBtn" : "Open a Gateway"
    },
    "title" : {
        "t-index" : "37coins",
        "t-merchant" : ""
    },
    "desc" : {
        "d-index" : "A Bitcoin Wallet for the Low-teched and Unbanked. It makes financial transactions as simple as sending a text.",
        "d-merchant" : ""
    },
    "email" : {
        "ResetSubject" : "Password Reset",
        "Reset" : "Please click this link to reset your password: {0}",
        "RegisterSubject" : "Email Verification",
        "Register" : "Please click this link to verify your email: {0}",
        "GatewayAlertSubject" : "Gateway offline",
        "GatewayAlert" : "Your 37coins gateway went offline a few minutes ago. Please restore operations. We will notify customers after an extended downtime.",
        "Byebye" : "Best regards,\r\n{0}"
    },
    "commands" : {
        "SignupCmd" : ["signup"],
        "HelpCmd" : ["help","hlp"],
        "DepositReqCmd" : ["addr","deposit","adr","address","adress"],
        "BalanceCmd" : ["bal","balance","blance","balnce"],
        "TransactionsCmd" : ["txns","transactions","trans","tran"],
        "WithdrawalReqCmd" : ["send","sending","sent"],
        "VoiceCmd" : ["voice","PIN"],
        "ChargeCmd" : ["req","request","charge"],
        "ProductCmd" : ["prod","product"],
        "PayCmd" : ["pay"],
        "PriceCmd" : ["price"],
        "SellCmd" : ["sell"],
        "BuyCmd" : ["buy"]
    },
    "sms" : {
        "Signup" : "Welcome to 37coins BETA! Bitcoin via SMS. Save this number, send commands to this gateway. Reply HELP for more info {0}",
        "DepositReq" : "{0}",
        "Charge" : "Payable through \"pay {0}\"",
        "Product" : "Payable through \"pay {0}\"",
        "DepositNotify" : "{0}mBTC{1} on the way to your wallet. Spendable in about 10 min, notify when complete.",
        "DepositConf" : "Received {0}mBTC{1}",
        "DepositConfSndr" : "Received {0}mBTC{1} from {2}",
        "DepositConfSndrMsg" : "Received {0}mBTC{1} from {2} for \"{3}\"",
        "Balance" : "Available balance {0}mBTC{1} {2}",
        "BalanceInst" : "\r\nReply ADDR, for Bitcoin address",
        "WithdrawalReq" : "Send {0}mBTC{1} to {2}?",
        "WithdrawalReqHelp" : "Please use SEND <amount> <phone no>\r\nEx: SEND 1.42USD 5558675309\r\n<amount>use XXUSD for US value, or just XX to indicate mili-bitcoin",
        "WithdrawalReqPay" : "Pay {0}mBTC{1} to {2} for {3}?",
        "WithdrawalReqIstr" : "To confirm transaction, reply {0}",
        "WithdrawalConf" : "Transferred {0}mBTC{1} to {2}.",
        "Help" : "SEND limit: 12mBTC before PIN setup\r\nADDR - Bitcoin deposit address\r\nBAL - see available balance\r\nPIN - setup PIN\r\n{0}",
        "Buy" : "Your number has been added as a buyer.",
        "FormatError" : "We had trouble understanding your request. Please resend in the correct format.",
        "InsufficientFunds" : "Insufficient funds:\r\nAvailable balance {0}mBTC{1},\r\nrequired {2}mBTC{3}.\r\nReply ADDR for Bitcoin deposit address",
        "UnknownCommand" : "You have sent an unknown command. Reply HELP or {0}",
        "Timeout" : "Confirmation response not received in time. Transaction canceled.",
        "TransactionFailed" : "Transaction failed due to an unknown reason.",
        "TransactionCanceled" : "Transaction canceled.",
        "Unavailable" : "{0} is currently unavailable, please try again in 1 hour.\r\nSorry for the inconvenience.",
        "DestinationUnreachable" : "No reliable gateway found in the country you are trying to send to.",
        "BelowFee" : "This transactions is not worth sending. Double-check the amount.",
        "AccountBlocked" : "Account blocked",
        "Overuse" : "Your command was found redundant and skipped to prevent high cost.",
        "Voice" : "Security PIN activated successfully"
    },
    "voice" : {
        "VoiceHello" : "37 Coins, your global wallet.",
        "VoiceSetup" : "To secure large transactions, create a secret 4-digit PIN.",
        "VoiceCreate" : "Please enter a new 4-digit PIN, followed by the hash key.",
        "VoiceConfirm" : "Please reenter your new 4-digit PIN, followed by the hash key.",
        "VoiceMerchantConfirm" : "Please enter the 4-digit number, followed by the hash key.",
        "VoiceMismatch" : "The PIN does not match, please try again.",
        "VoiceSuccess" : "Please remember this PIN for future transactions.",
        "VoiceEnter" : "Please enter your 4-digit PIN, followed by the hash key.",
        "VoiceOk" : "The PIN is correct. Transaction executing.",
        "VoiceFail" : "The PIN is not correct. The account will be blocked after 3 failed attempts.",
        "VoiceRegister" : "Hello from 37 coins. Your verification-code is ${payload}. Please enter ${payload} to complete verification."
    }
});
