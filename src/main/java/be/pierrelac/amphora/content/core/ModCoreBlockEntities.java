package be.pierrelac.amphora.content.core;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.content.kinetics.juice_press.MechanicalJuicePressBlockEntity;
import be.pierrelac.amphora.content.kinetics.fermenter.FermentationValveBlockEntity;
import be.pierrelac.amphora.content.kinetics.fermenter.FermentationBaseBlockEntity;
import be.pierrelac.amphora.content.kinetics.fermenter.InputValveBlockEntity;
import be.pierrelac.amphora.content.kinetics.fermenter.OutputValveBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Gestion de tous les BlockEntityTypes du mod Create: Vinery
 */
public class ModCoreBlockEntities {

    public static BlockEntityType<MechanicalJuicePressBlockEntity> MECHANICAL_JUICE_PRESS;
    public static BlockEntityType<FermentationValveBlockEntity> FERMENTATION_VALVE;
    public static BlockEntityType<FermentationBaseBlockEntity> FERMENTATION_BASE;
    public static BlockEntityType<InputValveBlockEntity> INPUT_VALVE;
    public static BlockEntityType<OutputValveBlockEntity> OUTPUT_VALVE;

    /**
     * Enregistre tous les BlockEntityTypes
     */
    public static void register() {
        Amphora.LOGGER.info("Registering Create: Vinery block entities");

        MECHANICAL_JUICE_PRESS = FabricBlockEntityTypeBuilder.<MechanicalJuicePressBlockEntity>create(
            (pos, state) -> new MechanicalJuicePressBlockEntity(MECHANICAL_JUICE_PRESS, pos, state))
            .addBlocks(ModCoreBlocks.MECHANICAL_JUICE_PRESS.get())
            .build();

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation("amphora", "mechanical_juice_press"),
            MECHANICAL_JUICE_PRESS);

        FERMENTATION_VALVE = FabricBlockEntityTypeBuilder.<FermentationValveBlockEntity>create(
            (pos, state) -> new FermentationValveBlockEntity(FERMENTATION_VALVE, pos, state))
            .addBlocks(ModCoreBlocks.FERMENTATION_VALVE.get())
            .build();

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation("amphora", "fermentation_valve"),
            FERMENTATION_VALVE);

        // Nouveaux block entities pour le système modulaire
        FERMENTATION_BASE = FabricBlockEntityTypeBuilder.<FermentationBaseBlockEntity>create(
            (pos, state) -> new FermentationBaseBlockEntity(FERMENTATION_BASE, pos, state))
            .addBlocks(ModCoreBlocks.FERMENTATION_BASE.get())
            .build();

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation("amphora", "fermentation_base"),
            FERMENTATION_BASE);

        INPUT_VALVE = FabricBlockEntityTypeBuilder.<InputValveBlockEntity>create(
            (pos, state) -> new InputValveBlockEntity(INPUT_VALVE, pos, state))
            .addBlocks(ModCoreBlocks.INPUT_VALVE.get())
            .build();

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation("amphora", "input_valve"),
            INPUT_VALVE);

        OUTPUT_VALVE = FabricBlockEntityTypeBuilder.<OutputValveBlockEntity>create(
            (pos, state) -> new OutputValveBlockEntity(OUTPUT_VALVE, pos, state))
            .addBlocks(ModCoreBlocks.OUTPUT_VALVE.get())
            .build();

        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation("amphora", "output_valve"),
            OUTPUT_VALVE);

        Amphora.LOGGER.info("✓ BlockEntities registered successfully");
    }
}
