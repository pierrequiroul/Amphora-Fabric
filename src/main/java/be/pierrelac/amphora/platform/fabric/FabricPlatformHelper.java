package be.pierrelac.amphora.platform.fabric;

import be.pierrelac.amphora.platform.PlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Implémentation Fabric du PlatformHelper
 */
public class FabricPlatformHelper implements PlatformHelper {
    
    @Override
    public String getPlatformName() {
        return "Fabric";
    }
    
    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
    
    @Override
    public String getModVersion(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElse("Unknown");
    }
    
    @Override
    public java.nio.file.Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
    
    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
    
    @Override
    public boolean isServer() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
    }
    
    @Override
    public void executeOnClient(Runnable clientCode) {
        if (isClient()) {
            clientCode.run();
        }
    }
    
    @Override
    public void executeOnServer(Runnable serverCode) {
        if (isServer()) {
            serverCode.run();
        }
    }
    
    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
