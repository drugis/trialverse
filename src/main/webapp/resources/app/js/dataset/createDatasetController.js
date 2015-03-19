'use strict';
define([],
  function() {
    var dependencies = ['$scope', '$state', '$modalInstance', 'DatasetResource', 'callback'];
    var CreateDatasetController = function($scope, $state, $modalInstance, DatasetResource, callback) {
      $scope.dataset = {};
      $scope.createDataset = function() {
        DatasetResource.save($scope.dataset).$promise.then(function() {
          callback();
        });
        $modalInstance.close();
      };
      $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
      };
    };
    return dependencies.concat(CreateDatasetController);
  });
