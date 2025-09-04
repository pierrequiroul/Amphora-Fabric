package be.pierrelac.amphora.content.core;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.AmphoraRegistrate;
import be.pierrelac.amphora.content.vinery.ModVineryContent;
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
 * Cet onglet contient tous les items du mod.
 */
public class ModCoreCreativeTab {
    private static ResourceKey<CreativeModeTab> TAB_KEY;
    
    public static void register() {
        Amphora.LOGGER.info("Registering creative tab with Registrate items");
        
        TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, Amphora.id("main"));
        
        CreativeModeTab tab = FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.amphora.main"))
            .icon(() -> new ItemStack(Items.BUCKET)) // Icône temporaire
            .displayItems((parameters, output) -> {
                // Ajouter le pressoir mécanique depuis Registrate
                output.accept(ModCoreBlocks.MECHANICAL_JUICE_PRESS.asStack());
                
                // Ajouter la bouteille de jus de cerise depuis Registrate
                output.accept(ModCoreItems.CHERRY_JUICE_BOTTLE.asStack());
                
                // Ajouter les seaux de jus depuis ModCoreContent (Registrate)
                if (ModCoreContent.APPLE_JUICE != null) {
                    var appleBucket = ModCoreContent.APPLE_JUICE.getSource().getBucket();
                    if (appleBucket != null) {
                        output.accept(new ItemStack(appleBucket));
                    }
                }
                
                if (ModCoreContent.CHERRY_JUICE != null) {
                    var cherryBucket = ModCoreContent.CHERRY_JUICE.getSource().getBucket();
                    if (cherryBucket != null) {
                        output.accept(new ItemStack(cherryBucket));
                    }
                }
                
                // Ajouter les seaux Vinery conditionnellement (si le mod est chargé)
                if (AmphoraRegistrate.isModLoaded("vinery")) {
                    addVineryBuckets(output);
                }
                
                // Ajouter tous les seaux de jus depuis ModCoreItems
                ModCoreItems.FLUID_BUCKETS.values().forEach(bucket -> {
                    output.accept(new ItemStack(bucket));
                });
            })
            .build();
            
        // Enregistrer l'onglet dans le registre de Minecraft
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB_KEY, tab);
        
        Amphora.LOGGER.info("✓ Creative tab registered successfully with Registrate items");
    }
    
    private static void addVineryBuckets(CreativeModeTab.Output output) {
        // Ajouter tous les seaux de raisin depuis ModVineryContent
        if (ModVineryContent.RED_GRAPE_JUICE != null) {
            var bucket = ModVineryContent.RED_GRAPE_JUICE.getSource().getBucket();
            if (bucket != null) output.accept(new ItemStack(bucket));
        }
        if (ModVineryContent.WHITE_GRAPE_JUICE != null) {
            var bucket = ModVineryContent.WHITE_GRAPE_JUICE.getSource().getBucket();
            if (bucket != null) output.accept(new ItemStack(bucket));
        }
        
        // Seaux de raisin de savane
        if (ModVineryContent.RED_SAVANNA_GRAPE_JUICE != null) {
            var bucket = ModVineryContent.RED_SAVANNA_GRAPE_JUICE.getSource().getBucket();
            if (bucket != null) output.accept(new ItemStack(bucket));
        }
        if (ModVineryContent.WHITE_SAVANNA_GRAPE_JUICE != null) {
            var bucket = ModVineryContent.WHITE_SAVANNA_GRAPE_JUICE.getSource().getBucket();
            if (bucket != null) output.accept(new ItemStack(bucket));
        }
        
        // Seaux de raisin de taïga
        if (ModVineryContent.RED_TAIGA_GRAPE_JUICE != null) {
            var bucket = ModVineryContent.RED_TAIGA_GRAPE_JUICE.getSource().getBucket();
            if (bucket != null) output.accept(new ItemStack(bucket));
        }
        if (ModVineryContent.WHITE_TAIGA_GRAPE_JUICE != null) {
            var bucket = ModVineryContent.WHITE_TAIGA_GRAPE_JUICE.getSource().getBucket();
            if (bucket != null) output.accept(new ItemStack(bucket));
        }
        
        // Seaux de raisin de jungle
        if (ModVineryContent.RED_JUNGLE_GRAPE_JUICE != null) {
            var bucket = ModVineryContent.RED_JUNGLE_GRAPE_JUICE.getSource().getBucket();
            if (bucket != null) output.accept(new ItemStack(bucket));
        }
        if (ModVineryContent.WHITE_JUNGLE_GRAPE_JUICE != null) {
            var bucket = ModVineryContent.WHITE_JUNGLE_GRAPE_JUICE.getSource().getBucket();
            if (bucket != null) output.accept(new ItemStack(bucket));
        }
    }
    
    public static ResourceKey<CreativeModeTab> getTabKey() {
        return TAB_KEY;
    }
}
