package be.pierrelac.amphora;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Fluides de jus créés avec Fabric API - approche minimaliste
 */
public class ModFluids {
    
    // Registres pour nos fluides - les blocs et items sont maintenant gérés dans ModBlocks et ModItems
    public static final Map<String, SimpleJuiceFluid.Still> STILL_FLUIDS = new HashMap<>();
    public static final Map<String, SimpleJuiceFluid.Flowing> FLOWING_FLUIDS = new HashMap<>();
    
    // Couleurs pour chaque type de jus
    public static final Map<String, Integer> JUICE_COLORS = new HashMap<>();
    
    // Informations complètes pour chaque fluide (ID, couleur, clés de traduction)
    public static final Map<String, FluidInfo> FLUID_DEFINITIONS = new HashMap<>();
    
    /**
     * Classe contenant toutes les informations d'un fluide
     */
    public static class FluidInfo {
        public final String id;
        public final int color;
        public final String fluidTranslationKey;
        public final String bucketTranslationKey;
        public final String stillTexture;
        public final String flowTexture;
        
        public FluidInfo(String id, int color, String fluidTranslationKey, String bucketTranslationKey, String stillTexture, String flowTexture) {
            this.id = id;
            this.color = color;
            this.fluidTranslationKey = fluidTranslationKey;
            this.bucketTranslationKey = bucketTranslationKey;
            this.stillTexture = stillTexture;
            this.flowTexture = flowTexture;
        }
    }
    
    static {
        // Définir tous les fluides avec leurs informations complètes
        // Note: Les fluides utilisent les clés "block." car ils sont enregistrés comme LiquidBlock
        FLUID_DEFINITIONS.put("red_grape", new FluidInfo(
            "red_grape", 0x6e386c, 
            "block.amphora.red_grape_juice", 
            "item.amphora.red_grape_juice_bucket",
            "fluid/grapejuice_still",
            "fluid/juice_flow"
        ));
        FLUID_DEFINITIONS.put("white_grape", new FluidInfo(
            "white_grape", 0x80c04c,
            "block.amphora.white_grape_juice",
            "item.amphora.white_grape_juice_bucket",
            "fluid/grapejuice_still",
            "fluid/juice_flow"
        ));
        FLUID_DEFINITIONS.put("red_savanna_grape", new FluidInfo(
            "red_savanna_grape", 0xa23661,
            "block.amphora.red_savanna_grape_juice",
            "item.amphora.red_savanna_grape_juice_bucket",
            "fluid/grapejuice_still",
            "fluid/juice_flow"
        ));
        FLUID_DEFINITIONS.put("white_savanna_grape", new FluidInfo(
            "white_savanna_grape", 0xbbbd44,
            "block.amphora.white_savanna_grape_juice",
            "item.amphora.white_savanna_grape_juice_bucket",
            "fluid/grapejuice_still",
            "fluid/juice_flow"
        ));
        FLUID_DEFINITIONS.put("red_taiga_grape", new FluidInfo(
            "red_taiga_grape", 0x6633a4,
            "block.amphora.red_taiga_grape_juice",
            "item.amphora.red_taiga_grape_juice_bucket",
            "fluid/grapejuice_still",
            "fluid/juice_flow"
        ));
        FLUID_DEFINITIONS.put("white_taiga_grape", new FluidInfo(
            "white_taiga_grape", 0x77b476,
            "block.amphora.white_taiga_grape_juice",
            "item.amphora.white_taiga_grape_juice_bucket",
            "fluid/grapejuice_still",
            "fluid/juice_flow"
        ));
        FLUID_DEFINITIONS.put("red_jungle_grape", new FluidInfo(
            "red_jungle_grape", 0x943682,
            "block.amphora.red_jungle_grape_juice",
            "item.amphora.red_jungle_grape_juice_bucket",
            "fluid/grapejuice_still",
            "fluid/juice_flow"
        ));
        FLUID_DEFINITIONS.put("white_jungle_grape", new FluidInfo(
            "white_jungle_grape", 0xabbb5c,
            "block.amphora.white_jungle_grape_juice",
            "item.amphora.white_jungle_grape_juice_bucket",
            "fluid/grapejuice_still",
            "fluid/juice_flow"
        ));
        FLUID_DEFINITIONS.put("apple", new FluidInfo(
            "apple", 0xe8be72,
            "block.amphora.apple_juice",
            "item.amphora.apple_juice_bucket",
            "fluid/grapejuice_still",
            "fluid/juice_flow"
        ));
        FLUID_DEFINITIONS.put("cherry", new FluidInfo(
            "cherry", 0xc44b55,
            "block.amphora.cherry_juice",
            "item.amphora.cherry_juice_bucket",
            "fluid/grapejuice_still",
            "fluid/juice_flow"
        ));
        
        // Remplir la map des couleurs pour compatibilité
        for (FluidInfo info : FLUID_DEFINITIONS.values()) {
            JUICE_COLORS.put(info.id, info.color);
        }
    }
    
