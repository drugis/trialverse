'use strict';
define(['angular', 'angular-mocks', 'testUtils'], function(angular, angularMocks, testUtils) {
  describe('the unit service service', function() {

    var graphUri = 'http://karma-test/';
    var scratchStudyUri = 'http://localhost:9876/scratch';

    var mockStudyUuid = 'mockStudyUuid';

    var rootScope, q, httpBackend;
    var remotestoreServiceStub;
    var commentServiceStub;
    var studyService;

    var unitService;
    var queryUnitTemplate;


    beforeEach(function() {
      module('trialverse.util', function($provide) {
        remotestoreServiceStub = jasmine.createSpyObj('RemoteRdfStoreService', [
          'create',
          'load',
          'executeUpdate',
          'executeQuery',
          'getGraph',
          'deFusekify'
        ]);
        commentServiceStub = jasmine.createSpyObj('CommentService', [
          'addComment'
        ]);
        $provide.value('RemoteRdfStoreService', remotestoreServiceStub);
        $provide.value('CommentService', commentServiceStub);
      });
    });

    beforeEach(module('trialverse.activity'));

    beforeEach(inject(function($q, $rootScope, $httpBackend, UnitService, StudyService, SparqlResource) {
      q = $q;
      httpBackend = $httpBackend;
      rootScope = $rootScope;
      studyService = StudyService;
      unitService = UnitService;

      testUtils.dropGraph(graphUri);

      // load study service templates
      testUtils.loadTemplate('createEmptyStudy.sparql', httpBackend);
      testUtils.loadTemplate('queryStudyData.sparql', httpBackend);

      queryUnitTemplate = testUtils.loadTemplate('queryUnit.sparql', httpBackend);

      httpBackend.flush();

      // create and load empty test store
      var createStoreDeferred = $q.defer();
      var createStorePromise = createStoreDeferred.promise;
      remotestoreServiceStub.create.and.returnValue(createStorePromise);

      var loadStoreDeferred = $q.defer();
      var loadStorePromise = loadStoreDeferred.promise;
      remotestoreServiceStub.load.and.returnValue(loadStorePromise);

      studyService.loadStore();
      createStoreDeferred.resolve(scratchStudyUri);
      loadStoreDeferred.resolve();

      rootScope.$digest();
    }));


    describe('query units', function() {

      beforeEach(function(done) {
        var xmlHTTP = new XMLHttpRequest();
        xmlHTTP.open('GET', 'base/test_graphs/unitsQueryMockGraph.ttl', false);
        xmlHTTP.send(null);
        var unitsQueryMockGraph = xmlHTTP.responseText;

        xmlHTTP.open('PUT', scratchStudyUri + '/data?graph=' + graphUri, false);
        xmlHTTP.setRequestHeader('Content-type', 'text/turtle');
        xmlHTTP.send(unitsQueryMockGraph);

        // stub remotestoreServiceStub.executeQuery method
        remotestoreServiceStub.executeQuery.and.callFake(function(uri, query) {
          query = query.replace(/\$graphUri/g, graphUri);

          //// console.log('graphUri = ' + uri);
          //// console.log('query = ' + query);

          var result = testUtils.queryTeststore(query);
          // console.log('queryResponce ' + result);
          var resultObject = testUtils.deFusekify(result)

          var executeUpdateDeferred = q.defer();
          executeUpdateDeferred.resolve(resultObject);
          return executeUpdateDeferred.promise;
        });

        done();
      });

      it('should return the units contained in the graph', function(done) {

        // call function under test
        unitService.queryItems(mockStudyUuid).then(function(result){
          var units = result;

          // verify query result
          expect(units.length).toBe(2);
          expect(units[1].label).toEqual('milligram');
          expect(units[0].label).toEqual('liter');

          done();
        });
        rootScope.$digest();
      });
    });



  });
});
