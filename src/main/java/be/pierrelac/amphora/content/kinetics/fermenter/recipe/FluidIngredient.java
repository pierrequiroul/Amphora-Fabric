package be.pierrelac.amphora.content.kinetics.fermenter.recipe;

import net.minecraft.world.level.material.Fluid;

/**
 * Simple representation d'un fluide avec quantité pour Fabric.
 * Alternative à FluidStack pour la compatibilité Fabric.
 */
public class FluidIngredient {
    private final Fluid fluid;
    private final long amount; // En millibuckets
    
    public FluidIngredient(Fluid fluid, long amount) {
        this.fluid = fluid;
        this.amount = amount;
    }
    
    public Fluid getFluid() {
        return fluid;
    }
    
    public long getAmount() {
        return amount;
    }
    
    public FluidIngredient withAmount(long newAmount) {
        return new FluidIngredient(fluid, newAmount);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof FluidIngredient)) return false;
        
        FluidIngredient other = (FluidIngredient) obj;
        return fluid.equals(other.fluid) && amount == other.amount;
    }
    
    @Override
    public int hashCode() {
        return fluid.hashCode() * 31 + Long.hashCode(amount);
    }
    
    @Override
    public String toString() {
        return String.format("%dmB %s", amount, fluid);
    }
}
