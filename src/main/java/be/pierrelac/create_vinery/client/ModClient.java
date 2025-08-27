package be.pierrelac.create_vinery.client;

import be.pierrelac.create_vinery.ModFluids;
import be.pierrelac.create_vinery.CreateVinery;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import be.pierrelac.create_vinery.client.FluidRenderHandlerFactory;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

// imports cleaned

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
                int color = ModFluids.JUICE_COLORS.getOrDefault(key, 0xFFFFFF);
                var handler = FluidRenderHandlerFactory.colorHandler(color);
                try {
                    var fluidEntry = entry.get();
                    // Log the expected texture locations and check they exist in resources (best-effort via getResource)
                    try {
                        var stillLoc = new net.minecraft.resources.ResourceLocation("create_vinery", "fluid/juice_still");
                        var flowLoc = new net.minecraft.resources.ResourceLocation("create_vinery", "fluid/juice_flow");
                        var mgr = net.minecraft.client.Minecraft.getInstance().getResourceManager();
                        var r1 = mgr.getResource(stillLoc);
                        var r2 = mgr.getResource(flowLoc);
                        CreateVinery.LOGGER.info("Found textures for {}: still={}, flow={}", key, r1 != null, r2 != null);
                    } catch (Exception texEx) {
                        CreateVinery.LOGGER.warn("Could not verify textures for {}: {}", key, texEx.toString());
                    }

                    FluidVariantRendering.register(fluidEntry.getSource(), handler);
                    FluidVariantRendering.register(fluidEntry.getFlowing(), handler);
                    CreateVinery.LOGGER.info("Registered fluid render handler for {} (source={}, flowing={}) with color=0x{}", key, fluidEntry.getSource(), fluidEntry.getFlowing(), Integer.toHexString(color));
                } catch (Exception e) {
                    try {
                        FluidVariantRendering.register(entry.getSource(), handler);
                        CreateVinery.LOGGER.info("Registered fluid render handler for {} (entry source) with color=0x{}", key, Integer.toHexString(color));
                    } catch (Throwable ignored) {}
                }
            });
            done[0] = true;
        });
    }
}
