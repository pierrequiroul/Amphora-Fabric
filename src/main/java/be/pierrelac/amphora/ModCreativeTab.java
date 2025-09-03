package be.pierrelac.amphora;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Gestion de l'onglet créatif du mod Create: Vinery.
 * 
 * Cet onglet contient tous les seaux de jus du mod.
 */
public class ModCreativeTab {
    private static ResourceKey<CreativeModeTab> TAB_KEY;
    
    public static void register() {
        Amphora.LOGGER.info("Registering creative tab with Fabric fluid buckets");
        
        TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Amphora.id("main"));
        
        CreativeModeTab tab = FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.amphora.main"))
            .icon(() -> new ItemStack(Items.BUCKET)) // Icône temporaire
            .displayItems((parameters, output) -> {
                // Ajouter le pressoir mécanique
                output.accept(new ItemStack(ModBlockAccess.getMechanicalJuicePressItem()));
                
                // Ajouter les bouteilles de jus
                output.accept(new ItemStack(ModItems.CHERRY_JUICE_BOTTLE));
                
                // Ajouter tous les seaux de jus depuis ModItems
                ModItems.FLUID_BUCKETS.values().forEach(bucket -> {
                    output.accept(new ItemStack(bucket));
                });
            })
            .build();
            
        // Enregistrer l'onglet dans le registre de Minecraft
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB_KEY, tab);
        
        Amphora.LOGGER.info("✓ Creative tab registered successfully");
    }
    
    public static ResourceKey<CreativeModeTab> getTabKey() {
        return TAB_KEY;
    }
}
