package be.pierrelac.create_vinery;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModCreativeTab {
    private static CreativeModeTab TAB;

    public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation(CreateVinery.ID, "main"));

    public static void register() {
        // Créer et enregistrer l'onglet créatif
        TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            TAB_KEY,
            FabricItemGroup.builder()
                .icon(() -> new ItemStack(Items.GLASS_BOTTLE))
                .title(Component.translatable("itemGroup." + CreateVinery.ID + ".main"))
                .displayItems((context, entries) -> {
                    // Ajouter les seaux de fluides au tab créatif
                    ModFluids.JUICE_FLUIDS.values().forEach(fluidEntry -> {
                        if (fluidEntry != null && fluidEntry.get() != null) {
                            entries.accept(fluidEntry.get().getBucket());
                        }
                    });
                })
                .build());

        CreateVinery.LOGGER.info("Registered Create: Vinery creative tab");
    }

    public static ResourceKey<CreativeModeTab> getTabKey() {
        return TAB_KEY;
    }

    public static CreativeModeTab getTab() {
        return TAB;
    }
}
