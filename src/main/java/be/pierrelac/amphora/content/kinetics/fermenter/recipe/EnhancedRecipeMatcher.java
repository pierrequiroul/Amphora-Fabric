package be.pierrelac.amphora.content.kinetics.fermenter.recipe;

import be.pierrelac.amphora.Amphora;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced recipe matcher avec expansion d'ingrédients et matching sophistiqué.
 * Supporte les tags, wildcards, correspondances complexes et recettes JSON.
 */
public class EnhancedRecipeMatcher {
    
    private static final EnhancedRecipeMatcher INSTANCE = new EnhancedRecipeMatcher();
    
    // Cache multi-niveaux pour optimiser les lookups
    private final Map<Fluid, FermentationRecipe> directFluidCache = new ConcurrentHashMap<>();
    private final Map<TagKey<Fluid>, List<FermentationRecipe>> tagCache = new ConcurrentHashMap<>();
    private final Map<String, List<FermentationRecipe>> wildcardCache = new ConcurrentHashMap<>();
    
    // Recettes avec différents types d'ingrédients
    private final List<DirectFluidRecipe> directRecipes = new ArrayList<>();
    private final List<TagBasedRecipe> tagRecipes = new ArrayList<>();
    private final List<WildcardRecipe> wildcardRecipes = new ArrayList<>();
    
    // Intégration JSON
    private final JsonRecipeManager jsonManager = JsonRecipeManager.getInstance();
    
    private boolean initialized = false;
    
    private EnhancedRecipeMatcher() {}
    
    public static EnhancedRecipeMatcher getInstance() {
        return INSTANCE;
    }
    
    /**
     * Initialise le système avec les recettes par défaut et enhanced matching.
     */
    public void initialize() {
        initialize(null);
    }
    
    /**
     * Initialise le système avec level pour charger les recettes JSON.
     */
    public void initialize(Level level) {
        if (initialized) return;
        
        // Charger les recettes JSON en priorité si level disponible
        if (level != null) {
            jsonManager.initialize(level);
            loadJsonRecipes();
        }
        
        // Charger les recettes hardcodées en fallback
        loadEnhancedRecipes();
        buildCaches();
        
        initialized = true;
        
        int totalRecipes = directRecipes.size() + tagRecipes.size() + wildcardRecipes.size();
        Amphora.LOGGER.info("Enhanced recipe matcher initialized with {} JSON + {} direct + {} tag-based + {} wildcard recipes",
            jsonManager.getRecipeCount(), directRecipes.size(), tagRecipes.size(), wildcardRecipes.size());
    }
    
    /**
     * Charge les recettes JSON dans le cache direct.
     */
    private void loadJsonRecipes() {
        List<FermentationRecipe> jsonRecipes = jsonManager.getAllLegacyRecipes();
        for (FermentationRecipe recipe : jsonRecipes) {
            directFluidCache.put(recipe.getInputFluid(), recipe);
        }
        
        Amphora.LOGGER.debug("Loaded {} JSON recipes into enhanced matcher", jsonRecipes.size());
    }
    
    /**
     * Charge les recettes avec expansion d'ingrédients sophistiquée.
     */
    private void loadEnhancedRecipes() {
        // Recettes directes (exactes)
        addDirectRecipe("vinery:apple_juice", "vinery:apple_wine", 200, 16, 100);
        addDirectRecipe("vinery:cherry_juice", "vinery:cherry_wine", 250, 18, 100);
        addDirectRecipe("vinery:red_grape_juice", "vinery:red_wine", 300, 20, 100);
        addDirectRecipe("vinery:white_grape_juice", "vinery:white_wine", 280, 18, 100);
        
        // Recettes basées sur les tags (expansion automatique)
        addTagRecipe("c:grape_juices", "c:wines", 280, 18, 100, "grape_wine_fermentation");
        addTagRecipe("c:fruit_juices", "c:fruit_wines", 250, 16, 100, "fruit_wine_fermentation");
        
        // Recettes avec wildcards (matching par pattern)
        addWildcardRecipe("*:*_juice", "*:*_wine", 200, 16, 100, "generic_juice_fermentation");
        addWildcardRecipe("vinery:*_juice", "vinery:*_wine", 250, 18, 100, "vinery_juice_fermentation");
        
        Amphora.LOGGER.debug("Loaded enhanced recipes with ingredient expansion");
    }
    
    /**
     * Ajoute une recette directe (fluide exact).
     */
    private void addDirectRecipe(String inputFluid, String outputFluid, int time, int minRpm, int quantumSize) {
        Fluid input = getFluidByName(inputFluid);
        Fluid output = getFluidByName(outputFluid);
        
        if (input != null && output != null) {
            DirectFluidRecipe recipe = new DirectFluidRecipe(
                new ResourceLocation("amphora", inputFluid.replace(":", "_") + "_fermentation"),
                input, output, time, minRpm, quantumSize
            );
            directRecipes.add(recipe);
            
            Amphora.LOGGER.debug("Added direct recipe: {} -> {}", inputFluid, outputFluid);
        } else {
            Amphora.LOGGER.warn("Failed to add direct recipe: {} -> {} (fluids not found)", inputFluid, outputFluid);
        }
    }
    
