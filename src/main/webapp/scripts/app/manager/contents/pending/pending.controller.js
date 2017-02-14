'use strict';
angular.module('publisherApp')
    .controller('PendingController', function ($scope, $state, Item, ContentDTO,ParseLinks, Subject) {
        $scope.items = [];
        $scope.page = 1;
        /** in manager state resolve
         $scope.itemStateList = itemStatusList;
         $scope.organizations = organizationList;
         $scope.redactors = redactorList;*/
        $scope.loadAll = function() {
            Item.query({page: $scope.page, per_page: 20, owned: false, item_status: getEnumKey('PENDING')}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.items = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();


        $scope.validate = function (id) {
            Item.get({id: id}, function(result) {
                $scope.item = result;
                $('#validateItemConfirmation').modal('show');
            });
        };

        $scope.confirmValidate = function (id) {
            Item.patch({objectId:id, attribute : "validate", value: "true"},
                function () {
                    $scope.loadAll();
                    $('#validateItemConfirmation').modal('hide');
                    //$scope.clear();
                });
        };

        $scope.delete = function (id) {
            Item.get({id: id}, function(result) {
                $scope.item = result;
                $('#deleteItemConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ContentDTO.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteItemConfirmation').modal('hide');
                    //$scope.clear();
                });
        };

        /*$scope.clear = function () {
            $scope.item = {title: null, enclosure: null, endDate: null, startDate: null, validatedBy: null, validatedDate: null, status: null, summary: null, body: null, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
            $scope.editForm.$setPristine();
            $scope.editForm.$setUntouched();
        };*/
        $scope.viewSubject= function(subject) {
            if (subject.subject.modelId) {
                $state.go('pending.subject',  subject.subject.modelId);
            }
            return false;
        };
        $scope.userAttrs = Subject.getUserDisplayedAttrs();

        function getEnumKey (name) {
            return $scope.itemStateList.filter(function(val) {
                return val.name === name;
            })[0].id;
        }

    });

