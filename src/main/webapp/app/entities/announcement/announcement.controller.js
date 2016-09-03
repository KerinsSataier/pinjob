(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('AnnouncementController', AnnouncementController);

    AnnouncementController.$inject = ['$scope', '$state', 'Announcement', 'AnnouncementSearch'];

    function AnnouncementController ($scope, $state, Announcement, AnnouncementSearch) {
        var vm = this;
        
        vm.announcements = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Announcement.query(function(result) {
                vm.announcements = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            AnnouncementSearch.query({query: vm.searchQuery}, function(result) {
                vm.announcements = result;
            });
        }    }
})();
