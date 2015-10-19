
angular.module('productService').controller('NewProductController', function ($scope, $location, locationParser, flash, ProductResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.product = $scope.product || {};
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The product was created successfully.'});
            $location.path('/Products');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        ProductResource.save($scope.product, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Products");
    };
});