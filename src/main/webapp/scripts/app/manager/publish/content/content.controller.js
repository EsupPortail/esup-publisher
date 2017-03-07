'use strict';
angular.module('publisherApp')
    .controller('ContentWriteController', function ($scope, $state, EnumDatas, $rootScope, loadedItem, Item, Upload, Configuration, $timeout, FileManager, Base64,
                                                    DateService, $translate, taTranslations, DateUtils, $filter, toaster) {
        $scope.$parent.item = $scope.$parent.item || {};
        $scope.$parent.itemValidated = $scope.$parent.itemValidated || false;
        //$scope.content = {type : '', picFile: null, picUrl: null, file:null};
        $scope.content = {type : '', file: '', resultImage: '', picUrl: ''};
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

        $scope.today = DateUtils.addDaysToLocalDate(new Date(), 0);
        //$scope.startdate = angular.copy($scope.today);
        // init default max and min date;
        $scope.minDate = DateUtils.addDaysToLocalDate($scope.today, 0);
        $scope.maxDate = DateUtils.addDaysToLocalDate($scope.today, 366);


        $scope.dtformats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'dd/MM/yyyy','shortDate'];
        $scope.dtformat = $scope.dtformats[3];

        $scope.changeContentType = function(oldValue) {
            //console.log("changeContentType ", oldValue, $scope.content.type);
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

        //if (loadedItem && loadedItem.startDate != '') {
        //    $scope.startdate = angular.copy(loadedItem.startDate);
        //    //console.log('loaded Item start date:' + JSON.stringify(loadedItem.startDate));
        //}

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
            // checking validity independently of validators
            testItemValidity();
            // Change min and max Date depending on publishing context/type
            if (angular.isDefined(newType) && angular.isDefined(newType.type) && (!angular.isDefined(oldType) || oldType == null)
                || newType.type != oldType.type || newType.startDate != oldType.startDate) {
                switch (newType.type) {
                    case 'NEWS':
                        $scope.maxDate = DateUtils.addDaysToLocalDate(newType.startDate, 168);
                        //console.log("date : ", $filter('date')($scope.maxDate, 'yyyy-MM-dd'));
                        break;
                    case 'FLASH':
                        //$scope.minDate = angular.copy($scope.today);
                        $scope.maxDate = DateUtils.addDaysToLocalDate(newType.startDate, 90);
                        //console.log("date : ", $filter('date')($scope.maxDate, 'yyyy-MM-dd'));
                        break;
                    default: throw "Type not managed :"+ newType.type; break;
                }
            }
            $scope.initCropper();
        }, true);

        $scope.$watch('publishContentForm.$valid', function(newType, oldType){
            // checking validity after validators checks
            testItemValidity();
        }, true);

        function testItemValidity(){
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
        }

        $scope.isItemValidated = function() {
            return $scope.$parent.itemValidated;
        };

        $scope.defaultCropperConf = {
            size:[{w:240, h:240}],
            quality: 0.9,
            format: 'image/jpeg',
            type: 'rectangle',
            changeOnFly: false,
            allowCropResizeOnCorners: false
        };

        $scope.cropperConf = angular.copy($scope.defaultCropperConf);

        $scope.initCropper = function(){

            switch ($scope.content.type) {
                case 'NEWS':
                    $scope.cropperConf.size = [{w:240, h:240}];
                    $scope.cropperConf.type = "square";
                    break;
                case 'FLASH':
                    $scope.cropperConf.size = [{w:800, h:240}];
                    $scope.cropperConf.quality = 0.8;
                    $scope.cropperConf.type = "rectangle";
                    $scope.cropperConf.ratio = 3.3;
                    break;
                default :
                    $scope.cropperConf = angular.copy($scope.defaultCropperConf);break;
            }
            //console.log("call init cropper ", $scope.content.type, $scope.cropperConf);
        };

        // default conf for cropper

        $scope.sizeMax = Configuration.getConfUploadImageSize();

        $scope.initItem = function () {
            //console.log("init Item with type " + JSON.stringify($scope.type));
            var entityID = $scope.$parent.publisher.context.organization.id;
            var redactorID = $scope.$parent.publisher.context.redactor.id;

            // tomorrow isn't more tomorrow as param should be 1 instead of 0
            var tomorrow = DateUtils.addDaysToLocalDate($scope.today, 0);
            var next4weeks = DateUtils.addDaysToLocalDate($scope.today, 28);

            $scope.$parent.item = {};

            $scope.initCropper();

            switch ($scope.content.type) {
                case 'NEWS':
                    $scope.$parent.item = {
                        type: "NEWS",
                        title: null,
                        enclosure: null,
                        endDate: next4weeks,
                        startDate: tomorrow,
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
                        endDate: next4weeks,
                        startDate: tomorrow,
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
                        endDate: next4weeks,
                        startDate: tomorrow,
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
                        endDate: DateUtils.addDaysToLocalDate($scope.today, 14),
                        startDate: tomorrow,
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
                default: throw "Type not managed :" + $scope.content.type; break;
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
            $scope.progressStatus = 'success';
            var dataFile = (typeof dataUrl !== 'undefined') ? Upload.dataUrltoBlob(dataUrl, file.name.substr(0, file.name.lastIndexOf(".")) + ".jpg") : file;
            //console.log("try to download ",dataFile);
            // we upload cropped file with extension jpg, it's lighter than png
            if (dataFile.size <= $scope.sizeMax){
                var upload = Upload.upload({
                    url: 'app/upload/',
                    data: {
                        file: dataFile,
                        isPublic: true,
                        entityId: $scope.$parent.publisher.context.organization.id
                    }
                });
                upload.then(function (response) {
                    //SUCCESS
                    $timeout(function () {
                        $scope.result = response.headers("Location");
                        if ($scope.$parent.item.id) {
                            Item.patch({objectId:$scope.$parent.item.id, attribute : "enclosure", value: $scope.result}, function() {
                                $scope.$parent.item.enclosure = $scope.result;
                                $('#cropImageModale').modal('hide');
                                $scope.progress = null;
                                //$scope.publishContentForm.enclosure.$setValidity('url', true);
                            });
                        } else {
                            $scope.$parent.item.enclosure = $scope.result;
                            $('#cropImageModale').modal('hide');
                            $scope.progress = null;
                        }
                    });
                }, function (response) {
                    //ERROR
                    //console.log("error upload");
                    manageUploadError(response);
                }, function (evt) {
                    $scope.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                });
            } else {
                // FILE SIZE ISSUE - useless as the server control it, but avoid a useless request
                $translate('errors.upload.filesize', {size: $filter('byteFmt')($scope.sizeMax,2)}).then(function (translatedValue) {
                    toaster.pop({type: "warning", title: translatedValue});
                });
                return true;
            }
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
            //console.log("clear crop");
            $scope.content.file = '';
            $scope.content.resultImage = '';
            //$scope.content.picFile= '';
            $scope.content.picUrl = '';
            $scope.errorMsg = null;
            $scope.progress = null;
            $scope.enclosureDirty = true;
            angular.element("input[type='file']").val(null);
        };

        $scope.removeEnclosure = function() {
            if (!angular.isDefined($scope.$parent.item.enclosure) || $scope.$parent.item.enclosure == null) return;
            deleteEnclosure();
        };
        $scope.cancel = function() {
            // on cancel we try to remove enclosure only on new item (without id)
            if (!angular.isDefined($scope.$parent.item.enclosure) || $scope.$parent.item.enclosure == null || !angular.isDefined($scope.$parent.item.id)) return;
            deleteEnclosure();
        };

        function deleteEnclosure() {
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
        }

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
            return $scope.mediaType === 'image' && (width > maxwidth || height > maxheight);
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
                if (file.size <= $scope.sizeMax){
                    $scope.progressStatus = 'success';
                    Upload.upload({
                        url: 'app/upload/',
                        data: {
                            file : file,
                            isPublic: true,
                            entityId: $scope.$parent.publisher.context.organization.id
                        }
                    }).then(function (response) {
                        // SUCCESS
                        var resultUrl = response.headers("Location");
                        insertAction('insertImage', resultUrl, true);
                        $timeout(function() {
                            $scope.progress = null;
                        }, 5000);
                    }, function (response) {
                        // ERROR
                        manageUploadError(response);
                    }, function (evt) {
                        $scope.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                    });
                    return true;
                } else {
                    // FILE SIZE ISSUE - useless as the server control it, but avoid a useless request
                    $translate('errors.upload.filesize', {size: $filter('byteFmt')($scope.sizeMax,2)}).then(function (translatedValue) {
                        toaster.pop({type: "warning", title: translatedValue});
                    });
                    return true;
                }
            }
            // FILE TYPE ISSUE
            $translate('taDropHandler.error.filetype').then(function (translatedValue) {
                toaster.pop({type: "warning", title: translatedValue});
            });
            return true;
        };

        function manageUploadError(response) {
            if (response.status > 0) {
                $scope.progressStatus = 'warning';
                //console.log("Response : ", response.data);
                if (response.data && response.data.message) {
                    var params = {};
                    if (response.data.params)  {
                        params = angular.copy(response.data.params);
                        if (response.data.params.size) {
                            params.size = $filter('byteFmt')(response.data.params.size, 2);
                        }
                    }
                    params.code = response.status;
                    $translate(response.data.message, params).then(function (translatedValue) {
                        toaster.pop({type: "warning", title: translatedValue});
                    });
                } else {
                    $translate('errors.upload.generic', {code: response.status}).then(function (translatedValue) {
                        toaster.pop({type: "warning", title: translatedValue});
                    });
                }
            } else if (response.status == 0){
                $scope.progressStatus = 'warning';
                $translate('errors.upload.ERR_INTERNET_DISCONNECTED').then(function (translatedValue) {
                    toaster.pop({type: "warning", title: translatedValue});
                });
            }
            $timeout(function() {
                $scope.progress = null;
            }, 3000);
        }

        $scope.getItemTypeLabel = function (name) {
            return $scope.itemStatusList.filter(function (val) {
                return val.name === name;
            })[0].label;
        };

    });
