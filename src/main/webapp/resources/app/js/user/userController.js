'use strict';
define([],
  function() {
    var dependencies = ['$scope', '$location', '$q', '$modal', '$filter', '$window',
      '$stateParams', 'DatasetResource', 'DatasetOverviewService', 'UserResource', 'md5'
    ];

    var UserController = function($scope, $location, $q, $modal, $filter, $window,
      $stateParams, DatasetResource, DatasetOverviewService, UserResource, md5) {
      $scope.stripFrontFilter = $filter('stripFrontFilter');
      $scope.otherUsers = [];
      $scope.userUid = $stateParams.userUid;
      $scope.loginUser = $window.config.user;
      $scope.reloadDatasets = reloadDatasets;

      // if no user is supplied, then go to the logged-in user user-page
      if (!$scope.userUid || $scope.userUid.length === 0) {
        $location.path('/users/' + $scope.loginUser.userNameHash);
      } else {
        UserResource.query(function(users) {
          _.each(users, function(user) {
            user.md5 = md5.createHash(user.username);
            if ($scope.userUid === user.userNameHash) {
              $scope.user = user;
            } else {
              $scope.otherUsers.push(user);
            }
          });
        });
        reloadDatasets();
      }

      function reloadDatasets() {
        DatasetOverviewService.reset();
        DatasetResource.query($stateParams, function(response) {
          DatasetOverviewService.loadStore(response.data).then(function() {
            console.log('loading dataset-store success');
            DatasetOverviewService.queryDatasetsOverview().then(function(datasets) {
              $scope.datasets = datasets;
              $scope.datasetsLoaded = true;
              if (datasets.length > 0) {
                $scope.versionUrlBase = datasets[0].headVersion.split('/').slice(0, 4).join('/') + '/';
              }
            }, function() {
              console.error('failed loading datasetstore');
            });
          });
        });
      }

      $scope.createDatasetDialog = function() {
        $modal.open({
          templateUrl: 'app/js/user/createDataset.html',
          controller: 'CreateDatasetController',
          resolve: {
            callback: function() {
              return reloadDatasets;
            }
          }
        });
      };

    };
    return dependencies.concat(UserController);
  });
