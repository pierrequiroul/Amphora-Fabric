package be.pierrelac.amphora.content.kinetics.fermenter.scanner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import be.pierrelac.amphora.Amphora;

import java.util.*;

/**
 * Représente un groupe de Fluid Tanks connectés pour la fermentation quantique.
 * Version simplifiée pour commencer l'implémentation.
 */
public class TankGroup {
    
    private final Level level;
    private final Set<BlockPos> tankPositions;
    private final BlockPos center;
    private final Map<BlockPos, FluidTankBlockEntity> tankEntities;
    
    // État du groupe
    private boolean isArmed = false;
    private long lastUpdateTime = 0;
    
    public TankGroup(Level level, Set<BlockPos> tankPositions) {
        this.level = level;
        this.tankPositions = new HashSet<>(tankPositions);
        this.center = calculateCenter();
        this.tankEntities = new HashMap<>();
        
        // Cache des entités de tanks
        refreshTankEntities();
    }
    
    /**
     * Calcule le centre géométrique du groupe de tanks.
     */
    private BlockPos calculateCenter() {
        if (tankPositions.isEmpty()) return BlockPos.ZERO;
        
        double avgX = tankPositions.stream().mapToInt(BlockPos::getX).average().orElse(0);
        double avgY = tankPositions.stream().mapToInt(BlockPos::getY).average().orElse(0);
        double avgZ = tankPositions.stream().mapToInt(BlockPos::getZ).average().orElse(0);
        
        return new BlockPos((int) avgX, (int) avgY, (int) avgZ);
    }
    
