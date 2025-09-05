package be.pierrelac.amphora.content.kinetics.fermenter.metrics;

import be.pierrelac.amphora.Amphora;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Collecteur de métriques pour la fermentation quantique
 */
public class FermentationMetrics {
    
    private static final FermentationMetrics INSTANCE = new FermentationMetrics();
    
    // Compteurs de performance
    private final AtomicLong totalQuantumsProcessed = new AtomicLong(0);
    private final AtomicLong totalTanksScanned = new AtomicLong(0);
    private final AtomicLong totalGroupsFormed = new AtomicLong(0);
    private final AtomicLong totalRecipeMatches = new AtomicLong(0);
    private final AtomicLong averageProcessingTime = new AtomicLong(0);
    
    // Statistiques par type de fluide
    private final ConcurrentHashMap<String, AtomicLong> fluidProcessingCounts = new ConcurrentHashMap<>();
    
    // Temps de mesure
    private long lastReportTime = System.currentTimeMillis();
    private static final long REPORT_INTERVAL = 60000; // 1 minute
    
    private FermentationMetrics() {}
    
    public static FermentationMetrics getInstance() {
        return INSTANCE;
    }
    
    /**
     * Enregistre le traitement d'un quantum
     */
    public void recordQuantumProcessed(String fluidType, long processingTimeNanos) {
        totalQuantumsProcessed.incrementAndGet();
        
        // Enregistrer par type de fluide
        fluidProcessingCounts.computeIfAbsent(fluidType, k -> new AtomicLong(0)).incrementAndGet();
        
        // Mettre à jour le temps de traitement moyen
        long currentAvg = averageProcessingTime.get();
        long newAvg = (currentAvg + processingTimeNanos / 1_000_000L) / 2; // Convertir en ms
        averageProcessingTime.set(newAvg);
    }
    
    /**
     * Enregistre un scan de tanks
     */
    public void recordTanksScan(int tankCount) {
        totalTanksScanned.addAndGet(tankCount);
    }
    
    /**
     * Enregistre la formation d'un groupe
     */
    public void recordGroupFormed() {
        totalGroupsFormed.incrementAndGet();
    }
    
    /**
     * Enregistre une correspondance de recette
     */
    public void recordRecipeMatch() {
        totalRecipeMatches.incrementAndGet();
    }
    
    /**
     * Rapport périodique des métriques
     */
    public void reportMetrics() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastReportTime < REPORT_INTERVAL) return;
        
        lastReportTime = currentTime;
        
        StringBuilder report = new StringBuilder();
        report.append("=== FERMENTATION METRICS ===\n");
        report.append("Quantums processés: ").append(totalQuantumsProcessed.get()).append("\n");
        report.append("Tanks scannés: ").append(totalTanksScanned.get()).append("\n");
        report.append("Groupes formés: ").append(totalGroupsFormed.get()).append("\n");
        report.append("Recettes correspondantes: ").append(totalRecipeMatches.get()).append("\n");
        report.append("Temps moyen de traitement: ").append(averageProcessingTime.get()).append("ms\n");
        
        if (!fluidProcessingCounts.isEmpty()) {
            report.append("Fluides traités:\n");
            fluidProcessingCounts.forEach((fluid, count) -> 
                report.append("  ").append(fluid).append(": ").append(count.get()).append("\n"));
        }
        
        Amphora.LOGGER.info(report.toString());
    }
    
    /**
     * Réinitialise toutes les métriques
     */
    public void reset() {
        totalQuantumsProcessed.set(0);
        totalTanksScanned.set(0);
        totalGroupsFormed.set(0);
        totalRecipeMatches.set(0);
        averageProcessingTime.set(0);
        fluidProcessingCounts.clear();
        lastReportTime = System.currentTimeMillis();
    }
    
    // Getters pour le debug
    public long getTotalQuantumsProcessed() { return totalQuantumsProcessed.get(); }
    public long getTotalTanksScanned() { return totalTanksScanned.get(); }
    public long getTotalGroupsFormed() { return totalGroupsFormed.get(); }
    public long getTotalRecipeMatches() { return totalRecipeMatches.get(); }
    public long getAverageProcessingTime() { return averageProcessingTime.get(); }
    public ConcurrentHashMap<String, AtomicLong> getFluidProcessingCounts() { return fluidProcessingCounts; }
}
