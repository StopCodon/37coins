define([
	'backbone',
	'communicator',
	'hbs!tmpl/notFoundView_tmpl',
	'i18n!nls/labels',
    'i18n!nls/webLabels'
],
function(Backbone, Communicator, NotFoundTmpl, myLabels, myWebLabels) {
    'use strict';
    return Backbone.Marionette.ItemView.extend({
        template: NotFoundTmpl,
        className: 'container',
		templateHelpers: function(){
            console.log ("Loading helpers and localization for our 404 page");
			return window.helpers(myLabels, myWebLabels);
		},
        initialize: function() {
            
        }
    });
});