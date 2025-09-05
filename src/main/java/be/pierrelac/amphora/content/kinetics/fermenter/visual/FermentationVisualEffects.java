package be.pierrelac.amphora.content.kinetics.fermenter.visual;

import be.pierrelac.amphora.content.kinetics.fermenter.FermentationValveBlockEntity;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Effets visuels pour la fermentation quantique
 */
public class FermentationVisualEffects {
    
    /**
     * Affiche les particules de fermentation active
     */
    public static void showFermentationParticles(Level level, BlockPos valvePos, FermentationValveBlockEntity valve) {
        if (level.isClientSide && valve.isActive()) {
            ClientLevel clientLevel = (ClientLevel) level;
            RandomSource random = clientLevel.random;
            
            // Particules de bulles au niveau de la valve
            for (int i = 0; i < 3; i++) {
                double x = valvePos.getX() + 0.3 + random.nextDouble() * 0.4;
                double y = valvePos.getY() + 0.2 + random.nextDouble() * 0.6;
                double z = valvePos.getZ() + 0.3 + random.nextDouble() * 0.4;
                
                clientLevel.addParticle(ParticleTypes.BUBBLE,
                    x, y, z,
                    0, 0.05, 0);
            }
            
            // Particules de vapeur occasionnelles
            if (random.nextFloat() < 0.3f) {
                double x = valvePos.getX() + 0.4 + random.nextDouble() * 0.2;
                double y = valvePos.getY() + 0.8;
                double z = valvePos.getZ() + 0.4 + random.nextDouble() * 0.2;
                
                clientLevel.addParticle(ParticleTypes.CLOUD,
                    x, y, z,
                    0, 0.02, 0);
            }
            
            // Particules spéciales pour les quantum processings
            if (valve.getProcessor() != null && valve.getProcessor().isProcessing()) {
                showQuantumProcessingEffects(clientLevel, valvePos, random);
            }
        }
    }
    
    /**
     * Effets spéciaux pour le traitement quantique
     */
    private static void showQuantumProcessingEffects(ClientLevel level, BlockPos pos, RandomSource random) {
        // Particules d'enchantement pour l'effet "quantique"
        for (int i = 0; i < 2; i++) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            
            double offsetX = (random.nextDouble() - 0.5) * 2.0;
            double offsetY = (random.nextDouble() - 0.5) * 2.0;
            double offsetZ = (random.nextDouble() - 0.5) * 2.0;
            
            level.addParticle(ParticleTypes.ENCHANT,
                x + offsetX, y + offsetY, z + offsetZ,
                -offsetX * 0.1, -offsetY * 0.1, -offsetZ * 0.1);
        }
    }
    
    /**
     * Effets lors de la formation d'un groupe de tanks
     */
    public static void showGroupFormationEffects(Level level, BlockPos valvePos, int tankCount) {
        if (level.isClientSide) {
            ClientLevel clientLevel = (ClientLevel) level;
            RandomSource random = clientLevel.random;
            
            // Explosion de particules dorées
            for (int i = 0; i < tankCount * 2; i++) {
                double x = valvePos.getX() + 0.5;
                double y = valvePos.getY() + 0.5;
                double z = valvePos.getZ() + 0.5;
                
                double velocityX = (random.nextDouble() - 0.5) * 0.8;
                double velocityY = random.nextDouble() * 0.5;
                double velocityZ = (random.nextDouble() - 0.5) * 0.8;
                
                clientLevel.addParticle(ParticleTypes.END_ROD,
                    x, y, z,
                    velocityX, velocityY, velocityZ);
            }
        }
    }
    
    /**
     * Effets lors de la complétion d'un quantum
     */
    public static void showQuantumCompletionEffects(Level level, BlockPos valvePos) {
        if (level.isClientSide) {
            ClientLevel clientLevel = (ClientLevel) level;
            RandomSource random = clientLevel.random;
            
            // Particules de succès
            for (int i = 0; i < 5; i++) {
                double x = valvePos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
                double y = valvePos.getY() + 0.8;
                double z = valvePos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
                
                clientLevel.addParticle(ParticleTypes.HAPPY_VILLAGER,
                    x, y, z,
                    0, 0.1, 0);
            }
        }
    }
}
