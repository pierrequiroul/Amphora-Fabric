package be.pierrelac.create_vinery.blockentity;

import be.pierrelac.create_vinery.CreateVinery;
import be.pierrelac.create_vinery.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Gestion de tous les BlockEntityTypes du mod Create: Vinery
 */
public class ModBlockEntities {
    
    public static BlockEntityType<MechanicalJuicePressBlockEntity> MECHANICAL_JUICE_PRESS;
    
    /**
     * Enregistre tous les BlockEntityTypes
     */
    public static void register() {
        CreateVinery.LOGGER.info("Registering Create: Vinery block entities");
        
        MECHANICAL_JUICE_PRESS = FabricBlockEntityTypeBuilder.<MechanicalJuicePressBlockEntity>create(
            (pos, state) -> new MechanicalJuicePressBlockEntity(MECHANICAL_JUICE_PRESS, pos, state))
            .addBlocks(ModBlocks.MECHANICAL_JUICE_PRESS)
            .build();
        
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, 
            new ResourceLocation("create_vinery", "mechanical_juice_press"), 
            MECHANICAL_JUICE_PRESS);
        
        CreateVinery.LOGGER.info("✓ BlockEntities registered successfully");
    }
}
