package be.pierrelac.amphora.content.core;

import be.pierrelac.amphora.AmphoraRegistrate;
import be.pierrelac.amphora.content.vinery.ModVineryContent;

import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import com.tterrag.registrate.util.entry.FluidEntry;
import net.minecraft.world.level.material.Fluid;

import java.util.Map;
import java.util.HashMap;

/**
 * Gestionnaire centralisé des utilitaires pour tous les fluides du mod
 * API unifiée pour accéder aux fluides Core et Vinery
 */
public class ModCoreFluids {
    
    // ===== FLUIDES CORE (toujours disponibles) =====
    public static final FluidEntry<SimpleFlowableFluid.Flowing> APPLE_JUICE = ModCoreContent.APPLE_JUICE;
    public static final FluidEntry<SimpleFlowableFluid.Flowing> CHERRY_JUICE = ModCoreContent.CHERRY_JUICE;
    
    // ===== REGISTRE CENTRALISÉ =====
    private static final Map<String, FluidEntry<SimpleFlowableFluid.Flowing>> ALL_FLUIDS = new HashMap<>();
    
    static {
        // Initialiser le registre centralisé au chargement de la classe
        initializeFluidRegistry();
    }
    
    /**
     * Initialise le registre centralisé de tous les fluides
     */
    private static void initializeFluidRegistry() {
        // Fluides Core (toujours disponibles)
        registerFluid("apple_juice", APPLE_JUICE);
        registerFluid("cherry_juice", CHERRY_JUICE);
        
        // Vins Core (toujours disponibles)
        registerFluid("apple_wine", ModCoreContent.APPLE_WINE);
        registerFluid("cherry_wine", ModCoreContent.CHERRY_WINE);
        
        // Fluides Vinery (conditionnels - via réflexion depuis ModVineryContent)
        registerVineryFluidsIfAvailable();
    }
    
    /**
     * Enregistre les fluides Vinery s'ils sont disponibles
     */
    private static void registerVineryFluidsIfAvailable() {
        // Enregistrer les fluides Vinery en accédant directement à ModVineryContent
        registerFluidIfAvailable("red_grape_juice", ModVineryContent.RED_GRAPE_JUICE);
        registerFluidIfAvailable("white_grape_juice", ModVineryContent.WHITE_GRAPE_JUICE);
        registerFluidIfAvailable("red_savanna_grape_juice", ModVineryContent.RED_SAVANNA_GRAPE_JUICE);
        registerFluidIfAvailable("white_savanna_grape_juice", ModVineryContent.WHITE_SAVANNA_GRAPE_JUICE);
        registerFluidIfAvailable("red_taiga_grape_juice", ModVineryContent.RED_TAIGA_GRAPE_JUICE);
        registerFluidIfAvailable("white_taiga_grape_juice", ModVineryContent.WHITE_TAIGA_GRAPE_JUICE);
        registerFluidIfAvailable("red_jungle_grape_juice", ModVineryContent.RED_JUNGLE_GRAPE_JUICE);
        registerFluidIfAvailable("white_jungle_grape_juice", ModVineryContent.WHITE_JUNGLE_GRAPE_JUICE);
        
        // Enregistrer les vins Vinery
        registerFluidIfAvailable("red_grape_wine", ModVineryContent.RED_GRAPE_WINE);
        registerFluidIfAvailable("white_grape_wine", ModVineryContent.WHITE_GRAPE_WINE);
        registerFluidIfAvailable("red_savanna_grape_wine", ModVineryContent.RED_SAVANNA_GRAPE_WINE);
        registerFluidIfAvailable("white_savanna_grape_wine", ModVineryContent.WHITE_SAVANNA_GRAPE_WINE);
        registerFluidIfAvailable("red_taiga_grape_wine", ModVineryContent.RED_TAIGA_GRAPE_WINE);
        registerFluidIfAvailable("white_taiga_grape_wine", ModVineryContent.WHITE_TAIGA_GRAPE_WINE);
        registerFluidIfAvailable("red_jungle_grape_wine", ModVineryContent.RED_JUNGLE_GRAPE_WINE);
        registerFluidIfAvailable("white_jungle_grape_wine", ModVineryContent.WHITE_JUNGLE_GRAPE_WINE);
    }
    
