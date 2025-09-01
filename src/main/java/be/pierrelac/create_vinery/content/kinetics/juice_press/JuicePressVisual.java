package be.pierrelac.create_vinery.content.kinetics.juice_press;

import be.pierrelac.create_vinery.ModPartials;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.mojang.math.Axis;
import org.joml.Quaternionf;

/**
 * Visual pour la presse à jus mécanique - Compatible avec l'ancienne API Flywheel
 */
public class JuicePressVisual extends BlockEntityInstance<MechanicalJuicePressBlockEntity> implements DynamicInstance {

    private final OrientedData handleInstance;
    private final OrientedData screwInstance;
    private final MechanicalJuicePressBlockEntity juicePress;

    public JuicePressVisual(MaterialManager materialManager, MechanicalJuicePressBlockEntity blockEntity) {
        super(materialManager, blockEntity);
        this.juicePress = blockEntity;

        // Instance pour la poignée rotative
        handleInstance = materialManager.defaultSolid()
            .material(com.jozufozu.flywheel.core.Materials.ORIENTED)
            .getModel(ModPartials.JUICE_PRESS_HANDLE, blockState)
            .createInstance();

        // Instance pour la vis mobile
        screwInstance = materialManager.defaultSolid()
            .material(com.jozufozu.flywheel.core.Materials.ORIENTED)
            .getModel(ModPartials.JUICE_PRESS_SCREW, blockState)
            .createInstance();

        animate();
    }

    @Override
    public void beginFrame() {
        animate();
    }

    private void animate() {
        float pt = AnimationTickHolder.getPartialTicks();
        float renderedScrewOffset = juicePress.getRenderedScrewOffset(pt);
        float speed = juicePress.getSpeed(); // Vitesse kinétique de base

        transformScrew(renderedScrewOffset);
        transformHandle(speed); // Le handle ne doit pas osciller avec le screw
    }

    private void transformHandle(float speed) {
        // Rotation continue de la poignée basée SEULEMENT sur la vitesse kinétique
        // Pas d'oscillation, juste rotation constante
        float angle = (System.currentTimeMillis() / 50.0f) * speed * 0.1f; // Rotation fluide
        Quaternionf rotation = Axis.YP.rotationDegrees(angle);

        handleInstance.setPosition(getInstancePosition())
            .setRotation(rotation); // Pas de nudge vertical pour le handle
    }

    private void transformScrew(float renderedScrewOffset) {
        screwInstance.setPosition(getInstancePosition())
            .nudge(0, -renderedScrewOffset, 0);
    }

    @Override
    public void updateLight() {
        relight(pos, handleInstance);
        relight(pos, screwInstance);
    }

    @Override
    public void remove() {
        handleInstance.delete();
        screwInstance.delete();
    }
}
