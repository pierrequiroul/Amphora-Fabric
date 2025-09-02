package be.pierrelac.create_vinery.mixins;

import be.pierrelac.create_vinery.CreateVinery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void create_vinery$init(GameConfig gameConfig, CallbackInfo ci) {
        System.out.println("[CREATE_VINERY-MIXIN] Mixin was called successfully!");
        CreateVinery.LOGGER.info("Initializing Create: Vinery integration");
    }
}
