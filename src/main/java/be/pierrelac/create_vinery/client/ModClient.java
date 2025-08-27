package be.pierrelac.create_vinery.client;

import be.pierrelac.create_vinery.ModFluids;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Mod init ordering can cause the client entrypoint to run before ModFluids.register().
        // Delay registration until the juice fluids exist by checking on client ticks.
        final boolean[] done = {false};
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (done[0]) return;
            if (ModFluids.JUICE_FLUIDS.isEmpty()) return;
            ModFluids.JUICE_FLUIDS.forEach((key, entry) -> {
                int color = ModFluids.JUICE_COLORS.getOrDefault(key, 0xFFFFFF) | 0xFF000000;
                FluidVariantRenderHandler handler = new FluidVariantRenderHandler() {
                    @Override
                    public int getColor(FluidVariant fluidVariant, @Nullable BlockAndTintGetter view, @Nullable BlockPos pos) {
                        return color;
                    }

                    @Override
                    public void appendTooltip(FluidVariant fluidVariant, java.util.List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag tooltipContext) {
                        // no-op
                    }
                };
                try {
                    var fluidEntry = entry.get();
                    FluidVariantRendering.register(fluidEntry.getSource(), handler);
                    FluidVariantRendering.register(fluidEntry.getFlowing(), handler);
                } catch (Exception e) {
                    try {
                        FluidVariantRendering.register(entry.getSource(), handler);
                    } catch (Throwable ignored) {}
                }
            });
            done[0] = true;
        });
    }
}
