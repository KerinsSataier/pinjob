(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('AnnouncementDetailController', AnnouncementDetailController);

    AnnouncementDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Announcement', 'User', 'Status', 'Category'];

    function AnnouncementDetailController($scope, $rootScope, $stateParams, previousState, entity, Announcement, User, Status, Category) {
        var vm = this;

        vm.announcement = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('pinjobApp:announcementUpdate', function(event, result) {
            vm.announcement = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
