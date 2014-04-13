define(['backbone',
    'communicator',
    'GA',
    'models/loginModel',
    'models/accountRequest',
    'models/resetRequest',
    'models/resetConf',
    'models/signupConf',
    'models/balanceModel',
    'models/feeModel',
    'collections/gatewayCollection',
    'views/indexView',
    'views/loginView',
    'views/gatewayView',
    'views/faqView',
    'views/contactView',
    'views/verifyView',
    'views/validateView',
    'views/captchaView',
    'views/logoutView',
    'views/signupView',
    'views/resetView',
    'views/resetConfView',
    'views/signupConfView',
    'views/balanceView',
    'views/feeView',
    'views/gatewayLayout',
    'views/notFoundView',
    'views/exampleView',
    'routeFilter',
    'socketio',
    'views/merchantLoginView',
    'views/merchantConnectingView',
    'views/MerchantDisconnectView'
    ], function(Backbone, Communicator, GA, LoginModel, AccountRequest, ResetRequest, ResetConf, SignupConf, BalanceModel, FeeModel, GatewayCollection, IndexView, LoginView, GatewayView, FaqView, ContactView, VerifyView, ValidateView, CaptchaView, LogoutView, SignupView, ResetView, ResetConfView, SignupConfView, BalanceView, FeeView, GatewayLayout, NotFoundView, ExampleView, io, MerchantLoginView, MerchantConnectingView, MerchantDisconnectView) {
    'use strict';

    var Controller = {};

    // private module/app router  capture route and call start method of our controller
    Controller.Router = Backbone.Marionette.AppRouter.extend({
        initialize: function(opt){
            this.app = opt.app;
        },
        appRoutes: {
            '': 'showIndex',
            'gateways': 'showGateway',
            'balance': 'showBalance',
            'faq': 'showFaq',
            'confSignup/:token': 'confirmSignUp',
            'confReset/:token': 'confirmReset',
            'reset': 'showReset',
            'contact': 'showContact',
            'signUp': 'showSignUp',
            'logout': 'showLogout',
            'merchant': 'showMerchantFront',
            'example': 'showExample',
            'notFound': 'showNotFound'
        },
        before:{
            'signUp': 'getTicket',
            'reset': 'getTicket',
            'gateways': 'showLogin',
            'balance': 'showLogin',
            'merchant': 'showMerchantLogin',
            '*any': function(fragment, args, next){
                //set title
                if (fragment){
                    $(document).attr('title', '37 Coins - ' + fragment);
                }else {
                    $(document).attr('title', '37 Coins');
                }
                //set meta tag
                $('meta[name=description]').remove();
                $('head').append( '<meta name="description" content="this is new">' );
                //track page visit
                GA.view(fragment);
                next();
            }
        },
        getTicket: function(fragment, args, next) {
            if (!this.options.controller.ticket){
                //TODO: show wain screen
                var self = this;
                $.post( window.opt.basePath + '/ticket', function( data ) {
                    self.options.controller.ticket = data.value;
                    next();
                },'json').fail(function() {
                    var view = new CaptchaView({next:next,controller:self.options.controller});
                    Communicator.mediator.trigger('app:show', view);
                });
            }else{
                next();
            }
        },
        showLogin: function(fragment, args, next) {
            if (!this.options.controller.loginStatus){
                this.options.controller.loginStatus = new LoginModel();
            }
            var view;
            var model = this.options.controller.loginStatus;
            if (model.get('roles')){
                next();
            }else{
                view = new LoginView({model:model,next:next});
                Communicator.mediator.trigger('app:show', view);
            }
        },
        showMerchantLogin: function(fragment, args, next){
            //take care of the socket
            if (!this.app.socketio){
                var self = this;
                var socket = io.connect(window.opt.basePath.split(':8')[0]+':8081');
                this.app.socketio = socket;
                socket.on('message', function (data) {
                    Communicator.mediator.trigger('app:message', data);
                    //new events: charge  pay
                    //data return for txns
                });
                socket.on('connecting', function () {
                    var view = new MerchantConnectingView();
                    Communicator.mediator.trigger('app:show', view);
                });
                socket.on('reconnecting', function () {
                    var view = new MerchantConnectingView();
                    Communicator.mediator.trigger('app:show', view);
                });
                socket.on('disconnect', function () {
                    var view = new MerchantDisconnectView();
                    Communicator.mediator.trigger('app:show', view);
                });
                socket.on('reconnect', function () {
                    self.reconnect();
                });
                socket.on('connect', function (data) {
                    //send subscribe event for new session
                    self.reconnect(data,next);
                });
            }
            next();
        },
        reconnect: function(data,next){
            var sessionToken = (!sessionStorage.getItem('sessionToken')||sessionStorage.getItem('sessionToken')==='undefined')?undefined:sessionStorage.getItem('sessionToken');
            if (!sessionToken){
                if (next){
                    var view = new MerchantLoginView({next:next});
                    Communicator.mediator.trigger('app:show', view);
                }else{
                    console.log('reconnect, but no prev session.');
                }
            }else{
                //we have a session, subscribe to it
                var obj = { '@class' : 'com._37coins.web.Subscribe',
                    'sessionToken' : sessionToken
                };
                this.app.socketio.json.send(obj);
            }
        }
    });

    Communicator.mediator.on('app:verify', function() {
        var view;
        if (Controller.loginStatus.get('mobile') && Controller.loginStatus.get('fee')){
            var layout = new GatewayLayout();
            Communicator.mediator.trigger('app:show', layout);
            var configView = new GatewayView({model:Controller.loginStatus});
            layout.conf.show(configView);
            var balance = new BalanceModel();
            var balanceView = new BalanceView({model:balance});
            layout.bal.show(balanceView);
            var feeModel = new FeeModel({fee:sessionStorage.getItem('fee')});
            var feeView = new FeeView({model:feeModel});
            layout.fee.show(feeView);
        }else if (Controller.loginStatus.get('mobile')){
            view = new ValidateView({model:Controller.loginStatus});
            Communicator.mediator.trigger('app:show', view);
        }else {
            view = new VerifyView({model:Controller.loginStatus});
            Communicator.mediator.trigger('app:show', view);
        }
    });

    Controller.showIndex = function() {
        if (!this.gateways){
            this.gateways = new GatewayCollection();
        }
        var view = new IndexView({collection:this.gateways,model:new Backbone.Model({resPath:window.opt.resPath})});
        Communicator.mediator.trigger('app:show', view);
        if (this.gateways.length<1){
            //load dependency manually
            var script = document.createElement('script');
            script.type = 'text/javascript';
            var self = this;
            script.onload = function(){
                console.log('fetched');
                Communicator.mediator.trigger('app:init');
                self.gateways.fetch({reset: true});
            };

            script.src = window.opt.resPath + '/scripts/vendor/libphonenumbers.js';
            document.getElementsByTagName('head')[0].appendChild(script);
        }
    };

    Controller.showGateway = function() {
        Communicator.mediator.trigger('app:verify');
    };

    Controller.showFaq = function() {
        var view = new FaqView();
        Communicator.mediator.trigger('app:show', view);
    };

    Controller.showContact = function() {
        var view = new ContactView();
        Communicator.mediator.trigger('app:show', view);
    };

    Controller.showBalance = function() {
        var balance = new BalanceModel();
        var view = new BalanceView({model:balance});
        Communicator.mediator.trigger('app:show', view);
        balance.fetch();
    };

    Controller.showLogin = function(fragment, args, next) {
        if (!this.loginStatus){
            this.loginStatus = new LoginModel();
        }
        var view = new LoginView({model:this.loginStatus,next:next});
        Communicator.mediator.trigger('app:show', view);
    };
    Controller.showLogout = function() {
        var contentView = new LogoutView();
        Communicator.mediator.trigger('app:show',contentView);
    };
    Controller.showMerchantFront = function() {
        var contentView = new MerchantFrontView();
        Communicator.mediator.trigger('app:show',contentView);
    };
    Controller.showNotFound = function() {
        var contentView = new NotFoundView();
        Communicator.mediator.trigger('app:show',contentView);
    };
    Controller.showExample = function() {
        var contentView = new ExampleView();
        Communicator.mediator.trigger('app:show',contentView);
    };
    Controller.showSignUp = function() {
        var accountRequest = new AccountRequest({ticket:Controller.ticket});
        var contentView = new SignupView({model:accountRequest});
        Communicator.mediator.trigger('app:show',contentView);
    };
    Controller.confirmSignUp = function(token) {
        var model = new SignupConf({token:token});
        var contentView = new SignupConfView({model: model});
        Communicator.mediator.trigger('app:show',contentView);
        model.save();
    };
    Controller.showReset = function() {
        var model = new ResetRequest({ticket:Controller.ticket});
        var contentView = new ResetView({model:model});
        Communicator.mediator.trigger('app:show',contentView);
    };
    Controller.confirmReset = function(token) {
        var model = new ResetConf({token:token});
        var contentView = new ResetConfView({model: model});
        Communicator.mediator.trigger('app:show',contentView);
    };

    return Controller;
});