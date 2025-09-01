package be.pierrelac.create_vinery.content.kinetics.juice_press;

import be.pierrelac.create_vinery.ModPartials;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

/**
 * Renderer pour la presse à jus mécanique - Architecture alignée sur MechanicalMixerRenderer
 */
public class MechanicalJuicePressRenderer extends KineticBlockEntityRenderer<MechanicalJuicePressBlockEntity> {
	private static final boolean debugRender = false;
    public MechanicalJuicePressRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(MechanicalJuicePressBlockEntity be) {
        return true;
    }

    @Override
    protected void renderSafe(MechanicalJuicePressBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {

        // Debug logging
		if(debugRender) System.out.println("[JuicePress Renderer] Starting render...");

        // Temporairement désactiver la vérification Flywheel pour débugger
        // if (com.jozufozu.flywheel.backend.Backend.isOn()) return;

        BlockState blockState = be.getBlockState();
        VertexConsumer vb = buffer.getBuffer(RenderType.solid());

        try {
            // 1. Rendu du SHAFTLESS_COGWHEEL principal (comme le mixer)
			if(debugRender) System.out.println("[JuicePress] Rendering SHAFTLESS_COGWHEEL...");
            SuperByteBuffer cogwheelBuffer = CachedBufferer.partial(AllPartialModels.SHAFTLESS_COGWHEEL, blockState);
            standardKineticRotationTransform(cogwheelBuffer, be, light).renderInto(ms, vb);
			if(debugRender) System.out.println("[JuicePress] SHAFTLESS_COGWHEEL rendered successfully");

            // 2. Variables d'animation
            float screwOffset = be.getRenderedScrewOffset(partialTicks);
            float handleSpeed = be.getRenderedHandleRotationSpeed(partialTicks);
			if(debugRender) System.out.println("[JuicePress] Animation vars - screwOffset: " + screwOffset + ", handleSpeed: " + handleSpeed);

            // 3. Rendu de la vis mobile (équivalent au MIXER_POLE)
			if(debugRender) System.out.println("[JuicePress] Rendering SCREW...");
            SuperByteBuffer screwRender = CachedBufferer.partial(ModPartials.JUICE_PRESS_SCREW, blockState);
            screwRender.translate(0, -screwOffset, 0)
                    .light(light)
                    .renderInto(ms, vb);
			if(debugRender) System.out.println("[JuicePress] SCREW rendered successfully");

            // 4. Rendu de la poignée rotative (écrou qui tourne seulement pendant le pressage)
			if(debugRender) System.out.println("[JuicePress] Rendering HANDLE...");

            // Animation du handle : tourne seulement quand la vis descend (pendant une recette)
            float handleAngle = 0f;
            if (be.isRunning()) {
                // L'écrou tourne proportionnellement au mouvement de la vis
                // Plus la vis descend, plus l'écrou tourne
                float screwProgress = screwOffset / (7f / 16f); // Normaliser entre 0 et 1
                handleAngle = screwProgress * 360f * 2f; // 2 tours complets sur la course
            }

            if(debugRender) System.out.println("[JuicePress] Handle angle: " + handleAngle + " (running: " + be.isRunning() + ")");

            // Utiliser RenderType.solid() pour le handle
            SuperByteBuffer handleRender = CachedBufferer.partial(ModPartials.JUICE_PRESS_HANDLE, blockState);
            handleRender.rotateCentered(Direction.UP, (float) Math.toRadians(handleAngle))
                    // .translate(0, -screwOffset, 0) // Le handle reste en position fixe
                    .light(light)
                    .renderInto(ms, vb);
			if(debugRender) System.out.println("[JuicePress] HANDLE rendered successfully");

        } catch (Exception e) {
            System.err.println("[JuicePress] Error during rendering: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
