package be.pierrelac.create_vinery.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import be.pierrelac.create_vinery.CreateVinery;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void create_vinery$init(GameConfig gameConfig, CallbackInfo ci) {
        CreateVinery.LOGGER.info("Initializing Create: Vinery integration");
    }
}
