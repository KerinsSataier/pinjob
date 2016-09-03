(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .controller('AnnouncementDialogController', AnnouncementDialogController);

    AnnouncementDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Announcement', 'User', 'Status', 'Category'];

    function AnnouncementDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Announcement, User, Status, Category) {
        var vm = this;

        vm.announcement = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();
        vm.statuses = Status.query();
        vm.categories = Category.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.announcement.id !== null) {
                Announcement.update(vm.announcement, onSaveSuccess, onSaveError);
            } else {
                Announcement.save(vm.announcement, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('pinjobApp:announcementUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;
        vm.datePickerOpenStatus.expiration = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
