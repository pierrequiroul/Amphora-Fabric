package be.pierrelac.amphora.core.registration.modules;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.core.ModCompatibility;
import be.pierrelac.amphora.core.registration.RegistrationModule;
import be.pierrelac.amphora.content.kinetics.juice_press.MechanicalJuicePressBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.resources.ResourceLocation;

/**
 * Module d'enregistrement pour les éléments dépendants de Create.
 * Machines kinétiques, presses, block entities...
 */
public class CreateRegistrationModule implements RegistrationModule {
    
    // Références statiques pour l'accès global
    private static Block MECHANICAL_JUICE_PRESS;
    private static BlockEntityType<MechanicalJuicePressBlockEntity> MECHANICAL_JUICE_PRESS_ENTITY;
    private static Item MECHANICAL_JUICE_PRESS_ITEM;
    
    @Override
    public String getModuleId() {
        return "create";
    }
    
    @Override
    public String getDescription() {
        return "Create kinetic machinery integration";
    }
    
    @Override
    public boolean shouldRegister() {
        return ModCompatibility.isCreateAvailable();
    }
    
    @Override
    public void registerContent() {
        logInfo("Registering Create-dependent content...");
        
        try {
            // Enregistrement de la Mechanical Juice Press
            registerMechanicalJuicePress();
            
            logInfo("Create registration complete - mechanical juice press registered");
            
        } catch (Exception e) {
            logError("Failed to register Create content", e);
        }
    }
    
    /**
     * Enregistre la Mechanical Juice Press complète
     */
    private void registerMechanicalJuicePress() {
        logDebug("Registering mechanical juice press...");
        
        // 1. Block
        ResourceLocation blockId = new ResourceLocation(Amphora.MOD_ID, "mechanical_juice_press");
        
        try {
            // Charge la classe JuicePress dynamiquement
            Class<?> juicePressClass = Class.forName("be.pierrelac.amphora.content.kinetics.juice_press.MechanicalJuicePressBlock");
            Block juicePress = (Block) juicePressClass.getDeclaredConstructor().newInstance();
            
            MECHANICAL_JUICE_PRESS = Registry.register(BuiltInRegistries.BLOCK, blockId, juicePress);
            logDebug("Registered juice press block: {}", blockId);
            
        } catch (Exception e) {
            // Fallback : créer un bloc simple si la classe n'existe pas
            logDebug("JuicePressBlock not found, creating fallback block");
            MECHANICAL_JUICE_PRESS = Registry.register(BuiltInRegistries.BLOCK, blockId, 
                new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)
                    .strength(3.5f)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));
        }
        
        // 2. Block Entity
        ResourceLocation entityId = new ResourceLocation(Amphora.MOD_ID, "mechanical_juice_press");
        MECHANICAL_JUICE_PRESS_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            entityId,
            FabricBlockEntityTypeBuilder.<MechanicalJuicePressBlockEntity>create(
                (pos, state) -> new MechanicalJuicePressBlockEntity(MECHANICAL_JUICE_PRESS_ENTITY, pos, state),
                MECHANICAL_JUICE_PRESS
            ).build()
        );
        logDebug("Registered juice press entity: {}", entityId);
        
        // 3. Item
        ResourceLocation itemId = new ResourceLocation(Amphora.MOD_ID, "mechanical_juice_press");
        MECHANICAL_JUICE_PRESS_ITEM = Registry.register(BuiltInRegistries.ITEM, itemId,
            new BlockItem(MECHANICAL_JUICE_PRESS, new Item.Properties()));
        logDebug("Registered juice press item: {}", itemId);
    }
    
    // === GETTERS STATIQUES POUR ACCÈS GLOBAL ===
    
    public static Block getMechanicalJuicePress() {
        return MECHANICAL_JUICE_PRESS;
    }
    
    public static BlockEntityType<MechanicalJuicePressBlockEntity> getMechanicalJuicePressEntity() {
        return MECHANICAL_JUICE_PRESS_ENTITY;
    }
    
    public static Item getMechanicalJuicePressItem() {
        return MECHANICAL_JUICE_PRESS_ITEM;
    }
    
    /**
     * Vérifie si les éléments Create sont disponibles
     */
    public static boolean isContentAvailable() {
        return MECHANICAL_JUICE_PRESS != null 
            && MECHANICAL_JUICE_PRESS_ENTITY != null 
            && MECHANICAL_JUICE_PRESS_ITEM != null;
    }
    
    @Override
    public void cleanup() {
        // Reset des références si nécessaire
        logDebug("Cleaning up Create module references");
    }
}
