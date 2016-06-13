'use strict';

angular.module('publisherApp')
    .directive('hasAnyRole', ['Principal', function (Principal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var setVisible = function () {
                        element.removeClass('hidden');
                    },
                    setHidden = function () {
                        element.addClass('hidden');
                    },
                    defineVisibility = function (reset) {
                        var result;
                        if (reset) {
                            setVisible();
                        }

                        result = Principal.isInAnyRole(roles);
                        if (result) {
                            setVisible();
                        } else {
                            setHidden();
                        }
                    },
                    roles = attrs.hasAnyRole.replace(/\s+/g, '').split(',');
                setHidden();
                if (roles.length > 0) {
                    defineVisibility(true);
                }
            }
        };
    }])
    .directive('hasRole', ['Principal', function (Principal) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var setVisible = function () {
                        element.removeClass('hidden');
                    },
                    setHidden = function () {
                        element.addClass('hidden');
                    },
                    defineVisibility = function (reset) {
                        var result;
                        if (reset) {
                            setVisible();
                        }

                        result = Principal.isInRole(role);
                        if (result) {
                            setVisible();
                        } else {
                            setHidden();
                        }
                    },
                    role = attrs.hasRole.replace(/\s+/g, '');
                setHidden();
                if (role.length > 0) {
                    defineVisibility(true);
                }
            }
        };
    }])
    .directive('canEdit', ['User', function (User) {
        return {
            restrict: 'A',
            scope: {
                context: '=canEdit'
            },
            link: function (scope, element, attrs) {
                scope.$watch('context', function(ctx) {
                    var setVisible = function () {
                            element.removeClass('hidden');
                        },
                        setHidden = function () {
                            element.addClass('hidden');
                        },
                        defineVisibility = function (reset) {
                            if (reset) {
                                setVisible();
                            }
                            User.canEditCtx(scope.context, function (data) {
                                var result = data.value;
                                if (result == true) {
                                    setVisible();
                                } else {
                                    setHidden();
                                }
                            }, function () {
                                setHidden();
                            });
                        };
                    setHidden();
                    if (scope.context && scope.context.keyId && scope.context.keyType) {
                        defineVisibility(false);
                    }
                });
            }

        };
    }])
    .directive('canEditPerms', ['User', function (User) {
        return {
            restrict: 'A',
            scope: {
                context: '=canEditPerms'
            },
            link: function (scope, element, attrs) {
                scope.$watch('context', function(ctx) {
                    var setVisible = function () {
                            element.removeClass('hidden');
                        },
                        setHidden = function () {
                            element.addClass('hidden');
                        },
                        defineVisibility = function (reset) {
                            if (reset) {
                                setVisible();
                            }
                            User.canEditCtxPerms(scope.context, function (data) {
                                var result = data.value;
                                if (result == true) {
                                    setVisible();
                                } else {
                                    setHidden();
                                }
                            }, function () {
                                setHidden();
                            });
                        };
                    setHidden();
                    if (scope.context && scope.context.keyId && scope.context.keyType) {
                        defineVisibility(false);
                    }
                });
            }

        };
    }])
    .directive('canEditTargets', ['User', function (User) {
        return {
            restrict: 'A',
            scope: {
                context: '=canEditTargets'
            },
            link: function (scope, element, attrs) {
                scope.$watch('context', function(ctx) {
                    var setVisible = function () {
                            element.removeClass('hidden');
                        },
                        setHidden = function () {
                            element.addClass('hidden');
                        },
                        defineVisibility = function (reset) {
                            if (reset) {
                                setVisible();
                            }
                            User.canEditCtxTargets(scope.context, function (data) {
                                var result = data.value;
                                if (result == true) {
                                    setVisible();
                                } else {
                                    setHidden();
                                }
                            }, function () {
                                setHidden();
                            });
                        };
                    setHidden();
                    if (scope.context && scope.context.keyId && scope.context.keyType) {
                        defineVisibility(false);
                    }
                });
            }

        };
    }])
    .directive('canDelete', ['User', function (User) {
        return {
            restrict: 'A',
            scope: {
                context: '=canDelete'
            },
            link: function (scope, element, attrs) {
                scope.$watch('context', function(ctx) {
                    var setVisible = function () {
                            element.removeClass('hidden');
                        },
                        setHidden = function () {
                            element.addClass('hidden');
                        },
                        defineVisibility = function (reset) {
                            if (reset) {
                                setVisible();
                            }
                            User.canDeleteCtx(scope.context, function (data) {
                                var result = data.value;
                                if (result == true) {
                                    setVisible();
                                } else {
                                    setHidden();
                                }
                            }, function () {
                                setHidden();
                            });
                        };
                    setHidden();
                    if (scope.context && scope.context.keyId && scope.context.keyType) {
                        defineVisibility(false);
                    }
                });
            }

        };
    }])
    .directive('canCreateIn', ['User', function (User) {
        return {
            restrict: 'A',
            scope: {
                context: '=canCreateIn'
            },
            link: function (scope, element, attrs) {
                scope.$watch('context', function(ctx) {
                    var setVisible = function () {
                            element.removeClass('hidden');
                        },
                        setHidden = function () {
                            element.addClass('hidden');
                        },
                        defineVisibility = function (reset) {
                            if (reset) {
                                setVisible();
                            }
                            User.canCreateInCtx(scope.context, function (data) {
                                var result = data.value;
                                if (result == true) {
                                    setVisible();
                                } else {
                                    setHidden();
                                }
                            }, function () {
                                setHidden();
                            });
                        };
                    setHidden();
                    if (scope.context && scope.context.keyId && scope.context.keyType) {
                        defineVisibility(false);
                    }
                });
            }

        };
    }])
    .directive('canModerate', ['User', function (User) {
        return {
            restrict: 'A',
            scope: {},
            link: function (scope, element, attrs) {
                var setVisible = function () {
                        element.removeClass('hidden');
                    },
                    setHidden = function () {
                        element.addClass('hidden');
                    },
                    defineVisibility = function (reset) {
                        var result;
                        if (reset) {
                            setVisible();
                        }

                        User.canModerateAnyThing(function (data) {
                            var result = data.value;
                            if (result == true) {
                                setVisible();
                            } else {
                                setHidden();
                            }
                        }, function () {
                            setHidden();
                        });
                    };
                setHidden();
                defineVisibility(false);
            }

        };
    }]);
