'use strict';

angular.module('publisherApp')
    .controller('ConfigurationController', function ($scope, ConfigurationService) {
    	$scope.configuration = [];
    	
        ConfigurationService.get().then(function(configuration) {
        	var properties = [];
        	var propertiesObject = $scope.getConfigPropertiesObjects(configuration);
        	angular.forEach(propertiesObject, function (key,value) {
        		if (Object.prototype.hasOwnProperty.call(propertiesObject, value)) {
                    properties.push(propertiesObject[value]);
                }
        	});
        	$scope.configuration = $scope.configuration.concat(properties);
        });

        ConfigurationService.getEnv().then(function(configuration) {
        	var properties = [];
        	var propertiesObject = configuration['propertySources'];
        	angular.forEach(propertiesObject, function (obj) {
        		var name = obj['name'];
        		var detailProperties = obj['properties'];
        		var vals = {};
        		angular.forEach(detailProperties, function (value,keyDetail) {
        			vals[keyDetail] = value.value;
        		});
              properties.push({prefix: name, properties: vals});

        	});
            $scope.configuration = $scope.configuration.concat(properties);
        });
        
        $scope.getConfigPropertiesObjects = function (res) {
            return res[0]['application']['beans'];
          }
        
    });