    /**
     * Ajoute une recette basée sur les tags.
     */
    private void addTagRecipe(String inputTag, String outputTag, int time, int minRpm, int quantumSize, String recipeId) {
        try {
            TagKey<Fluid> inputTagKey = TagKey.create(Registries.FLUID, new ResourceLocation(inputTag));
            TagKey<Fluid> outputTagKey = TagKey.create(Registries.FLUID, new ResourceLocation(outputTag));
            
            TagBasedRecipe recipe = new TagBasedRecipe(
                new ResourceLocation("amphora", recipeId),
                inputTagKey, outputTagKey, time, minRpm, quantumSize
            );
            tagRecipes.add(recipe);
            
            Amphora.LOGGER.debug("Added tag-based recipe: {} -> {}", inputTag, outputTag);
        } catch (Exception e) {
            Amphora.LOGGER.error("Failed to add tag recipe: {} -> {}", inputTag, outputTag, e);
        }
    }
    
    /**
     * Ajoute une recette avec wildcard pattern.
     */
    private void addWildcardRecipe(String inputPattern, String outputPattern, int time, int minRpm, int quantumSize, String recipeId) {
        WildcardRecipe recipe = new WildcardRecipe(
            new ResourceLocation("amphora", recipeId),
            inputPattern, outputPattern, time, minRpm, quantumSize
        );
        wildcardRecipes.add(recipe);
        
        Amphora.LOGGER.debug("Added wildcard recipe: {} -> {}", inputPattern, outputPattern);
    }
    
    /**
     * Construit les caches pour optimiser les lookups.
     */
    private void buildCaches() {
        // Cache direct fluids
        for (DirectFluidRecipe recipe : directRecipes) {
            directFluidCache.put(recipe.getInputFluid(), recipe);
        }
        
        // Cache tag-based recipes - expansion des tags
        for (TagBasedRecipe recipe : tagRecipes) {
            List<Fluid> inputFluids = expandTag(recipe.getInputTag());
            List<Fluid> outputFluids = expandTag(recipe.getOutputTag());
            
            for (int i = 0; i < inputFluids.size() && i < outputFluids.size(); i++) {
                Fluid input = inputFluids.get(i);
                Fluid output = outputFluids.get(i);
                
                FermentationRecipe expandedRecipe = new FermentationRecipe(
                    recipe.getId(), input, output, recipe.getProcessingTime(), 
                    recipe.getMinRpm(), recipe.getQuantumSize()
                );
                
                directFluidCache.put(input, expandedRecipe);
            }
        }
        
        // Cache wildcard recipes - sera résolu à la demande
        Amphora.LOGGER.debug("Built recipe caches with {} direct entries", directFluidCache.size());
    }
    
    /**
     * Expand tag pour obtenir tous les fluides correspondants.
     */
    private List<Fluid> expandTag(TagKey<Fluid> tag) {
        List<Fluid> fluids = new ArrayList<>();
        
        for (var holder : BuiltInRegistries.FLUID.getTagOrEmpty(tag)) {
            fluids.add(holder.value());
        }
        
        Amphora.LOGGER.debug("Expanded tag {} to {} fluids", tag.location(), fluids.size());
        return fluids;
    }
    
    /**
     * Trouve une recette pour un fluide avec matching sophistiqué.
     */
    public FermentationRecipe findRecipe(Fluid inputFluid) {
        if (!initialized) {
            initialize();
        }
        
        // 1. Lookup direct dans le cache
        FermentationRecipe cached = directFluidCache.get(inputFluid);
        if (cached != null) {
            Amphora.LOGGER.debug("Found cached recipe for {}", getFluidName(inputFluid));
            return cached;
        }
        
        // 2. Essayer les wildcards dynamiquement
        String fluidName = getFluidName(inputFluid);
        for (WildcardRecipe wildcardRecipe : wildcardRecipes) {
            if (matchesWildcard(fluidName, wildcardRecipe.getInputPattern())) {
                Fluid outputFluid = resolveWildcardOutput(fluidName, wildcardRecipe);
                if (outputFluid != null) {
                    FermentationRecipe recipe = new FermentationRecipe(
                        wildcardRecipe.getId(), inputFluid, outputFluid,
                        wildcardRecipe.getProcessingTime(), wildcardRecipe.getMinRpm(), wildcardRecipe.getQuantumSize()
                    );
                    
                    // Cache pour les prochaines fois
                    directFluidCache.put(inputFluid, recipe);
                    
                    Amphora.LOGGER.debug("Found wildcard recipe for {}: {} -> {}", 
                        fluidName, wildcardRecipe.getInputPattern(), wildcardRecipe.getOutputPattern());
                    return recipe;
                }
            }
        }
        
        Amphora.LOGGER.debug("No recipe found for fluid: {}", fluidName);
        return null;
    }
    
