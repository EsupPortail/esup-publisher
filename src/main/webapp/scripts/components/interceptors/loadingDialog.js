'use strict';

angular.module('publisherApp')
    .factory('loadingDialogInterceptor', ['$q', '$injector', '$http', function ($q, $injector, $http) {
        var error;

        function success(response) {
            // get $http via $injector because of circular dependency problem
            $http = $http || $injector.get('$http');
            if($http.pendingRequests.length < 1) {
                $('#loadingWidget').hide();
            }
            return response;
        }

        function error(response) {
            // get $http via $injector because of circular dependency problem
            $http = $http || $injector.get('$http');
            if($http.pendingRequests.length < 1) {
                $('#loadingWidget').hide();
            }
            return $q.reject(response);
        }

        return function (promise) {
                $('#loadingWidget').show();
                return promise.then(success, error);
            }

    }])
    .factory('requestNotificationChannel', ['$rootScope', function($rootScope){
        // private notification messages
        var _START_REQUEST_ = '_START_REQUEST_';
        var _END_REQUEST_ = '_END_REQUEST_';

        // publish start request notification
        var requestStarted = function() {
            $rootScope.$broadcast(_START_REQUEST_);
        };
        // publish end request notification
        var requestEnded = function() {
            $rootScope.$broadcast(_END_REQUEST_);
        };
        // subscribe to start request notification
        var onRequestStarted = function($scope, handler){
            $scope.$on(_START_REQUEST_, function(event){
                handler();
            });
        };
        // subscribe to end request notification
        var onRequestEnded = function($scope, handler){
            $scope.$on(_END_REQUEST_, function(event){
                handler();
            });
        };

        return {
            requestStarted:  requestStarted,
            requestEnded: requestEnded,
            onRequestStarted: onRequestStarted,
            onRequestEnded: onRequestEnded
        };
    }]);


        /*var modalHide = function(config) {
            var timeout = 500;
            console.log(JSON.stringify(config));
            if (config.url.indexOf("api/enums/filtertype") !=-1) {
                timeout = 3000;
            }
            $timeout(function () {
                //console.log("waiting");
                if (config.waitmodal) {
                    if (config.waitmodal > 1) {
                        config.waitmodal--;
                    } else if (config.waitmodal == 1) {
                        config.waitmodal = 0;
                        $("#pleaseWaitDialog").modal('hide');
                    }
                }
            }, timeout);


        };

        return {
            request: function (config) {
                if (!config.waitmodal) {
                    $("#pleaseWaitDialog").modal('show');
                    config.waitmodal = 1;

                } else if (config.waitmodal >= 0) {
                    config.waitmodal++;
                }
                //config.requestTimestamp = new Date().getTime();
                console.log("enter request :", config.waitmodal);
                return config;
            },
            response: function (response) {
                modalHide(response.config);
                //response.config.responseTimestamp = new Date().getTime();
                console.log("enter reponse :", response.config.waitmodal);
                return response;
            },
            responseError: function (rejection) {
                modalHide(response.config);
                //$("#errorDialog").modal('show');
                console.log("enter reponse :", response.config.waitmodal);
                return $q.reject(rejection);
            }
        };
    }]);*/
