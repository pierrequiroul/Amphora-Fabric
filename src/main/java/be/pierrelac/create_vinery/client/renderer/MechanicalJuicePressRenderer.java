package be.pierrelac.create_vinery.client.renderer;

import be.pierrelac.create_vinery.blockentity.MechanicalJuicePressBlockEntity;
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
 * Synchronisé avec l'architecture du MechanicalMixer Create
 */
public class MechanicalJuicePressRenderer extends KineticBlockEntityRenderer<MechanicalJuicePressBlockEntity> {

    public MechanicalJuicePressRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(MechanicalJuicePressBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                            MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        Direction facing = blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        boolean hasKineticPower = Math.abs(blockEntity.getSpeed()) > 0;

        // 1) Vis qui descend avec animation en 3 phases (comme le MechanicalMixer)
        float screwYOffset = hasKineticPower ? blockEntity.getRenderedHeadOffset(partialTicks) : 0;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing)));
        poseStack.translate(0, screwYOffset, 0);
        poseStack.translate(-0.5, -0.5, -0.5);

        CachedBufferer.partial(ModPartials.JUICE_PRESS_SCREW, blockEntity.getBlockState())
            .light(combinedLight)
            .renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));
        poseStack.popPose();

        // 2) Volant/poignée qui tourne avec vitesse variable selon la phase
        float animationSpeed = hasKineticPower ? blockEntity.getAnimationSpeed(partialTicks) : 0;
        float handleAngle = (blockEntity.getLevel().getGameTime() + partialTicks) * animationSpeed * 3f;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing)));
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(handleAngle));
        poseStack.translate(-0.5, -0.5, -0.5);

        CachedBufferer.partial(ModPartials.JUICE_PRESS_HANDLE, blockEntity.getBlockState())
            .light(combinedLight)
            .renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));
        poseStack.popPose();

        // 3) Shaftless cogwheel synchronisée avec la vitesse kinétique réelle
        float cogwheelAngle = hasKineticPower ?
            (blockEntity.getLevel().getGameTime() + partialTicks) * blockEntity.getSpeed() * 0.75f : 0;

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
