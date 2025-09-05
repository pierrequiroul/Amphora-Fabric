package be.pierrelac.amphora.content.kinetics.fermenter.recipe;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.resources.ResourceLocation;

/**
 * Représente une recette de fermentation quantique.
 * Définit la transformation d'un jus en vin avec paramètres spécifiques.
 */
public class FermentationRecipe {
    
    private final ResourceLocation id;
    private final Fluid inputFluid;     // Jus d'entrée
    private final Fluid outputFluid;    // Vin de sortie
    private final int baseTime;         // Temps de base en ticks (20 ticks = 1 seconde)
    private final int minRpm;           // RPM minimum requis
    private final int quantumSize;      // Taille des quantums en mB (défaut: 100mB)
    
    public FermentationRecipe(ResourceLocation id, Fluid inputFluid, Fluid outputFluid, 
                             int baseTime, int minRpm, int quantumSize) {
        this.id = id;
        this.inputFluid = inputFluid;
        this.outputFluid = outputFluid;
        this.baseTime = baseTime;
        this.minRpm = minRpm;
        this.quantumSize = quantumSize;
    }
    
    /**
     * Calcule le temps de fermentation réel basé sur les RPM.
     * Plus de RPM = fermentation plus rapide.
     * 
     * @param actualRpm RPM actuels de la valve
     * @return Temps en ticks pour fermenter un quantum
     */
    public int calculateProcessingTime(float actualRpm) {
        if (actualRpm < minRpm) {
            return Integer.MAX_VALUE; // Pas assez de vitesse
        }
        
        // Formule: temps de base divisé par le ratio de vitesse
        float speedRatio = actualRpm / minRpm;
        return Math.max(1, (int) (baseTime / speedRatio));
    }
    
    /**
     * Vérifie si cette recette peut traiter le fluide donné.
     */
    public boolean matches(Fluid fluid) {
        return inputFluid.equals(fluid);
    }
    
    /**
     * Vérifie si les conditions RPM sont remplies.
     */
    public boolean hasValidSpeed(float rpm) {
        return rpm >= minRpm;
    }
    
    // Getters
    public ResourceLocation getId() { return id; }
    public Fluid getInputFluid() { return inputFluid; }
    public Fluid getOutputFluid() { return outputFluid; }
    public int getBaseTime() { return baseTime; }
    public int getMinRpm() { return minRpm; }
    public int getQuantumSize() { return quantumSize; }
    
    @Override
    public String toString() {
        return String.format("FermentationRecipe{%s: %s -> %s, %dt@%drpm, quantum=%dmB}", 
            id, inputFluid, outputFluid, baseTime, minRpm, quantumSize);
    }
}
