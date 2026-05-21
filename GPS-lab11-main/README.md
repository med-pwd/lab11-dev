# LAB 11 : GPS et Map (Google Maps Activity)
analyste:souaid med amine
## 1. Objectifs du Laboratoire
Ce travail pratique vise à maîtriser les services de localisation d'Android et l'intégration du SDK Google Maps. Les points clés abordés sont :
- L'affichage d'une carte interactive via **Google Maps Activity**.
- La gestion rigoureuse des **permissions de localisation** au runtime.
- L'écoute dynamique des flux de position via le **LocationManager**.
- La matérialisation de la position par des **Markers** visuels.
- L'implémentation de mécanismes de sécurité (boîte de dialogue d'activation du GPS).
- L'optimisation de l'expérience utilisateur (Zoom automatique et centrage).

---

## 2. Phase d'Initialisation : Création du Projet
### Étape 1 : Configuration initiale
Nous avons débuté par la création d'un nouveau projet sous Android Studio nommé **GPS-lab11**. Lors de la sélection du template, nous avons privilégié le modèle **"Google Maps Activity"** afin de bénéficier d'une structure pré-configurée incluant les dépendances nécessaires.

### Étape 2 : Analyse de la structure générée
L'architecture du projet s'articule autour de :
- `MainActivity.java` : Le cœur de la logique applicative.
- `activity_main.xml` : Définit l'interface contenant le fragment de la carte.
- `google_maps_api.xml` : Fichier de configuration pour la clé API.

---

## 3. Configuration de la Clé API Google Maps
Pour rendre la carte opérationnelle, une clé API valide est indispensable. 
1. **Génération** : Accès à la console Google Cloud pour activer le **Maps SDK for Android**.
2. **Intégration** : La clé est placée dans le fichier `res/values/google_maps_api.xml`.
   ```xml
   <string name="google_maps_key" templateMergeStrategy="preserve">VOTRE_CLE_ICI</string>
   ```

---

## 4. Gestion des Permissions et Sécurité
### Permissions Manifest
L'accès aux données de localisation nécessite l'ajout des permissions suivantes dans le `AndroidManifest.xml` :
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

### Runtime Permissions (Android 6+)
Même déclarées, les permissions doivent être demandées explicitement à l'utilisateur lors de l'exécution pour éviter toute `SecurityException`. Nous utilisons `ActivityCompat.requestPermissions` pour solliciter cet accès.

---

## 5. Implémentation de la Logique de Localisation
### Écoute des mises à jour (LocationListener)
Le service `LocationManager` est configuré pour écouter les changements de position via le `NETWORK_PROVIDER` (pour la réactivité) ou le `GPS_PROVIDER` (pour la précision).

```java
gpsSystemSvc.requestLocationUpdates(
    LocationManager.NETWORK_PROVIDER, 
    1000, // Temps min (ms)
    50,   // Distance min (m)
    locationListener
);
```

### Gestion du GPS Désactivé
Si le capteur GPS est inactif, l'application propose une boîte de dialogue via `showGpsDisabledDialog()`. Cette méthode redirige l'utilisateur vers les paramètres système grâce à l'intent `Settings.ACTION_LOCATION_SOURCE_SETTINGS`.

---

## 6. Visualisation et Interaction sur la Carte
### Mise à jour des Markers
À chaque changement de coordonnées, un nouveau **Marker** est positionné sur la carte. Pour une version optimisée, nous recommandons de déplacer un marker unique plutôt que d'en créer de multiples, évitant ainsi la pollution visuelle de l'interface.

### Contrôle de la Caméra
Pour maintenir l'utilisateur au centre de l'action, nous utilisons `moveCamera` ou `animateCamera` avec un niveau de zoom adéquat (par exemple, 15.0f pour une vue de quartier).

```java
float zoomLevel = 15.0f;
googleMapsObject.animateCamera(CameraUpdateFactory.newLatLngZoom(position, zoomLevel));
```

---

## 7. Conclusion
Ce laboratoire a permis de comprendre l'importance de la gestion asynchrone des services de localisation et la complexité de l'intégration d'API tierces comme Google Maps. L'application résultante est robuste, gérant aussi bien les refus de permissions que l'indisponibilité matérielle des capteurs.

