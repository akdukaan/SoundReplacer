package org.acornmc.soundreplacer;

import com.google.common.base.Throwables;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class Config {
    public static HashMap<String, Object> SOUNDS_RAW = defaultSounds();
    public static HashMap<Sound, Sound> SOUNDS = new HashMap<>();
    public static String LANGUAGE_FILE = "lang-en.yml";
    private static YamlConfiguration config;

    private static void init() {
        LANGUAGE_FILE = getString("language-file", LANGUAGE_FILE);
        SOUNDS_RAW = getSoundHashMap("sounds", SOUNDS_RAW);
        SOUNDS.clear();
        for (String key : SOUNDS_RAW.keySet()) {
            SOUNDS.put(Sound.valueOf(key), Sound.valueOf((String) SOUNDS_RAW.get(key)));
        }
    }

    private static HashMap<String, Object> getSoundHashMap(String path, HashMap<String, Object> def) {
        config.addDefault(path, def);
        ConfigurationSection section = config.getConfigurationSection(path);
        Map<String, Object> objectMap;
        if (section == null) {
            config.createSection(path, def);
            sort(config);
            return def;
        } else {
            objectMap = section.getValues(true);
            return new HashMap<>(objectMap);

        }
    }

    private static void sort(YamlConfiguration config) {
        sortConfigurationSection(config);

        try {
            File configFile = new File(config.getCurrentPath());
            File configDir = configFile.getParentFile();
            if (!configDir.exists()) {
                configDir.mkdirs();
            }
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sortConfigurationSection(ConfigurationSection section) {
        section.getKeys(false).stream().sorted().forEach(key -> {
            Object value = section.get(key);
            if (value instanceof ConfigurationSection) {
                sortConfigurationSection((ConfigurationSection) value);
            }
        });

        section.getKeys(false).stream().sorted().forEach(key -> {
            Object value = section.get(key);
            section.set(key, null);
            section.set(key, value);
        });
    }

    private static HashMap<String, Object> defaultSounds() {
        HashMap<String, Object> map = new HashMap<>();
        Sound[] soundList = Sound.values();
        for (Sound sound : soundList) {
            String soundString = sound.toString();
            map.put(soundString, soundString);
        }
        return map;
    }

    // ############################  DO NOT EDIT BELOW THIS LINE  ############################

    /**
     * Reload the configuration file
     */
    public static void reload(Plugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load config.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header("This is the configuration file for " + plugin.getName());
        config.options().copyDefaults(true);

        Config.init();

        try {
            config.save(configFile);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + configFile, ex);
        }
    }

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

}