    /**
     * Actualise le cache des entités de tanks.
     */
    private void refreshTankEntities() {
        tankEntities.clear();
        
        for (BlockPos pos : tankPositions) {
            if (!level.isLoaded(pos)) continue;
            
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FluidTankBlockEntity tank) {
                tankEntities.put(pos, tank);
            }
        }
    }
    
    /**
     * Vérifie si le groupe est encore valide (tous les tanks existent).
     */
    public boolean isValidGroup() {
        if (tankPositions.size() < 2) return false;
        
        refreshTankEntities();
        return tankEntities.size() == tankPositions.size();
    }
    
    /**
     * Obtient tous les fluides contenus dans le groupe (version simplifiée).
     * Pour l'instant, on se base sur le premier tank trouvé.
     */
    public Map<Fluid, Integer> getTotalFluids() {
        Map<Fluid, Integer> fluids = new HashMap<>();
        
        for (FluidTankBlockEntity tank : tankEntities.values()) {
            if (tank.getTankInventory() != null) {
                // Utiliser l'API de Create pour obtenir les fluides
                var fluidStack = tank.getTankInventory().getFluid();
                if (!fluidStack.isEmpty()) {
                    Fluid fluid = fluidStack.getFluid();
                    int amount = (int) fluidStack.getAmount();
                    fluids.merge(fluid, amount, Integer::sum);
                }
            }
        }
        
        return fluids;
    }
    
    /**
     * Vérifie si le groupe contient un type de fluide spécifique.
     */
    public boolean containsFluid(Fluid fluid) {
        return getTotalFluids().containsKey(fluid);
    }
    
    /**
     * Obtient la quantité d'un fluide spécifique dans le groupe.
     */
    public int getFluidAmount(Fluid fluid) {
        return getTotalFluids().getOrDefault(fluid, 0);
    }
    
    /**
     * Vérifie si le groupe contient des jus (pour la fermentation).
     */
    public boolean containsJuice() {
        var fluids = getTotalFluids();
        
        // Pour l'instant, on considère tout fluide non-eau comme potentiellement fermentable
        for (Fluid fluid : fluids.keySet()) {
            if (fluid != Fluids.WATER && fluid != Fluids.EMPTY) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Obtient le premier fluide fermentable trouvé.
     */
    public Fluid getFirstJuice() {
        var fluids = getTotalFluids();
        
        for (Fluid fluid : fluids.keySet()) {
            if (fluid != Fluids.WATER && fluid != Fluids.EMPTY) {
                return fluid;
            }
        }
        
        return Fluids.EMPTY;
    }
    
    // Getters
    public Set<BlockPos> getTankPositions() { return Collections.unmodifiableSet(tankPositions); }
    public BlockPos getCenter() { return center; }
    public int getTankCount() { return tankPositions.size(); }
    public boolean isArmed() { return isArmed; }
    public void setArmed(boolean armed) { this.isArmed = armed; }
    public long getLastUpdateTime() { return lastUpdateTime; }
    public void setLastUpdateTime(long time) { this.lastUpdateTime = time; }
    public Level getLevel() { return level; }
    
    /**
     * Calcule la hauteur du groupe de tanks.
     * @return Différence entre Y maximum et Y minimum + 1
     */
    public int getHeight() {
        if (tankPositions.isEmpty()) return 0;
        
        int minY = tankPositions.stream().mapToInt(BlockPos::getY).min().orElse(0);
        int maxY = tankPositions.stream().mapToInt(BlockPos::getY).max().orElse(0);
        
        return maxY - minY + 1;
    }
    
    /**
     * Draine un fluide via Fabric Transfer API (vrai drainage transactionnel).
     * @param fluid Le fluide à drainer
     * @param amount La quantité en mB à drainer
     * @return La quantité réellement drainée
     */
    public long drainFluid(Fluid fluid, long amount) {
        if (amount <= 0 || fluid == Fluids.EMPTY || !isValidGroup()) {
            return 0;
        }
        
        FluidVariant fluidVariant = FluidVariant.of(fluid);
        long totalDrained = 0;
        
        try (Transaction transaction = Transaction.openOuter()) {
            // Parcourir tous les tanks et drainer le fluide demandé
            for (FluidTankBlockEntity tank : tankEntities.values()) {
                if (amount <= totalDrained) break;
                
                // Obtenir le storage du tank via Fabric Transfer API
                Storage<FluidVariant> storage = FluidStorage.SIDED.find(
                    tank.getLevel(), tank.getBlockPos(), Direction.UP);
                
                if (storage != null) {
                    long toExtract = amount - totalDrained;
                    long extracted = storage.extract(fluidVariant, toExtract, transaction);
                    totalDrained += extracted;
                    
                    if (extracted > 0) {
                        Amphora.LOGGER.debug("Drained {}mB of {} from tank at {}", 
                            extracted, fluid, tank.getBlockPos());
                    }
                }
            }
            
            // Valider la transaction si on a drainé quelque chose
            if (totalDrained > 0) {
                transaction.commit();
                Amphora.LOGGER.info("Successfully drained {}mB of {} from tank group", 
                    totalDrained, fluid);
            }
            
            return totalDrained;
            
        } catch (Exception e) {
            Amphora.LOGGER.error("Error during fluid drain operation", e);
            return 0;
        }
    }
    
    /**
     * Remplit un fluide via Fabric Transfer API (vrai remplissage transactionnel).
     * @param fluid Le fluide à injecter
     * @param amount La quantité en mB à injecter
     * @return La quantité réellement injectée
     */
    public long fillFluid(Fluid fluid, long amount) {
        if (amount <= 0 || fluid == Fluids.EMPTY || !isValidGroup()) {
            return 0;
        }
        
        FluidVariant fluidVariant = FluidVariant.of(fluid);
        long totalFilled = 0;
        
        try (Transaction transaction = Transaction.openOuter()) {
            // Parcourir tous les tanks et injecter le fluide
            for (FluidTankBlockEntity tank : tankEntities.values()) {
                if (amount <= totalFilled) break;
                
                // Obtenir le storage du tank via Fabric Transfer API
                Storage<FluidVariant> storage = FluidStorage.SIDED.find(
                    tank.getLevel(), tank.getBlockPos(), Direction.UP);
                
                if (storage != null) {
                    // Vérifier l'anti-mélange : ne pas injecter dans un tank qui contient déjà un autre fluide
                    boolean canInsert = true;
                    for (StorageView<FluidVariant> view : storage) {
                        if (!view.isResourceBlank() && !view.getResource().equals(fluidVariant)) {
                            canInsert = false;
                            break;
                        }
                    }
                    
                    if (canInsert) {
                        long toInsert = amount - totalFilled;
                        long inserted = storage.insert(fluidVariant, toInsert, transaction);
                        totalFilled += inserted;
                        
                        if (inserted > 0) {
                            Amphora.LOGGER.debug("Filled {}mB of {} into tank at {}", 
                                inserted, fluid, tank.getBlockPos());
                        }
                    }
                }
            }
            
            // Valider la transaction si on a injecté quelque chose
            if (totalFilled > 0) {
                transaction.commit();
                Amphora.LOGGER.info("Successfully filled {}mB of {} into tank group", 
                    totalFilled, fluid);
            }
            
            return totalFilled;
            
        } catch (Exception e) {
            Amphora.LOGGER.error("Error during fluid fill operation", e);
            return 0;
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TankGroup other)) return false;
        return tankPositions.equals(other.tankPositions);
    }
    
    @Override
    public int hashCode() {
        return tankPositions.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format("TankGroup{tanks=%d, center=%s, armed=%s}", 
            tankPositions.size(), center, isArmed);
    }
}
