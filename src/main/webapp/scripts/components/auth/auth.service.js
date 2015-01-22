'use strict';

angular.module('publisherApp')
    .factory('Auth', function Auth($rootScope, $state, $q, $translate, Principal, AuthServerProvider, Account, Register, Activate, Password) {
        return {
            login: function (credentials, callback) {
                var cb = callback || angular.noop;
                var deferred = $q.defer();

                AuthServerProvider.login(credentials).then(function (data) {
                    // retrieve the logged account information
                    Principal.identity(true).then(function (account) {
                        // After the login the language will be changed to
                        // the language selected by the user during his registration
                        if (angular.isDefined(account) && account.hasOwnProperty('langKey'))
                            $translate.use(account.langKey);
                        deferred.resolve(account);
                    }).catch(function(err){
                        deferred.reject(err);
                    });
                    deferred.resolve(data);

                    return cb();
                }).catch(function (err) {
                    this.logout();
                    deferred.reject(err);
                    return cb(err);
                }.bind(this));

                return deferred.promise;
            },

            logout: function () {
                AuthServerProvider.logout();
                Principal.authenticate(null);
            },

            authorize: function() {
                return Principal.identity()
                    .then(function() {
                        var isAuthenticated = Principal.isAuthenticated();

                        if ($rootScope.toState.data.roles && $rootScope.toState.data.roles.length > 0 && !Principal.isInAnyRole($rootScope.toState.data.roles)) {
                            if (isAuthenticated) {
                                // user is signed in but not authorized for desired state
                                $state.go('accessdenied');
                            }
                            else {
                                // user is not authenticated. stow the state they wanted before you
                                // send them to the signin state, so you can return them when you're done
                                $rootScope.returnToState = $rootScope.toState;
                                $rootScope.returnToStateParams = $rootScope.toStateParams;

                                // now, send them to the signin state so they can log in
                                $state.go('login');
                            }
                        }
                    });
            },
            createAccount: function (account, callback) {
                var cb = callback || angular.noop;

                return Register.save(account,
                    function () {
                        return cb(account);
                    },
                    function (err) {
                        this.logout();
                        return cb(err);
                    }.bind(this)).$promise;
            },

            updateAccount: function (account, callback) {
                var cb = callback || angular.noop;

                return Account.save(account,
                    function () {
                        return cb(account);
                    },
                    function (err) {
                        return cb(err);
                    }.bind(this)).$promise;
            },

            activateAccount: function (key, callback) {
                var cb = callback || angular.noop;

                return Activate.get(key,
                    function (response) {
                        return cb(response);
                    },
                    function (err) {
                        return cb(err);
                    }.bind(this)).$promise;
            },

            changePassword: function (newPassword, callback) {
                var cb = callback || angular.noop;

                return Password.save(newPassword, function () {
                    return cb();
                }, function (err) {
                    return cb(err);
                }).$promise;
            }
        };
    })
    .factory('AuthInterceptor', ['$timeout', '$q', '$injector', function AuthInterceptor($timeout, $q, $injector) {
        var Auth, $http, $state, $rootScope, Principal;

        // this trick must be done so that we don't receive
        // `Uncaught Error: [$injector:cdep] Circular dependency found`
        $timeout(function () {
            Auth = Auth || $injector.get('Auth');
            Principal = Principal || $injector.get('Principal');
            $http = $http ||$injector.get('$http');
            $state = $state || $injector.get('$state');
            $rootScope = $rootScope || $injector.get('$rootScope');
        });

        return {
            responseError: function (rejection) {
                // don't launch auth when request only the account as it's requested at first access
                if (rejection.status !== 401 || $rootScope.modalOpened || rejection.data.path === "/api/account" ) {
                    return $q.reject(rejection);
                }

                var deferred = $q.defer();

                Principal.authenticate(null);
                Auth.login()
                    .then(function () {
                        deferred.resolve( $http(rejection.config) );
                    })
                    .catch(function () {
                        $state.go('site');
                        deferred.reject(rejection);
                    });

                return deferred.promise;
            }
        };
    }]);