    public static void register() {
        Amphora.LOGGER.info("=== REGISTERING MINIMAL FABRIC FLUIDS ===");
        
        // Créer chaque fluide de jus avec ses informations complètes
        for (FluidInfo fluidInfo : FLUID_DEFINITIONS.values()) {
            String fluidName = fluidInfo.id + "_juice";
            Amphora.LOGGER.info("Creating minimal Fabric fluid: {} with translation keys", fluidName);
            
            registerJuiceFluid(fluidInfo);
        }
        
        Amphora.LOGGER.info("=== MINIMAL FABRIC FLUIDS REGISTERED ===");
    }
    
    private static void registerJuiceFluid(FluidInfo fluidInfo) {
        try {
            String juiceId = fluidInfo.id;
            String fluidName = juiceId + "_juice";
            
            Amphora.LOGGER.info("Attempting fluid registration for: {} with translations [fluid: {}, bucket: {}]", 
                fluidName, fluidInfo.fluidTranslationKey, fluidInfo.bucketTranslationKey);
            
            // Créer d'abord les ResourceLocations
            ResourceLocation stillId = new ResourceLocation("amphora", fluidName);
            ResourceLocation flowingId = new ResourceLocation("amphora", "flowing_" + fluidName);
            
            // Créer des instances simples de fluides - pas de références circulaires
            var stillFluid = new SimpleJuiceFluid.Still();
            var flowingFluid = new SimpleJuiceFluid.Flowing();
            
            // Enregistrer d'abord les fluides de base
            Registry.register(BuiltInRegistries.FLUID, stillId, stillFluid);
            Registry.register(BuiltInRegistries.FLUID, flowingId, flowingFluid);
            
            // Après l'enregistrement, configurer les liens
            stillFluid.setFlowing(flowingFluid);
            flowingFluid.setStill(stillFluid);
            
            // Utiliser ModBlocks pour enregistrer le bloc liquide
            var fluidBlock = ModBlocks.registerFluidBlock(juiceId, stillFluid);
            stillFluid.setBlock(fluidBlock);
            flowingFluid.setBlock(fluidBlock);
            
            // Utiliser ModItems pour enregistrer le seau
            ModItems.registerJuiceBucket(juiceId, stillFluid, fluidInfo.color);
            var bucketItem = ModItems.getBucketItem(juiceId);
            stillFluid.setBucket(bucketItem);
            flowingFluid.setBucket(bucketItem);
            
            // Stocker dans nos maps
            STILL_FLUIDS.put(juiceId, stillFluid);
            FLOWING_FLUIDS.put(juiceId, flowingFluid);
            
            Amphora.LOGGER.info("✓ Successfully registered fluid: {} ({}) with translations", juiceId, fluidName);
            
        } catch (Exception e) {
            Amphora.LOGGER.error("✗ Failed to register fluid {}: ", fluidInfo.id, e);
        }
    }
    
    // Getters pour compatibilité avec le code existant
    public static Integer getJuiceColor(String juiceId) {
        return JUICE_COLORS.get(juiceId);
    }
    
    // Nouveaux getters pour les informations de fluide avec traductions
    public static FluidInfo getFluidInfo(String juiceId) {
        return FLUID_DEFINITIONS.get(juiceId);
    }
    
    public static String getFluidTranslationKey(String juiceId) {
        FluidInfo info = FLUID_DEFINITIONS.get(juiceId);
        return info != null ? info.fluidTranslationKey : null;
    }
    
