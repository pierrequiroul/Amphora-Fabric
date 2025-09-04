package be.pierrelac.amphora.content.core;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.AmphoraRegistrate;
import be.pierrelac.amphora.content.kinetics.juice_press.MechanicalJuicePressBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.Blocks;

/**
 * Blocs de base d'Amphora - blocs toujours disponibles
 * Utilise Registrate pour un enregistrement propre et éviter les problèmes de timing
 */
public class ModCoreBlocks {
    
    // ===== REGISTRATE BLOCKS =====
    // Blocs de base toujours disponibles
    
    public static final BlockEntry<MechanicalJuicePressBlock> MECHANICAL_JUICE_PRESS = 
        AmphoraRegistrate.REGISTRATE.block("mechanical_juice_press", MechanicalJuicePressBlock::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(properties -> properties.noOcclusion())
            .lang("Mechanical Juice Press")
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), 
                prov.models().getExistingFile(prov.modLoc("block/mechanical_juice_press/block"))))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), 
                prov.modLoc("block/mechanical_juice_press/item")))
            .build()
            .register();
    
    public static void register() {
        AmphoraRegistrate.logModularRegistration("core", "blocks");
        
        Amphora.LOGGER.info("✓ Registrate core blocks registered: {}", 
            MECHANICAL_JUICE_PRESS.getId());
        
        // Note: L'enregistrement effectif se fait automatiquement par Registrate
        // Les BlockEntry ci-dessus sont créés lors du chargement de la classe
    }
}
