'use strict';

var App = angular.module('App', ['ui.bootstrap', 'btford.phonegap.ready']);
var API_URL = "http://localhost:9000/api";
// API_URL = "http://192.168.1.55:9000/api";

// Show complete error messages in console
window.onerror = function (errorMsg, url, lineNumber, columnNumber, errorObject) {
    if (errorObject && /<omitted>/.test(errorMsg)) {
        alert('Full exception message: ' + errorObject.message);
    }
}

var s;

App.controller('mainCtrl', function ($scope, $rootScope, $http) {
    moment.locale('fr');

    var menus = [];

    var today = new Date();
    var shift = today.getDay();
    if (shift === 0) {
        shift = 7;
    }
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

    $scope.nutriments = [];

    $scope.getNutriments = function (id, cb) {
        $http.get(API_URL + "/nutriments/" + id)
            .success(function (data) {
                $scope.nutriments = data.nutriments;
                cb(data.nutriments)
            })
            .error(function (data, status, headers, config) {
                alert("Erreur de connexion. Impossible de récupérer les nutriments.");
            });
    }

});

App.filter('momentAgo', function () {
  return function (date) {
    return moment(date).fromNow();
  };
});


App.controller('AccordionCtrl', function ($scope) {
  $scope.oneAtATime = true;
});

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

function capFirst(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

App.controller('nutrimentsCtrl', function ($scope, $modal) {
    $scope.open = function (id) {
        var modalInstance = $modal.open({
            templateUrl: 'nutrimentsContent.html',
            controller: 'nutrimentsInstanceCtrl',
            resolve: {
                id: function () {
                    return id;
                },
                getNutriments: function () {
                    return $scope.getNutriments;
                }
            }
        });

        modalInstance.result.then(function () {
            console.log("closed")
        });
    };
});

App.controller('nutrimentsInstanceCtrl', ['$scope', '$modalInstance', 'id', 'getNutriments', function ($scope, $modalInstance, id, getNutriments) {
    $scope.nutriments = [];
    
    getNutriments(id, function (data) {
        for (var i = 0, l = data.length; i < l; i++) {
            for (var j = 0, k = data[i].values.length; j < k; j++) {
                var n = data[i].values[j];
                var unit = n.unit;
                var type = n['value-type'];

                unit = shortenUnit(unit);
                type = translateType(type);

                var prefix = (type === 'less than') ? '<' : '';
                var suffix = (type !== 'less than') ? ' ('+type+')' : '';

                var res = n['matrix-unit'].match(/^per\s*(\w*)\s*(\w*)\s*.*/);
                data[i].per = (res) ? res[1] : '???';
                n.o = { prefix: prefix, value: n.value, unit: unit, suffix: suffix };
            }
        }

        $scope.nutriments = data;
    })

    $scope.ok = function () {
        $modalInstance.close();
    };
}]);

function shortenUnit(u) {
    switch (u) {
        case "gram":
            return "g";
        case "milligram":
            return "mg";
        case "microgram":
            return "µg";
        case "kilojoule":
            return "kJ";
        case "kilocalorie":
            return "kcal";
        case "retinol equivalent":
            return " RE";
        case "alpha-tocopherol equivalent":
            return " α-TE";
        default:
            return ' '+u;
    }
}

function translateType(t) {
    switch (t) {
        case "weighted":
            return "mesuré";
        case "mean":
            return "en moyenne";
        case "trace":
            return "trace";
        case "as reported":
            return "rapportée";
        case "logical zero":
            return "logiquement";
        case "best estimate":
            return "meilleur estimation";
        case "value type not known":
            return "inconnu";
        default:
            return t;
    }
}