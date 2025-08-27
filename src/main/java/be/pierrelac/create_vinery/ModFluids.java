package be.pierrelac.create_vinery;

import com.simibubi.create.AllFluids;
import com.simibubi.create.AllTags;
import com.simibubi.create.AllTags.AllFluidTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributeHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import static net.minecraft.world.item.Items.BUCKET;

import javax.annotation.Nullable;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralise l'enregistrement des fluides "juice" du mod.
 *
 * Cette classe garde seulement la logique de création/stockage des fluides
 * et des métadonnées (couleur). Le code client (rendering) est dans `client`.
 */
public class ModFluids {
    private static final CreateRegistrate REGISTRATE = CreateVinery.registrate();

    public static final FluidEntry<SimpleFlowableFluid.Flowing> JUICE =
		REGISTRATE.standardFluid("juice")
			.lang("Juice")
			.fluidProperties(p -> p.levelDecreasePerBlock(2)
				.tickRate(25)
				.flowSpeed(3)
				.blastResistance(100f))
			.fluidAttributes(() -> new JuiceAttributeHandler("block.create_vinery.juice", 2000, 1400))
			.tag(FluidTags.WATER) // fabric: water tag controls physics
			.source(SimpleFlowableFluid.Source::new) // TODO: remove when Registrate fixes FluidBuilder
			.block()
			.properties(p -> p.mapColor(MapColor.TERRACOTTA_YELLOW))
			.build()
			.bucket()
			.onRegister(ModFluids::registerFluidDispenseBehavior)
			.tag(AllTags.forgeItemTag("juice_buckets"))
			.build()
			.onRegisterAfter(Registries.ITEM, juice -> {
				Fluid source = juice.getSource();
				FluidStorage.combinedItemApiProvider(source.getBucket()).register(context ->
					new FullItemFluidStorage(context, bucket -> ItemVariant.of(BUCKET), FluidVariant.of(source), FluidConstants.BUCKET));
				FluidStorage.combinedItemApiProvider(BUCKET).register(context ->
					new EmptyItemFluidStorage(context, bucket -> ItemVariant.of(source.getBucket()), source, FluidConstants.BUCKET));
			})
			.register();

    private static final DispenseItemBehavior DEFAULT = new DefaultDispenseItemBehavior();
	private static final DispenseItemBehavior DISPENSE_FLUID = new DefaultDispenseItemBehavior(){
			@Override
			protected ItemStack execute(BlockSource pSource, ItemStack pStack) {
				DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) pStack.getItem();
				BlockPos pos = pSource.getPos().relative(pSource.getBlockState().getValue(DispenserBlock.FACING));
				Level level = pSource.getLevel();
				if (dispensibleContainerItem.emptyContents(null, level, pos, null)) {
					return new ItemStack(Items.BUCKET);
				}
				return DEFAULT.dispense(pSource, pStack);
			}
		};

	private static void registerFluidDispenseBehavior(BucketItem bucket) {
		DispenserBlock.registerBehavior(bucket, DISPENSE_FLUID);
	}

    public static final Map<String, FluidEntry<SimpleFlowableFluid.Flowing>> JUICE_FLUIDS = new HashMap<>();
    // Store the display color for each registered juice (0xRRGGBB)
    public static final Map<String, Integer> JUICE_COLORS = new HashMap<>();
    
    // Un seau = 4 bouteilles (comme le miel dans Create)
    public static final long JUICE_BOTTLE_AMOUNT = FluidConstants.BUCKET / 2;

    public static void register() {
        // Enregistrement de tous les types de jus définis dans l'enum
        for (JuiceTypes juiceType : JuiceTypes.values()) {
            registerJuiceFluid(juiceType.getId(), juiceType.getTranslationKey(), juiceType.getColor());
        }
    }

    private record JuiceAttributeHandler(Component name, int viscosity, boolean lighterThanAir) implements FluidVariantAttributeHandler {
        private JuiceAttributeHandler(String translationKey, int viscosity, int density) {
            this(Component.translatable(translationKey), viscosity, density <= 0);
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

    private static void registerJuiceFluid(String id, String translationKey, int color) {
        String fluidId = id + "_juice";
        CreateVinery.LOGGER.info("Registering juice fluid {} with translationKey={} color=0x{}", fluidId, translationKey, Integer.toHexString(color));
        CreateVinery.LOGGER.info("Expected shared fluid textures = create_vinery:fluid/juice_still and create_vinery:fluid/juice_flow");

        // Création du fluide avec Create (utilise une translation key pour le nom)
        FluidEntry<SimpleFlowableFluid.Flowing> entry = REGISTRATE
                .standardFluid(fluidId)
                .lang(translationKey)
                .fluidProperties(props -> props
                    .levelDecreasePerBlock(2)
                    .tickRate(25)
                    .flowSpeed(3)
                    .blastResistance(100f)
                )
                .fluidAttributes(() -> new JuiceAttributeHandler(translationKey, 1500, 1400))
                .source(SimpleFlowableFluid.Source::new)
                .block()
                .properties(p -> p.mapColor(MapColor.COLOR_PURPLE))
                .build()
                .bucket()
                .build()
                .onRegisterAfter(Registries.ITEM, juice -> {
                    Fluid source = juice.getSource();
                    FabricFluidHelpers.registerItemStoragesForJuice(source, source.getBucket(), JUICE_BOTTLE_AMOUNT);
                })
                .register();

        // Stockage du fluide pour référence ultérieure
        JUICE_FLUIDS.put(id, entry);
        // Stocker la couleur pour le rendu client
        JUICE_COLORS.put(id, color & 0xFFFFFF);
    }    public static Fluid getFluid(String id) {
        FluidEntry<SimpleFlowableFluid.Flowing> entry = JUICE_FLUIDS.get(id);
        return entry != null ? entry.getSource() : Fluids.EMPTY;
    }
}
