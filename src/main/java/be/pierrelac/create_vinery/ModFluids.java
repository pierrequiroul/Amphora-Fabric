package be.pierrelac.create_vinery;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;

import java.util.HashMap;
import java.util.Map;

public class ModFluids {
    private static final CreateRegistrate REGISTRATE = CreateVinery.registrate();
    public static final Map<String, FluidEntry<SimpleFlowableFluid.Flowing>> JUICE_FLUIDS = new HashMap<>();
    // Store the display color for each registered juice (0xRRGGBB)
    public static final Map<String, Integer> JUICE_COLORS = new HashMap<>();
    
    // Un seau = 4 bouteilles (comme le miel dans Create)
    public static final long JUICE_BOTTLE_AMOUNT = FluidConstants.BUCKET / 2;

    public static void register() {
        // Enregistrement des fluides de jus
        registerJuiceFluid("red_grape", "Red Grape Juice", 0x8B0000);
        registerJuiceFluid("white_grape", "White Grape Juice", 0xF5F5DC);
        registerJuiceFluid("red_savanna_grape", "Red Savanna Grape Juice", 0x800000);
        registerJuiceFluid("white_savanna_grape", "White Savanna Grape Juice", 0xFFFAF0);
        registerJuiceFluid("red_taiga_grape", "Red Taiga Grape Juice", 0x8B0000);
        registerJuiceFluid("white_taiga_grape", "White Taiga Grape Juice", 0xFFFAF0);
        registerJuiceFluid("red_jungle_grape", "Red Jungle Grape Juice", 0x8B0000);
        registerJuiceFluid("white_jungle_grape", "White Jungle Grape Juice", 0xFFFAF0);
        registerJuiceFluid("apple", "Apple Juice", 0xFFE4B5);
    }

    private record JuiceAttributeHandler(Component name, int viscosity, boolean lighterThanAir) implements FluidVariantAttributeHandler {
        private JuiceAttributeHandler(String name, int viscosity, int density) {
            this(Component.translatable(name), viscosity, density <= 0);
        }

        @Override
        public Component getName(FluidVariant fluidVariant) {
            return name.copy();
        }

        @Override
        public int getViscosity(FluidVariant variant, @Nullable Level world) {
            return viscosity;
        }

        @Override
        public boolean isLighterThanAir(FluidVariant variant) {
            return lighterThanAir;
        }
    }

    private static void registerJuiceFluid(String id, String name, int color) {
        String fluidId = id + "_juice";
        
        // Création du fluide avec Create
        FluidEntry<SimpleFlowableFluid.Flowing> fluid = REGISTRATE
                .standardFluid(fluidId)
                .lang(name)
                .fluidProperties(props -> props
                    .levelDecreasePerBlock(2)
                    .tickRate(25)
                    .flowSpeed(3)
                    .blastResistance(100f)
                )
                .fluidAttributes(() -> new JuiceAttributeHandler(name, 1500, 1400))
                .source(SimpleFlowableFluid.Source::new)
                .block()
                .properties(p -> p.mapColor(MapColor.COLOR_PURPLE))
                .build()
                .bucket()
                .build()
                .onRegisterAfter(Registries.ITEM, juice -> {
                    Fluid source = juice.getSource();
                    // Gérer les bouteilles
                    FluidStorage.combinedItemApiProvider(Items.GLASS_BOTTLE).register(context ->
                        new EmptyItemFluidStorage(context, bottle -> ItemVariant.of(Items.POTION), source, JUICE_BOTTLE_AMOUNT));
                    // Gérer les seaux
                    FluidStorage.combinedItemApiProvider(source.getBucket()).register(context ->
                        new FullItemFluidStorage(context, bucket -> ItemVariant.of(Items.BUCKET), FluidVariant.of(source), FluidConstants.BUCKET));
                    FluidStorage.combinedItemApiProvider(Items.BUCKET).register(context ->
                        new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(source.getBucket()), source, FluidConstants.BUCKET));
                })
                .register();

        // Stockage du fluide pour référence ultérieure
        JUICE_FLUIDS.put(id, fluid);
    // Stocker la couleur pour le rendu client
    JUICE_COLORS.put(id, color & 0xFFFFFF);
    }

    public static Fluid getFluid(String id) {
        FluidEntry<SimpleFlowableFluid.Flowing> entry = JUICE_FLUIDS.get(id);
        return entry != null ? entry.getSource() : Fluids.EMPTY;
    }
}
