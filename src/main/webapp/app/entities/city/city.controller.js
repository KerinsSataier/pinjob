(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('CityController', CityController);

    CityController.$inject = ['$scope', '$state', 'City', 'CitySearch'];

    function CityController ($scope, $state, City, CitySearch) {
        var vm = this;
        
        vm.cities = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            City.query(function(result) {
                vm.cities = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            CitySearch.query({query: vm.searchQuery}, function(result) {
                vm.cities = result;
            });
        }    }
})();
