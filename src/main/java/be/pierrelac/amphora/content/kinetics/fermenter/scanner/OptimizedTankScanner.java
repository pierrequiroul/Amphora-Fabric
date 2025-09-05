package be.pierrelac.amphora.content.kinetics.fermenter.scanner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import be.pierrelac.amphora.Amphora;

import java.util.*;

/**
 * Scanner optimisé pour détecter des formations de tanks en carré (1×1, 2×2, 3×3) 
 * de hauteur uniforme au lieu du BFS coûteux sur rayon 16.
 */
public class OptimizedTankScanner {
    
    private final Level level;
    private final BlockPos valvePos;
    private final Direction valveFacing;
    
    // Configuration optimisée
    private static final int MAX_SQUARE_SIZE = 3;      // Carré 3×3 maximum
    private static final int MAX_HEIGHT = 16;          // Hauteur maximum du multibloc
    private static final int MIN_HEIGHT = 1;           // Hauteur minimum
    
    // Cooldown pour éviter les rescans constants
    private long lastScanTime = 0;
    private static final long SCAN_COOLDOWN_TICKS = 10; // 0.5 secondes
    private TankGroup cachedResult = null;
    
    public OptimizedTankScanner(Level level, BlockPos valvePos, Direction valveFacing) {
        this.level = level;
        this.valvePos = valvePos;
        this.valveFacing = valveFacing;
    }
    
    /**
     * Constructeur sans direction pour compatibilité legacy
     */
    public OptimizedTankScanner(Level level, BlockPos valvePos) {
        this(level, valvePos, Direction.NORTH); // Direction par défaut
    }
    
    /**
     * Getter pour le level (compatibilité avec legacy scanner)
     */
    public Level getLevel() {
        return level;
    }
    
    /**
     * Scanne pour un multibloc de tanks en forme de carré avec cooldown.
     * @return TankGroup trouvé ou null
     */
    public TankGroup scanWithCooldown() {
        long currentTime = level.getGameTime();
        
        // Vérifier le cooldown
        if (currentTime - lastScanTime < SCAN_COOLDOWN_TICKS) {
            return cachedResult;
        }
        
        lastScanTime = currentTime;
        cachedResult = scanForSquareTankGroup();
        return cachedResult;
    }
    
    /**
     * Méthode de compatibilité avec TankGroupScanner legacy.
     * Utilise le scan optimisé par carrés.
     */
    public TankGroup findNearestTankGroup() {
        return scanWithCooldown();
    }
    
    /**
     * Scanne pour un multibloc de tanks en forme de carré uniforme.
     * Cherche des carrés 1×1, 2×2, ou 3×3 de hauteur constante.
     */
    public TankGroup scanForSquareTankGroup() {
        // Calculer la position de départ du scan basée sur la facing de la valve
        BlockPos scanStart = valvePos.relative(valveFacing);
        
        Amphora.LOGGER.debug("Scanning for square tank group from valve at {} facing {}", 
            valvePos, valveFacing);
        
        // Essayer différentes tailles de carré (3×3, 2×2, 1×1)
        for (int size = MAX_SQUARE_SIZE; size >= 1; size--) {
            TankGroup group = scanSquareOfSize(scanStart, size);
            if (group != null) {
                Amphora.LOGGER.info("Found {}×{} tank multibloc with {} tanks at height {}", 
                    size, size, group.getTankCount(), group.getHeight());
                return group;
            }
        }
        
        Amphora.LOGGER.debug("No valid square tank group found");
        return null;
    }
    
