(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('announcement', {
            parent: 'entity',
            url: '/announcement',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pinjobApp.announcement.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/announcement/announcements.html',
                    controller: 'AnnouncementController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('announcement');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('announcement-detail', {
            parent: 'entity',
            url: '/announcement/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'pinjobApp.announcement.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/announcement/announcement-detail.html',
                    controller: 'AnnouncementDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('announcement');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Announcement', function($stateParams, Announcement) {
                    return Announcement.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'announcement',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('announcement-detail.edit', {
            parent: 'announcement-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/announcement/announcement-dialog.html',
                    controller: 'AnnouncementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Announcement', function(Announcement) {
                            return Announcement.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('announcement.new', {
            parent: 'announcement',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/announcement/announcement-dialog.html',
                    controller: 'AnnouncementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                title: null,
                                body: null,
                                date: null,
                                place: null,
                                price: null,
                                bets: null,
                                expiration: null,
                                picture: null,
                                pictures: null,
                                city: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('announcement', null, { reload: 'announcement' });
                }, function() {
                    $state.go('announcement');
                });
            }]
        })
        .state('announcement.edit', {
            parent: 'announcement',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/announcement/announcement-dialog.html',
                    controller: 'AnnouncementDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Announcement', function(Announcement) {
                            return Announcement.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('announcement', null, { reload: 'announcement' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('announcement.delete', {
            parent: 'announcement',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/announcement/announcement-delete-dialog.html',
                    controller: 'AnnouncementDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Announcement', function(Announcement) {
                            return Announcement.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('announcement', null, { reload: 'announcement' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
