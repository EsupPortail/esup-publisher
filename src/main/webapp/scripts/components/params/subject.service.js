'use strict';

angular.module('publisherApp')
    .factory('Subject', function($q, $state, User, Group) {
        var userDisplayedAttrs, groupDisplayedAttrs, UserFonctionalAttrs;
        return {
            init : function () {
                if (!userDisplayedAttrs || !groupDisplayedAttrs || !UserFonctionalAttrs)
                return $q.all([User.attributes().$promise, Group.attributes().$promise, User.funtionalAttributes().$promise])
                    .then(function (results) {
                        userDisplayedAttrs = results[0];
                        groupDisplayedAttrs = results[1];
                        UserFonctionalAttrs = results[2];
                    }).catch(function (error) {
                        //console.log(JSON.stringify(error));
                        $state.go("error");
                    });
                return $q.all();

            },
            getUserDisplayedAttrs : function () {
                if (!userDisplayedAttrs) {this.init();}
                return userDisplayedAttrs;
            },
            getGroupDisplayedAttrs : function () {
                if (!groupDisplayedAttrs) {this.init();}
                return groupDisplayedAttrs;
            },
            getUserFonctionalAttrs : function() {
                if (!UserFonctionalAttrs) {this.init();}
                return UserFonctionalAttrs;
            },
            getUserInfos : function(id){
                return User.details({login: id});
            },
            getGroupInfos : function(id){
                return Group.details({id: id});
            },
            getSubjectInfos : function(type, id){
                //console.log("call getSubjectInfos :", type, id);
                switch (type) {
                    case "PERSON" : return this.getUserInfos(id);
                    case "GROUP" : return this.getGroupInfos(id);
                    default: return {};
                }
            }
        }
    });
