package be.pierrelac.create_vinery;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.resources.ResourceLocation;

/**
 * Modèles partiels pour l'animation des machines Create: Vinery
 */
public class ModPartials {

    public static final PartialModel JUICE_PRESS_SCREW =
        new PartialModel(new ResourceLocation("create_vinery", "block/mechanical_juice_press/screw"));

    public static final PartialModel JUICE_PRESS_HANDLE =
        new PartialModel(new ResourceLocation("create_vinery", "block/mechanical_juice_press/handle"));

    public static final PartialModel JUICE_PRESS_SHAFT =
        new PartialModel(new ResourceLocation("create_vinery", "block/mechanical_juice_press/shaft"));

    /**
     * Initialise les modèles partiels - appelé depuis ModClient
     */
    public static void init() {
        // Force le chargement de la classe et l'initialisation des constantes
    }
}
