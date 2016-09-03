(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('ReviewDetailController', ReviewDetailController);

    ReviewDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Review', 'User'];

    function ReviewDetailController($scope, $rootScope, $stateParams, previousState, entity, Review, User) {
        var vm = this;

        vm.review = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('pinjobApp:reviewUpdate', function(event, result) {
            vm.review = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
