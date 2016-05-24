'use strict';
angular.module('publisherApp')
    .controller('PublisherChoiceController', function ($scope, $filter, loadedPublishers, loadedPublisher) {
        $scope.selectedPublisher = {context :{reader:{}, redactor:{},organization:{}}};

        $scope.$parent.publisher = $scope.$parent.publisher || {};

        if (angular.equals({},$scope.$parent.publisher) && loadedPublisher) {
            $scope.$parent.publisher = angular.copy(loadedPublisher.publisher);
        } /*else if (!angular.equals({},$scope.$parent.publisher)) {
         console.log("init publisher");
         $scope.$parent.publisher = {context : {organization: {}, redactor: {}, reader: {}}};
         }*/

        // init of model object for the form
        if (!angular.equals({},$scope.$parent.publisher)) {
            $scope.selectedPublisher = {context :{reader:$scope.$parent.publisher.context.reader,
                redactor:$scope.$parent.publisher.context.redactor,
                organization:$scope.$parent.publisher.context.organization}};
        }

        $scope.isPublisherSelected = function() {
            return !angular.equals({},$scope.selectedPublisher.context.reader)
                && !angular.equals({},$scope.selectedPublisher.context.redactor)
                && !angular.equals({},$scope.selectedPublisher.context.organization);
        };

        $scope.validatePublisher = function() {
            //console.log("validatePublisher");
            if ($scope.isPublisherSelected) {
                //console.log("validatePublisher pub selected");
                for (var i = 0, size = $scope.publishers.length; i < size; i++) {
                    if ($scope.publishers[i].context.reader.id == $scope.selectedPublisher.context.reader.id
                        && $scope.publishers[i].context.redactor.id == $scope.selectedPublisher.context.redactor.id
                        && $scope.publishers[i].context.organization.id == $scope.selectedPublisher.context.organization.id) {
                        $scope.$parent.publisher = angular.copy($scope.publishers[i]);
                        //console.log("validatePublisher pub selected : ", JSON.stringify($scope.$parent.publisher));
                        break;
                    }
                }
            }
        };

        $scope.loadAll = function() {
            $scope.publishers = [];
            angular.copy(loadedPublishers, $scope.publishers);

            $scope.readers = [];
            $scope.redactors = [];
            $scope.organizations = [];
            $scope.filteredFromReader = [];
            $scope.filteredFromRedactor = [];

            // init all selectors if new publication
            if (angular.equals({},$scope.$parent.publisher)) {
                initReaders();
                if ($scope.publishers.length == 1) {
                    $scope.selectedPublisher.context.reader = angular.copy($scope.publishers[0].context.reader);
                    $scope.selectedPublisher.context.redactor = angular.copy($scope.publishers[0].context.redactor);
                    $scope.selectedPublisher.context.organization = angular.copy($scope.publishers[0].context.organization);
                    $scope.readers = [$scope.publishers[0].context.reader];
                    $scope.redactors = [$scope.publishers[0].context.redactor];
                    $scope.organizations = [$scope.publishers[0].context.organization];
                }
            } else {
                // init selectors from provided publisher
                // we provide all readers as it can be changed and is the first select that will permit filtering other selects
                initReaders();
                initRedactors($scope.publishers,true);
                initOrganizations($scope.filteredFromReader, true);

            }
        };
        $scope.loadAll();


        $scope.selectReader = function(item) {
            $scope.selectedPublisher.context.reader = item;

            initRedactors($scope.publishers, true);
            if ($scope.filteredFromReader.length == 1) {
                $scope.selectedPublisher.context.redactor = angular.copy($scope.filteredFromReader[0].context.redactor);
                $scope.selectedPublisher.context.organization = angular.copy($scope.filteredFromReader[0].context.organization);
                $scope.redactors = [$scope.filteredFromReader[0].context.redactor];
                $scope.organizations = [$scope.filteredFromReader[0].context.organization];
            } else if ($scope.redactors.length == 1) {
                $scope.selectRedactor($scope.redactors[0]);
            } else {
                $scope.selectedPublisher.context.redactor = {};
                $scope.selectedPublisher.context.organization = {};
            }
        };

        $scope.selectRedactor = function(item) {
            $scope.selectedPublisher.context.redactor = item;

            initOrganizations($scope.filteredFromReader, true);
            if ($scope.filteredFromRedactor.length == 1) {
                $scope.selectedPublisher.context.organization = angular.copy($scope.filteredFromRedactor[0].context.organization);
                $scope.organizations = [$scope.filteredFromReader[0].context.organization];
            } else {
                $scope.selectedPublisher.context.organization = {};
            }

        };

        $scope.selectOrganization = function(item) {
            $scope.selectedPublisher.context.organization = item;
            var filteredFromOrganization = filterFromOrganization($scope.filteredFromRedactor, $scope.selectedPublisher.context.organization.id);
            /*$scope.$parent.publisher = angular.copy(filteredFromOrganization[0]);*/
        };

        function initReaders() {
            $scope.readers = [];
            var uniqreaders = {};
            for (var i = 0, size = $scope.publishers.length; i < size; i++) {
                if (!uniqreaders[$scope.publishers[i].context.reader.id]) {
                    $scope.readers.push($scope.publishers[i].context.reader);
                    uniqreaders[$scope.publishers[i].context.reader.id] = $scope.publishers[i].context.reader;
                }
            }
        }

        function initRedactors(publishers, filterFromPublisherReader){
            $scope.redactors = [];
            $scope.filteredFromReader = [];
            publishers = (angular.isObject(publishers)) ? toArray(publishers) : publishers;
            if(!angular.isArray(publishers)) {
                return [];
            }
            if (filterFromPublisherReader && $scope.selectedPublisher.context.reader && $scope.selectedPublisher.context.reader.id) {
                $scope.filteredFromReader = filterFromReader(publishers, $scope.selectedPublisher.context.reader.id);
            } else {
                $scope.filteredFromReader = angular.copy(publishers);
            }
            var uniqredactors = {};
            for (var i = 0, size = $scope.filteredFromReader.length; i < size; i++) {
                if (!uniqredactors[$scope.filteredFromReader[i].context.redactor.id]) {
                    $scope.redactors.push($scope.filteredFromReader[i].context.redactor);
                    uniqredactors[$scope.filteredFromReader[i].context.redactor.id] = $scope.filteredFromReader[i].context.redactor;
                }
            }
        }

        function initOrganizations(publishers, filterFromPublisherRedactor){
            $scope.organizations = [];
            $scope.filteredFromRedactor = [];
            publishers = (angular.isObject(publishers)) ? toArray(publishers) : publishers;
            if(!angular.isArray(publishers)) {
                return [];
            }
            if (filterFromPublisherRedactor && $scope.selectedPublisher.context.redactor && $scope.selectedPublisher.context.redactor.id) {
                $scope.filteredFromRedactor = filterFromRedactor(publishers, $scope.selectedPublisher.context.redactor.id);
            } else {
                $scope.filteredFromRedactor = angular.copy(publishers);
            }
            var uniqorganizations = {};
            for (var i = 0, size = $scope.filteredFromRedactor.length; i < size; i++) {
                if (!uniqorganizations[$scope.filteredFromRedactor[i].context.organization.id]) {
                    $scope.organizations.push($scope.filteredFromRedactor[i].context.organization);
                    uniqorganizations[$scope.filteredFromRedactor[i].context.organization.id] = $scope.filteredFromRedactor[i].context.organization;
                }
            }
        }

        function filterFromReader(collection, search){
            search = (angular.isString(search) || angular.isNumber(search)) ?
                String(search).toLowerCase() : undefined;

            collection = (angular.isObject(collection)) ? toArray(collection) : collection;

            if(!angular.isArray(collection) || angular.isUndefined(search)) {
                //return collection;
                return [];
            }
            var filtered = [];
            for (var i = 0, size = collection.length; i < size; i++) {
                if (collection[i].context.reader.id == search) {
                    filtered.push(angular.copy(collection[i]));
                }
            }
            return filtered;
        }
        function filterFromRedactor(collection, search){
            search = (angular.isString(search) || angular.isNumber(search)) ?
                String(search).toLowerCase() : undefined;

            collection = (angular.isObject(collection)) ? toArray(collection) : collection;

            if(!angular.isArray(collection) || angular.isUndefined(search)) {
                //return collection;
                return [];
            }
            var filtered = [];
            for (var i = 0, size = collection.length; i < size; i++) {
                if (collection[i].context.redactor.id == search) {
                    filtered.push(angular.copy(collection[i]));
                }
            }
            return filtered;
        }
        function filterFromOrganization(collection, search){
            search = (angular.isString(search) || angular.isNumber(search)) ?
                String(search).toLowerCase() : undefined;

            collection = (angular.isObject(collection)) ? toArray(collection) : collection;

            if(!angular.isArray(collection) || angular.isUndefined(search)) {
                //return collection;
                return [];
            }
            var filtered = [];
            for (var i = 0, size = collection.length; i < size; i++) {
                if (collection[i].context.organization.id == search) {
                    filtered.push(angular.copy(collection[i]));
                }
            }
            return filtered;
        }
        function toArray(object) {
            return angular.isArray(object) ? object :
                Object.keys(object).map(function(key) {
                    return object[key];
                });
        }

    });
