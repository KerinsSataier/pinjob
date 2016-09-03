(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('CategoryController', CategoryController);

    CategoryController.$inject = ['$scope', '$state', 'Category', 'CategorySearch'];

    function CategoryController ($scope, $state, Category, CategorySearch) {
        var vm = this;
        
        vm.categories = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Category.query(function(result) {
                vm.categories = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            CategorySearch.query({query: vm.searchQuery}, function(result) {
                vm.categories = result;
            });
        }    }
})();
