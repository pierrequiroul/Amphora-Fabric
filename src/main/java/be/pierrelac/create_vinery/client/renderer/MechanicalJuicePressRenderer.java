package be.pierrelac.create_vinery.client.renderer;

import be.pierrelac.create_vinery.content.machines.MechanicalJuicePressBlockEntity;
import be.pierrelac.create_vinery.client.ModPartials;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import javax.annotation.Nonnull;

/**
 * Renderer pour la presse à jus mécanique avec animation de vis et volant
 */
public class MechanicalJuicePressRenderer extends KineticBlockEntityRenderer<MechanicalJuicePressBlockEntity> {

    public MechanicalJuicePressRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(MechanicalJuicePressBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                            MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        Direction facing = blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        // Vérifier s'il y a de la force kinétique
        boolean hasKineticPower = Math.abs(blockEntity.getSpeed()) > 0;

        // 1) Vis qui descend linéairement (seulement si alimentée)
        float screwYOffset = hasKineticPower ? blockEntity.getScrewYOffset(partialTicks) : 0;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing)));
        poseStack.translate(0, screwYOffset, 0);
        poseStack.translate(-0.5, -0.5, -0.5);

        CachedBufferer.partial(ModPartials.JUICE_PRESS_SCREW, blockEntity.getBlockState())
            .light(combinedLight)
            .renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));
        poseStack.popPose();

        // 2) Volant/poignée qui tourne (seulement si alimentée)
        float handleAngle = hasKineticPower ? blockEntity.getHandleAngleDeg(partialTicks) : 0;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing)));
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(handleAngle));
        poseStack.translate(-0.5, -0.5, -0.5);

        CachedBufferer.partial(ModPartials.JUICE_PRESS_HANDLE, blockEntity.getBlockState())
            .light(combinedLight)
            .renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));
        poseStack.popPose();

        // 3) Shaftless cogwheel qui tourne dès qu'il y a de la puissance kinétique
        // La méthode getCogwheelAngleDeg() gère déjà la logique de vitesse en interne
        float cogwheelAngle = blockEntity.getCogwheelAngleDeg(partialTicks);
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing)));
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(cogwheelAngle));
        poseStack.translate(-0.5, -0.5, -0.5);

        CachedBufferer.partial(AllPartialModels.SHAFTLESS_COGWHEEL, blockEntity.getBlockState())
            .light(combinedLight)
            .renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));
        poseStack.popPose();

        // 4) Shaft kinetic pour la connexion aux cogs Create
        super.renderSafe(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }

    @Override
    public boolean shouldRenderOffScreen(@Nonnull MechanicalJuicePressBlockEntity blockEntity) {
        return false;
    }
}
