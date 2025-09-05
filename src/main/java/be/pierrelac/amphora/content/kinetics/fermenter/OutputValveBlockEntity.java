package be.pierrelac.amphora.content.kinetics.fermenter;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

/**
 * Output Valve Block Entity - Smart pipe specialized for wine output
 * Only outputs fluids tagged as "wine" and extracts from fermentation chambers
 */
public class OutputValveBlockEntity extends BlockEntity {

    // Internal fluid buffer for output valve
    private final SingleVariantStorage<FluidVariant> fluidBuffer = new SingleVariantStorage<FluidVariant>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return FluidConstants.BUCKET * 2; // 2000mB buffer (larger for output)
        }

        @Override
        protected void onFinalCommit() {
            OutputValveBlockEntity.this.setChanged();
        }
    };

    private static final TagKey<Fluid> WINE_TAG = TagKey.create(net.minecraft.core.registries.Registries.FLUID, new ResourceLocation("c", "wine"));
    private BlockPos fermentationBase = null;
    private int searchCooldown = 0;
    private int extractCooldown = 0;

    public OutputValveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;
        
        // Search for fermentation base every 40 ticks (2 seconds)
        if (searchCooldown <= 0) {
            findFermentationBase();
            searchCooldown = 40;
        } else {
            searchCooldown--;
        }
        
        // Extract fluids from connected tanks every 10 ticks (0.5 seconds)
        if (extractCooldown <= 0) {
            extractFromTanks();
            extractCooldown = 10;
        } else {
            extractCooldown--;
        }
    }

    /**
     * Find nearby fermentation base to connect to
     */
    private void findFermentationBase() {
        fermentationBase = null;
        
        // Search in 5x5x5 area around valve
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos pos = worldPosition.offset(x, y, z);
                    if (level.getBlockState(pos).getBlock() instanceof FermentationBaseBlock) {
                        fermentationBase = pos;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Extract processed fluids from connected tanks
     */
    private void extractFromTanks() {
        if (fermentationBase == null || !fluidBuffer.variant.isBlank()) return;
        
        // Get fermentation base entity
        if (level.getBlockEntity(fermentationBase) instanceof FermentationBaseBlockEntity base) {
            // Try to extract fluid from connected tanks through the base
            // This would interface with Create's fluid tank system
            // For now, simplified logic
            if (base.isActive() && base.isProcessing()) {
                // Extract wine from completed fermentation
                // This is placeholder logic - would need proper tank integration
            }
        }
    }

    /**
     * Check if a fluid can be output (wine tag)
     */
    public boolean canOutputFluid(FluidVariant fluid) {
        if (fluid.isBlank()) return false;
        // Simplified check - output any non-water fluid for now
        return !fluid.getFluid().isSame(net.minecraft.world.level.material.Fluids.WATER);
    }

    /**
     * Get fluid storage for external access
     */
    public Storage<FluidVariant> getFluidStorage(Direction direction) {
        return fluidBuffer;
    }

    /**
     * Get current fluid amount in buffer
     */
    public long getFluidAmount() {
        return fluidBuffer.amount;
    }

    /**
     * Get current fluid type in buffer
     */
    public FluidVariant getFluidType() {
        return fluidBuffer.variant;
    }

    /**
     * Invalidate connections when valve is removed
     */
    public void invalidateConnections() {
        fermentationBase = null;
    }

    /**
     * Add processed fluid to buffer
     */
    public long addFluid(FluidVariant fluid, long amount) {
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = fluidBuffer.insert(fluid, amount, transaction);
            if (inserted > 0) {
                transaction.commit();
                setChanged();
                return inserted;
            }
        }
        return 0;
    }

    /**
     * Extract fluid from buffer for external pipes
     */
    public long extractFluid(FluidVariant fluid, long maxAmount) {
        try (Transaction transaction = Transaction.openOuter()) {
            long extracted = fluidBuffer.extract(fluid, maxAmount, transaction);
            if (extracted > 0) {
                transaction.commit();
                setChanged();
                return extracted;
            }
        }
        return 0;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        fluidBuffer.variant = FluidVariant.fromNbt(tag.getCompound("FluidBuffer"));
        fluidBuffer.amount = tag.getLong("FluidAmount");
        
        if (tag.contains("FermentationBase")) {
            CompoundTag baseTag = tag.getCompound("FermentationBase");
            fermentationBase = new BlockPos(
                baseTag.getInt("x"),
                baseTag.getInt("y"),
                baseTag.getInt("z")
            );
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("FluidBuffer", fluidBuffer.variant.toNbt());
        tag.putLong("FluidAmount", fluidBuffer.amount);
        
        if (fermentationBase != null) {
            CompoundTag baseTag = new CompoundTag();
            baseTag.putInt("x", fermentationBase.getX());
            baseTag.putInt("y", fermentationBase.getY());
            baseTag.putInt("z", fermentationBase.getZ());
            tag.put("FermentationBase", baseTag);
        }
    }
}