    /**
     * Scanne un carré de taille spécifique autour de la position de départ.
     */
    private TankGroup scanSquareOfSize(BlockPos center, int size) {
        // Pour un carré de taille N, scanner de -(N-1)/2 à +(N-1)/2
        int offset = (size - 1) / 2;
        
        // Déterminer les axes horizontaux selon la facing de la valve
        Direction rightAxis = valveFacing.getClockWise();
        Direction forwardAxis = valveFacing;
        
        // Scanner différentes hauteurs
        for (int height = MIN_HEIGHT; height <= MAX_HEIGHT; height++) {
            Set<BlockPos> squareTanks = new HashSet<>();
            boolean isValidSquare = true;
            
            // Scanner le carré en XZ
            for (int x = -offset; x <= offset; x++) {
                for (int z = -offset; z <= offset; z++) {
                    // Scanner verticalement
                    for (int y = 0; y < height; y++) {
                        BlockPos tankPos = center
                            .relative(rightAxis, x)
                            .relative(forwardAxis, z)
                            .above(y);
                        
                        if (!isFluidTank(tankPos)) {
                            isValidSquare = false;
                            break;
                        }
                        
                        squareTanks.add(tankPos);
                    }
                    
                    if (!isValidSquare) break;
                }
                
                if (!isValidSquare) break;
            }
            
            // Si le carré est valide et a au moins 2 tanks
            if (isValidSquare && squareTanks.size() >= 2) {
                Amphora.LOGGER.debug("Found valid {}×{}×{} tank formation with {} tanks", 
                    size, size, height, squareTanks.size());
                
                return new OptimizedTankGroup(level, squareTanks, size, height, 
                    center.relative(rightAxis, -offset).relative(forwardAxis, -offset), valveFacing);
            }
        }
        
        return null;
    }
    
    /**
     * Vérifie si une position contient un Fluid Tank Create.
     */
    private boolean isFluidTank(BlockPos pos) {
        if (!level.isLoaded(pos)) return false;
        
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof FluidTankBlockEntity;
    }
    
    /**
     * Version optimisée de TankGroup avec informations de forme.
     */
    public static class OptimizedTankGroup extends TankGroup {
        private final int squareSize;
        private final int height;
        private final BlockPos bottomLeftCorner;
        private final Direction facing;
        
        public OptimizedTankGroup(Level level, Set<BlockPos> tankPositions, 
                                 int squareSize, int height, BlockPos bottomLeftCorner, Direction facing) {
            super(level, tankPositions);
            this.squareSize = squareSize;
            this.height = height;
            this.bottomLeftCorner = bottomLeftCorner;
            this.facing = facing;
        }
        
        public int getSquareSize() { return squareSize; }
        public int getHeight() { return height; }
        public BlockPos getBottomLeftCorner() { return bottomLeftCorner; }
        public Direction getFacing() { return facing; }
        
        @Override
        public String toString() {
            return String.format("OptimizedTankGroup{size=%dx%dx%d, tanks=%d, corner=%s, facing=%s}",
                squareSize, squareSize, height, getTankCount(), bottomLeftCorner, facing);
        }
        
        /**
         * Calcule les bornes du multibloc pour UI/FX.
         */
        public BlockPos getTopRightCorner() {
            Direction rightAxis = facing.getClockWise();
            return bottomLeftCorner
                .relative(rightAxis, squareSize - 1)
                .relative(facing, squareSize - 1)
                .above(height - 1);
        }
        
        /**
         * Vérifie si une position est dans les bornes du multibloc.
         */
        public boolean isInBounds(BlockPos pos) {
            BlockPos topRight = getTopRightCorner();
            
            int minX = Math.min(bottomLeftCorner.getX(), topRight.getX());
            int maxX = Math.max(bottomLeftCorner.getX(), topRight.getX());
            int minY = Math.min(bottomLeftCorner.getY(), topRight.getY());
            int maxY = Math.max(bottomLeftCorner.getY(), topRight.getY());
            int minZ = Math.min(bottomLeftCorner.getZ(), topRight.getZ());
            int maxZ = Math.max(bottomLeftCorner.getZ(), topRight.getZ());
            
            return pos.getX() >= minX && pos.getX() <= maxX &&
                   pos.getY() >= minY && pos.getY() <= maxY &&
                   pos.getZ() >= minZ && pos.getZ() <= maxZ;
        }
    }
}
