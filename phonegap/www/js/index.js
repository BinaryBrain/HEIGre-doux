'use strict';

var App = angular.module('App', ['btford.phonegap.ready']);

// Show complete error messages in console
window.onerror = function (errorMsg, url, lineNumber, columnNumber, errorObject) {
    if (errorObject && /<omitted>/.test(errorMsg)) {
        alert('Full exception message: ' + errorObject.message);
    }
}

App.controller('mainCtrl', function ($scope, $rootScope) {
    $scope.hello = "Hello Angular!";
})
