'use strict';

var App = angular.module('App', ['ui.bootstrap', 'btford.phonegap.ready', 'chart.js']);
var API_URL = "http://localhost:9000/api";
// API_URL = "http://10.192.94.46:9000/api";

// Show complete error messages in console
window.onerror = function (errorMsg, url, lineNumber, columnNumber, errorObject) {
    if (errorObject && /<omitted>/.test(errorMsg)) {
        alert('Full exception message: ' + errorObject.message);
    }
}

// Main controller
App.controller('mainCtrl', function ($scope, $rootScope, $http) {
    moment.locale('fr');

    // Getting and processing menus
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
    $scope.disableVotes = [];

    // Getting and processing nutriments
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

    $scope.upvote = function (m) {
        $http.post(API_URL + "/upvote/" + m.id)
            .success(function (data) {
                $scope.disableVotes[m.id] = true;
                m.upvote++;
            })
            .error(function (data, status, headers, config) {
                alert("Erreur de connexion. Impossible de récupérer les nutriments.");
            });
    }

    $scope.downvote = function (m) {
        $http.post(API_URL + "/downvote/" + m.id)
            .success(function (data) {
                $scope.disableVotes[m.id] = true;
                m.downvote++;
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


// Nutriment controller
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

// Nutriment modal controller
App.controller('nutrimentsInstanceCtrl', ['$scope', '$modalInstance', 'id', 'getNutriments', function ($scope, $modalInstance, id, getNutriments) {
    $scope.nutriments = [];
    
    $scope.labels = [];
    $scope.data = [];
    $scope.options = {
        animationSteps: 30,
        animationEasing: 'easeOutCubic',
        tooltipFontSize: 28
    };

    getNutriments(id, function (data) {
        for (var i = 0, l = data.length; i < l; i++) {
            for (var j = 0, k = data[i].values.length; j < k; j++) {
                var n = data[i].values[j];
                var unit = n.unit;
                var type = n['value-type'];
                var name = n.name;

                name = translateName(name);
                unit = shortenUnit(unit);
                type = translateType(type);

                var prefix = (type === 'less than') ? '<' : '';
                var suffix = (type !== 'less than') ? ' ('+type+')' : '';

                var res = n['matrix-unit'].match(/^per\s*(\w*)\s*(\w*)\s*.*/);
                data[i].per = (res) ? res[1] : '???';
                n.o = { name: name, prefix: prefix, value: n.value, unit: unit, suffix: suffix };

                // Adding values to the pie chart
                if (n.name === 'charbohydrate_available') {
                    $scope.labels.push('Glucides');
                    $scope.data.push(n.value);
                } else if (n.name === 'dietary_fibre_total') {
                    $scope.labels.push('Fibres alimentaires');
                    $scope.data.push(n.value);
                } else if (n.name === 'fat_total') {
                    $scope.labels.push('Lipides');
                    $scope.data.push(n.value);
                } else if (n.name === 'protein') {
                    $scope.labels.push('Protéines');
                    $scope.data.push(n.value);
                }
            }
        }

        $scope.nutriments = data;
    })

    $scope.ok = function () {
        $modalInstance.close();
    };
}]);

// Various conversions
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
            return "mg-ATE";
        case "beta-carotene equivalent":
            return "µg-BCE";
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

function translateName(n) {
    switch (n) {
        case "protein":
            return "Protéines";
        case "charbohydrate_total":
            return "Glucides totaux";
        case "charbohydrate_available":
            return "Glucides disponibles";
        case "fat_total":
            return "Lipides, totaux";
        case "alcohol":
            return "Alcool";
        case "energy_kJ":
            return "Énergie, kilojoules";
        case "energy_kcal":
            return "Énergie, calories";
        case "water":
            return "Eau";
        case "fatty_acids_total_saturated":
            return "Acides gras sat.";
        case "fatty_acids_total_mono_unsaturated":
            return "Acides gras mono-insat.";
        case "fatty_acids_total_poly_unsaturated":
            return "Acides gras poly-insat.";
        case "cholesterol":
            return "Cholestérol";
        case "starch_total":
            return "Amidon";
        case "sugar_total":
            return "Sucre";
        case "dietary_fibre_total":
            return "Fibres alimentaires";
        case "sodium":
            return "Sodium";
        case "potassium":
            return "Potassium";
        case "chlorid":
            return "Clor";
        case "calcium":
            return "Calcium";
        case "magnesium":
            return "Magnesium";
        case "phosphor":
            return "Phosphore";
        case "iron_total":
            return "Fer";
        case "zinc":
            return "Zinc";
        case "iodide":
            return "Iode";
        case "vit_A":
            return "Activité de vit. A";
        case "B1":
            return "Vit. B1 (thiamine)";
        case "B2":
            return "Vit. B2 (riboflavine)";
        case "B6":
            return "Vit. B6 (pyridoxine)";
        case "B12":
            return "Vit. B12 (cobalamine)";
        case "C":
            return "Vit. C (acide ascorbique)";
        case "D":
            return "Vit. D (calciférol)";
        case "E":
            return "Activité de vit. E";
        case "niacine":
            return "Niacine";
        case "folate":
            return "Folate";
        case "pantothenic_acid":
            return "Acide pantothénique";
        default:
            return n;
    }
}
