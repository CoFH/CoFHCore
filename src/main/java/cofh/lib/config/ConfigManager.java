package cofh.lib.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    protected List<IBaseConfig> serverSubConfigs = new ArrayList<>();
    protected List<IBaseConfig> clientSubConfigs = new ArrayList<>();

    protected final ForgeConfigSpec.Builder serverConfig = new ForgeConfigSpec.Builder();
    protected ForgeConfigSpec serverSpec;

    protected final ForgeConfigSpec.Builder clientConfig = new ForgeConfigSpec.Builder();
    protected ForgeConfigSpec clientSpec;

    public ConfigManager register(IEventBus bus) {

        bus.register(this);
        return this;
    }

    public ConfigManager addClientConfig(IBaseConfig config) {

        clientSubConfigs.add(config);
        return this;
    }

    public ConfigManager addServerConfig(IBaseConfig config) {

        serverSubConfigs.add(config);
        return this;
    }

    /**
     * Must be called in Mod Constructor.
     */
    public void setupClient() {

        genClientConfig();
        clientSpec = clientConfig.build();
        refreshClientConfig();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
    }

    /**
     * Must be called in Mod Constructor or Common Setup event.
     */
    public void setupServer() {

        genServerConfig();
        serverSpec = serverConfig.build();
        refreshServerConfig();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
    }

    public ForgeConfigSpec getServerSpec() {

        return serverSpec;
    }

    public ForgeConfigSpec getClientSpec() {

        return clientSpec;
    }

    protected void genServerConfig() {

        for (IBaseConfig cfg : serverSubConfigs) {
            cfg.apply(serverConfig);
        }
    }

    protected void genClientConfig() {

        for (IBaseConfig cfg : clientSubConfigs) {
            cfg.apply(clientConfig);
        }
    }

    protected void refreshServerConfig() {

        serverSubConfigs.forEach(IBaseConfig::refresh);
    }

    protected void refreshClientConfig() {

        clientSubConfigs.forEach(IBaseConfig::refresh);
    }

    // region CONFIGURATION
    @SubscribeEvent
    public void configLoading(ModConfigEvent.Loading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                refreshClientConfig();
                break;
            case SERVER:
                refreshServerConfig();
        }
    }

    @SubscribeEvent
    public void configReloading(ModConfigEvent.Reloading event) {

        switch (event.getConfig().getType()) {
            case CLIENT:
                refreshClientConfig();
                break;
            case SERVER:
                refreshServerConfig();
        }
    }
    // endregion
}
