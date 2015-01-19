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

App.controller('mainCtrl', function ($scope, $rootScope, $http) {
    moment.locale('fr');

    $scope.searches = [];
    $scope.results = [];

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

        for (var i = 0; i < dailyMenus.length; i++) {
            var m = dailyMenus[i].menus;
            for (var j = 0; j < m.length; j++) {
                var a = m[j].aliments;
                for (var k = 0; k < a.length; k++) {
                    var n = a[k].nutriments;
                    if (n && n.id && n.name) {
                        if (typeof $scope.results[a[k].id] === 'undefined') {
                            $scope.results[a[k].id] = [];
                        }

                        $scope.results[a[k].id].push(n);
                    }
                }
            }
        }
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
    
    function getNutriments(name, cb) {
        $http.get(API_URL + "/nutriments/" + name)
            .success(function (data) {
                if (cb) {
                    cb(data.nutriments);
                }
            })
            .error(function (data, status, headers, config) {
                alert("Erreur de connexion. Impossible de récupérer les nutriments.");
            });
    }

    function setNutriments(aid, nid, cb) {
        $http.post(API_URL + "/nutriments/" + aid + "/" + nid)
            .success(function (data) {
                if (cb) {
                    cb(data);
                }
            })
            .error(function (data, status, headers, config) {
                alert("Erreur de connexion. Impossible de récupérer les nutriments.");
            });
    }

    $scope.getNutrimentsList = function(a) {
        var name = stripAccents($scope.searches[a.id]);
        
        if (name.length < 3) {
            return;
        }

        getNutriments(name, function (data) {
            $scope.results[a.id] = data;
        });
    }

    $scope.updateNutriments = function(a) {
        var aid = a.id;
        var nid = a.nutriments.id;
        setNutriments(aid, nid);
    }

    $scope.addNutriment = function (arr, nut) {
        if (typeof arr === 'undefined') {
            return arr;
        }

        for (var i = 0, l = arr.length; i < l; i++) {
            if (arr[i] === nut) {
                return arr;
            }
        }

        arr.push(nut);
        return arr;
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

function stripAccents(s) {
    var r=s.toLowerCase();
    r = r.replace(new RegExp(/\s/g),"");
    r = r.replace(new RegExp(/[àáâãäå]/g),"a");
    r = r.replace(new RegExp(/æ/g),"ae");
    r = r.replace(new RegExp(/ç/g),"c");
    r = r.replace(new RegExp(/[èéêë]/g),"e");
    r = r.replace(new RegExp(/[ìíîï]/g),"i");
    r = r.replace(new RegExp(/ñ/g),"n");                
    r = r.replace(new RegExp(/[òóôõö]/g),"o");
    r = r.replace(new RegExp(/œ/g),"oe");
    r = r.replace(new RegExp(/[ùúûü]/g),"u");
    r = r.replace(new RegExp(/[ýÿ]/g),"y");
    r = r.replace(new RegExp(/\W/g),"");
    return r;
};