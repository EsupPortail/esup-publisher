'use strict';

angular.module('publisherApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
