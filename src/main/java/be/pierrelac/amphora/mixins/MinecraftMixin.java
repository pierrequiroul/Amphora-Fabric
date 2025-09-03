package be.pierrelac.amphora.mixins;

import be.pierrelac.amphora.Amphora;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void amphora$init(GameConfig gameConfig, CallbackInfo ci) {
        System.out.println("[Amphora-MIXIN] Mixin was called successfully!");
        Amphora.LOGGER.info("Initializing Amphora integration");
    }
}
