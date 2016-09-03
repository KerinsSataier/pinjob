(function() {
    'use strict';

    angular
        .module('pinjobApp')
        .factory('CategorySearch', CategorySearch);

    CategorySearch.$inject = ['$resource'];

    function CategorySearch($resource) {
        var resourceUrl =  'api/_search/categories/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
