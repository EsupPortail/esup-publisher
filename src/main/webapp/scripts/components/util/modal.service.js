'use strict';

myApp.factory('ModalService', ['$uibModal', '$uibModalStack', function($uibModal, $uibModalStack) {
    return {
        trigger: function(template) {
            $uibModal.open({
                templateUrl: template,
                size: 'lg',
                controller: function($scope, $uibModalInstance) {
                    $scope.ok = function() {
                        $uibModalInstance.close($scope.selected.item);
                    };
                    $scope.cancel = function() {
                        $uibModalInstance.dismiss('cancel');
                    };
                }
            });
        },
        close: function(reason) {
            $uibModalStack.dismissAll(reason);
        }
    };
}]);
