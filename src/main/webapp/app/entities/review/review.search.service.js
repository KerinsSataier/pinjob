(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .factory('ReviewSearch', ReviewSearch);

    ReviewSearch.$inject = ['$resource'];

    function ReviewSearch($resource) {
        var resourceUrl =  'api/_search/reviews/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
