(function() {
    'use strict';
    angular
        .module('pinjobApp')
        .factory('Announcement', Announcement);

    Announcement.$inject = ['$resource', 'DateUtils'];

    function Announcement ($resource, DateUtils) {
        var resourceUrl =  'api/announcements/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.date = DateUtils.convertDateTimeFromServer(data.date);
                        data.expiration = DateUtils.convertDateTimeFromServer(data.expiration);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