    /**
     * Vérifie si un nom de fluide correspond à un pattern wildcard.
     */
    private boolean matchesWildcard(String fluidName, String pattern) {
        // Convertir pattern en regex
        String regex = pattern.replace("*", "([^:]+)");
        return fluidName.matches(regex);
    }
    
    /**
     * Résout le fluide de sortie basé sur le wildcard pattern.
     */
    private Fluid resolveWildcardOutput(String inputName, WildcardRecipe recipe) {
        String inputPattern = recipe.getInputPattern();
        String outputPattern = recipe.getOutputPattern();
        
        // Extraire les parties variables du nom d'entrée
        String regex = inputPattern.replace("*", "([^:]+)");
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(inputName);
        
        if (matcher.matches()) {
            String outputName = outputPattern;
            
            // Remplacer chaque * par la partie correspondante
            for (int i = 1; i <= matcher.groupCount(); i++) {
                outputName = outputName.replaceFirst("\\*", matcher.group(i));
            }
            
            return getFluidByName(outputName);
        }
        
        return null;
    }
    
    /**
     * Récupère un fluide par son nom de registre.
     */
    private Fluid getFluidByName(String fluidName) {
        try {
            ResourceLocation location = new ResourceLocation(fluidName);
            Fluid fluid = BuiltInRegistries.FLUID.get(location);
            return fluid != Fluids.EMPTY ? fluid : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Obtient le nom de registre d'un fluide.
     */
    private String getFluidName(Fluid fluid) {
        ResourceLocation location = BuiltInRegistries.FLUID.getKey(fluid);
        return location != null ? location.toString() : "unknown";
    }
    
    /**
     * Vérifie si un fluide peut être fermenté.
     */
    public boolean canFerment(Fluid fluid) {
        return findRecipe(fluid) != null;
    }
    
    /**
     * Vide tous les caches (pour rechargement).
     */
    public void clearCaches() {
        directFluidCache.clear();
        tagCache.clear();
        wildcardCache.clear();
        initialized = false;
    }
    
    /**
     * Statistiques de debug.
     */
    public void logStatistics() {
        Amphora.LOGGER.info("=== Enhanced Recipe Matcher Statistics ===");
        Amphora.LOGGER.info("Direct recipes: {}", directRecipes.size());
        Amphora.LOGGER.info("Tag-based recipes: {}", tagRecipes.size());
        Amphora.LOGGER.info("Wildcard recipes: {}", wildcardRecipes.size());
        Amphora.LOGGER.info("Cached direct fluids: {}", directFluidCache.size());
        Amphora.LOGGER.info("=========================================");
    }
    
    // Classes internes pour les différents types de recettes
    
    private static class DirectFluidRecipe extends FermentationRecipe {
        public DirectFluidRecipe(ResourceLocation id, Fluid input, Fluid output, int time, int minRpm, int quantumSize) {
            super(id, input, output, time, minRpm, quantumSize);
        }
    }
    
    private static class TagBasedRecipe {
        private final ResourceLocation id;
        private final TagKey<Fluid> inputTag;
        private final TagKey<Fluid> outputTag;
        private final int processingTime;
        private final int minRpm;
        private final int quantumSize;
        
        public TagBasedRecipe(ResourceLocation id, TagKey<Fluid> inputTag, TagKey<Fluid> outputTag, 
                             int processingTime, int minRpm, int quantumSize) {
            this.id = id;
            this.inputTag = inputTag;
            this.outputTag = outputTag;
            this.processingTime = processingTime;
            this.minRpm = minRpm;
            this.quantumSize = quantumSize;
        }
        
        public ResourceLocation getId() { return id; }
        public TagKey<Fluid> getInputTag() { return inputTag; }
        public TagKey<Fluid> getOutputTag() { return outputTag; }
        public int getProcessingTime() { return processingTime; }
        public int getMinRpm() { return minRpm; }
        public int getQuantumSize() { return quantumSize; }
    }
    
    private static class WildcardRecipe {
        private final ResourceLocation id;
        private final String inputPattern;
        private final String outputPattern;
        private final int processingTime;
        private final int minRpm;
        private final int quantumSize;
        
        public WildcardRecipe(ResourceLocation id, String inputPattern, String outputPattern,
                             int processingTime, int minRpm, int quantumSize) {
            this.id = id;
            this.inputPattern = inputPattern;
            this.outputPattern = outputPattern;
            this.processingTime = processingTime;
            this.minRpm = minRpm;
            this.quantumSize = quantumSize;
        }
        
        public ResourceLocation getId() { return id; }
        public String getInputPattern() { return inputPattern; }
        public String getOutputPattern() { return outputPattern; }
        public int getProcessingTime() { return processingTime; }
        public int getMinRpm() { return minRpm; }
        public int getQuantumSize() { return quantumSize; }
    }
}
