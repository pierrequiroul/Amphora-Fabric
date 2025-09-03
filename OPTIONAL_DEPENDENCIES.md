# Amphora - Configuration des Dépendances Optionnelles

## Vue d'ensemble

Amphora supporte maintenant les dépendances optionnelles pour Create et Vinery, permettant une utilisation flexible selon les mods installés dans votre environnement.

## Configurations Supportées

### 🟢 Configuration Complète (Recommandée)
```properties
optional_create = true
optional_vinery = true
```
- **Fonctionnalités** : Toutes les fonctionnalités d'Amphora disponibles
- **Dépendances** : Create + Vinery + EMI (optionnel)
- **Machines** : Presse à jus mécanique avec système kinétique
- **Recettes** : Pressage de tous les fruits Vinery
- **Intégration** : Support EMI/JEI/REI complet

### 🟡 Mode Create Seul
```properties
optional_create = true
optional_vinery = false
```
- **Fonctionnalités** : Machines kinétiques avec items vanilla
- **Dépendances** : Create seulement
- **Machines** : Presse à jus mécanique
- **Recettes** : Items vanilla (pommes, baies, etc.)
- **Limitation** : Moins de variété de jus

### 🟠 Mode Vinery Seul
```properties
optional_create = false
optional_vinery = true
```
- **Fonctionnalités** : Jus avec recettes simplifiées
- **Dépendances** : Vinery seulement
- **Machines** : Alternatives sans système kinétique
- **Recettes** : Crafting/Brewing recipes
- **Note** : Mode expérimental

### 🔴 Mode Standalone
```properties
optional_create = false
optional_vinery = false
```
- **Fonctionnalités** : Fonctionnalités de base uniquement
- **Dépendances** : Aucune dépendance externe
- **Contenu** : Fluides et items de base
- **Usage** : Tests ou environnements minimalistes

## Configuration

### Fichier `gradle.properties`
```properties
# Optional Dependencies Control
optional_create = true    # true/false - Active Create
optional_vinery = true    # true/false - Active Vinery  
target_platform = fabric  # fabric/forge (forge à venir)
```

### Fichier `fabric.mod.json`
```json
{
  "depends": {
    "fabricloader": ">=0.16.9",
    "fabric-api": ">=0.92.2",
    "minecraft": "1.20.1"
  },
  "suggests": {
    "create": "0.5.1-j-build.1631+mc1.20.1",
    "vinery": "*",
    "emi": "*"
  }
}
```

## Détection Automatique

Le mod détecte automatiquement les mods présents au runtime :

```java
// Exemples de détection
ModCompatibility.CREATE_LOADED    // true si Create est chargé
ModCompatibility.VINERY_LOADED    // true si Vinery est chargé
ModCompatibility.EMI_LOADED       // true si EMI est chargé
```

## Logs de Compatibilité

Au démarrage, Amphora affiche un rapport de compatibilité :

```
=== MOD COMPATIBILITY DETECTION ===
Create: ✓ LOADED
Vinery: ✗ MISSING  
EMI: ✓ LOADED
=== FEATURE COMPATIBILITY ===
Mechanical Processing: ENABLED
Juice Recipes: ENABLED
Recipe Viewer Integration: ENABLED
=======================================
```

## Cas d'Usage

### Développement
```properties
# Pour tester sans Create
optional_create = false
optional_vinery = true
```

### Production Légère
```properties
# Pack minimaliste
optional_create = false
optional_vinery = false
```

### Intégration Maximum
```properties
# Toutes les fonctionnalités
optional_create = true
optional_vinery = true
```

## Résolution de Problèmes

### Create non détecté
- Vérifiez la version de Create dans `gradle.properties`
- Assurez-vous que Flywheel est présent
- Consultez les logs pour les erreurs de chargement

### Recettes manquantes
- Mode standalone : Recettes alternatives activées
- Vinery manquant : Fallback vers items vanilla
- Consultez EMI/JEI pour voir les recettes disponibles

### Erreurs de compilation
- Mode `compile-only` : Normal pour les dépendances désactivées
- Classes manquantes : Vérifiez la configuration conditionnelle
- Flywheel : Requis si Create est activé

## Migration

### Depuis la version précédente
1. Aucun changement requis par défaut
2. Toutes les dépendances restent activées
3. Comportement identique à la version précédente

### Vers mode conditionnel
1. Modifiez `gradle.properties` selon vos besoins
2. Rebuild le projet
3. Testez les fonctionnalités dans votre environnement

## Support Multiplateforme (Futur)

La structure est préparée pour supporter Forge :
- Architecture modulaire avec interfaces d'abstraction
- Système de détection de plateforme
- Classes conditionnelles séparées

## Aide

Questions ? Consultez les logs ou créez une issue sur GitHub.
