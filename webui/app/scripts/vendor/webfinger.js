// -*- mode:js; js-indent-level:2 -*-
/*!
 * webfinger.js
 * version 0.1.0
 * http://github.com/silverbucket/webfinger.js
 *
 * Copyright 2012-2014 Nick Jennings <nick@silverbucket.net>
 *
 * With contributions from:
 * Michiel de Jong <michiel@michielbdejong.com>
 *
 * webfinger.js is released under the AGPL (see LICENSE).
 *
 * You don't have to do anything special to choose one license or the other and you don't
 * have to notify anyone which license you are using.
 * Please see the corresponding license file for details of these licenses.
 * You are free to use, modify and distribute this software, but all copyright
 * information must remain.
 *
 */
// if (typeof XMLHttpRequest !== 'function') {
//   var XMLHttpRequest = require('xmlhttprequest').XMLHttpRequest;
// }
// if (typeof document === 'undefined') {
//   var document = {};
// }
// if (typeof window === 'undefined') {
//   var window = {};
// }
(function (window, document, undefined) {

  // list of endpoints to try, fallback from beginning to end.
  var uris = ['webfinger', 'host-meta', 'host-meta.json'];
  var DEBUG = false; // wrapper flag for log

  function log() {
    var args = Array.prototype.splice.call(arguments, 0);
    if (DEBUG) {
      console.log.apply(undefined, args);
    }
  }

  function isValidJSON(str) {
    try {
      JSON.parse(str);
    } catch (e) {
      return false;
    }
    return true;
  }

  function isValidDomain(domain) {
    var pattern = /^[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$/;
    return pattern.test(domain);
  }

  // make an http request and look for JRD response, fails if request fails
  // or response not json.
  function getJRD(url, cb) {
    log('URL: ' + url);
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);
    xhr.onreadystatechange = function () {
      //log('xhr for '+url, xhr);
      if (xhr.readyState === 4) {
        if (xhr.status === 200) {
          log(xhr.responseText);
          if (isValidJSON(xhr.responseText)) {
            cb(null, xhr.responseText);
          } else {
            // invalid json response
            cb({
              message: 'invalid json',
              url: url,
              status: xhr.status
            });
          }
        } else {
          // request failed
          cb({
            message: 'webfinger endpoint unreachable',
            url: url,
            status: xhr.status
          });
        }
      }
    };
    xhr.setRequestHeader('Accept', 'application/json');
    xhr.send();
  }

  // processes JRD object as if it's a webfinger response object
  // looks for known properties and adds them to profile datat struct.
  function processJRD(JRD, cb) {
    var links = JSON.parse(JRD).links;
    if (!links) {
      var serverResp = JSON.parse(JRD);
      if (typeof serverResp.error !== 'undefined') {
        cb(serverResp.error);
      } else {
        cb('received unknown response from server');
      }
      return;
    }

    var result = {};
    result.properties = {
      'name': undefined
    };
    result.links = {
      'avatar': [],
      'remotestorage': [],
      'blog': [],
      'vcard': [],
      'updates': [],
      'share': [],
      'profile': [],
      'webfist': [],
      'camilstore': []
    };
    result.JRD = JRD; // raw webfinger JRD

    // process links
    for (var i = 0, len = links.length; i < len; i = i + 1) {
      log('finding match for ['+links[i].rel+']');
      switch (links[i].rel) {
        case "http://webfist.org/spec/rel":
          result.links.webfist.push(links[i].href);
          break;
        case 'http://webfinger.net/rel/avatar':
          log('found avatar: ' + links[i].href);
          result.links.avatar.push(links[i].href);
          break;
        case 'remotestorage':
        case 'remoteStorage':
          result.links.remotestorage.push(links[i].href);
          break;
        case 'http://www.packetizer.com/rel/share':
          result.links.share.push(links[i].href);
          break;
        case 'http://webfinger.net/rel/profile-page':
        case 'me':
          result.links.profile.push(links[i].href);
          break;
        case 'vcard':
          result.links.vcard.push(links[i].href);
          break;
        case 'blog':
        case 'http://packetizer.com/rel/blog':
          result.links.blog.push(links[i].href);
          break;
        case 'http://schemas.google.com/g/2010#updates-from':
          result.links.updates.push(links[i].href);
          break;
        case 'https://camlistore.org/rel/server':
          result.links.camilstore.push(links[i].href);
          break;
        case 'http://webfist.org/spec/rel':
          result.links.webfist(links[i].href);
          break;
      }
    }

    // process properties
    var props = JSON.parse(JRD).properties;
    for (var key in props) {
      if (props.hasOwnProperty(key)) {
        if (key === 'http://packetizer.com/ns/name') {
          result.properties.name = props[key];
        }
      }
    }
    cb(null, result);
  }


  function callWebFinger(address, p, cb) {
    p.tls_only = true; // never fallback to http

    if (!isValidDomain(p.host)) {
      cb('invalid host name');
      return;
    }

    if (typeof p.uri_fallback === "undefined") {
      p.uri_fallback = false;
    }
    if (typeof p.uri_index === "undefined") {
      // try first URI first
      p.uri_index = 0;
    }

    if (typeof p.protocol === "undefined") {
      // we use https by default
      p.protocol = 'https';
    }

    // make request
    getJRD(p.protocol + '://' + p.host + '/.well-known/' +
        uris[p.uri_index] + '?resource=acct:' + address,
    function(err, JRD) {
      if (err) {
        fallbackChecks(err);
      } else {
        processJRD(JRD, cb);
      }
    });


    // control flow for failures, what to do in various cases, etc.
    function fallbackChecks(err) {
      if ((p.uri_fallback) && (p.uri_index !== uris.length - 1)) { // we have uris left to try
        p.uri_index = p.uri_index + 1;
        callWebFinger(address, p, cb);
      } else if ((!p.tls_only) && (p.protocol === 'https')) { // try normal http
        p.uri_index = 0;
        p.protocol = 'http';
        callWebFinger(address, p, cb);
      } else if ((p.webfist_fallback) && (p.host !== 'webfist.org')) { // webfist attempt
        p.uri_index = 0;
        p.protocol = 'http';
        p.host = 'webfist.org';
        p.uri_fallback = false;
        // webfist will
        // 1. make a query to the webfist server for the users account
        // 2. from the response, get a link to the actual webfinger json data
        //    (stored somewhere in control of the user)
        // 3. make a request to that url and get the json
        // 4. process it like a normal webfinger response
        callWebFinger(address, p, function(err, result) { // get link to users JRD
          if (err) {
            cb(err);
          } else if ((typeof result.links.webfist === "object") &&
                     (result.links.webfist[0])) {
            getJRD(result.links.webfist[0], function (err, JRD) {
              if (err) {
                cb(err);
              } else {
                processJRD(JRD, cb);
              }
            });
          }
        });
      } else {
        cb(err);
      }
    }
  }

  window.webfinger = function(address, o, cb) {
    if (typeof o === 'function') {
      cb = o;
      o = {};
    } else if (typeof cb !== 'function') {
      console.log('webfinger.js: no callback function specified. webfinger(address, options, callback)');
      return { error: "no callback function specified" };
    }

    var parts = address.replace(/ /g,'').split('@');
    if (parts.length !== 2) {
      cb({message: 'invalid user address ( user@host )'});
      return false;
    }

    DEBUG = (typeof o.debug !== 'undefined') ? o.debug : false;

    callWebFinger(address, {
      host: parts[1],
      tls_only: (typeof o.tls_only !== 'undefined') ? o.tls_only : true,
      webfist_fallback: (typeof o.webfist_fallback !== 'undefined') ? o.webfist_fallback : true
    }, cb);
  };

})(window, document);

if (typeof (define) === 'function' && define.amd) {
  define([], function() { return window.webfinger; });
} else {
  try {
    module.exports = window.webfiner;
  } catch (e) {}
}