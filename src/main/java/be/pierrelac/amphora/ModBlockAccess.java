package be.pierrelac.amphora;

import be.pierrelac.amphora.core.registration.modules.CreateRegistrationModule;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * Safe access to modular blocks without triggering static initialization conflicts
 */
public class ModBlockAccess {
    
    /**
     * Safe access to mechanical juice press block
     */
    public static Block getMechanicalJuicePress() {
        return CreateRegistrationModule.getMechanicalJuicePress();
    }
    
    /**
     * Safe access to mechanical juice press item
     */
    public static Item getMechanicalJuicePressItem() {
        return CreateRegistrationModule.getMechanicalJuicePressItem();
    }
    
    /**
     * Safe access to mechanical juice press block entity type
     */
    public static BlockEntityType<?> getMechanicalJuicePressEntity() {
        return CreateRegistrationModule.getMechanicalJuicePressEntity();
    }
}
