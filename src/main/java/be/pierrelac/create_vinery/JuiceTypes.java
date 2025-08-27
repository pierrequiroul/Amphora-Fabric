package be.pierrelac.create_vinery;

/**
 * Enum centralisé qui définit tous les types de jus disponibles.
 * 
 * Chaque type contient :
 * - id: identifiant unique pour le fluide
 * - translationKey: clé de traduction pour l'affichage
 * - color: couleur hexadécimale pour le rendu client
 * 
 * Ajouter un nouveau jus = ajouter une ligne ici.
 */
public enum JuiceTypes {
    RED_GRAPE("red_grape", "fluid.create_vinery.red_grape_juice", 0x8B0000),
    WHITE_GRAPE("white_grape", "fluid.create_vinery.white_grape_juice", 0xF5F5DC),
    RED_SAVANNA_GRAPE("red_savanna_grape", "fluid.create_vinery.red_savanna_grape_juice", 0x800000),
    WHITE_SAVANNA_GRAPE("white_savanna_grape", "fluid.create_vinery.white_savanna_grape_juice", 0xFFFAF0),
    RED_TAIGA_GRAPE("red_taiga_grape", "fluid.create_vinery.red_taiga_grape_juice", 0x8B0000),
    WHITE_TAIGA_GRAPE("white_taiga_grape", "fluid.create_vinery.white_taiga_grape_juice", 0xFFFAF0),
    RED_JUNGLE_GRAPE("red_jungle_grape", "fluid.create_vinery.red_jungle_grape_juice", 0x8B0000),
    WHITE_JUNGLE_GRAPE("white_jungle_grape", "fluid.create_vinery.white_jungle_grape_juice", 0xFFFAF0),
    APPLE("apple", "fluid.create_vinery.apple_juice", 0xFFE4B5);

    private final String id;
    private final String translationKey;
    private final int color;

    JuiceTypes(String id, String translationKey, int color) {
        this.id = id;
        this.translationKey = translationKey;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public int getColor() {
        return color;
    }

    public String getFluidId() {
        return id + "_juice";
    }
}
