(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('CityDetailController', CityDetailController);

    CityDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'City'];

    function CityDetailController($scope, $rootScope, $stateParams, previousState, entity, City) {
        var vm = this;

        vm.city = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('pinjobApp:cityUpdate', function(event, result) {
            vm.city = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
