'use strict';
angular.module('publisherApp')
    .controller('ContentWriteController', function ($scope, $state, EnumDatas, $rootScope, loadedItem, Item, Upload, $timeout, FileManager, Base64, DateService) {
        $scope.$parent.item = $scope.$parent.item || {};
        $scope.$parent.itemValidated = $scope.$parent.itemValidated || false;
        //$scope.content = {type : '', picFile: null, picUrl: null, file:null};
        $scope.content = {type : '', picFile: null, picUrl: null, file:null};
        $scope.itemTypeList = $scope.$parent.publisher.context.reader.authorizedTypes;
        $scope.enclosureDirty = false;

        var today = new Date();
        var tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);
        var nextweek = new Date(today.getTime() + 7 * 24 * 60 * 60 * 1000);
        var nextyear = new Date(today.getTime() + 366 * 24 * 60 * 60 * 1000);

        $scope.today = new Date(new Date(today.getFullYear(), today.getMonth(), today.getDate()));
        $scope.startdate = angular.copy($scope.today);
        $scope.tomorrow = new Date(new Date(tomorrow.getFullYear(), tomorrow.getMonth(), tomorrow.getDate()));
        $scope.nextweek = new Date(new Date(nextweek.getFullYear(), nextweek.getMonth(), nextweek.getDate()));
        $scope.nextyear = new Date(new Date(nextyear.getFullYear(), nextyear.getMonth(), nextyear.getDate()));


        $scope.dtformats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'dd/MM/yyyy','shortDate'];
        $scope.dtformat = $scope.dtformats[3];

        $scope.changeContentType = function(oldValue) {
            console.log("changeContentType ", oldValue, $scope.content.type);
            if (!angular.equals(oldValue, $scope.content.type) || !angular.isDefined($scope.$parent.item) || angular.equals({}, $scope.$parent.item)) {
                $scope.initItem();
            }
        };

        if (!$scope.$parent.publisher || !$scope.$parent.publisher.id){
            //console.log("go back previous state as publisher isn't setted");
            $rootScope.back();
        }

        if (angular.equals({},$scope.$parent.item) && loadedItem) {
            $scope.$parent.item = angular.copy(loadedItem);
            //console.log('loaded Item :' + JSON.stringify(loadedItem));
        }

        if (loadedItem && loadedItem.startDate != '') {
            $scope.startdate = angular.copy(loadedItem.startDate);
            //console.log('loaded Item start date:' + JSON.stringify(loadedItem.startDate));
        }

        if ($scope.$parent.item.type) {
            $scope.content.type = $scope.$parent.item.type;
        }

        if ($scope.itemTypeList.length == 1) {
            var oldVal = $scope.content.type;
            $scope.content.type = $scope.itemTypeList[0];
            $timeout(function() {$scope.changeContentType(oldVal);});
        }

        $scope.templates = [{name: 'NEWS', url: 'scripts/app/manager/publish/content/news.html'},
            {name: 'MEDIA', url: 'scripts/app/manager/publish/content/media.html'},
            {name: 'RESOURCE', url: 'scripts/app/manager/publish/content/resource.html'},
            {name: 'FLASH', url: 'scripts/app/manager/publish/content/flash.html'},
            {name: 'EVENT', url: 'scripts/app/manager/publish/content/resource.html'}];

        $scope.itemStatusList = EnumDatas.getItemStatusList();

        $scope.$watch('$parent.item', function(newType, oldType) {
            if (angular.isDefined($scope.publishContentForm) && $scope.publishContentForm.$valid
                && angular.isDefined($scope.$parent.item) && $scope.$parent.item.body != null && $scope.$parent.item.body.length > 5
                && DateService.getDateDifference($scope.today, $scope.$parent.item.endDate) > 0 && DateService.isValidDateRange($scope.$parent.item.startDate, $scope.$parent.item.endDate)) {
                $scope.$parent.itemValidated = true;
            } else {
                $scope.$parent.itemValidated = false;
            }

            //console.log("Form : ", $scope.publishContentForm);
            //console.log("item : ", $scope.$parent.item);
        }, true);

        $scope.validateItem = function (){
            $scope.$parent.itemValidated = true;
        };

        $scope.initItem = function () {
            //console.log("init Item with type " + JSON.stringify($scope.type));
            var entityID = $scope.$parent.publisher.context.organization.id;
            var redactorID = $scope.$parent.publisher.context.redactor.id;

            $scope.$parent.item = {};

            switch ($scope.content.type) {
                case 'NEWS':
                    $scope.$parent.item = {
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
                        rssAllowed: false,
                        createdBy: null,
                        createdDate: null,
                        lastModifiedBy: null,
                        lastModifiedDate: null,
                        id: null,
                        organization: {id: entityID},
                        redactor: {id: redactorID}
                    };
                    break;
                case 'MEDIA':
                    $scope.$parent.item = {
                        type: "MEDIA",
                        title: null,
                        enclosure: null,
                        endDate: $scope.nextweek,
                        startDate: $scope.today,
                        validatedBy: null,
                        validatedDate: null,
                        status: null,
                        summary: null,
                        rssAllowed: false,
                        createdBy: null,
                        createdDate: null,
                        lastModifiedBy: null,
                        lastModifiedDate: null,
                        id: null,
                        organization: {id: entityID},
                        redactor: {id: redactorID}
                    };
                    break;
                case 'RESOURCE':
                    $scope.$parent.item = {
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
                        rssAllowed: false,
                        createdBy: null,
                        createdDate: null,
                        lastModifiedBy: null,
                        lastModifiedDate: null,
                        id: null,
                        organization: {id: entityID},
                        redactor: {id: redactorID}
                    };
                    break;
                case 'FLASH':
                    $scope.$parent.item = {
                        type: "FLASH",
                        title: null,
                        enclosure: null,
                        endDate: $scope.nextweek,
                        startDate: $scope.today,
                        validatedBy: null,
                        validatedDate: null,
                        status: null,
                        summary: null,
                        body: null,
                        rssAllowed: false,
                        createdBy: null,
                        createdDate: null,
                        lastModifiedBy: null,
                        lastModifiedDate: null,
                        id: null,
                        organization: {id: entityID},
                        redactor: {id: redactorID}
                    };
                    break;
                default: console.log("Type not managed :", $scope.content.type); break;
            }
            /*if (angular.isDefined($scope.$parent.item)) {
                console.log("inited item :", $scope.$parent.item.type, $scope.$parent.item.startDate, $scope.$parent.item.endDate);
            }*/
            if ($scope.publishContentForm) {
                $scope.publishContentForm.$setPristine();
                $scope.publishContentForm.$setUntouched();
            }
            //console.log("item init : ",$scope.$parent.item);
        };

        $scope.goOnTargets = function() {
            //console.log("goOnTargets : " + JSON.stringify($scope.$parent.publisher.context.redactor.writingMode == "TARGETS_ON_ITEM"));
            return $scope.$parent.publisher.context.redactor.writingMode == "TARGETS_ON_ITEM";
        };

        $scope.invalidFiles = [];
        $scope.uploadFile = function (file) {
            Upload.upload({
                url: 'app/upload/',
                data: {
                    file: file,
                    isPublic: true,
                    entityId: $scope.$parent.publisher.context.organization.id
                }
            }).then(function (response) {
                $timeout(function () {
                    $scope.result = response.headers("Location");
                    //console.log("Upload result ",$scope.result);
                    //console.log("item id ",$scope.$parent.item.id);
                    if ($scope.$parent.item.id) {
                        Item.patch({objectId:$scope.$parent.item.id, attribute : "enclosure", value: $scope.result}, function() {
                            $scope.$parent.item.enclosure = $scope.result;
                            $('#cropImageModale').modal('hide');
                            //$scope.publishContentForm.enclosure.$setValidity('url', true);
                        });
                    } else {
                        $scope.$parent.item.enclosure = $scope.result;
                        $('#cropImageModale').modal('hide');
                    }
                });
            }, function (response) {
                //console.log('Request status: ' + response.status);
                if (response.status > 0)
                    $scope.errorMsg = response.status + ': ' + response.data;
            }, function (evt) {
                $scope.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                //$scope.progress = parseInt(100.0 * evt.loaded / evt.total);
                //console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% ' + evt.config.data.file.name);
            });
        };

        $scope.uploadCroppedFile = function(dataUrl, file) {
            //console.log("uploadFiles", dataUrl, file, $scope.$parent.publisher.context.organization.id);
            //console.log("FileSize", Upload.dataUrltoBlob(dataUrl).size());
            //console.log("datas", $scope.content, $scope.crop);
            Upload.upload({
                url: 'app/upload/',
                data: {
                    file : Upload.dataUrltoBlob(dataUrl, file.name),
                    isPublic: true,
                    entityId: $scope.$parent.publisher.context.organization.id
                }
            }).then(function (response) {
                $timeout(function () {
                    $scope.result = response.headers("Location");
                    //console.log("Upload result ",$scope.result);
                    if ($scope.$parent.item.id) {
                        Item.patch({objectId:$scope.$parent.item.id, attribute : "enclosure", value: $scope.result}, function() {
                            $scope.$parent.item.enclosure = $scope.result;
                            $('#cropImageModale').modal('hide');
                            //$scope.publishContentForm.enclosure.$setValidity('url', true);
                        });
                    } else {
                        $scope.$parent.item.enclosure = $scope.result;
                        $('#cropImageModale').modal('hide');
                    }
                });
            }, function (response) {
                //console.log('Reponse status: ', response.status);
                //console.log('Reponse data: ', response.data);
                //console.log('Reponse : ', response);
                if (response.status > 0)
                    $scope.errorMsg = response.status + ': ' + response.data;
            }, function (evt) {
                $scope.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                //$scope.progress = parseInt(100.0 * evt.loaded / evt.total);
                //console.log('progress: ' + parseInt(100.0 * evt.loaded / evt.total) + '% ' + evt.config.data.file.name);
            });
        };

        $scope.crop =  {};
        $scope.crop.initCrop = false;

        //$scope.$watch('crop.imageCropStep', function(newType, oldType) {
        //        console.log('Step :', $scope.crop.imageCropStep);
        //});

        $scope.fileCropChanged = function(e) {

            var files = e.target.files;

            var fileReader = new FileReader();
            fileReader.readAsDataURL(files[0]);

            fileReader.onload = function(e) {
                $scope.content.picFile = this.result;
                $scope.content.file = this.result;
                $scope.$apply();
            };

        };

        $scope.clearCrop = function() {
            $scope.crop =  {};
            $scope.crop.imageCropStep = 1;
            $scope.crop.initCrop = false;
            delete $scope.content.picFile;
            delete $scope.crop.resultBlob;
            delete $scope.crop.result;
            angular.element("input[type='file']").val(null);
        };

        $scope.removeEnclosure = function() {
            //console.log("Remove Enclosure", $scope.$parent.publisher.context.organization.id, $scope.$parent.item.enclosure, $scope.$parent.item.id);
            FileManager.delete({entityId: $scope.$parent.publisher.context.organization.id, isPublic: true, fileUri: Base64.encode($scope.$parent.item.enclosure)},
                function () {
                    if ($scope.$parent.item.id) {
                        Item.patch({
                            objectId: $scope.$parent.item.id,
                            attribute: "enclosure",
                            value: null
                        }, function () {
                            $scope.clearUpload();
                            $scope.$parent.item.enclosure = null;
                            $('#deleteEnclosureConfirmation').modal('hide');
                        });
                    } else {
                        $scope.clearUpload();
                        $scope.$parent.item.enclosure = null;
                        $('#deleteEnclosureConfirmation').modal('hide');
                    }
                });
        };

        $scope.validatePicUrl = function (picUrl) {
            //console.log("Set Enclosure :", picUrl);
            $scope.$parent.item.enclosure = picUrl;
            $('#cropImageModale').modal('hide');
        };

        $scope.clearUpload = function() {
            $scope.croppedDataUrl = null;
            $scope.content.picFile = null;
            $scope.content.picUrl = null;
            $scope.content.file = null;
            $scope.result = null;
            $scope.errorMsg = null;
            $scope.progress = null;
            $scope.enclosureDirty = true;
        };

        $scope.mediaType = '';

        $scope.resizeCondition = function(file, width, height, maxwidth, maxheight) {
            $scope.content.picFile = file;
            $scope.mediaType = file.type.substring(0, 5);
            //console.log("Media Type :", $scope.mediaType);
            if ($scope.mediaType === 'image' && (width > maxwidth || height > maxheight)) {
                return true;
            }
            return false;
        };

        $scope.changeFile = function (file, newFiles, invalidFiles, event) {
            //console.log("file ", file, newFiles, invalidFiles, event);
            if (file) {
                $scope.mediaType = file.type.substring(0, 5);
                //console.log("Media Type :", $scope.mediaType);
            } else {
                $scope.mediaType = '';
            }
        };
        $scope.changeCropFile = function (file, newFiles, invalidFiles, event) {
            //console.log("file ", file, newFiles, invalidFiles, event);
            var files = event.target.files;

            var fileReader = new FileReader();
            fileReader.readAsDataURL(files[0]);

            fileReader.onload = function(e) {
                $scope.content.picFile = this.result;
                $scope.$apply();
            };
        };

        $scope.getItemTypeLabel = function (name) {
            return $scope.itemStatusList.filter(function (val) {
                return val.name === name;
            })[0].label;
        };

    });
