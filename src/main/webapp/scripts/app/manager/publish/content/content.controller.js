'use strict';
angular.module('publisherApp')
    .controller('ContentWriteController', function ($scope, $state, EnumDatas, $rootScope, loadedItem, Item, Upload, Configuration, $timeout, FileManager, Base64, DateService, $translate, taTranslations) {
        $scope.$parent.item = $scope.$parent.item || {};
        $scope.$parent.itemValidated = $scope.$parent.itemValidated || false;
        //$scope.content = {type : '', picFile: null, picUrl: null, file:null};
        $scope.content = {type : '', file: null, resultImage: null};
        $scope.itemTypeList = $scope.$parent.publisher.context.reader.authorizedTypes;
        $scope.enclosureDirty = false;

        // I18n TextAngular Adds that can't be applied during app.config
        angular.forEach(taTranslations, function(key, value){
            translateSubKeys(key, value, 'textangular.');
        });
        function translateSubKeys(key, value, parentPartKey) {
            angular.forEach(Object.keys(key), function(subkey, subvalue) {
                if (typeof key[subkey] === 'object' && key[subkey] !== null) {
                    translateSubKeys(key[subkey], subvalue, parentPartKey + value + '.' + subkey + ".");
                } else {
                    var subpath = '';
                    if (typeof value !== 'number') {
                        subpath = value + ".";
                    }
                    //console.log("translate :", parentPartKey + subpath + subkey, key[subkey]);
                    $translate(parentPartKey + subpath + subkey).then(function (translatedValue) {
                        key[subkey] = translatedValue;
                        //console.log("translated :", JSON.stringify(taTranslations) );
                    });
                }
            });
        }

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
                && angular.isDefined($scope.$parent.item) && DateService.getDateDifference($scope.today, $scope.$parent.item.endDate) > 0
                && DateService.isValidDateRange($scope.$parent.item.startDate, $scope.$parent.item.endDate)) {

                var booleanComplement = true;
                // for MEDIA the body is optional
                if ($scope.$parent.item.type != "MEDIA" && $scope.$parent.item.type != "RESOURCE" && ($scope.$parent.item.body == null || $scope.$parent.item.body.length <= 15)) {
                    booleanComplement = false;
                }
                // for flash and media enclosure is mandatory
                if (($scope.$parent.item.type == "FLASH" || $scope.$parent.item.type == "MEDIA") && $scope.$parent.item.enclosure == null ) {
                    booleanComplement = false;
                }

                $scope.$parent.itemValidated = booleanComplement;
            } else {
                $scope.$parent.itemValidated = false;
            }

            //console.log("Form : ", $scope.publishContentForm);
            //console.log("item : ", $scope.$parent.item);
        }, true);

        $scope.isItemValidated = function() {
            return $scope.$parent.itemValidated;
        };

        // default conf for cropper
        $scope.cropperConf = {
            width: 240,
            height: 240
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
                    $scope.cropperConf = {
                        width: 240,
                        height: 240
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
                    $scope.cropperConf = {
                        width: 800,
                        height: 240
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
        $scope.uploadFile = function (file, dataUrl) {
            if (!file) return;
            // we upload cropped file with extension jpg, it's lighter than png
            var upload = Upload.upload({
                url: 'app/upload/',
                data: {
                    file: (typeof dataUrl !== 'undefined') ? Upload.dataUrltoBlob(dataUrl, file.name.substr(0, file.name.lastIndexOf(".")) + ".jpg") : file,
                    isPublic: true,
                    entityId: $scope.$parent.publisher.context.organization.id
                }
            });
            upload.then(function (response) {
                $timeout(function () {
                    $scope.result = response.headers("Location");
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
                console.log("error upload");
                if (response.status > 0) {
                    $translate('error.fileupload', {code: response.status}).then(function (translatedValue) {
                        $scope.errorMsg = translatedValue;
                        // in case that the error is managed in backend we could want to show the message, else hide tomcat error
                        // TODO review this part
                        if (!response.data.indexOf("<html>")) {
                            $scope.errorMsg = $scope.errorMsg + " " + $filter('translate')(response.data);
                        }
                        $scope.progress=null;
                    });
                } else if (response.status == 0){
                    $translate('error.ERR_INTERNET_DISCONNECTED').then(function (translatedValue) {
                        $scope.errorMsg = translatedValue;
                        $scope.progress=null;
                    });
                }
            }, function (evt) {
                $scope.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
            });
        };

        //$scope.clearUpload = function() {
        //    $scope.content.file = null;
        //    $scope.errorMsg = null;
        //    $scope.progress = null;
        //    $scope.enclosureDirty = true;
        //};

        //$scope.fileCropChanged = function(e) {
        //    if (e.target) {
        //        var file = e.target.files[0];
        //        var fileReader = new FileReader();
        //        fileReader.onload = function (e) {
        //            $scope.$apply(function () {
        //                $scope.content.file = fileReader.result;
        //            });
        //        };
        //        fileReader.readAsDataURL(file);
        //    } else {
        //        clearCrop();
        //    }
        //};

        $scope.clearUpload = function() {
            console.log("clear crop");
            delete $scope.content.file;
            delete $scope.content.resultImage;
            delete $scope.content.picFile;
            delete $scope.content.picUrl;
            $scope.errorMsg = null;
            $scope.progress = null;
            $scope.enclosureDirty = true;
            angular.element("input[type='file']").val(null);
        };

        $scope.removeEnclosure = function() {
            if (!angular.isDefined($scope.$parent.item.enclosure) || $scope.$parent.item.enclosure == null) return;
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

        $scope.mediaType = '';

        $scope.resizeCondition = function(file, width, height, maxwidth, maxheight) {
            //$scope.content.picFile = file;
            $scope.mediaType = file.type.substring(0, 5);
            //console.log("Media Type :", $scope.mediaType);
            if ($scope.mediaType === 'image' && (width > maxwidth || height > maxheight)) {
                return true;
            }
            return false;
        };

        $scope.changeFileType = function (file, newFiles, invalidFiles, event) {
            //console.log("file ", file, newFiles, invalidFiles, event);
            if (file) {
                $scope.mediaType = file.type.substring(0, 5);
                //console.log("Media Type :", $scope.mediaType);
            } else {
                $scope.mediaType = '';
            }
        };

        $scope.taDropHandler =  function(file, insertAction){
            if (file.type.substring(0, 5) === 'image' ){
                var sizeMax = Configuration.getConfUploadImageSize();
                if (file.size <= sizeMax){
                    Upload.upload({
                        url: 'app/upload/',
                        data: {
                            file : file,
                            isPublic: true,
                            entityId: $scope.$parent.publisher.context.organization.id
                        }
                    }).then(function (response) {
                        // SUCCESS
                        $scope.errorMsgTa = null;
                        var resultUrl = response.headers("Location");
                        insertAction('insertImage', resultUrl, true);
                    }, function (response) {
                        // ERROR
                        if (response.status > 0){
                            $scope.errorMsgTa = "taDropHandler.error.server";
                            $scope.errorMsgTaExplain = response.statusText;
                        }
                    }, function (evt) {
                        $scope.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                    });
                    return true;
                } else {
                    // FILE SIZE ISSUE
                    $scope.errorMsgTa = "taDropHandler.error.filesize";
                    $scope.errorMsgTaExplain = sizeMax;
                    return true;
                }
            }
            // FILE TYPE ISSUE
            $scope.errorMsgTa = "taDropHandler.error.filetype";
            return true;
        };
        $scope.cleanErrorMsgTa = function() {
            $scope.errorMsgTa = null;
            $scope.errorMsgTaExplain = null;
        };

        $scope.getItemTypeLabel = function (name) {
            return $scope.itemStatusList.filter(function (val) {
                return val.name === name;
            })[0].label;
        };

    });
