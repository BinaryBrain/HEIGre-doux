'use strict';

var App = angular.module('App', ['ui.bootstrap', 'btford.phonegap.ready']);
var API_URL = "http://localhost:9000/api";
API_URL = "http://192.168.1.55:9000/api";

// Show complete error messages in console
window.onerror = function (errorMsg, url, lineNumber, columnNumber, errorObject) {
    if (errorObject && /<omitted>/.test(errorMsg)) {
        alert('Full exception message: ' + errorObject.message);
    }
}

var s;

App.controller('mainCtrl', function ($scope, $rootScope, $http) {
    s = $scope;

    moment.locale('fr');

    var menus = [];

    var today = new Date();
    var shift = today.getDay();
    var startDate = new Date(today.getFullYear(), today.getMonth(), today.getDate() - shift);
    var endDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate() + 7);
    var startDateStr = dateToStr(startDate);
    var endDateStr = dateToStr(endDate);

    $scope.today = today;

    $http.get(API_URL + "/menus/" + startDateStr + "/" + endDateStr)
        .success(function (data) {
            processMenus(data.menus);
        })
        .error(function (data, status, headers, config) {
            alert("Erreur de connexion. Impossible de récupérer les menus.");
    });

    function processMenus(menus) {
        menus = menusArrayByDay(menus);

        var dailyMenus = [];
        
        for (var key in menus) {
            var isToday = toFrenchDate(key) == toFrenchDate(new Date(today.getFullYear(), today.getMonth(), today.getDate()));
            var l = dailyMenus.push({ date: key, frenchDate: toFrenchDate(key), menus: menus[key], isOpen: isToday });
        }

        $scope.dailyMenus = dailyMenus;
    }

    function dateToStr(date) {
        return date.getFullYear() + "-" + pad((date.getMonth()+1), 2) + "-" + pad(date.getDate(), 2);
        
        function pad(num, size) {
            var s = num + "";
            while (s.length < size) s = "0" + s;
            return s;
        }
    }

    function toFrenchDate(date) {
        return capFirst(moment(date).format("dddd - Do MMMM YYYY"));
    }

    function menusArrayByDay(menus) {
        var arr = {};

        for (var i = 0, l = menus.length; i < l; i++) {
            if (arr[menus[i].date] === undefined) {
                arr[menus[i].date] = [];
            }

            arr[menus[i].date].push(menus[i]);
        }

        return arr;
    }

    function capFirst(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }
});

App.filter('momentAgo', function () {
  return function (date) {
    return moment(date).fromNow();
  };
});


App.controller('AccordionDemoCtrl', function ($scope) {
  $scope.oneAtATime = true;
});