(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('ReviewController', ReviewController);

    ReviewController.$inject = ['$scope', '$state', 'Review', 'ReviewSearch'];

    function ReviewController ($scope, $state, Review, ReviewSearch) {
        var vm = this;
        
        vm.reviews = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Review.query(function(result) {
                vm.reviews = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ReviewSearch.query({query: vm.searchQuery}, function(result) {
                vm.reviews = result;
            });
        }    }
})();
