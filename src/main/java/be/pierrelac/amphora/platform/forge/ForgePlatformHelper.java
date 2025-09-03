package be.pierrelac.amphora.platform.forge;

import be.pierrelac.amphora.platform.PlatformHelper;

/**
 * Implémentation Forge du PlatformHelper (futur)
 * Cette classe sera implémentée quand le support Forge sera ajouté.
 */
public class ForgePlatformHelper implements PlatformHelper {
    
    @Override
    public String getPlatformName() {
        return "Forge";
    }
    
    @Override
    public boolean isModLoaded(String modId) {
        // TODO: Implémenter avec net.minecraftforge.fml.ModList
        throw new UnsupportedOperationException("Forge support not yet implemented");
    }
    
    @Override
    public String getModVersion(String modId) {
        // TODO: Implémenter avec ModContainer
        throw new UnsupportedOperationException("Forge support not yet implemented");
    }
    
    @Override
    public java.nio.file.Path getConfigDirectory() {
        // TODO: Implémenter avec FMLPaths
        throw new UnsupportedOperationException("Forge support not yet implemented");
    }
    
    @Override
    public boolean isClient() {
        // TODO: Implémenter avec DistExecutor
        throw new UnsupportedOperationException("Forge support not yet implemented");
    }
    
    @Override
    public boolean isServer() {
        // TODO: Implémenter avec DistExecutor
        throw new UnsupportedOperationException("Forge support not yet implemented");
    }
    
    @Override
    public void executeOnClient(Runnable clientCode) {
        // TODO: Implémenter avec DistExecutor.unsafeRunWhenOn
        throw new UnsupportedOperationException("Forge support not yet implemented");
    }
    
    @Override
    public void executeOnServer(Runnable serverCode) {
        // TODO: Implémenter avec DistExecutor.unsafeRunWhenOn
        throw new UnsupportedOperationException("Forge support not yet implemented");
    }
    
    @Override
    public boolean isDevelopmentEnvironment() {
        // TODO: Vérifier l'environnement de développement Forge
        throw new UnsupportedOperationException("Forge support not yet implemented");
    }
}
