package be.pierrelac.amphora.content.core;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.AmphoraRegistrate;
import be.pierrelac.amphora.content.kinetics.juice_press.MechanicalJuicePressBlock;
import be.pierrelac.amphora.content.kinetics.fermenter.FermentationValveBlock;
import be.pierrelac.amphora.content.kinetics.fermenter.FermentationBaseBlock;
import be.pierrelac.amphora.content.kinetics.fermenter.InputValveBlock;
import be.pierrelac.amphora.content.kinetics.fermenter.OutputValveBlock;
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
    
    public static final BlockEntry<FermentationValveBlock> FERMENTATION_VALVE = 
        AmphoraRegistrate.REGISTRATE.block("fermentation_valve", FermentationValveBlock::new)
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .properties(properties -> properties.noOcclusion())
            .lang("Fermentation Valve")
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), 
                prov.models().getExistingFile(prov.modLoc("block/fermentation_valve/block"))))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), 
                prov.modLoc("block/fermentation_valve/item")))
            .build()
            .register();
    
    // ===== NOUVEAUX BLOCS MODULAIRES =====
    
    public static final BlockEntry<FermentationBaseBlock> FERMENTATION_BASE = 
        AmphoraRegistrate.REGISTRATE.block("fermentation_base", FermentationBaseBlock::new)
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .properties(properties -> properties.noOcclusion())
            .lang("Fermentation Base")
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), 
                prov.models().getExistingFile(prov.modLoc("block/fermentation_base/block"))))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), 
                prov.modLoc("block/fermentation_base/item")))
            .build()
            .register();
    
    public static final BlockEntry<InputValveBlock> INPUT_VALVE = 
        AmphoraRegistrate.REGISTRATE.block("input_valve", InputValveBlock::new)
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .properties(properties -> properties.noOcclusion())
            .lang("Input Valve")
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), 
                prov.models().getExistingFile(prov.modLoc("block/input_valve/block"))))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), 
                prov.modLoc("block/input_valve/item")))
            .build()
            .register();
    
    public static final BlockEntry<OutputValveBlock> OUTPUT_VALVE = 
        AmphoraRegistrate.REGISTRATE.block("output_valve", OutputValveBlock::new)
            .initialProperties(() -> Blocks.COPPER_BLOCK)
            .properties(properties -> properties.noOcclusion())
            .lang("Output Valve")
            .blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), 
                prov.models().getExistingFile(prov.modLoc("block/output_valve/block"))))
            .item()
            .model((ctx, prov) -> prov.withExistingParent(ctx.getName(), 
                prov.modLoc("block/output_valve/item")))
            .build()
            .register();
    
    public static void register() {
        AmphoraRegistrate.logModularRegistration("core", "blocks");
        
        Amphora.LOGGER.info("✓ Registrate core blocks registered: {}, {}, {}, {}, {}", 
            MECHANICAL_JUICE_PRESS.getId(), 
            FERMENTATION_VALVE.getId(),
            FERMENTATION_BASE.getId(),
            INPUT_VALVE.getId(),
            OUTPUT_VALVE.getId());
        
        // Note: L'enregistrement effectif se fait automatiquement par Registrate
        // Les BlockEntry ci-dessus sont créés lors du chargement de la classe
    }
}
