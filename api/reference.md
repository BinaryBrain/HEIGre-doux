API Reference
=============

Here is some example of what you will get by calling the API.

Menus
-----

### Request

```
/api/menus               # Today's Menu
/api/menus/2015-01-06    # Menus for a given day
/api/menus/14             # Menu with given id
```

### Result

```javascript
    menus: [{
        id: 14,
        date: "2015-01-06",
        aliments: [
            <aliment1>,
            <...>,
            <alimentN>
        ]
    }]
```

Aliment
-------

### Request

```
/api/aliment/2             # Menu with given id
```

### Result

```javascript
    aliment: { id: 2, name: "riz", type: <type> }
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

Complete Example
----------------

```javascript
    {
        menus: [{
            id: 14,
            date: "2015-01-06",
            aliments: [
                { id: 2, name: "riz", type: { id: 4, name: "feculent" } },
                { id: 4, name: "salade verte", type: { id: 2, name: "legume" } },
                { id: 8, name: "pavé de saumon", type: { id: 3, name: "poisson" } },
                { id: 12, name: "fruit", type: { id: 1, name: "dessert" } }
            ]
        },
        {
            id: 14,
            date: "2015-01-06",
            aliments: [
                { id: 2, name: "riz", type: { id: 4, name: "feculent" } },
                { id: 4, name: "salade verte", type: { id: 2, name: "legume" } },
                { id: 8, name: "pavé de saumon", type: { id: 3, name: "poisson" } },
                { id: 12, name: "fruit", type: { id: 1, name: "dessert" } }
            ]
        }]
    }
```
