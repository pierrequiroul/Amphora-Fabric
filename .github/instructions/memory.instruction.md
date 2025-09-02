---
applyTo: '**'
---

# Mémoire - Create: Vinery Fabric

## État du Projet
- **Statut actuel** : Système de recette de pressage de jus fonctionnel avec logique de correspondance personnalisée
- **Problème résolu** : Les recettes de juice press ne fonctionnaient pas comme espéré, mais maintenant le système de correspondance personnalisé est implémenté
- **Date de dernière mise à jour** : 2 septembre 2025

## Architecture Technique

### Plateforme
- Minecraft 1.20.1
- Fabric Loader 0.16.9
- Create 0.5.1-j-build.1631+mc1.20.1
- Vinery 1.4.40

### Fichiers Clés
1. **MechanicalJuicePressBlockEntity.java** : BlockEntity principal avec logique de correspondance personnalisée
2. **JuicePressRecipe.java** : Classe de recette étendant BasinRecipe
3. **ModRecipeTypes.java** : Enregistrement des types de recettes
4. **Recettes JSON** : apple_juice_pressing.json, red_grape_juice_pressing.json, white_grape_juice_pressing.json

### Fonctionnalités Implémentées
- ✅ Système de correspondance de recettes personnalisé dans `matchBasinRecipe()`
- ✅ Logique de comptage d'ingrédients avec Map pour gérer les recettes Create
- ✅ Méthode `getMatchingRecipes()` avec filtrage basé sur la correspondance personnalisée
- ✅ Debug étendu pour comprendre le comportement des recettes
- ✅ Gestion des ingrédients avec comptage correct (Create étend les ingrédients avec count)

### Problèmes Résolus
1. **Correspondance des recettes** : Create's BasinRecipe.matches() était trop strict, résolu avec logique personnalisée
2. **Format des recettes JSON** : Utilise le format correct avec `{"item":"minecraft:apple","count":4}`
3. **Expansion des ingrédients** : Compréhension que Create étend automatiquement count en entrées séparées
4. **Intégration du système** : Le filtrage dans getMatchingRecipes() s'assure que seules les recettes correspondantes sont ajoutées

### Observations du Debug
- La recette apple_juice_pressing montre "Has all required items: true" avec 4 pommes dans le basin
- Le système de correspondance personnalisé fonctionne correctement
- Le debug montre que les recettes sont ajoutées à la liste de correspondance après filtrage

### Points d'Attention
- Le système utilise une logique de correspondance personnalisée pour contourner les limitations de Create
- Les messages de debug avec "MATCHES!" et "does not match" indiquent que le filtrage fonctionne
- Le system est conçu pour être compatible avec l'architecture existante de Create

## Configuration
- Répertoire de travail : `d:\Dev\Create-Vinery-Fabric-`
- Structure des recettes : `src/main/resources/data/create_vinery/recipes/juice_pressing/`
- Package principal : `be.pierrelac.create_vinery`
