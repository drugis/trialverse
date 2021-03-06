'use strict';
define([], function() {

  var dependencies = ['$resource'];
  var GraphResource = function($resource) {
    return $resource(
      '/users/:userUid/datasets/:datasetUUID/graphs/:graphUuid', {
        userUid: '@userUid',
        datasetUUID: '@datasetUUID',
        graphUuid: '@graphUuid',
        commitTitle: '@commitTitle',
        commitDescription: '@commitDescription'
      }, {
        'put': {
          method: 'put'
        }
      });
  };
  return dependencies.concat(GraphResource);
});