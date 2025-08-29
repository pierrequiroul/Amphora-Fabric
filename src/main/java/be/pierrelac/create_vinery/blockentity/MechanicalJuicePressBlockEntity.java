package be.pierrelac.create_vinery.blockentity;

import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * BlockEntity pour la presse à jus mécanique avec animation synchronisée
 * Suit l'architecture Create standard: étend BasinOperatingBlockEntity
 */
public class MechanicalJuicePressBlockEntity extends BasinOperatingBlockEntity {
    
    private int progress;        // 0..maxTime (ticks)
    private int maxTime = 160;   // temps de base (sera modulé par RPM)
    
    // Constantes d'animation 
    private static final float STROKE = 6f/16f; // course verticale totale de la vis (6px)
    private static final float PITCH = 2f/16f;  // pas de vis: descente par tour (2px = 1/8 block)
    
    public MechanicalJuicePressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;
        
        float rpm = Math.abs(getSpeed());
        if (rpm <= 0) return;
        
        // Avance proportionnelle à la vitesse (ajuste le 20f pour le feeling)
        int step = Math.max(1, (int)(rpm / 20f));
        if (hasWork()) {
            progress += step;
            if (progress >= maxTime) {
                progress = 0;
                runOneCycle();
            }
            setChanged();
            sendData();
        }
    }
    
    /**
     * Vérifie s'il y a du travail à faire
     */
    private boolean hasWork() {
        // Vérifie si la machine a de la force kinétique et des ingrédients
        return Math.abs(getSpeed()) > 0 && hasIngredients();
    }
    
    /**
     * Vérifie s'il y a des ingrédients à traiter
     */
    private boolean hasIngredients() {
        // Pour l'instant, toujours vrai si le bloc a de la force
        // Plus tard: vérifier présence d'ingrédients et basin de sortie
        return true;
    }
    
    /**
     * Exécute un cycle de pressage
     */
    private void runOneCycle() {
        // Logique de production : consommer 3 fruits, produire 250 mB dans le Basin
        // À implémenter plus tard
    }
    
    /**
     * Progression du cycle actuel (0.0 à 1.0)
     */
    public float getProgress(float partialTicks) {
        return Mth.clamp((progress + partialTicks) / Math.max(1, maxTime), 0f, 1f);
    }
    
    /**
     * Offset Y de la vis (descente linéaire)
     */
    public float getScrewYOffset(float partialTicks) {
        return -getProgress(partialTicks) * STROKE; // descend vers le bas
    }
    
    /**
     * Angle de rotation du volant en degrés (lié à la descente par le pas de vis)
     */
    public float getHandleAngleDeg(float partialTicks) {
        // angle = (descente / pas) * 360°
        float turns = (getProgress(partialTicks) * STROKE) / PITCH;
        return (turns * 360f) % 360f;
    }
    
    /**
     * Angle de rotation de la cogwheel basé sur la vitesse kinétique (comme Create's shaftless cogwheel)
     */
    public float getCogwheelAngleDeg(float partialTicks) {
        if (level == null || !level.isClientSide) return 0f;
        
        // Calcul de l'angle basé sur la vitesse kinétique
        float speed = getSpeed();
        long time = level.getGameTime();
        float angle = (time + partialTicks) * speed * 0.75f; // Facteur d'ajustement pour la vitesse visuelle
        return angle % 360f;
    }
    
    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("Progress", progress);
        compound.putInt("MaxTime", maxTime);
    }
    
    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        progress = compound.getInt("Progress");
        maxTime = compound.getInt("MaxTime");
    }
    
    // ===============================
    // Méthodes abstraites requises par BasinOperatingBlockEntity
    // ===============================
    
    @Override
    protected boolean isRunning() {
        return Math.abs(getSpeed()) > 0 && progress > 0;
    }
    
    @Override
    protected void onBasinRemoved() {
        // Arrêter le processus si le bassin est retiré
        progress = 0;
        setChanged();
        sendData();
    }
    
    @Override
    protected Object getRecipeCacheKey() {
        // Clé unique pour le cache de recettes (exemple basé sur MechanicalMixer)
        return "juice_pressing_recipes";
    }
    
    @Override
    protected <C extends net.minecraft.world.Container> boolean matchStaticFilters(net.minecraft.world.item.crafting.Recipe<C> recipe) {
        // Pour l'instant, aucun filtre statique - accepter toutes les recettes
        // Plus tard: filtrer seulement les recettes de pressage de jus
        return true;
    }
}
