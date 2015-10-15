import apiServices from '../api';

apiServices.factory('mediaUsage', ['mediaApi', function (mediaApi) {
    function getUsage(image) {
        return image.follow('usages').getData();
    }

    return {
        getUsage
    }
}]);
