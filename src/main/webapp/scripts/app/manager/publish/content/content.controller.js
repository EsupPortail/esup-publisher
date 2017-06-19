'use strict';
angular.module('publisherApp')
    .controller('ContentWriteController', function ($scope, $state, EnumDatas, $rootScope, loadedItem, loadedFilesInContent,
                                                    Item, Upload, Configuration, $timeout, FileManager, Base64,
                                                    DateService, $translate, taTranslations, DateUtils, $filter, toaster) {
        $scope.$parent.item = $scope.$parent.item || {};
        $scope.$parent.itemValidated = $scope.$parent.itemValidated || false;
        //$scope.content = {type : '', picFile: null, picUrl: null, file:null};
        $scope.content = {type : '', file: '', resultImage: '', picUrl: ''};
        $scope.itemTypeList = $scope.$parent.publisher.context.reader.authorizedTypes;
        $scope.enclosureDirty = false;
        $scope.$parent.linkedFilesToContent = $scope.$parent.linkedFilesToContent || [];
        $scope.$parent.highlight = $scope.$parent.highlight || false;

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

        $scope.dtformats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'dd.MM.yyyy', 'dd/MM/yyyy','shortDate', 'yyyy-MM-dd'];
        $scope.dtformat = $scope.dtformats[6];

        $scope.today = DateUtils.addDaysToLocalDate(new Date(), 0);
        //$scope.startdate = angular.copy($scope.today)
        // init default max and min date;
        $scope.minDate = DateUtils.addDaysToLocalDate($scope.today, 0);
        $scope.endMinDate = DateUtils.addDaysToLocalDate($scope.today, 0);
        $scope.defaultMaxDuration = $scope.$parent.publisher.context.redactor.nbDaysMaxDuration > 0 ? $scope.$parent.publisher.context.redactor.nbDaysMaxDuration : 365;
        $scope.maxDate = DateUtils.addDaysToLocalDate($scope.today, $scope.defaultMaxDuration);
        $scope.updateMaxDate = function(item) {
            if (angular.isDefined(item) && angular.isDefined(item.type) && angular.isDefined(item.startDate)) {
                $scope.maxDate = DateUtils.addDaysToLocalDate(item.startDate, $scope.defaultMaxDuration);
            }
        };
        // used when we edit an item to avoid to change startDate
        $scope.updateMinDate = function(item) {
            if (angular.isDefined(item) && angular.isDefined(item.type) && angular.isDefined(item.startDate)) {
                $scope.minDate = item.startDate;
                $scope.endMinDate = item.startDate;
                //console.log("date : ", $filter('date')($scope.maxDate, 'yyyy-MM-dd'));
                if (DateService.getDateDifference($scope.today, $scope.minDate) <= 0) {
                    $scope.endMinDate = $scope.today;
                }
            }
        };

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
            $scope.updateMinDate(loadedItem);
            //console.log('loaded Item :' + JSON.stringify(loadedItem));
            $scope.$parent.item.highlight = $scope.$parent.highlight;
        } else if ($scope.$parent.item) {
            $scope.updateMinDate($scope.$parent.item);
        }
        //if (loadedItem && loadedItem.startDate != '') {
        //    $scope.startdate = angular.copy(loadedItem.startDate);
        //    //console.log('loaded Item start date:' + JSON.stringify(loadedItem.startDate));
        //}
        if (angular.equals([],$scope.$parent.linkedFilesToContent) && loadedFilesInContent) {
            $scope.$parent.linkedFilesToContent = angular.copy(loadedFilesInContent);

        }
        //console.log('after loaded parent loadedFilesInContent :' , $scope.$parent.linkedFiles);


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
            {name: 'ATTACHMENT', url: 'scripts/app/manager/publish/content/attachment.html'},
            {name: 'EVENT', url: 'scripts/app/manager/publish/content/resource.html'}];

        $scope.itemStatusList = EnumDatas.getItemStatusList();

        $scope.$watch('$parent.item', function(newItem, oldItem) {
            // checking validity independently of validators
            testItemValidity();
            // Change min and max Date depending on publishing context/type
            if (angular.isDefined(newItem) && angular.isDefined(newItem.type) && (!angular.isDefined(oldItem) || oldItem == null)
                || newItem.type != oldItem.type || newItem.startDate != oldItem.startDate) {
                $scope.updateMaxDate(newItem);
            }
            $scope.initCropper();
        }, true);

        $scope.$watch('publishContentForm.$valid + $parent.linkedFilesToContent', function(newObj, oldObj){
            // checking validity after validators checks
            testItemValidity();
        }, true);

        function testItemValidity(){
            if (angular.isDefined($scope.publishContentForm) && $scope.publishContentForm.$valid
                && angular.isDefined($scope.$parent.item)
                && ($scope.$parent.publisher.context.redactor.optionalPublishTime && $scope.$parent.item.endDate == null
                    || (DateService.getDateDifference($scope.today, $scope.$parent.item.endDate) > 0
                    && DateService.isValidDateRange($scope.$parent.item.startDate, $scope.$parent.item.endDate)))) {

                var booleanComplement = true;
                // for MEDIA the body is optional
                if ($scope.$parent.item.type != "MEDIA" && $scope.$parent.item.type != "RESOURCE" && $scope.$parent.item.type != "ATTACHMENT" && ($scope.$parent.item.body == null || $scope.$parent.item.body.length <= 15)) {
                    booleanComplement = false;
                }
                // for attachment
                if ($scope.$parent.item.type == "ATTACHMENT" && $scope.$parent.linkedFilesToContent.length < 1) {
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
            //console.log("test validity returned ", $scope.$parent.itemValidated, $scope.$parent.linkedFilesToContent);
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

        $scope.imageSizeMax = Configuration.getConfUploadImageSize();
        $scope.fileSizeMax = Configuration.getConfUploadFileSize();

        $scope.initItem = function () {
            //console.log("init Item with type " + JSON.stringify($scope.type));
            var entityID = $scope.$parent.publisher.context.organization.id;
            var redactorID = $scope.$parent.publisher.context.redactor.id;

            // tomorrow isn't more tomorrow as param should be 1 instead of 0
            var tomorrow = DateUtils.addDaysToLocalDate($scope.today, 0);
            // warning should be < $scope.defaultMaxDuration
            var next4weeks = DateUtils.addDaysToLocalDate($scope.today, 28);

            $scope.$parent.item = {};

            $scope.initCropper();

            // generic item
            $scope.$parent.item = {
                title: null,
                enclosure: null,
                endDate: $scope.$parent.publisher.context.redactor.optionalPublishTime ? null : next4weeks,
                startDate: tomorrow,
                validatedBy: null,
                validatedDate: null,
                status: null,
                summary: null,
                rssAllowed: false,
                highlight: $scope.$parent.highlight,
                createdBy: null,
                createdDate: null,
                lastModifiedBy: null,
                lastModifiedDate: null,
                id: null,
                organization: {id: entityID},
                redactor: {id: redactorID}
            };

            switch ($scope.content.type) {
                case 'NEWS':
                    $scope.$parent.item.type = "NEWS";
                    $scope.$parent.item.body = null;
                    break;
                case 'MEDIA':
                    $scope.$parent.item.type = "MEDIA";
                    break;
                case 'RESOURCE':
                    $scope.$parent.item.type = "RESOURCE";
                    $scope.$parent.item.ressourceUrl = null;
                    break;
                case 'FLASH':
                    $scope.$parent.item.type = "FLASH";
                    $scope.$parent.item.body = null;
                    $scope.$parent.item.endDate = $scope.$parent.publisher.context.redactor.optionalPublishTime ? null : DateUtils.addDaysToLocalDate($scope.today, 14);
                    break;
                case 'ATTACHMENT':
                    $scope.$parent.item.type = "ATTACHMENT";
                    break;
                default: throw "Type not managed :" + $scope.content.type; break;
            }
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
            return uploadLinkedFile(dataFile, dataFile.name, true, true, function(response) {
                $timeout(function () {
                    $scope.result = response.headers("Location");
                    if ($scope.$parent.item.id) {
                        Item.patch({
                            objectId: $scope.$parent.item.id,
                            attribute: "enclosure",
                            value: $scope.result
                        }, function () {
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
            });
        };

        $scope.uploadAttachment = function(inputfile) {
            if (!inputfile) return;
            var file = inputfile;

            var doUpload = function(theFile, theFileName, theType){
                return uploadLinkedFile(theFile, theFileName, theType, false, function (response) {
                    // SUCCESS
                    var resultUrl = response.headers("Location");
                    $scope.$parent.linkedFilesToContent.push({ uri: decodeURIComponent(resultUrl), filename: theFileName, inBody: false, contentType: theFile.type });
                    $timeout(function() {
                        $scope.progress = null;
                    }, 5000);
                });
            };

            //console.log("File type :", file.type, file.name, file.size);
            var isImage = file.type.substring(0, 5) === 'image';
            // In case of image we convert it into jpeg
            if (isImage) {
                return Upload.imageDimensions(file).then(function(dimensions){
                     Upload.resize(inputfile, dimensions.width, dimensions.height, 0.9, 'image/jpeg')
                         .then(function(resizedFile){
                             var filename = file.name.substr(0, file.name.lastIndexOf(".")) + ".jpg";
                             return doUpload(resizedFile, filename, false); // isImage to false to avoid maxSize on Image as it's an attachment.
                         });
                });
            }

            return doUpload(file, file.name, false);
        };

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

        $scope.deleteAttachment = function (attachment) {
            $scope.attachmentToDel = attachment;
            $('#deleteAttachmentConfirmation').modal('show');
        };

        $scope.confirmDeleteAttachment = function (attachment) {
            FileManager.delete({entityId: $scope.$parent.publisher.context.organization.id, isPublic: false, fileUri: Base64.encode(attachment.uri)},
                function () {
                    $scope.$parent.linkedFilesToContent = $scope.$parent.linkedFilesToContent.filter(function (val) {
                        return val.uri !== attachment.uri;
                    });
                    if ($scope.$parent.item.id) {
                        Item.patch({
                            objectId: $scope.$parent.item.id,
                            attribute: "attachment",
                            value: attachment.uri
                        }, function () {
                            $('#deleteAttachmentConfirmation').modal('hide');
                        });
                    } else {
                        $('#deleteAttachmentConfirmation').modal('hide');
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

        $scope.taDropHandler =  function(inputfile, insertAction){
            var file = inputfile;
            var filetype = file.type.substring(0, 5);
            //var isImage = filetype === 'image';
            //if (isImage) {
            //    console.log("IsImage");
            //    //upload.resize = function (file, width, height, quality, type, ratio, centerCrop, resizeIf, restoreExif)
            //    Upload.resize(inputfile, 1000).then(function (result) {
            //        file = result;
            //        console.log("Image :", file.size);
            //        return taUploadHandler(file, insertAction, filetype);
            //    });
            //
            //}
            return taUploadHandler(file, insertAction, filetype);
        };
        function taUploadHandler(file, insertAction, filetype) {
            // to show an icon depending on filetype
            var cssClassType = $rootscope.getCssFileFromType(file.type, file.name);

            return uploadLinkedFile(file, file.name, filetype === 'image', (filetype === 'image' || filetype === 'audio' || filetype === "video"),  function (response) {
                    // SUCCESS
                    var resultUrl = response.headers("Location");
                    if (isImage) {
                        insertAction('insertImage', resultUrl, true);
                    } else {
                        insertAction('createLink', [resultUrl, file.name, cssClassType], true);
                    }
                    $scope.$parent.linkedFilesToContent.push({ uri: decodeURIComponent(resultUrl), filename: file.name, inBody: true, contentType: file.type });
                    $timeout(function() {
                        $scope.progress = null;
                    }, 5000);
                });
        }

        function uploadLinkedFile(file, filename, isImage, isPublic, successCallBack) {
            var maxSize = isImage ? $scope.imageSizeMax : $scope.fileSizeMax;
            if (file.size <= maxSize){
                $scope.progressStatus = 'success';
                Upload.upload({
                    url: 'app/upload/',
                    data: {
                        file : file,
                        isPublic: isPublic,
                        entityId: $scope.$parent.publisher.context.organization.id,
                        name: filename
                    }
                }).then(function (response) {
                    successCallBack(response);
                }, function (response) {
                    // ERROR
                    manageUploadError(response);
                }, function (evt) {
                    $scope.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
                });
                return true;
            }
            // FILE SIZE ISSUE - useless as the server control it, but avoid a useless request
            $translate('errors.upload.filesize', {size: $filter('byteFmt')(maxSize,2)}).then(function (translatedValue) {
                toaster.pop({type: "warning", title: translatedValue});
            });
            return true;
        }

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
