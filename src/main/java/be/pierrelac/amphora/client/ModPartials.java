package be.pierrelac.amphora.client;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.resources.ResourceLocation;

/**
 * Modèles partiels pour l'animation des machines Create: Vinery
 */
public class ModPartials {

    public static final PartialModel JUICE_PRESS_SCREW =
        new PartialModel(new ResourceLocation("amphora", "block/mechanical_juice_press/screw"));

    public static final PartialModel JUICE_PRESS_HANDLE =
        new PartialModel(new ResourceLocation("amphora", "block/mechanical_juice_press/handle"));

    public static final PartialModel JUICE_PRESS_SHAFT =
        new PartialModel(new ResourceLocation("amphora", "block/mechanical_juice_press/shaft"));

    /**
     * Initialise tous les modèles partiels.
     * Appelé depuis AmphoraClient pour s'assurer que les modèles sont enregistrés.
     */
    public static void init() {
        // L'initialisation se fait automatiquement par l'accès aux champs statiques
    }
}