    public static String getBucketTranslationKey(String juiceId) {
        FluidInfo info = FLUID_DEFINITIONS.get(juiceId);
        return info != null ? info.bucketTranslationKey : null;
    }
    
    public static String getStillTexture(String juiceId) {
        FluidInfo info = FLUID_DEFINITIONS.get(juiceId);
        return info != null ? info.stillTexture : null;
    }
    
    public static String getFlowTexture(String juiceId) {
        FluidInfo info = FLUID_DEFINITIONS.get(juiceId);
        return info != null ? info.flowTexture : null;
    }
    
    public static Fluid getStillFluid(String juiceId) {
        return STILL_FLUIDS.get(juiceId);
    }
    
    public static Fluid getFlowingFluid(String juiceId) {
        return FLOWING_FLUIDS.get(juiceId);
    }
    
    public static Block getFluidBlock(String juiceId) {
        return ModBlocks.getFluidBlock(juiceId);
    }
    
    public static Item getBucketItem(String juiceId) {
        return ModItems.getBucketItem(juiceId);
    }
    
    /**
     * Fluide de jus minimaliste avec implémentation directe des méthodes requises
     */
    public static abstract class SimpleJuiceFluid extends FlowingFluid {
        protected Fluid flowing;
        protected Fluid still;
        protected LiquidBlock block;
        protected Item bucket;
        
        public void setFlowing(Fluid flowing) {
            this.flowing = flowing;
        }
        
        public void setStill(Fluid still) {
            this.still = still;
        }
        
        public void setBlock(LiquidBlock block) {
            this.block = block;
        }
        
        public void setBucket(Item bucket) {
            this.bucket = bucket;
        }
        
        @Override
        public Fluid getFlowing() {
            return flowing;
        }
        
        @Override
        public Fluid getSource() {
            return still;
        }
        
        @Override
        public Item getBucket() {
            return bucket;
        }
        
        @Override
        public boolean canConvertToSource(@Nonnull Level level) {
            return false;
        }
        
        @Override
        protected void beforeDestroyingBlock(@Nonnull LevelAccessor level, @Nonnull BlockPos pos, @Nonnull BlockState state) {
            // Pas d'effet
        }
        
        @Override
        protected int getSlopeFindDistance(@Nonnull LevelReader level) {
            return 4;
        }
        
        @Override
        protected int getDropOff(@Nonnull LevelReader level) {
            return 1;
        }
        
        @Override
        public int getTickDelay(@Nonnull LevelReader level) {
            return 5;
        }
        
        @Override
        protected float getExplosionResistance() {
            return 100.0F;
        }
        
        @Override
        protected @Nonnull BlockState createLegacyBlock(@Nonnull FluidState state) {
            return block.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
        }
        
        @Override
        public boolean canBeReplacedWith(@Nonnull FluidState fluidState, @Nonnull BlockGetter blockGetter, @Nonnull BlockPos blockPos, @Nonnull Fluid fluid, @Nonnull Direction direction) {
            return false;
        }
        
        public static class Still extends SimpleJuiceFluid {
            @Override
            public int getAmount(@Nonnull FluidState state) {
                return 8;
            }
            
            @Override
            public boolean isSource(@Nonnull FluidState state) {
                return true;
            }
            
            @Override
            public Fluid getSource() {
                return this;  // Le fluide Still retourne lui-même
            }
            
            @Override
            protected void createFluidStateDefinition(@Nonnull StateDefinition.Builder<Fluid, FluidState> builder) {
                builder.add(FALLING);  // Ajouter la propriété FALLING pour compatibilité
            }
        }
        
        public static class Flowing extends SimpleJuiceFluid {
            @Override
            public int getAmount(@Nonnull FluidState state) {
                return state.getValue(LEVEL);
            }
            
            @Override
            public boolean isSource(@Nonnull FluidState state) {
                return false;
            }
            
            @Override
            public Fluid getSource() {
                return still != null ? still : this;  // Retourne le fluide Still si disponible
            }
            
            @Override
            protected void createFluidStateDefinition(@Nonnull StateDefinition.Builder<Fluid, FluidState> builder) {
                builder.add(LEVEL);
                builder.add(FALLING);  // Ajouter la propriété FALLING pour compatibilité
            }
        }
    }
}
