package cofh.core.common.config;

import cofh.core.util.ProxyUtils;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConfigManager {

    protected List<IBaseConfig> commonSubConfigs = new ArrayList<>();
    protected List<IBaseConfig> serverSubConfigs = new ArrayList<>();
    protected List<IBaseConfig> clientSubConfigs = new ArrayList<>();

    protected boolean commonInit = false;
    protected boolean clientInit = false;
    protected boolean serverInit = false;

    protected final ModConfigSpec.Builder commonConfig = new ModConfigSpec.Builder();
    protected ModConfigSpec commonSpec;

    protected final ModConfigSpec.Builder clientConfig = new ModConfigSpec.Builder();
    protected ModConfigSpec clientSpec;

    protected final ModConfigSpec.Builder serverConfig = new ModConfigSpec.Builder();
    protected ModConfigSpec serverSpec;

    public ConfigManager register(IEventBus bus) {

        bus.register(this);
        return this;
    }

    public synchronized ConfigManager addCommonConfig(IBaseConfig config) {

        commonSubConfigs.add(config);
        return this;
    }

    public synchronized ConfigManager addClientConfig(IBaseConfig config) {

        clientSubConfigs.add(config);
        return this;
    }

    public synchronized ConfigManager addServerConfig(IBaseConfig config) {

        serverSubConfigs.add(config);
        return this;
    }

    public void setupCommon() {

        if (!commonInit) {
            genCommonConfig();
            commonSpec = commonConfig.build();
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonSpec);
            commonInit = true;

            String configName = String.format(Locale.ROOT, "%s-%s.toml",
                    ModLoadingContext.get().getActiveContainer().getModId(), ModConfig.Type.COMMON.extension());
            loadConfig(commonSpec, FMLPaths.CONFIGDIR.get().resolve(configName));
        }
    }

    public static void loadConfig(ModConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .preserveInsertionOrder()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }

    /**
     * Must be called in Mod Constructor or NewRegistryEvent at latest.
     */
    public void setupClient() {

        if (ProxyUtils.isClient() && !clientInit) {
            genClientConfig();
            clientSpec = clientConfig.build();
            ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);
            clientInit = true;
        }
    }

    /**
     * Must be called in Mod Constructor or Common Setup event at latest.
     */
    public void setupServer() {

        if (!serverInit) {
            genServerConfig();
            serverSpec = serverConfig.build();
            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
            serverInit = true;
        }
    }

    public boolean isClientInit() {

        return clientInit;
    }

    public boolean isServerInit() {

        return serverInit;
    }

    public ModConfigSpec getServerSpec() {

        return serverSpec;
    }

    public ModConfigSpec getClientSpec() {

        return clientSpec;
    }

    protected void genCommonConfig() {

        for (IBaseConfig cfg : commonSubConfigs) {
            cfg.apply(commonConfig);
        }
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

    protected void refreshCommonConfig() {

        commonSubConfigs.forEach(IBaseConfig::refresh);
    }

    protected void refreshServerConfig() {

        serverSubConfigs.forEach(IBaseConfig::refresh);
    }

    protected void refreshClientConfig() {

        clientSubConfigs.forEach(IBaseConfig::refresh);
    }

    // region CONFIGURATION
    @SubscribeEvent
    public void configRefresh(ModConfigEvent event) {

        switch (event.getConfig().getType()) {
            case COMMON -> refreshCommonConfig();
            case CLIENT -> refreshClientConfig();
            case SERVER -> refreshServerConfig();
        }
    }
    // endregion
}
