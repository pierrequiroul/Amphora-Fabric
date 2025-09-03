package be.pierrelac.amphora.compat.emi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

import be.pierrelac.amphora.ModBlocks;
import be.pierrelac.amphora.ModBlockAccess;
import be.pierrelac.amphora.ModPartials;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Animations EMI pour Create: Vinery
 * Inspire des animations de Create (CreateEmiAnimations)
 */
public class AmphoraEmiAnimations {

    public static GuiGameElement.GuiRenderBuilder defaultBlockElement(BlockState state) {
        return GuiGameElement.of(state);
    }

    public static float getCurrentAngle() {
        return (AnimationTickHolder.getRenderTime() * 4f) % 360;
    }

    public static BlockState shaft(Axis axis) {
        return AllBlocks.SHAFT.get().defaultBlockState().setValue(BlockStateProperties.AXIS, axis);
    }

    public static GuiGameElement.GuiRenderBuilder blockElement(BlockState state) {
        return defaultBlockElement(state);
    }

    /**
     * Ajoute l'animation de la juice press (inspiré du mixer de Create)
     */
    public static void addJuicePress(WidgetHolder widgets, int x, int y) {
        widgets.addDrawable(x, y, 0, 0, (graphics, mouseX, mouseY, delta) -> {
            renderJuicePress(graphics);
        });
    }

    /**
     * Rend l'animation de la juice press avec le bassin
     */
    public static void renderJuicePress(GuiGraphics graphics) {
        PoseStack matrices = graphics.pose();
        matrices.translate(0, 0, 200);
        matrices.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-15.5f));
        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(22.5f));
        int scale = 23;

        // Bloc principal de la juice press
        blockElement(ModBlockAccess.getMechanicalJuicePress().defaultBlockState())
            .atLocal(0, 0, 0)
            .scale(scale)
            .render(graphics);

        // Animation du shaft interne de la juice press
        GuiGameElement.of(com.simibubi.create.AllPartialModels.SHAFTLESS_COGWHEEL)
            .rotateBlock(0, getCurrentAngle() * 2, 0)
            .atLocal(0, 0.5, 0)
            .scale(scale)
            .render(graphics);

        // Animation de pressage - vis mobile avec mouvement vertical
        float pressAnimation = Mth.sin(AnimationTickHolder.getRenderTime() / 8f) * 0.15f;
        GuiGameElement.of(ModPartials.JUICE_PRESS_SCREW)
            .rotateBlock(0, getCurrentAngle() * 1.5f, 0)
            .atLocal(0, pressAnimation - 0.3f, 0)
            .scale(scale)
            .render(graphics);

        // Poignée rotative
        GuiGameElement.of(ModPartials.JUICE_PRESS_HANDLE)
            .rotateBlock(0, getCurrentAngle() * 2, 0)
            .atLocal(0, -0.5, 0)
            .scale(scale)
            .render(graphics);

        // Bassin en dessous
        blockElement(AllBlocks.BASIN.get().defaultBlockState())
            .atLocal(0, 1.65, 0)
            .scale(scale)
            .render(graphics);
    }
}
