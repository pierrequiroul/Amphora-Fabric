# Amphora - Create: Vinery Fluid Integration

## Vue d'ensemble
Ce mod ajoute une intégration fluide entre Create et Vinery, permettant la production automatisée de jus via des machines cinétiques.

## Architecture Moderne (Post-Migration)

### Structure Registrate Modulaire
- **`Amphora.java`** : Point d'entrée principal du mod
- **`AmphoraRegistrate.java`** : Système d'enregistrement conditionnel 
- **`ModCoreContent.java`** : Fluides de base (toujours présents)
- **`ModVineryContent.java`** : Fluides Vinery (conditionnels)

### Fluides Disponibles

**Fluides Core** (toujours actifs):
- Apple Juice (jus de pomme)
- Cherry Juice (jus de cerise)

**Fluides Vinery** (si mod présent):
- 8 variantes de jus de raisin par biomes (rouge/blanc × standard/savanna/taiga/jungle)

### Ajout d'un Nouveau Fluide Core

1. Dans `ModCoreContent.java`:
```java
public static final FluidEntry<SimpleFlowableFluid.Flowing> NEW_JUICE = 
    AmphoraRegistrate.INSTANCE.coloredJuiceFluid("new_juice", 0xFF5733)
        .lang("New Juice")
        .tag(AmphoraRegistrate.JUICE_TAG)
        .register();
```

2. Ajouter le rendering dans `AmphoraClient.java`
3. Ajouter les recettes de pressage dans `data/`

### Architecture Moderne
Le système utilise exclusivement **Registrate Refabricated** pour tous les fluides :
- **Fluides Core** : Toujours enregistrés (apple_juice, cherry_juice)
- **Fluides Vinery** : Enregistrés conditionnellement si Vinery est présent
