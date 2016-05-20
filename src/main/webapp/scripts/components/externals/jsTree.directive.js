/*
 * jstree.directive [http://www.jstree.com]
 * http://arvindr21.github.io/jsTree-Angular-Directive
 *
 * Copyright (c) 2014 Arvind Ravulavaru
 * Licensed under the MIT license.
 */

var ngJSTree = angular.module('jsTree.directive', []);
ngJSTree.directive('jsTree', ['$http', function($http) {

    var treeDir = {
        restrict: 'EA',
        fetchResource: function(url, cb) {
            return $http.get(url).then(function(data) {
                if (cb) cb(data.data);
            });
        },

        managePlugins: function(s, e, a, config) {
            if (a.treePlugins) {
                config.plugins = a.treePlugins.split(',');
                config.core = config.core || {};
                config.core.check_callback = config.core.check_callback || true;

                if (config.plugins.indexOf('state') >= 0) {
                    config.state = config.state || {};
                    config.state.key = a.treeStateKey;
                }

                if (config.plugins.indexOf('search') >= 0) {
                    var to = false;
                    if (e.next().attr('class') !== 'ng-tree-search') {
                        e.after('<input type="text" placeholder="Search Tree" class="ng-tree-search"/>')
                            .next()
                            .on('keyup', function(ev) {
                                if (to) {
                                    clearTimeout(to);
                                }
                                to = setTimeout(function() {
                                    treeDir.tree.jstree(true).search(ev.target.value);
                                }, 250);
                            });
                    }
                }

                if (config.plugins.indexOf('checkbox') >= 0) {
                    if (a.treeCheckbox) {
                        config.checkbox = s[a.treeCheckbox];
                    } else {
                        config.checkbox = config.checkbox || {};
                    }
                    if (angular.isDefined(config.checkbox)) {
                        config.checkbox.keep_selected_style = false;
                    }
                }

                if (config.plugins.indexOf('contextmenu') >= 0) {
                    if (a.treeContextmenu) {
                        config.contextmenu = s[a.treeContextmenu];
                    }
                }

                if (config.plugins.indexOf('types') >= 0) {
                    if (a.treeTypes) {
                        config.types = s[a.treeTypes];
                        console.log(config);
                    }
                }

                if (config.plugins.indexOf('dnd') >= 0) {
                    if (a.treeDnd) {
                        config.dnd = s[a.treeDnd];
                        console.log(config);
                    }
                }
            }
            return config;
        },
        manageEvents: function(s, e, a) {
            if (a.treeEvents) {
                var evMap = a.treeEvents.split(';');
                for (var i = 0; i < evMap.length; i++) {
                    if (evMap[i].length > 0) {
                        // plugins could have events with suffixes other than '.jstree'
                        var evt = evMap[i].split(':')[0];
                        if (evt.indexOf('.') < 0) {
                            evt = evt + '.jstree';
                        }
                        var cb = evMap[i].split(':')[1];
                        treeDir.tree.on(evt, s[cb]);
                    }
                }
            }
        },
        link: function(s, e, a) { // scope, element, attribute \O/
            function getCookie(name) {
                var cookieValue = null;
                if (document.cookie && document.cookie != '') {
                    var cookies = document.cookie.split(';');
                    for (var i = 0; i < cookies.length; i++) {
                        var cookie = jQuery.trim(cookies[i]);
                        // Does this cookie string begin with the name we want?
                        if (cookie.substring(0, name.length + 1) == (name + '=')) {
                            cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                            break;
                        }
                    }
                }
                return cookieValue;
            }

            $(function() {
                var config = {};

                // users can define 'core'
                config.core = {};
                if (a.treeCore) {
                    config.core = $.extend(config.core, s[a.treeCore]);
                }

                // clean Case
                a.treeData = a.treeData ? a.treeData.toLowerCase() : '';
                a.treeSrc = a.treeSrc ? a.treeSrc.toLowerCase() : '';

                if (a.treeData == 'html') {
                    treeDir.fetchResource(a.treeSrc, function(data) {
                        e.html(data);
                        treeDir.init(s, e, a, config);
                    });
                } else if (a.treeData == 'json') {
                    treeDir.fetchResource(a.treeSrc, function(data) {
                        config.core.data = data;
                        treeDir.init(s, e, a, config);
                    });
                } else if (a.treeData == 'scope') {
                    s.$watch(a.treeModel, function(n, o) {
                        if (n) {
                            config.core.data = s[a.treeModel];
                            $(e).jstree('destroy');
                            treeDir.init(s, e, a, config);
                        }
                    }, true);
                    // Trigger it initally
                    // Fix issue #13
                    config.core.data = s[a.treeModel];
                    treeDir.init(s, e, a, config);
                } else if (a.treeAjax) {
                    if (config.core.ajax_method && config.core.ajax_method == 'POST') {
                        config.core.data = {
                            'url': a.treeAjax,
                            'data': function (node) {
                                var obj = {'search': node.id != '#' ? node.id : 1};
                                if (config.core.ajax_params) {
                                    var obj = $.extend({
                                        'search': node.id != '#' ? node.id : 1
                                    }, config.core.ajax_params);
                                }
                                if (config.core.ajax_method && config.core.ajax_method == 'POST') {
                                    return JSON.stringify(obj);
                                }
                                return obj;
                            },
                            'beforeSend' : function(jqXHR, settings) {
                                jqXHR.setRequestHeader("X-CSRF-TOKEN",getCookie('CSRF-TOKEN'));
                            },
                            'dataType': 'json',
                            'contentType': "application/json; charset=utf-8",
                            'method': config.core.ajax_method
                        };
                    } else {
                        config.core.data = {
                            'url': a.treeAjax,
                            'data': function (node) {
                                var obj = {'search': node.id != '#' ? node.id : 1};
                                if (config.core.ajax_params) {
                                    var obj = $.extend({
                                        'search': node.id != '#' ? node.id : 1
                                    }, config.core.ajax_params);
                                }
                                return obj;
                            }
                        };
                    }
                    treeDir.init(s, e, a, config);
                }
            });

        },
        init: function(s, e, a, config) {
            treeDir.managePlugins(s, e, a, config);
            this.tree = $(e).jstree(config);
            treeDir.manageEvents(s, e, a);
        }
    };

    return treeDir;

}]);
