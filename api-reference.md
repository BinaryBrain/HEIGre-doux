API Reference
=============

Here is some example of what you will get by calling the API.

Menus
-----

### Request

```
/api/menus               # Today's Menu
/api/menus/2015-01-06    # Menus for a given day
```

### Result

```javascript
    menus: [{
        id: 14,
        date: "2015-01-06",
        upvote: 4,
        downvote: 2,
        aliments: [
            <aliment1>,
            <...>,
            <alimentN>
        ]
    }]
```

Aliment
-------

```javascript
    aliment: { id: 2, name: "riz", occurrence: 3, last: "2015-01-18T23:59:54.00Z", type: <type>, nutriments: <nutriments> }
```

Type
----

### Request

```
/api/type/4             # Menu with given id
```

### Result

```javascript
    type: { id: 4, name: "feculent" }
```

Nutriments
----------

```javascript
    nutriments: { id: 1066, name: "Riz blanc, cuit à l'eau salée (sel non iodé)" }
```

Complete Example
----------------

```javascript
    {
        menus: [{
            id: 66,
            name: "Riz blanc",
            occurrence: 0,
            last: "2015-01-18T23:59:54.00Z",
            type: {
                id: 3,
                name: "Accompagnement"
            },
            nutriments: {
                id: 1066,
                name: "Riz blanc, cuit à l'eau salée (sel non iodé)"
            }
        },
        {
            id: 63,
            name: "Fruit",
            occurrence: 0,
            last: "2015-01-18T23:59:54.00Z",
            type: {
                id: 5,
                name: "Dessert"
            },
            nutriments: {
                id: 1123,
                name: "Fruit (moyenne), cru"
            }
        }]
    }
```

Nutriments
----------

### Requests

```
GET        /api/nutriments/8  # Get nutriment's values for a given id
```

### Results

```javascript
nutriments: [{
    name: "Fruit (moyenne), cru",
    values: [{
        name: "protein",
        value: 0.662,
        unit: "gram",
        matrix-unit: "per 100g edible portion",
        value-type: "weighted"
    },
    {
        name: "charbohydrate_total",
        value: 13.9,
        unit: "gram",
        matrix-unit: "per 100g edible portion",
        value-type: "weighted"
    },
    ...
    ]}
]
```

### Requests

```
GET        /api/nutriments/frites   # Get aliment's names containing "frites"
GET        /api/nutriments          # Get every aliment's names
```

### Results

```javascript
nutriments: [{
        id: 818,
        name: "Frites au four (pommes de terre précuites en friture), surgelées"
    },
    {
        id: 850,
        name: "Pommes frites"
    }]
```

