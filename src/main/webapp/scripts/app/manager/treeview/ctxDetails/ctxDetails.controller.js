'use strict';

angular.module('publisherApp')
    .controller('DetailsTreeViewController', function ($scope, $state, $stateParams, $filter, EnumDatas, Organization, Publisher, Classification,
                                                       Category, Item, ContentDTO, Permission, PermissionOnContext, Subscriber, User, Principal) {

        $scope.context = {};
        $scope.ctxType = $stateParams.ctxType;
        $scope.permissions = {};
        $scope.targets = {};
        $scope.publisher = {};
        $scope.subscriber = {};
        $scope.selectedSubject = {};
        $scope.selectedSubjects = [];
        $scope.availableRoles = [];
        $scope.search = {subject: {}, target: {}};
        $scope.edit = {authorizedSubject: {}};
        $scope.permissionAdvanced = false;//Principal.isInRole("ROLE_ADMIN") || false;

        $scope.permissionTypeList = EnumDatas.getPermissionTypeList();
        $scope.permissionClassList = EnumDatas.getPermissionClassList();
        $scope.subscribeTypeList = [{"name":"FORCED","id":0,"label":"enum.subscribe.forced.title"}] || EnumDatas.getSubscribeTypeList();
        $scope.subjectTypeList = EnumDatas.getSubjectTypeList();
        $scope.displayOrderTypeList = EnumDatas.getDisplayOrderTypeList();
        $scope.stringEvaluationModeList = EnumDatas.getStringEvaluationModeList();
        $scope.operatorTypeList = EnumDatas.getOperatorTypeList();
        $scope.accessTypeList = EnumDatas.getAccessTypeList();
        $scope.langList = [{id: 'fr', label:'publisherApp.category.langList.fr'}, {id: 'en', label:'publisherApp.category.langList.en'}
            , {id: 'de', label:'publisherApp.category.langList.de'}, {id: 'es', label:'publisherApp.category.langList.es'}];
        $scope.userAttributes = User.funtionalAttributes();

        $scope.canManage = false;
        $scope.hasTargetManagment = false;

        $scope.contextTemplate = 'scripts/app/manager/treeview/ctxDetails/context/empty-detail.html';
        $scope.permissionTemplate = 'scripts/app/manager/treeview/ctxDetails/permissions/permissionOnCtx-detail.html';
        $scope.targetsTemplate = 'scripts/app/manager/treeview/ctxDetails/targets/targets-detail.html';

        $scope.activeNav = 'content';

        var jstree = $('.tree-browser > .jstree').jstree(true);

        $scope.load = function (ctxType, ctxId) {
            switch(ctxType) {
                case 'ORGANIZATION' :
                    Organization.get({id: ctxId}, function(result) {
                        $scope.context = result;
                        $scope.publisher = {};
                        $scope.canManage = getUserCanManage(result.contextKey);
                        $scope.hasTargetManagment = getHasTargetManagment(ctxType);
                    });
                    PermissionOnContext.queryForCtx({ctx_type: ctxType, ctx_id: ctxId}, function(result) {
                        $scope.permissions = result;
                        $scope.loadAvailableRoles();
                    });
                    Subscriber.queryForCtx({ctx_type: ctxType, ctx_id: ctxId}, function(result) {
                        console.log(result);
                        $scope.targets = result;
                    });
                    $scope.contextTemplate = 'scripts/app/manager/treeview/ctxDetails/context/organization-detail.html';
                    $scope.ctxPermissionType = "CONTEXT";
                    selectTemplatePermissionTemplate($scope.ctxPermissionType);
                    break;
                case 'PUBLISHER' :
                    Publisher.get({id: ctxId}, function(result) {
                        $scope.context = result;
                        $scope.publisher = result;
                        $scope.canManage = getUserCanManage(result.contextKey);
                        $scope.hasTargetManagment = getHasTargetManagment(ctxType);
                        $scope.ctxPermissionType = $scope.publisher.permissionType;
                        selectTemplatePermissionTemplate($scope.ctxPermissionType);
                    });
                    PermissionOnContext.queryForCtx({ctx_type: ctxType, ctx_id: ctxId}, function(result) {
                        $scope.permissions = result;
                        $scope.loadAvailableRoles();
                    });
                    Subscriber.queryForCtx({ctx_type: ctxType, ctx_id: ctxId}, function(result) {
                        $scope.targets = result;
                    });
                    $scope.contextTemplate = 'scripts/app/manager/treeview/ctxDetails/context/publisher-detail.html';
                    break;
                case 'CATEGORY' :
                    Classification.get({id: ctxId}, function(result) {
                        $scope.context = result;
                        $scope.publisher = result.publisher;
                        $scope.canManage = getUserCanManage(result.contextKey);
                        $scope.hasTargetManagment = getHasTargetManagment(ctxType);
                        $scope.ctxPermissionType = $scope.publisher.permissionType;
                        selectTemplatePermissionTemplate($scope.ctxPermissionType);
                    });
                    Permission.queryForCtx({ctx_type: ctxType, ctx_id: ctxId}, function(result) {
                        $scope.permissions = result;
                        $scope.loadAvailableRoles();
                    });
                    Subscriber.queryForCtx({ctx_type: ctxType, ctx_id: ctxId}, function(result) {
                        $scope.targets = result;
                    });
                    $scope.contextTemplate = 'scripts/app/manager/treeview/ctxDetails/context/category-detail.html';
                    break;
                case 'FEED' :
                    Classification.get({id: ctxId}, function(result) {
                        $scope.context = result;
                        $scope.publisher = result.publisher;
                        $scope.canManage = getUserCanManage(result.contextKey);
                        $scope.hasTargetManagment = getHasTargetManagment(ctxType);
                        switch ($scope.context.type) {
                            case 'EXTERNALFEED' :
                                $scope.contextTemplate = 'scripts/app/manager/treeview/ctxDetails/context/external-detail.html';
                                break;
                            case 'INTERNALFEED' :
                                $scope.contextTemplate = 'scripts/app/manager/treeview/ctxDetails/context/internal-detail.html';
                                break;
                        }
                        $scope.ctxPermissionType = $scope.publisher.permissionType;
                        selectTemplatePermissionTemplate($scope.ctxPermissionType);
                    });
                    Permission.queryForCtx({ctx_type: ctxType, ctx_id: ctxId}, function(result) {
                        $scope.permissions = result;
                        $scope.loadAvailableRoles();
                    });
                    Subscriber.queryForCtx({ctx_type: ctxType, ctx_id: ctxId}, function(result) {
                        $scope.targets = result;
                    });
                    break;
                case 'ITEM' :
                    Item.get({id: ctxId}, function(result) {
                        $scope.context = result;
                        $scope.canManage = getUserCanManage(result.contextKey);
                        $scope.hasTargetManagment = getHasTargetManagment(ctxType);
                        switch ($scope.context.type) {
                            case 'NEWS':
                                $scope.contextTemplate = 'scripts/app/manager/treeview/ctxDetails/context/news-detail.html';
                                break;
                            case 'MEDIA':
                                $scope.contextTemplate = 'scripts/app/manager/treeview/ctxDetails/context/media-detail.html';
                                break;
                            case 'RESOURCE':
                                $scope.contextTemplate = 'scripts/app/manager/treeview/ctxDetails/context/resource-detail.html';
                                break;
                        }
                    });
                    $scope.permissions = {};
                    Subscriber.queryForCtx({ctx_type: ctxType, ctx_id: ctxId}, function(result) {
                        $scope.targets = result;
                    });

                    break;
                default :
                    $scope.contextTemplate = 'scripts/app/manager/treeview/ctxDetails/context/empty-detail.html';
                    break;

            }


        };
        $scope.load($stateParams.ctxType, $stateParams.ctxId);

        $scope.showNav = function(div){
            $scope.activeNav = div;
        };

        $scope.confirmDeleteContext = function () {
            var manager;
            var parentNode = jstree.get_parent($scope.$parent.currentNode);
            var values = parentNode.split(":");
            var parentId = values[0], parentType = values[1];

            switch($scope.ctxType) {
                case 'ORGANIZATION' :
                    manager = Organization;
                    break;
                case 'PUBLISHER' :
                    manager = Publisher;
                    break;
                case 'CATEGORY' :
                    manager = Category;
                    break;
                case 'FEED' :
                    manager = Classification;
                    break;
                case 'ITEM' :
                    manager = ContentDTO;
                    break;
            }
            if (manager) {
                manager.delete({id: $scope.context.contextKey.keyId},
                    function () {
                        $('#deleteContextConfirmation').modal('hide');
                        $('body').removeClass('modal-open');
                        $('.modal-backdrop').remove();
                        jstree.delete_node($scope.$parent.currentNode);
                        jstree.select_node(parentNode);
                        $scope.clear();
                        if ($scope.ctxType == 'ORGANIZATION') {
                            $state.go("treeview");
                        } else {
                            $scope.load(parentType, parentId);
                        }
                    });
            }

        };

        $scope.createContext = function(type) {
            var div;
            switch(type) {
                case 'ORGANIZATION' :
                    $scope.editedContext = {};
                    div = "#saveOrganizationModal";
                    break;
                case 'PUBLISHER' :
                    $scope.editedContext = {};
                    div = "#savePublisherModal";
                    break;
                case 'CATEGORY' :
                    // in waiting all access are PUBLIC
                    $scope.editedContext = {type:'CATEGORY', publisher: $scope.context, rssAllowed: false, name: null, iconUrl: null, color: null,
                        lang: 'fr', ttl: 3600, displayOrder: 0, accessView: 'PUBLIC', description: null, defaultDisplayOrder: $scope.displayOrderTypeList[0].name,
                        createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null};
                    if ($scope.editCategoryForm) {
                        $scope.editCategoryForm.$setPristine();
                        $scope.editCategoryForm.$setUntouched();
                    }
                    div = "#saveCategoryModal";
                    break;
                case 'FEED' :
                    $scope.editedContext = {};
                    div = "#saveFeedModal";
                    break;
                default :
                    div = "#saveContextModal";
                    break;
            }
            console.log("createContext", type);
            $(div).modal('show');
        };

        $scope.updateContext = function() {
            var manager, div;
            switch($scope.ctxType) {
                case 'ORGANIZATION' :
                    manager = Organization;
                    div = "#saveOrganizationModal";
                    break;
                case 'PUBLISHER' :
                    manager = Publisher;
                    div = "#savePublisherModal";
                    break;
                case 'CATEGORY' :
                    manager = Category;
                    div = "#saveCategoryModal";
                    break;
                case 'FEED' :
                    manager = Classification;
                    div = "#saveFeedModal";
                    break;
                case 'ITEM' :
                    break;
                default :
                    div = "#saveContextModal";
                    break;
            }
            if (manager) {
                manager.get({id: $scope.context.id},
                    function (result) {
                        $scope.editedContext = result;
                        $(div).modal('show');
                    });
            }

        };

        $scope.confirmUpdateContext = function(type) {
            var manager, div;
            switch(type) {
                case 'ORGANIZATION' :
                    manager = Organization;
                    $scope.editedContext.displayName = $scope.editedContext.name;
                    div = "#saveOrganizationModal";
                    break;
                case 'PUBLISHER' :
                    manager = Publisher;
                    div = "#savePublisherModal";
                    break;
                case 'CATEGORY' :
                    manager = Category;
                    div = "#saveCategoryModal";
                    break;
                case 'FEED' :
                    manager = Classification;
                    div = "#saveFeedModal";
                    break;
                case 'ITEM' :
                    break;
                default :
                    div = "#saveContextModal";
                    break;
            }
            if (manager) {
                manager.update($scope.editedContext,
                    function () {
                        $(div).modal('hide');
                        /*$('body').removeClass('modal-open');
                        $('.modal-backdrop').remove();*/
                        //$('.tree-browser > .jstree').jstree('refresh');
                        //jstree.edit($scope.$parent.currentNode, $scope.editedContext.displayName, $scope.load($scope.ctxType, $scope.context.id));
                            /*$state.go("ctxDetails", {'ctxType' : 'ORGANIZATION','ctxId' :$scope.context.id}, {reload: true} )*/
                        if ($scope.editedContext.id) {
                            var parentNode = jstree.get_parent($scope.$parent.currentNode);
                        } else {
                            var parentNode = $scope.$parent.currentNode.id;
                        }
                        jstree.refresh(parentNode);
                        $scope.editedContext = {};
                        if ($scope.editForm) {
                            $scope.editForm.$setPristine();
                            $scope.editForm.$setUntouched();
                        }
                        if ($scope.editCategoryForm) {
                            $scope.editCategoryForm.$setPristine();
                            $scope.editCategoryForm.$setUntouched();
                        }
                        $scope.load($scope.ctxType, $scope.context.id);
                    });
            }

        };



        /* Subscribers */
        $scope.addSubscriber = function () {
            $scope.addSubs = true;
            $scope.selectedSubject = {};
            $scope.subscriber = {subscribeType: $scope.subscribeTypeList[0].name};
        };

        $scope.createSubscriber = function () {
            //console.log("call create subscriber");
            var subscriber = {subscribeType: $scope.subscriber.subscribeType, subjectCtxId: {subject: $scope.selectedSubject.modelId, context: $scope.context.contextKey}};
            Subscriber.save(subscriber, function() {
                    //console.log("createSubscriber POST is done");
                    $scope.clearSubscriber();
                    Subscriber.queryForCtx({ctx_type: $scope.ctxType, ctx_id: $scope.context.id}, function(result) {
                        //console.log("createSubscriber queryForCtx done");
                        //console.log(result);
                        $scope.targets = result;
                        $scope.load($scope.ctxType, $scope.context.id);
                    });
                });
        };

        $scope.deleteSubscriber = function (target) {
            Subscriber.get({subject_id: target.subjectDTO.modelId.keyId, subject_type: $scope.getSubjectTypeList(target.subjectDTO.modelId.keyType),
                ctx_id: target.contextKeyDTO.keyId,ctx_type: target.contextKeyDTO.keyType}, function(result) {
                $scope.subscriber = result;
                $('#deleteTargetConfirmation').modal('show');
            });
        };

        $scope.confirmDeleteSubscriber = function (target) {
            Subscriber.delete({subject_id: target.subjectCtxId.subject.keyId, subject_type: $scope.getSubjectTypeList(target.subjectCtxId.subject.keyType),
                    ctx_id: target.subjectCtxId.context.keyId,ctx_type: target.subjectCtxId.context.keyType},
                function () {
                    $scope.subscriber = {};
                    $('#deleteTargetConfirmation').modal('hide');
                    $scope.load($scope.ctxType, $scope.context.id);
                });
        };

        $scope.$watch("search.target", function(newVal, oldVal) {
            //console.log("watch search.target", newVal, oldVal);
            if (angular.isDefined(newVal) && !angular.equals({}, newVal) && !angular.equals(newVal, oldVal)) {
                $scope.selectedSubject = newVal;
            }
        });

        $scope.removeSelectedSubject = function() {
            $scope.selectedSubject = {};
        };

        $scope.clearSubscriber = function() {
            //console.log("clear Subscriber");
            $scope.subscriber = {subscribeType: $scope.subscribeTypeList[0].name};
            $scope.selectedSubject = {};
            $scope.addSubs = false;
        };

        $scope.isSubjectSelected = function () {
            //console.log("isSubjectSelected :", !angular.equals({},$scope.selectedSubject), $scope.selectedSubject);
            return !angular.equals({}, $scope.selectedSubject);
        };

        $scope.isInRole = function(role) {
            return Principal.isInRole(role);
        };

        $scope.clear = function () {
            $scope.context = {};
            $scope.ctxType = $stateParams.ctxType;
            $scope.permissions = {};
            $scope.targets = {};
            $scope.publisher = {};
            $scope.editedContext = {};
            $scope.canManage = false;
            $scope.hasTargetManagment = false;

            $scope.contextTemplate = 'scripts/app/manager/treeview/ctxDetails/context/empty-detail.html';
            $scope.permissionTemplate = 'scripts/app/manager/treeview/ctxDetails/permissions/permissionOnCtx-detail.html';

            $scope.activeNav = 'content';

            $scope.clearSubscriber();$scope.clearPermission();

        };

        /** Permissions */

        $scope.deletePermission = function (id) {
            Permission.get({id: id}, function(result) {
                $scope.permission = result;
                $('#deletePermissionConfirmation').modal('show');
            });
        };

        $scope.confirmDeletePermission = function (id) {
            Permission.delete({id: id},
                function () {
                    $scope.load($scope.ctxType, $scope.context.id);
                    $('#deletePermissionConfirmation').modal('hide');
                    $scope.permission={};

                });
        };

        $scope.clearPermission = function () {
            $scope.permission = {};
            /*$scope.deletePermissionForm.$setPristine();
            $scope.deletePermissionForm.$setUntouched();*/
            $scope.addPerm = false;
        };

        $scope.loadAvailableRoles = function(withRole) {
            $scope.availableRoles = [];
            // construct available role remaining to limit permissions of one role
            for (var i = 0; i < $scope.permissionTypeList.length; i++) {
                switch ($scope.ctxPermissionType) {
                    case 'CONTEXT_WITH_SUBJECTS' : // TODO what about setting several perm on same role if we could define different authorized subject on different evaluators ?
                    case 'CONTEXT':
                        var found = false;
                        for (var l = 0; l < $scope.permissions.length; l++) {
                            if ($scope.permissions[l].role == $scope.permissionTypeList[i].name) {
                                found = true;
                            }
                        }
                        if ((!found && 'ADMIN' != $scope.permissionTypeList[i].name)
                            || (angular.isDefined(withRole) && withRole == $scope.permissionTypeList[i].name)) {
                            $scope.availableRoles.push($scope.permissionTypeList[i])
                        }
                        break;
                    default :
                        if ('ADMIN' != $scope.permissionTypeList[i].name) {
                            $scope.availableRoles.push($scope.permissionTypeList[i])
                        }
                        break;
                }

            }
            console.log("loadAvailableRoles :",  $scope.availableRoles, $scope.permissionTypeList);
        };
        $scope.addPermission = function() {
            $scope.addPerm = true;
            $scope.loadAvailableRoles();
            $scope.selectedSubjects = [];
            console.log("addPermission :",  $scope.availableRoles, $scope.ctxPermissionType);
            switch ($scope.ctxPermissionType) {
                case 'CONTEXT':
                    $scope.permission = {type: 'PERMONCTX', context : {keyId: $scope.context.contextKey.keyId, keyType: $scope.context.contextKey.keyType},
                    role: $scope.availableRoles[0].name, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null,
                    evaluator: {class: 'OPERATOR', id: null, type: 'OR', evaluators: []}};
                    break;
                case 'CONTEXT_WITH_SUBJECTS' : $scope.permission = {type: 'PERMONCTXWSUBJS', context : {keyId: $scope.context.contextKey.keyId, keyType: $scope.context.contextKey.keyType},
                    role: $scope.availableRoles[0].name, createdBy: null, createdDate: null, lastModifiedBy: null, lastModifiedDate: null, id: null,
                    evaluator: {class: 'OPERATOR', id: null, type: 'OR', evaluators: []}, authorizedSubjects: []};
                    break;
                default :$scope.permission = {}; break;

                /*,
                 case: 'SUBJECT',
                 case: 'SUBJECT_WITH_CONTEXT',*/
            }
            console.log("addPermission :",  $scope.permission);

        };

        $scope.createPermission = function () {
            console.log("SavePermission :",  $scope.permission);
            if (!$scope.permissionAdvanced) {
                $scope.permission.evaluator.evaluators = [];
                for (var i = 0; i < $scope.selectedSubjects.length; i++) {
                    if ($scope.selectedSubjects[i].keyType == "PERSON") {
                        $scope.permission.evaluator.evaluators.push({class: 'USERATTRIBUTES',id: null, mode: "EQUALS", attribute: "uid", value: $scope.selectedSubjects[i].keyId});
                    } else if ($scope.selectedSubjects[i].keyType == "GROUP") {
                        $scope.permission.evaluator.evaluators.push({class: 'USERGROUP',id: null, group: $scope.selectedSubjects[i].keyId});
                    }
                }
            }
            switch ($scope.ctxPermissionType) {
                case 'CONTEXT': PermissionOnContext.
                    update($scope.permission,
                        function () {
                            $scope.load($scope.ctxType, $scope.context.id);
                            $scope.clearPermission();
                        });
                    break;
                case 'CONTEXT_WITH_SUBJECTS' :
                    Permission.
                        update($scope.permission,
                        function () {
                            $scope.load($scope.ctxType, $scope.context.id);
                            $scope.clearPermission();
                        });
                    break;
            }
        };

        $scope.updatePermission = function (id) {
            console.log("updatePermission");
            switch ($scope.ctxPermissionType) {
                case 'CONTEXT':
                    PermissionOnContext.get({id: id}, function (result) {
                        $scope.permission = result;
                        $scope.loadAvailableRoles(result.role);
                        console.log("available roles : ", $scope.availableRoles);
                        setSelectedSubjectsFromEvaluator($scope.permission.evaluator);
                        $scope.addPerm = true;
                    });
                    break;
                case 'CONTEXT_WITH_SUBJECTS' :
                    Permission.get({id: id}, function (result) {
                        $scope.permission = result;
                        $scope.loadAvailableRoles(result.role);
                        console.log("available roles : ", $scope.availableRoles);
                        setSelectedSubjectsFromEvaluator($scope.permission.evaluator);
                        // TODO what about setting several perm on same role if we could define different authorized subject on different evaluators ?
                        $scope.addPerm = true;
                    });
                    break;
                default : console.log("Case Not Managed !");
            }
        };
        function setSelectedSubjectsFromEvaluator(evaluator) {
            $scope.selectedSubjects = [];
            //check case if we are on simple mode
            if (!$scope.isAdvancedEvaluator(evaluator)) {
                for (var i = 0; i < evaluator.evaluators.length; i++) {
                    var subjectEval = evaluator.evaluators[i];
                    //console.log("evaluator :",subjectEval);
                    switch (subjectEval.class) {
                        case "USERATTRIBUTES":
                        case "USERMULTIVALUEDATTRIBUTES":
                            if (subjectEval.attribute == "uid" && subjectEval.mode == "EQUALS") {
                                $scope.selectedSubjects.push({keyType: 'PERSON', keyId: subjectEval.value});
                            }
                            break;
                        case "USERGROUP":
                            $scope.selectedSubjects.push({keyType: 'GROUP', keyId: subjectEval.group});
                            break;
                    }
                }
            }
        }
        $scope.$watch("permission.evaluator", function(newv) {
            //console.log("watch permission.evaluator", $scope.permission);
            if ($scope.addPerm) {
                setSelectedSubjectsFromEvaluator($scope.permission.evaluator);
            }
        }, true);
        $scope.$watch("edit.authorizedSubject", function(newVal, oldVal) {
            //console.log("watch edit.authorizedSubject ", newVal, oldVal);
            if (angular.isDefined(newVal) && !angular.equals({}, newVal) && !angular.equals(newVal, oldVal)) {
                var found = false;
                angular.forEach($scope.permission.authorizedSubjects, function (value) {
                    if(angular.equals(newVal.modelId, value)) {
                        found = true;
                    }
                });
                //console.log("authorized subject was found ? ",found);
                if (!found){
                    $scope.permission.authorizedSubjects.push(newVal.modelId);
                }
            }
        });
        $scope.$watch("search.subject", function(newVal, oldVal) {
            //console.log("watch search.subject", newVal, oldVal);
            if (angular.isDefined(newVal) && !angular.equals({}, newVal) && !angular.equals(newVal, oldVal)) {
                var found = false;
                angular.forEach($scope.selectedSubjects, function (value) {
                    if(angular.equals(newVal.modelId, value)) {
                        found = true;
                    }
                });
                //console.log("subject was found ? ",found);
                if (!found){
                    $scope.selectedSubjects.push(newVal.modelId);
                }
            }
        });
        $scope.removePermTarget = function(subject) {
            //console.log("removePermTarget", subject);
            $scope.permission.authorizedSubjects = $scope.permission.authorizedSubjects.filter(function(element) {
                if (subject.hasOwnProperty("modelId")) return !angular.equals(element,subject.modelId);
                return !angular.equals(element,subject);
            });
        };

        $scope.submitPermSubject = function(modelId) {
            if (angular.isDefined(modelId)) {
                $scope.selectedSubjects.push(modelId);
            }
        };

        $scope.removeFromSelectedSubjects = function(subject) {
            $scope.selectedSubjects = $scope.selectedSubjects.filter(function(element) {
                if (subject.hasOwnProperty("modelId")) return !angular.equals(element,subject.modelId);
                return !angular.equals(element,subject);
            });
        };

        $scope.areSubjectsSelected = function () {
            //console.log("areSubjectsSelected :", $scope.selectedSubjects.length);
            return $scope.selectedSubjects.length > 0;
        };

        $scope.validity = false;

        $scope.isAdvancedEvaluator = function (evaluator, depth) {
            depth = (typeof depth === 'undefined') ? 0 : depth;
            if (depth < 2 && angular.isDefined(evaluator) && angular.isDefined(evaluator.class)) {
                switch (evaluator.class) {
                    case "OPERATOR":
                        if (angular.isDefined(evaluator.type) && angular.equals('OR',evaluator.type) && angular.isDefined(evaluator.evaluators)) {
                            var boolEval = false;
                            for (var i = 0; i < evaluator.evaluators.length; i++) {
                                boolEval = boolEval && $scope.isAdvancedEvaluator(evaluator.evaluators[i], depth + 1);
                                if (boolEval) return true;
                            }
                            return false;
                        }
                        return true;
                    case "USERATTRIBUTES":
                    case "USERMULTIVALUEDATTRIBUTES":
                    case "USERGROUP":
                        return false;
                    default: return true;

                }
            }
            return true;
        };

        /** fin permissions */

        function getHasTargetManagment(ctxType) {
            switch(ctxType) {
                case 'ORGANIZATION' :
                    return true;
                case 'PUBLISHER' :
                    return false;
                case 'CATEGORY' :
                    if (!angular.equals({},$scope.publisher)) {
                        //console.log("getHasTargetManagment ", $scope.publisher.context.redactor.writingMode == 'STATIC');
                        return $scope.publisher.context.redactor.writingMode == 'STATIC';
                    }
                    return false;
                case 'FEED' :
                    //console.log("getHasTargetManagment ", $scope.publisher.context.redactor.writingMode == 'STATIC');
                    if (!angular.equals({},$scope.publisher)) {
                        return $scope.publisher.context.redactor.writingMode == 'STATIC';
                    }
                    return false;
                case 'ITEM' :
                    if (!angular.equals({},$scope.context)) {
                        return $scope.context.redactor.writingMode != 'STATIC';
                    }
                    return false;
                default : return false;
            }
        }

        function getUserCanManage(contextKey) {
            User.canEditCtx(contextKey, function (data) {
                var result = data.value;
                $scope.canManage = (result == true);

            });
        }

        function selectTemplatePermissionTemplate(permissionClass) {
            switch (permissionClass) {
                case 'CONTEXT': $scope.permissionTemplate = 'scripts/app/manager/treeview/ctxDetails/permissions/permissionOnCtx-detail.html';
                    break;
                case 'CONTEXT_WITH_SUBJECTS' :  $scope.permissionTemplate =  'scripts/app/manager/treeview/ctxDetails/permissions/permissionOnCtxWithSubjects-detail.html';
                    break;
                default : break;

                /*,
                 {name: 'SUBJECT', url: 'scripts/app/manager/publish/content/resource.html'},
                 {name: 'SUBJECT_WITH_CONTEXT', url: 'scripts/app/manager/publish/content/resource.html'}];*/
            }
        }

        $scope.showSubscribeType = function() {
            //console.log("showSubscribeType", $scope.ctxType);
            switch($scope.ctxType) {
                case 'ORGANIZATION' :
                    return false;
                case 'PUBLISHER' :
                    return false;
                default :
                    return getHasTargetManagment($scope.ctxType);
            }
        };

        $scope.getSubjectTypeList = function (name) {
            return $scope.subjectTypeList.filter(function (val) {
                return val.code === name;
            })[0].id;
        };
        $scope.viewSubject= function(target) {
            console.log("subject", target);
            if (target) {
                if (target.subjectDTO && target.subjectDTO.modelId) {
                    //console.log("go on state ", 'ctxDetails.subject', target.subjectDTO.modelId);
                    $state.go('ctxDetails.subject', target.subjectDTO.modelId);
                } else if (target.modelId) {
                    //console.log("go on state ", 'ctxDetails.subject', target.modelId);
                    $state.go('ctxDetails.subject', target.modelId);
                } else if (target.keyId && target.keyType) {
                    //console.log("go on state ", 'ctxDetails.subject', target);
                    $state.go('ctxDetails.subject', target);
                }
            }
            return false;
        };

        $scope.getAccessTypeLabel = function(name) {
            return getEnumlabel('accessType', name);
        };
        $scope.getPermissionTypeLabel = function (name) {
            return getEnumlabel('permissionType', name);
        };
        $scope.getSubscribeTypeLabel = function (name) {
            return getEnumlabel('subscribeType', name);
        };
        $scope.getSubjectTypeLabel = function (name) {
            return getEnumlabel('subjectType', name);
        };
        $scope.getPermissionClassLabel = function(name) {
            return getEnumlabel('permissionClass', name);
        };
        $scope.getDisplayOrderTypeLabel = function(name) {
            return getEnumlabel('displayOrderType', name);
        };
        $scope.getLangLabel = function(name) {
            return getEnumlabel('lang', name);
        };
        $scope.getItemStatusLabel = function(name) {
            return getEnumlabel('itemStatus', name);
        };

        function getEnumlabel (type, name) {
            if (name) {
                switch (type) {
                    case 'accessType' :
                        return $scope.accessTypeList.filter(function (val) {
                            return val.name === name;
                        })[0].label;
                    case 'permissionClass' :
                        return $scope.permissionClassList.filter(function (val) {
                            return val.name === name;
                        })[0].label;
                    case 'displayOrderType' :
                        return $scope.displayOrderTypeList.filter(function (val) {
                            return val.name === name;
                        })[0].label;
                    case 'subjectType' :
                        return $scope.subjectTypeList.filter(function (val) {
                            return val.code === name;
                        })[0].descKey;
                    case 'permissionType' :
                        return $scope.permissionTypeList.filter(function (val) {
                            return val.name === name;
                        })[0].label;
                    case 'subscribeType' :
                        return $scope.subscribeTypeList.filter(function (val) {
                            return val.name === name;
                        })[0].label;
                    case 'lang' :
                        return $scope.langList.filter(function (val) {
                            return val.id === name;
                        })[0].label;
                    case 'itemStatus' :
                        return EnumDatas.getItemStatusList().filter(function(val) {
                            return val.name === name;
                        })[0].label;
                }
            }
        }

    });
