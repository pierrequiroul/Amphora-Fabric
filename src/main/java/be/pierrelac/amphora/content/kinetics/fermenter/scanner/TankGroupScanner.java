package be.pierrelac.amphora.content.kinetics.fermenter.scanner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;

import java.util.*;

/**
 * Scanne et assemble les groupes de Fluid Tanks connectés en utilisant un algorithme BFS.
 * Responsable de détecter les groupes de tanks valides pour la fermentation quantique.
 */
public class TankGroupScanner {
    
    public final Level level;
    private final BlockPos startPos;
    
    // Configuration de scan
    private static final int MAX_SCAN_RADIUS = 16;        // Rayon maximum de scan
    private static final int MAX_GROUP_SIZE = 64;         // Nombre maximum de tanks par groupe
    private static final int MIN_GROUP_SIZE = 2;          // Minimum 2 tanks pour former un groupe
    
    public TankGroupScanner(Level level, BlockPos startPos) {
        this.level = level;
        this.startPos = startPos;
    }
    
    /**
     * Scanne et trouve tous les groupes de tanks connectés dans la zone.
     * Utilise un algorithme BFS pour explorer les connexions de tanks.
     * 
     * @return Liste des groupes de tanks trouvés
     */
    public List<TankGroup> scanForTankGroups() {
        List<TankGroup> groups = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        
        // Recherche BFS dans la zone de scan
        for (int x = -MAX_SCAN_RADIUS; x <= MAX_SCAN_RADIUS; x++) {
            for (int y = -MAX_SCAN_RADIUS; y <= MAX_SCAN_RADIUS; y++) {
                for (int z = -MAX_SCAN_RADIUS; z <= MAX_SCAN_RADIUS; z++) {
                    BlockPos pos = startPos.offset(x, y, z);
                    
                    if (visited.contains(pos)) continue;
                    
                    // Vérifier si c'est un Fluid Tank
                    if (isFluidTank(pos)) {
                        TankGroup group = scanTankGroup(pos, visited);
                        if (group != null && group.isValidGroup()) {
                            groups.add(group);
                        }
                    }
                }
            }
        }
        
        return groups;
    }
    
    /**
     * Scanne un groupe de tanks connectés à partir d'une position de départ.
     * Utilise BFS pour explorer tous les tanks connectés.
     * 
     * @param startTank Position du tank de départ
     * @param globalVisited Set des positions déjà visitées globalement
     * @return TankGroup si valide, null sinon
     */
    private TankGroup scanTankGroup(BlockPos startTank, Set<BlockPos> globalVisited) {
        Set<BlockPos> groupTanks = new HashSet<>();
        Queue<BlockPos> toVisit = new ArrayDeque<>();
        Set<BlockPos> localVisited = new HashSet<>();
        
        toVisit.offer(startTank);
        localVisited.add(startTank);
        
        // BFS pour explorer le groupe de tanks connectés
        while (!toVisit.isEmpty() && groupTanks.size() < MAX_GROUP_SIZE) {
            BlockPos current = toVisit.poll();
            
            if (!isFluidTank(current)) continue;
            
            groupTanks.add(current);
            globalVisited.add(current);
            
            // Explorer les 6 directions adjacentes
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.relative(direction);
                
                if (localVisited.contains(neighbor)) continue;
                if (neighbor.distSqr(startPos) > MAX_SCAN_RADIUS * MAX_SCAN_RADIUS) continue;
                
                // Vérifier si le voisin est un Fluid Tank connecté
                if (isFluidTank(neighbor) && areConnected(current, neighbor)) {
                    toVisit.offer(neighbor);
                    localVisited.add(neighbor);
                }
            }
        }
        
        // Créer le groupe si la taille est valide
        if (groupTanks.size() >= MIN_GROUP_SIZE) {
            return new TankGroup(level, groupTanks);
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
     * Vérifie si deux tanks sont connectés (adjacents et compatibles).
     * Dans Create, les Fluid Tanks se connectent automatiquement quand ils sont adjacents.
     */
    private boolean areConnected(BlockPos pos1, BlockPos pos2) {
        // Vérifier qu'ils sont adjacents (distance Manhattan = 1)
        return Math.abs(pos1.getX() - pos2.getX()) + 
               Math.abs(pos1.getY() - pos2.getY()) + 
               Math.abs(pos1.getZ() - pos2.getZ()) == 1;
    }
    
    /**
     * Trouve le groupe de tanks le plus proche de la valve.
     * Utile pour l'armement automatique.
     */
    public TankGroup findNearestTankGroup() {
        List<TankGroup> groups = scanForTankGroups();
        
        if (groups.isEmpty()) return null;
        
        // Trier par distance à la valve
        groups.sort((a, b) -> {
            double distA = a.getCenter().distSqr(startPos);
            double distB = b.getCenter().distSqr(startPos);
            return Double.compare(distA, distB);
        });
        
        return groups.get(0);
    }
    
    /**
     * Statistiques de scan pour le debug
     */
    public static class ScanStats {
        public final int totalTanksFound;
        public final int groupsFound;
        public final long scanTimeMs;
        
        public ScanStats(int totalTanks, int groups, long time) {
            this.totalTanksFound = totalTanks;
            this.groupsFound = groups;
            this.scanTimeMs = time;
        }
    }
    
    /**
     * Version avec statistiques pour le debug
     */
    public ScanStats scanWithStats() {
        long start = System.currentTimeMillis();
        List<TankGroup> groups = scanForTankGroups();
        long end = System.currentTimeMillis();
        
        int totalTanks = groups.stream()
            .mapToInt(group -> group.getTankPositions().size())
            .sum();
            
        return new ScanStats(totalTanks, groups.size(), end - start);
    }
    
    // Getter pour accéder au level
    public Level getLevel() {
        return level;
    }
}
