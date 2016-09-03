(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('RateController', RateController);

    RateController.$inject = ['$scope', '$state', 'Rate', 'RateSearch'];

    function RateController ($scope, $state, Rate, RateSearch) {
        var vm = this;
        
        vm.rates = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Rate.query(function(result) {
                vm.rates = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            RateSearch.query({query: vm.searchQuery}, function(result) {
                vm.rates = result;
            });
        }    }
})();
