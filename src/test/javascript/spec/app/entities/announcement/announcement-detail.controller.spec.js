'use strict';

describe('Controller Tests', function() {

    describe('Announcement Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockAnnouncement, MockUser, MockStatus, MockCategory;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockAnnouncement = jasmine.createSpy('MockAnnouncement');
            MockUser = jasmine.createSpy('MockUser');
            MockStatus = jasmine.createSpy('MockStatus');
            MockCategory = jasmine.createSpy('MockCategory');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Announcement': MockAnnouncement,
                'User': MockUser,
                'Status': MockStatus,
                'Category': MockCategory
            };
            createController = function() {
                $injector.get('$controller')("AnnouncementDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'pinjobApp:announcementUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
