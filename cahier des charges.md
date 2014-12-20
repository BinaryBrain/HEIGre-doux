Cahier des charges
==================

Ce document constitue le cahier des charges pour le projet du cours de base de donnée.

Membres du groupe
-----------------

- Léonard Berney
- Sacha Bron

Description de la problématique
-------------------------------

Actuellement, le seul moynen offert aux étudiants de l'HEIG-VD pour connaitre à l'avance les menus est d'aller consulter un document _Word_ sur le site [intranet de l'école](http://intra.heig-vd.ch). Nous trouvons que cette solution est peu pratique et peut être fortement améliorée par le biais d'une interface unique et simple pour l'utilisateur.

De plus, nous pensons qu'il serait intéressant d'analyser les données de ces menus (date de dernière proposition d'un menu, origine de la viande, estimation des valeurs nutritives, etc.).

Un autre point essentiel de l'application est de proposer aux caféterias un retour sur la satisfaction des gens par un système d'appréciations simples. Ceci dans l'intérêt des consommateurs et du restaurateur.


Analyse des besoins
-------------------

Les besoins techniques sont:
- la récupération des données (menus, éventuellement certaines sources externes)
- le stockage des données (sous forme de base de données)
- l'analyse et la mise en forme des données pour les utilisateurs (interface graphique)

Technologies utilisées
----------------------

Le projet sera effectué avec un approche _client-serveur_.

Ces technologies ont été choisies pour leur aspect pratique et moderne. Elles sont utilisées couramment pour des sites web de grandes envergures.

### Serveur (Backend)

- Scala, _probablement dans un paradigme pûrement fonctionnel_
- Play (incluant Akka), _serveur web_
- Slick, _Functional Relational Mapping (Scala - MySQL)_

### Client (Frontend)

- HTML5, CSS3, _web-based client_
- AngularJS, _MVC_
- Less, _ pré-processeur CSS_
- Bootstrap, _responsive design_
