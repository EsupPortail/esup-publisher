angular.module('color-palette-picker', [])
    //.run(['$templateCache', function($templateCache) {
    //    $templateCache.put('colorPalettePicker.html', '<div class="color-palette-picker">'+
    //        '<div class="selected-color" ng-style="{\'background-color\': vm.color}"></div>' +
    //        '<div class="color-palette">'+
    //        '<div ng-repeat="option in vm.options"'+
    //        'ng-style="{\'background-color\': option}"'+
    //        'ng-class="{\'palette-selected-color\': option == vm.color, \'transparent-color\': option == \'transparent\'}"'+
    //        'ng-click="vm.changeColor(option)"></div>'+
    //        '</div>'+
    //        '</div>');
    //}])
    .directive('colorPalettePicker', function () {
        return {
            restrict: 'E',
            templateUrl: 'scripts/components/form/input/color/colorPalettePicker.html',
            replace: true,
            controller: colorPalettePickerDirectiveController,
            controllerAs: 'vm',
            bindToController: true,
            scope: {
                color: '=',
                options: '=',
                onColorChanged: '&'
            }
        };

        function colorPalettePickerDirectiveController() {
            var vm = this;

            vm.changeColor = function (option) {
                if(vm.color != option) {
                    var old = vm.color;
                    vm.color = option;
                    vm.onColorChanged({newColor: option, oldColor: old});
                }
            };
            vm.init = function() {
                vm.colorOrigin = vm.color;
            };
            vm.cancel = function() {
                vm.color = vm.colorOrigin;
                $('#colorPaletteModal').modal('hide');
            };
            vm.validate = function() {
                $('#colorPaletteModal').modal('hide');
            };
        }
    });
