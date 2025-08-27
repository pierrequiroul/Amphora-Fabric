# Structure du Mod Create: Vinery

## Vue d'ensemble
Ce mod ajoute des fluides de jus à Create en utilisant l'intégration Fabric.

## Architecture du Code

### Fichiers Principaux
- **`CreateVinery.java`** : Point d'entrée du mod, initialise tout
- **`JuiceTypes.java`** : Enum centralisant tous les types de jus (ID, nom, couleur)
- **`ModFluids.java`** : Enregistrement des fluides basé sur JuiceTypes
- **`FabricFluidHelpers.java`** : Intégration Fabric pour les interactions bouteilles/seaux

### Côté Client
- **`client/ModClient.java`** : Enregistre les handlers de rendu
- **`client/FluidRenderHandlerFactory.java`** : Factory pour créer les handlers colorés

## Ajouter un Nouveau Jus

1. Ajouter une entrée dans `JuiceTypes.java` :
```java
NEW_JUICE("new_juice", "fluid.create_vinery.new_juice", 0xFF5733)
```

2. Ajouter les traductions dans `assets/create_vinery/lang/en_us.json` :
```json
"fluid.create_vinery.new_juice": "New Juice"
```

3. Ajouter les ressources :
- Blockstate: `assets/create_vinery/blockstates/new_juice.json`
- Modèle: `assets/create_vinery/models/block/fluid/new_juice.json`

## Resources Layout

### Textures Partagées
- `assets/create_vinery/textures/fluid/juice_still.png` : Texture statique
- `assets/create_vinery/textures/fluid/juice_flow.png` : Texture qui coule

### Modèles
- Tous les fluides utilisent `minecraft:block/fluid` comme parent
- Référencent les textures partagées qui sont teintées au runtime

### Blockstates
- Définissent les variantes `level=0` à `level=15`
- Pointent vers `create_vinery:block/fluid/{nom_du_jus}`
