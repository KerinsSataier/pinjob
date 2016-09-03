(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .factory('StatusSearch', StatusSearch);

    StatusSearch.$inject = ['$resource'];

    function StatusSearch($resource) {
        var resourceUrl =  'api/_search/statuses/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
