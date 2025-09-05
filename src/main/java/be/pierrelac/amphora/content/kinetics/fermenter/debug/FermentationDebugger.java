package be.pierrelac.amphora.content.kinetics.fermenter.debug;

import be.pierrelac.amphora.Amphora;
import be.pierrelac.amphora.content.kinetics.fermenter.FermentationValveBlockEntity;
import be.pierrelac.amphora.content.kinetics.fermenter.scanner.TankGroup;
import be.pierrelac.amphora.content.kinetics.fermenter.processor.QuantumFermentationProcessor;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Système de debug avancé pour la fermentation quantique
 */
public class FermentationDebugger {
    
    private final FermentationValveBlockEntity valve;
    private boolean debugMode = false;
    private long lastDebugTime = 0;
    private static final long DEBUG_INTERVAL = 5000; // 5 secondes
    
    public FermentationDebugger(FermentationValveBlockEntity valve) {
        this.valve = valve;
    }
    
    /**
     * Active/désactive le mode debug
     */
    public void toggleDebugMode(Player player) {
        debugMode = !debugMode;
        String status = debugMode ? "activé" : "désactivé";
        player.displayClientMessage(Component.literal("Debug fermentation " + status), false);
        
        if (debugMode) {
            showDetailedStatus(player);
        }
    }
    
    /**
     * Affiche le statut détaillé au joueur
     */
    public void showDetailedStatus(Player player) {
        List<String> lines = new ArrayList<>();
        
        // Informations générales
        lines.add("=== FERMENTATION VALVE DEBUG ===");
        lines.add("Position: " + valve.getBlockPos());
        lines.add("RPM actuel: " + String.format("%.1f", valve.getCurrentRpm()));
        lines.add("RPM minimum: " + valve.getMinRpmRequired());
        lines.add("Actif: " + valve.isActive());
        
        // Informations sur le groupe de tanks
        TankGroup group = valve.getCurrentTankGroup();
        if (group != null) {
            lines.add("");
            lines.add("=== GROUPE DE TANKS ===");
            lines.add("Nombre de tanks: " + group.getTankCount());
            lines.add("Centre: " + group.getCenter());
            lines.add("Valide: " + group.isValidGroup());
            lines.add("Armé: " + group.isArmed());
            lines.add("Contient du jus: " + group.containsJuice());
            
            // Détails des fluides
            group.getTotalFluids().forEach((fluid, amount) -> {
                lines.add("  " + fluid.toString() + ": " + amount + "mB");
            });
        } else {
            lines.add("");
            lines.add("=== AUCUN GROUPE DÉTECTÉ ===");
        }
        
        // Informations sur le processeur
        QuantumFermentationProcessor processor = valve.getProcessor();
        if (processor != null) {
            lines.add("");
            lines.add("=== PROCESSEUR QUANTIQUE ===");
            lines.add("Actif: " + processor.isActive());
            lines.add("Status: " + processor.getStatusInfo());
            lines.add("Progress: " + processor.getProgress() + "%");
        }
        
        // Envoyer toutes les lignes au joueur
        for (String line : lines) {
            player.displayClientMessage(Component.literal(line), false);
        }
    }
    
    /**
     * Log périodique pour le debug
     */
    public void periodicDebugLog() {
        if (!debugMode) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDebugTime < DEBUG_INTERVAL) return;
        
        lastDebugTime = currentTime;
        
        StringBuilder log = new StringBuilder();
        log.append("Fermentation Debug [").append(valve.getBlockPos()).append("] ");
        log.append("RPM: ").append(String.format("%.1f", valve.getCurrentRpm())).append(" ");
        
        TankGroup group = valve.getCurrentTankGroup();
        if (group != null) {
            log.append("Tanks: ").append(group.getTankCount()).append(" ");
            log.append("Fluids: ").append(group.getTotalFluids().size()).append(" ");
        }
        
        QuantumFermentationProcessor processor = valve.getProcessor();
        if (processor != null) {
            log.append("Active: ").append(processor.isActive()).append(" ");
            log.append("Progress: ").append(processor.getProgress()).append("%");
        }
        
        Amphora.LOGGER.debug(log.toString());
    }
    
    /**
     * Diagnostic des problèmes courants
     */
    public void diagnoseProblems(Player player) {
        List<String> issues = new ArrayList<>();
        
        // Vérifier RPM
        float rpm = valve.getCurrentRpm();
        if (rpm < valve.getMinRpmRequired()) {
            issues.add("⚠ RPM insuffisant: " + String.format("%.1f", rpm) + 
                      " (minimum: " + valve.getMinRpmRequired() + ")");
        }
        
        // Vérifier groupe de tanks
        TankGroup group = valve.getCurrentTankGroup();
        if (group == null) {
            issues.add("⚠ Aucun groupe de tanks détecté");
            issues.add("  Assurez-vous d'avoir des Fluid Tanks connectés");
        } else {
            if (!group.isValidGroup()) {
                issues.add("⚠ Groupe de tanks invalide");
            }
            if (group.getTankCount() < 2) {
                issues.add("⚠ Pas assez de tanks (minimum: 2)");
            }
            if (!group.containsJuice()) {
                issues.add("⚠ Aucun jus fermentable trouvé");
                issues.add("  Ajoutez du jus de fruit dans les tanks");
            }
        }
        
        // Afficher les résultats
        if (issues.isEmpty()) {
            player.displayClientMessage(Component.literal("✓ Aucun problème détecté"), false);
        } else {
            player.displayClientMessage(Component.literal("=== DIAGNOSTIC DES PROBLÈMES ==="), false);
            for (String issue : issues) {
                player.displayClientMessage(Component.literal(issue), false);
            }
        }
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
}
