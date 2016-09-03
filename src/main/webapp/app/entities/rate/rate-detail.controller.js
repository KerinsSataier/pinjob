(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('RateDetailController', RateDetailController);

    RateDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Rate', 'User'];

    function RateDetailController($scope, $rootScope, $stateParams, previousState, entity, Rate, User) {
        var vm = this;

        vm.rate = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('pinjobApp:rateUpdate', function(event, result) {
            vm.rate = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
