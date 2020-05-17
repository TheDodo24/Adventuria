package de.thedodo24.commonPackage.utils;

import com.google.common.collect.Lists;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigFile {

    private String configurationName;

    private File configurationFolder;
    private File configurationFile;

    private FileConfiguration configuration;

    public ConfigFile(File configurationFolder, String fileName) {
        this.configurationFolder = configurationFolder;
        this.configurationName = fileName;

        init();
    }

    public FileConfiguration init() {
        if(!configurationFolder.exists())
            configurationFolder.mkdirs();

        try {

            if(!(this.configurationFile = new File(configurationFolder, configurationName)).exists())
                this.configurationFile.createNewFile();

            if(configuration == null)
                this.configuration = YamlConfiguration.loadConfiguration(configurationFile);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.configuration;
    }

    public boolean save() {
        try {
            configuration.save(configurationFile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean create(String path, Object obj) {
        try {
            if(!configuration.contains(path))
                return set(path, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean set(String path, Object obj) {
        try {
            configuration.set(path, obj);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean contains(String path) {
        return get(path) != null;
    }

    public List<String> getKeys() {
        try {
            return Lists.newArrayList(configuration.getKeys(true));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object get(String path) {
        try {
            return configuration.get(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getString(String path) {
        try {
            return configuration.getString(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getStringList(String path) {
        try {
            return configuration.getStringList(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getInt(String path) {
        try {
            return configuration.getInt(path);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Integer> getIntList(String path) {
        try {
            return configuration.getIntegerList(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public double getDouble(String path) {
        try {
            return configuration.getDouble(path);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Double> getDoubleList(String path) {
        try {
            return configuration.getDoubleList(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long getLong(String path) {
        try {
            return configuration.getLong(path);
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public List<Long> getLongList(String path) {
        try {
            return configuration.getLongList(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
