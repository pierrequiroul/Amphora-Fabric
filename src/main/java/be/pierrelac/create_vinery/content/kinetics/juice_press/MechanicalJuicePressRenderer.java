package be.pierrelac.create_vinery.content.kinetics.juice_press;

import be.pierrelac.create_vinery.ModPartials;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import javax.annotation.Nonnull;

/**
 * Renderer pour la presse à jus mécanique avec animations synchronisées
 * Fixes: Cogwheel oscillation, speed matching with adjacent cog, screw/handle gated by recipe
 */
public class MechanicalJuicePressRenderer extends KineticBlockEntityRenderer<MechanicalJuicePressBlockEntity> {

    // Offsets constants pour le cogwheel dans l'espace local
    private static final float COG_OFF_X = 0f;   // vers l'avant (après facing)
    private static final float COG_OFF_Y = 0f;   // hauteur
    private static final float COG_OFF_Z = 0f;   // latéral

    public MechanicalJuicePressRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(MechanicalJuicePressBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                            MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        Direction facing = blockEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        boolean hasKineticPower = Math.abs(blockEntity.getSpeed()) > 0;

        // 1) SCREW: Only moves when recipe is running
        float screwYOffset = (hasKineticPower && blockEntity.isRunning())
            ? blockEntity.getScrewYOffset(partialTicks)
            : 0f;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing)));
        poseStack.translate(0, screwYOffset, 0);
        poseStack.translate(-0.5, -0.5, -0.5);

        CachedBufferer.partial(ModPartials.JUICE_PRESS_SCREW, blockEntity.getBlockState())
            .light(combinedLight)
            .renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));
        poseStack.popPose();

        // 2) HANDLE: Rotation tied to screw translation via thread pitch
        float handleAngleDeg = (hasKineticPower && blockEntity.isRunning())
            ? blockEntity.getHandleAngleDeg(partialTicks)
            : 0f;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing)));
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(handleAngleDeg));
        poseStack.translate(-0.5, -0.5, -0.5);

        CachedBufferer.partial(ModPartials.JUICE_PRESS_HANDLE, blockEntity.getBlockState())
            .light(combinedLight)
            .renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));
        poseStack.popPose();

        // 3) SHAFTLESS COG: Fixed oscillation and speed matching
        poseStack.pushPose();

        // Decorative cog – continuous angle, axis Y, never reset to 0
        float cogAngleDeg;

        // Prefer the neighbor's kinetic angle so the visual speed matches exactly
        Direction sampleSide = facing; // or the side where your decorative cog is drawn
        BlockPos npos = blockEntity.getBlockPos().relative(sampleSide);
        net.minecraft.world.level.block.entity.BlockEntity nbe = blockEntity.getLevel().getBlockEntity(npos);

        if (nbe instanceof com.simibubi.create.content.kinetics.base.KineticBlockEntity nke) {
            // Use neighbor's continuous kinetic angle around vertical axis
            cogAngleDeg = -getAngleForTe(nke, npos, Direction.Axis.Y);
        } else {
            // Fallback to this TE's kinetic angle
            cogAngleDeg = -getAngleForTe(blockEntity, blockEntity.getBlockPos(), Direction.Axis.Y);
        }

        // Transform order: center → face → offsets → rotateY(angle) → uncenter
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(AngleHelper.horizontalAngle(facing)));
        poseStack.translate(COG_OFF_X, COG_OFF_Y, COG_OFF_Z); // keep 0,0,0 unless you need an offset
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(cogAngleDeg));
        poseStack.translate(-0.5, -0.5, -0.5);

        CachedBufferer.partial(AllPartialModels.SHAFTLESS_COGWHEEL, blockEntity.getBlockState())
            .light(combinedLight)
            .renderInto(poseStack, bufferSource.getBuffer(RenderType.solid()));

        poseStack.popPose();

        // 4) SHAFT: Kinetic connection rendering
        super.renderSafe(blockEntity, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }

    @Override
    public boolean shouldRenderOffScreen(@Nonnull MechanicalJuicePressBlockEntity blockEntity) {
        return false;
    }
}
