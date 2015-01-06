API Reference
=============

Here is some example of what you will get by calling the API.

Menu
----
```javascript
    menu: {
        id: 14,
        date: "2015-01-06",
        aliments: [
            <aliment1>,
            <...>,
            <alimentN>
        ]
    }
```

Aliment
-------

```javascript
    aliment: { id: 2, name: "riz", type: <type> }
```

Type
----

```javascript
    type: { id: 4, name: "feculent" }
```

Complete Example
----------------

```javascript
    menu: {
        id: 14,
        date: "2015-01-06",
        aliments: [
            { id: 4, name: "salade verte", type: { id: 2, name: "legume" } },
            { id: 8, name: "pavé de saumon", type: { id: 3, name: "poisson" } },
            { id: 12, name: "fruit", type: { id: 1, name: "dessert" } }
        ]
    }
```
