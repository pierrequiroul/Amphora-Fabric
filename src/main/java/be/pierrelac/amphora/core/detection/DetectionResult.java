package be.pierrelac.amphora.core.detection;

/**
 * Résultat de la détection d'un mod avec plusieurs niveaux de validation
 */
public class DetectionResult {
    public final String modId;
    public boolean modPresent = false;
    public boolean classesAvailable = false;
    public boolean featuresWorking = false;
    public boolean versionCompatible = false;
    public String version = "Unknown";
    public String statusMessage = "";
    public Exception lastError = null;
    
    public DetectionResult(String modId) {
        this.modId = modId;
    }
    
    /**
     * Vérifie si le mod est entièrement fonctionnel
     */
    public boolean isFullyFunctional() {
        return modPresent && classesAvailable && featuresWorking && versionCompatible;
    }
    
    /**
     * Vérifie si le mod est au moins basiquement utilisable
     */
    public boolean isBasicallyUsable() {
        return modPresent && classesAvailable;
    }
    
    /**
     * Retourne un score de compatibilité (0-100)
     */
    public int getCompatibilityScore() {
        int score = 0;
        if (modPresent) score += 25;
        if (classesAvailable) score += 25;
        if (featuresWorking) score += 25;
        if (versionCompatible) score += 25;
        return score;
    }
    
    /**
     * Génère un message de statut descriptif
     */
    public String getDetailedStatus() {
        if (!modPresent) return "Not loaded";
        if (!classesAvailable) return "Classes inaccessible";
        if (!featuresWorking) return "Features non-functional";
        if (!versionCompatible) return "Version incompatible";
        return "Fully functional";
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s (score: %d%%)", 
            modId, getDetailedStatus(), getCompatibilityScore());
    }
}
