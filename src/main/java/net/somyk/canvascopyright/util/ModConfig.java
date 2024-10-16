package net.somyk.canvascopyright.util;

import net.fabricmc.loader.api.FabricLoader;
import org.simpleyaml.configuration.comments.format.YamlCommentFormat;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.IOException;
import java.nio.file.Path;

import static net.somyk.canvascopyright.CanvasCopyright.*;

public class ModConfig {

    private static final String CONFIG = MOD_ID + ".yml";
    private static final Path configDir = FabricLoader.getInstance().getConfigDir();
    private static final Path configFilePath = configDir.resolve(CONFIG);

    public static final String displayLore = "displayAuthorsLore";
    public static final String maxPlayerLore = "maxPlayersInLore";
    public static final String disableCopy = "disableCopy";
    public static final String authorsCopy = "authorsCanCopy";
    public static final String publicDomain = "publicDomainFeature";

    public static void registerConfigs() {
        final YamlFile config = new YamlFile(configFilePath.toFile().getAbsolutePath());
        try {
            if (!config.exists()) {
                config.createNewFile();
                LOGGER.info("[{}]: config has been created: {}", MOD_ID, configFilePath.toFile().getPath());
            } else {
                LOGGER.info("[{}]: loading configurations..", MOD_ID);
            }
            config.loadWithComments();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        config.addDefault("displayAuthorsLore", true);
        config.addDefault("maxPlayersInLore", 3);
        config.addDefault("disableCopy", true);
        config.addDefault("authorsCanCopy", true);
        config.addDefault("publicDomainFeature", true);

        config.setCommentFormat(YamlCommentFormat.PRETTY);
        config.setComment("disableCopy", "Nobody can make a copy of a canvas (except authors if 'authorsCanCopy' is 'true')");
        config.setComment("publicDomainFeature", "Main author can transfer a canvas to the public domain (all players can copy the canvas).\n" +
                "Doesn't work if `disableCopy` is `false`");

        try {
            config.save();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean getBooleanValue(String key) {
        final YamlFile config = new YamlFile((configFilePath.toFile()).getAbsolutePath());
        try {
            config.loadWithComments();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return config.getBoolean(key);
    }

    public static int getIntValue(String key) {
        final YamlFile config = new YamlFile((configFilePath.toFile()).getAbsolutePath());
        try {
            config.loadWithComments();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return config.getInt(key);
    }

    public static String getStringValue(String key) {
        final YamlFile config = new YamlFile((configFilePath.toFile()).getAbsolutePath());
        try {
            config.loadWithComments();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return config.getString(key);
    }

    public static void setValue(String key, Object newValue){
        final YamlFile config = new YamlFile((configFilePath.toFile()).getAbsolutePath());

        try {
            config.loadWithComments();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        config.set(key, newValue);

        try {
            config.save();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
