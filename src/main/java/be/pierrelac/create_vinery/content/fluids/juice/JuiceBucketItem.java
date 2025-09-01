package be.pierrelac.create_vinery.content.fluids.juice;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;

/**
 * Seau de jus personnalisé qui peut être teinté selon la couleur du fluide
 */
public class JuiceBucketItem extends BucketItem {
    private final int color;

    public JuiceBucketItem(Fluid fluid, int color, Properties properties) {
        super(fluid, properties);
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