    /**
     * Enregistre un fluide dans le registre centralisé
     */
    private static void registerFluid(String id, FluidEntry<SimpleFlowableFluid.Flowing> fluid) {
        if (fluid != null) {
            ALL_FLUIDS.put(id, fluid);
        }
    }
    
    /**
     * Enregistre un fluide conditionnel (peut être null)
     */
    private static void registerFluidIfAvailable(String id, FluidEntry<SimpleFlowableFluid.Flowing> fluid) {
        if (fluid != null) {
            ALL_FLUIDS.put(id, fluid);
        }
    }
    
    // ===== API CENTRALISÉE POUR TOUS LES FLUIDES =====
    
    /**
     * Obtient un FluidEntry par son ID (tous types confondus)
     */
    public static FluidEntry<SimpleFlowableFluid.Flowing> getFluidEntry(String fluidId) {
        return ALL_FLUIDS.get(fluidId);
    }
    
    /**
     * Obtient un fluide source par son ID (API unifiée pour tous les fluides)
     */
    public static Fluid getSourceFluid(String fluidId) {
        FluidEntry<SimpleFlowableFluid.Flowing> fluidEntry = getFluidEntry(fluidId);
        return fluidEntry != null ? fluidEntry.getSource() : null;
    }
    
    /**
     * Obtient un fluide en mouvement par son ID (API unifiée pour tous les fluides)
     */
    public static Fluid getFlowingFluid(String fluidId) {
        FluidEntry<SimpleFlowableFluid.Flowing> fluidEntry = getFluidEntry(fluidId);
        return fluidEntry != null ? fluidEntry.get() : null;
    }
    
    /**
     * Obtient la couleur d'un fluide (depuis le système Registrate)
     */
    public static Integer getFluidColor(String fluidId) {
        return AmphoraRegistrate.getRegistrateJuiceColor(fluidId);
    }
    
    /**
     * Vérifie si un fluide existe dans le système (Core ou Vinery)
     */
    public static boolean hasFluid(String fluidId) {
        return ALL_FLUIDS.containsKey(fluidId);
    }
    
    /**
     * Obtient tous les fluides disponibles (Core + Vinery)
     */
    public static Map<String, FluidEntry<SimpleFlowableFluid.Flowing>> getAllAvailableFluids() {
        return new HashMap<>(ALL_FLUIDS);
    }
    
    /**
     * Vérifie si un fluide est un fluide Core (toujours disponible)
     */
    public static boolean isCoreFluid(String fluidId) {
        return fluidId.equals("apple_juice") || fluidId.equals("cherry_juice") ||
               fluidId.equals("apple_wine") || fluidId.equals("cherry_wine");
    }
    
    /**
     * Vérifie si un fluide est un fluide Vinery (conditionnel)
     */
    public static boolean isVineryFluid(String fluidId) {
        return fluidId.contains("grape_juice") || fluidId.contains("grape_wine");
    }
    
    public static void register() {
        AmphoraRegistrate.logModularRegistration("core", "centralized fluid management system");
        
        // Note: L'enregistrement effectif est fait automatiquement par Registrate
        // via les FluidEntry dans ModCoreContent et ModVineryContent
        
        // Log du nombre de fluides disponibles
        int coreFluidCount = 2; // apple_juice, cherry_juice
        int vineryFluidCount = (int) ALL_FLUIDS.keySet().stream()
            .filter(ModCoreFluids::isVineryFluid)
            .count();
    }
    
    /**
     * Obtient tous les fluides Core (toujours disponibles)
     */
    public static Map<String, FluidEntry<SimpleFlowableFluid.Flowing>> getCoreFluidEntries() {
        return ALL_FLUIDS.entrySet().stream()
            .filter(entry -> isCoreFluid(entry.getKey()))
            .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);
    }
    
    /**
     * Obtient tous les fluides Vinery (conditionnels)
     */
    public static Map<String, FluidEntry<SimpleFlowableFluid.Flowing>> getVineryFluidEntries() {
        return ALL_FLUIDS.entrySet().stream()
            .filter(entry -> isVineryFluid(entry.getKey()))
            .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);
    }
    
    /**
     * Obtient le nombre total de fluides disponibles
     */
    public static int getTotalFluidCount() {
        return ALL_FLUIDS.size();
    }
}
