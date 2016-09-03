(function() {
    'use strict';
    angular
        .module('pinjobApp')
        .factory('Rate', Rate);

    Rate.$inject = ['$resource'];

    function Rate ($resource) {
        var resourceUrl =  'api/rates/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
