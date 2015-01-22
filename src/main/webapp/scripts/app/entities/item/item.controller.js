'use strict';

angular.module('publisherApp')
    .controller('ItemController', function ($scope, $state, $filter, $translate, Item, Organization, Redactor, EnumDatas, ParseLinks) {
        $scope.items = [];
        $scope.organizations = Organization.query();
        $scope.redactors = Redactor.query();
        $scope.page = 1;
        $scope.templates = [{name: 'news.html', url: 'scripts/app/entities/news/news-add.html'},
            {name: 'media.html', url: 'scripts/app/entities/media/media-add.html'},
            {name: 'resource.html', url: 'scripts/app/entities/resource/resource-add.html'}];

        $scope.itemStatusList = EnumDatas.getItemStatusList();

        $scope.loadAll = function () {
            Item.query({page: $scope.page, per_page: 20}, function (result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.items = result;
            });
        };
        $scope.loadPage = function (page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.create = function (type) {
            $scope.type = type;
            $scope.clear();
            switch (type) {
                case 'NEWS':
                    $('#saveNewsModal').modal('show');
                    break;
                case 'MEDIA':
                    $('#saveMediaModal').modal('show');
                    break;
                case 'RESOURCE':
                    $('#saveResourceModal').modal('show');
                    break;
            }
        };
        $scope.publish = function (status) {
            switch (status) {
                case 'PUBLISH':
                    $scope.item.status = "PUBLISHED";
                    break;
                default:
                    //DRAFT
                    $scope.item.status = "DRAFT";
                    break;
            }
            Item.update($scope.item,
                function () {
                    $scope.loadAll();
                    switch ($scope.item.type) {
                        case 'NEWS':
                            $('#saveNewsModal').modal('hide');
                            break;
                        case 'MEDIA':
                            $('#saveMediaModal').modal('hide');
                            break;
                        case 'RESOURCE':
                            $('#saveResourceModal').modal('hide');
                            break;
                    }
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            Item.get({id: id}, function (result) {
                $scope.item = result;
                switch ($scope.item.type) {
                    case 'NEWS':
                        $('#saveNewsModal').modal('show');
                        break;
                    case 'MEDIA':
                        $('#saveMediaModal').modal('show');
                        break;
                    case 'RESOURCE':
                        $('#saveResourceModal').modal('show');
                        break;
                }
            });
        };

        $scope.delete = function (id) {
            Item.get({id: id}, function (result) {
                $scope.item = result;
                $('#deleteItemConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Item.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteItemConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.dtformats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'dd/MM/yyyy','shortDate'];
        $scope.dtformat = $scope.dtformats[3];

        var today = new Date();
        var tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);
        var nextweek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
        var nextyear = new Date(today.getTime() + 366 * 24 * 60 * 60 * 1000);

        $scope.today = new Date(new Date(today.getFullYear(), today.getMonth(), today.getDate()));
        $scope.tomorrow = new Date(new Date(tomorrow.getFullYear(), tomorrow.getMonth(), tomorrow.getDate()));
        $scope.nextweek = new Date(new Date(nextweek.getFullYear(), nextweek.getMonth(), nextweek.getDate()));
        $scope.nextyear = new Date(new Date(nextyear.getFullYear(), nextyear.getMonth(), nextyear.getDate()));

        $scope.clear = function () {
            var entityID = $scope.organizations[0].id;
            var redactorID = $scope.redactors[0].id;
            //if ($scope.redactors) redactorID = $scope.redactors[0].id;
            //console.log("is defined :" + JSON.stringify($scope.currentOrganization));
            //console.log("is defined :" + JSON.stringify($scope.currentPublisher));
            if (angular.isObject($scope.currentOrganization)) {
                entityID = $scope.currentOrganization.id;
            }
            if (angular.isObject($scope.currentPublisher)) {
                redactorID = $scope.currentPublisher.redactor.id;
            }

            $scope.item = null;

            switch ($scope.type) {
                case 'NEWS':
                    $scope.item = {
                        type: "NEWS",
                        title: null,
                        enclosure: null,
                        endDate: $scope.nextweek,
                        startDate: $scope.today,
                        validatedBy: null,
                        validatedDate: null,
                        status: null,
                        summary: null,
                        body: null,
                        createdBy: null,
                        createdDate: null,
                        lastModifiedBy: null,
                        lastModifiedDate: null,
                        id: null,
                        organization: {id: entityID},
                        redactor: {id: redactorID}
                    };
                    if ($scope.editNewsForm) {
                        $scope.editNewsForm.$setPristine();
                        $scope.editNewsForm.$setUntouched();
                    }
                    break;
                case 'MEDIA':
                    $scope.item = {
                        type: "MEDIA",
                        title: null,
                        enclosure: null,
                        endDate: $scope.nextweek,
                        startDate: $scope.today,
                        validatedBy: null,
                        validatedDate: null,
                        status: null,
                        summary: null,
                        createdBy: null,
                        createdDate: null,
                        lastModifiedBy: null,
                        lastModifiedDate: null,
                        id: null,
                        organization: {id: entityID},
                        redactor: {id: redactorID}
                    };
                    if ($scope.editMediaForm) {
                        $scope.editMediaForm.$setPristine();
                        $scope.editMediaForm.$setUntouched();
                    }
                    break;
                case 'RESOURCE':
                    $scope.item = {
                        type: "RESOURCE",
                        title: null,
                        enclosure: null,
                        endDate: $scope.nextweek,
                        startDate: $scope.today,
                        validatedBy: null,
                        validatedDate: null,
                        status: null,
                        summary: null,
                        ressourceUrl: null,
                        createdBy: null,
                        createdDate: null,
                        lastModifiedBy: null,
                        lastModifiedDate: null,
                        id: null,
                        organization: {id: entityID},
                        redactor: {id: redactorID}
                    };
                    if ($scope.editResourceForm) {
                        $scope.editResourceForm.$setPristine();
                        $scope.editResourceForm.$setUntouched();
                    }
                    break;
            }
        };

        //Watch for date changes
        /*$scope.$watch('item.startDate', function(newDate) {
            console.log('New date set: ', newDate);

        }, false);*/

        $scope.getItemTypeLabel = function (name) {
            return $scope.itemStatusList.filter(function (val) {
                return val.name === name;
            })[0].label;
        };

    });
