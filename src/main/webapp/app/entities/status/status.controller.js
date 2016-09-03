(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('StatusController', StatusController);

    StatusController.$inject = ['$scope', '$state', 'Status', 'StatusSearch'];

    function StatusController ($scope, $state, Status, StatusSearch) {
        var vm = this;
        
        vm.statuses = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Status.query(function(result) {
                vm.statuses = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            StatusSearch.query({query: vm.searchQuery}, function(result) {
                vm.statuses = result;
            });
        }    }
})();
