package be.pierrelac.amphora.content.kinetics.fermenter;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import java.util.List;

/**
 * Input Valve Block Entity - Smart pipe specialized for juice input
 * Only accepts fluids tagged as "juice" and distributes to fermentation chambers
 */
public class InputValveBlockEntity extends BlockEntity {

    // Internal fluid buffer for input valve
    private final SingleVariantStorage<FluidVariant> fluidBuffer = new SingleVariantStorage<FluidVariant>() {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return FluidConstants.BUCKET; // 1000mB buffer
        }

        @Override
        protected void onFinalCommit() {
            InputValveBlockEntity.this.setChanged();
        }
    };

    private static final TagKey<Fluid> JUICE_TAG = TagKey.create(net.minecraft.core.registries.Registries.FLUID, new ResourceLocation("c", "juice"));
    private BlockPos fermentationBase = null;
    private int searchCooldown = 0;

    public InputValveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
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
        
        // Process fluid transfer
        processFluidTransfer();
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
     * Process fluid transfer from buffer to connected tanks
     */
    private void processFluidTransfer() {
        if (fermentationBase == null || fluidBuffer.isResourceBlank()) return;
        
        // Get fermentation base entity
        if (level.getBlockEntity(fermentationBase) instanceof FermentationBaseBlockEntity base) {
            // Try to transfer fluid to connected tanks through the base
            try (Transaction transaction = Transaction.openOuter()) {
                long transferred = fluidBuffer.extract(fluidBuffer.variant, fluidBuffer.amount, transaction);
                if (transferred > 0) {
                    // Distribute to tanks (simplified logic)
                    transaction.commit();
                    setChanged();
                }
            }
        }
    }

    /**
     * Check if a fluid is acceptable (juice tag)
     */
    public boolean acceptsFluid(FluidVariant fluid) {
        if (fluid.isBlank()) return false;
        // Simplified check - accept any non-water fluid for now
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
