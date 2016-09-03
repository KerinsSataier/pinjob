(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('ReviewDialogController', ReviewDialogController);

    ReviewDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Review', 'User'];

    function ReviewDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Review, User) {
        var vm = this;

        vm.review = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.review.id !== null) {
                Review.update(vm.review, onSaveSuccess, onSaveError);
            } else {
                Review.save(vm.review, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pinjobApp:reviewUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
