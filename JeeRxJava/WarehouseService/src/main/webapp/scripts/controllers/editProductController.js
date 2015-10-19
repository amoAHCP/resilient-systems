

angular.module('productService').controller('EditProductController', function($scope, $routeParams, $location, flash, ProductResource ) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.product = new ProductResource(self.original);
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The product could not be found.'});
            $location.path("/Products");
        };
        ProductResource.get({ProductId:$routeParams.ProductId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.product);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The product was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.product.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/Products");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The product was deleted.'});
            $location.path("/Products");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.product.$remove(successCallback, errorCallback);
    };
    
    
    $scope.get();
});