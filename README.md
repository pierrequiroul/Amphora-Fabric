# Amphora - Create ❤️ Vinery Integration

Amphora est un mod qui crée un pont entre Create et Let's Do Vinery, ajoutant un pipeline de production de jus entièrement automatisé avec des fluides personnalisés et des recettes pilotées par les données.

## ✨ Fonctionnalités

### 🔧 Presse à Jus Mécanique
- Machine kinétique compatible avec le système Create
- Traite les fruits en jus avec le système de bassins
- Animation 3D avec rendu Flywheel
- Support des recettes personnalisées

### 🧪 Fluides de Jus
- Raisins (variants biomes : standard, savane, taïga, jungle)
- Jus de pomme et de cerise
- Rendu avec couleurs spécifiques par fluide
- Compatible avec les systèmes de fluides Create

### 📜 Recettes Basées sur les Données
- Type de recette personnalisé : `amphora:juice_pressing`
- Rendements réalistes (6 raisins → 500 mB de jus)
- Sous-produits optionnels (graines, noyaux)
- Compatible avec les bassins Create

### 🎨 Intégration Recipe Viewers
- Support EMI/JEI/REI
- Animations personnalisées pour les recettes
- Catégories dédiées pour le pressage de jus

## 🔧 Dépendances Optionnelles

**NOUVEAU** : Amphora supporte maintenant les dépendances optionnelles !

### Configurations Supportées

| Create | Vinery | Fonctionnalités |
|--------|--------|-----------------|
| ✅ | ✅ | **Complet** - Toutes les fonctionnalités |
| ✅ | ❌ | **Create Seul** - Machines avec items vanilla |
| ❌ | ✅ | **Vinery Seul** - Recettes simplifiées |
| ❌ | ❌ | **Standalone** - Mode minimaliste |

### Configuration

```properties
# gradle.properties
optional_create = true    # Active/désactive Create
optional_vinery = true    # Active/désactive Vinery
target_platform = fabric  # fabric/forge (forge à venir)
```

Voir [OPTIONAL_DEPENDENCIES.md](OPTIONAL_DEPENDENCIES.md) pour plus de détails.

## 🚀 Installation

1. Téléchargez la dernière version depuis [Releases](../../releases)
2. Placez le fichier `.jar` dans votre dossier `mods/`
3. **Recommandé** : Installez Create et Vinery pour l'expérience complète
4. **Optionnel** : Ajoutez EMI/JEI/REI pour l'intégration des recettes

## ⚙️ Développement

### Prérequis
- JDK 17+
- Minecraft 1.20.1
- Fabric Loader 0.16.9+

### Build
```bash
./gradlew build
```

### Test
```bash
./gradlew runClient
```

### Configuration des Dépendances
```bash
# Mode complet (défaut)
./gradlew build -Poptional_create=true -Poptional_vinery=true

# Mode Create seul
./gradlew build -Poptional_create=true -Poptional_vinery=false

# Mode standalone
./gradlew build -Poptional_create=false -Poptional_vinery=false
```

## 🏗️ Architecture

```
src/main/java/be/pierrelac/amphora/
├── core/                 # Système principal
│   ├── ModCompatibility  # Détection des mods
│   └── ConditionalRecipeManager
├── compat/               # Intégrations conditionnelles
│   ├── create/           # Code spécifique Create
│   ├── vinery/           # Code spécifique Vinery
│   └── emi/              # Intégration EMI
├── platform/             # Abstraction multiplateforme
│   ├── fabric/           # Implémentation Fabric
│   └── forge/            # Implémentation Forge (futur)
└── content/              # Contenu du mod
    ├── fluids/           # Fluides de jus
    └── kinetics/         # Machines kinétiques
```

## 🌐 Support Multiplateforme

La structure est préparée pour supporter Forge à l'avenir :
- Architecture modulaire avec interfaces d'abstraction
- Système de détection de plateforme
- Classes conditionnelles séparées par loader

## 📊 Compatibility

| Mod | Version | Statut |
|-----|---------|--------|
| Minecraft | 1.20.1 | ✅ Supporté |
| Fabric Loader | 0.16.9+ | ✅ Requis |
| Fabric API | 0.92.2+ | ✅ Requis |
| Create (Fabric) | 0.5.1-j-build.1631 | 🟨 Optionnel |
| Let's Do Vinery | 1.4.40 | 🟨 Optionnel |
| EMI | 1.1.22+ | 🟦 Suggéré |

## 🤝 Contribution

1. Fork le projet
2. Créez une branche pour votre fonctionnalité
3. Committez vos changements
4. Poussez vers la branche
5. Ouvrez une Pull Request

## 📄 License

Ce projet est sous licence MIT. Voir [LICENSE](LICENSE) pour plus de détails.

## 💬 Support

Questions ? Rejoignez-nous sur le [Discord Create](https://discord.com/invite/hmaD7Se) dans le canal #devchat.

This template is available under the CC0 license. Feel free to do as you wish with it.
