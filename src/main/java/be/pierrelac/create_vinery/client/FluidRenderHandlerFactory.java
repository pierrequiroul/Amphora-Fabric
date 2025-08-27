package be.pierrelac.create_vinery.client;

import be.pierrelac.create_vinery.CreateVinery;
 
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import javax.annotation.Nullable;

/**
 * Small factory helpers to create FluidVariantRenderHandler instances.
 */
public final class FluidRenderHandlerFactory {

    private FluidRenderHandlerFactory() {}

    public static FluidVariantRenderHandler colorHandler(int color) {
        final int c = color | 0xFF000000;
        return new FluidVariantRenderHandler() {
            @Override
            public int getColor(FluidVariant fluidVariant, @Nullable BlockAndTintGetter view, @Nullable BlockPos pos) {
                try {
                    CreateVinery.LOGGER.debug("FluidVariantRenderHandler.getColor called for variant={} pos={}", fluidVariant, pos);
                } catch (Throwable ignored) {}
                return c;
            }

            @Override
            public void appendTooltip(FluidVariant fluidVariant, java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag tooltipContext) {
                // no-op
            }
        };
    }
}
