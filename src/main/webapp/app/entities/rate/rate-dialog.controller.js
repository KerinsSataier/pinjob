(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('RateDialogController', RateDialogController);

    RateDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Rate', 'User'];

    function RateDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Rate, User) {
        var vm = this;

        vm.rate = entity;
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
            if (vm.rate.id !== null) {
                Rate.update(vm.rate, onSaveSuccess, onSaveError);
            } else {
                Rate.save(vm.rate, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pinjobApp:rateUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
