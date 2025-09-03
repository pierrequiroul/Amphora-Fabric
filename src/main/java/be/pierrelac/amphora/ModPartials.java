package be.pierrelac.amphora;

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
     * Initialise les modèles partiels - appelé depuis ModClient
     */
    public static void init() {
        // Force le chargement de la classe et l'initialisation des constantes
        System.out.println("[Create Vinery] Loading partial models...");
        System.out.println("- JUICE_PRESS_SCREW: " + JUICE_PRESS_SCREW);
        System.out.println("- JUICE_PRESS_HANDLE: " + JUICE_PRESS_HANDLE);
        System.out.println("- JUICE_PRESS_SHAFT: " + JUICE_PRESS_SHAFT);
    }
}
