package com.github.idimabr.util;

import com.google.common.io.ByteStreams;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ConfigUtil extends FileConfiguration {

    private final Plugin owningPlugin;
    private final String name;
    protected File rootFile;
    private FileConfiguration configuration;

    public ConfigUtil(Plugin owningPlugin, String name) {
        if (!name.endsWith(".yml")) name += ".yml";

        this.owningPlugin = owningPlugin;
        this.name = name;

        this.rootFile = new File(owningPlugin.getDataFolder(), name);
        load();
    }

    @SneakyThrows
    public ConfigUtil(Plugin owningPlugin, String name, String root) {
        if (!name.endsWith(".yml")) name += ".yml";

        this.owningPlugin = owningPlugin;
        this.name = name;

        File directory = new File(owningPlugin.getDataFolder(), root);
        if (!directory.exists())
            directory.mkdirs();

        File file = new File(directory, name);
        file.createNewFile();

        this.rootFile = file;
        load();
    }

    public void load() {
        if (!rootFile.exists()) {
            rootFile.getParentFile().mkdirs();
            owningPlugin.saveResource(name, false);
        }

        this.configuration = new YamlConfiguration();
        try {
            configuration.load(rootFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return this.configuration.getBoolean(s, b);
    }

    @Override
    public List<Byte> getByteList(String s) {
        return this.configuration.getByteList(s);
    }

    @Override
    public ItemStack getItemStack(String s) {
        return this.configuration.getItemStack(s);
    }

    public Character getChar(String s){
        return this.configuration.getString(s).toCharArray()[0];
    }

    public Character getChar(String s, String s1){
        return this.configuration.isSet(s) ? this.configuration.getString(s).toCharArray()[0] : s1.toCharArray()[0];
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String s, OfflinePlayer offlinePlayer) {
        return this.configuration.getOfflinePlayer(s, offlinePlayer);
    }

    @Override
    public Configuration getRoot() {
        return this.configuration.getRoot();
    }

    public void save() {
        try {
            this.configuration.save(rootFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCurrentPath() {
        return this.configuration.getCurrentPath();
    }

    @Override
    public List<Map<?, ?>> getMapList(String s) {
        return this.configuration.getMapList(s);
    }

    @Override
    public ConfigurationSection createSection(String s) {
        return this.configuration.createSection(s);
    }

    @Override
    public void load(Reader reader) throws IOException, InvalidConfigurationException {
        this.configuration.load(reader);
    }

    @Override
    public List<Double> getDoubleList(String s) {
        return this.configuration.getDoubleList(s);
    }

    @Override
    public List<Float> getFloatList(String s) {
        return this.configuration.getFloatList(s);
    }

    @Override
    public ConfigurationSection createSection(String s, Map<?, ?> map) {
        return this.configuration.createSection(s, map);
    }

    @Override
    public Vector getVector(String s) {
        return this.configuration.getVector(s);
    }

    @Override
    public Set<String> getKeys(boolean b) {
        return this.configuration.getKeys(b);
    }

    @Override
    public List<?> getList(String s, List<?> list) {
        return this.configuration.getList(s, list);
    }

    @Override

    public List<Integer> getIntegerList(String s) {
        return this.configuration.getIntegerList(s);
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String s) {
        return this.configuration.getOfflinePlayer(s);
    }

    public <E extends Enum<E>> E getEnum(String path, Class<E> enumClazz) {
        return Enum.valueOf(enumClazz, path);
    }

    @Override
    public void load(File file) throws IOException, InvalidConfigurationException {
        this.configuration.load(file);
    }

    @Override
    public boolean isItemStack(String s) {
        return this.configuration.isItemStack(s);
    }

    @Override
    public boolean isConfigurationSection(String s) {
        return this.configuration.isConfigurationSection(s);
    }

    @Override
    public boolean isOfflinePlayer(String s) {
        return this.configuration.isOfflinePlayer(s);
    }

    @Override
    public int getInt(String s) {
        return this.configuration.getInt(s);
    }

    @Override
    public boolean isVector(String s) {
        return this.configuration.isVector(s);
    }

    @Override
    public void load(String file) throws IOException, InvalidConfigurationException {
        this.configuration.load(file);
    }

    @Override
    public Configuration getDefaults() {
        return this.configuration.getDefaults();
    }

    @Override
    public void setDefaults(Configuration defaults) {
        this.configuration.setDefaults(defaults);
    }

    @Override
    public Vector getVector(String s, Vector vector) {
        return this.configuration.getVector(s, vector);
    }

    @Override
    public List<Short> getShortList(String s) {
        return this.configuration.getShortList(s);
    }

    @Override
    public Map<String, Object> getValues(boolean b) {
        return this.configuration.getValues(b);
    }

    @Override
    public boolean isInt(String s) {
        return this.configuration.isInt(s);
    }

    @Override
    public double getDouble(String s, double v) {
        return this.configuration.getDouble(s, v);
    }

    @Override
    public boolean isLong(String s) {
        return this.configuration.isLong(s);
    }

    @Override
    public boolean contains(String s) {
        return this.configuration.contains(s);
    }

    @Override
    public List<String> getStringList(String s) {
        return this.configuration.getStringList(s).stream().map(line -> line.replace("&","§")).collect(Collectors.toList());
    }

    @Override
    public ConfigurationSection getConfigurationSection(String s) {
        return this.configuration.getConfigurationSection(s);
    }

    @Override
    public Object get(String s, Object o) {
        return this.configuration.get(s, o);
    }

    @Override
    public long getLong(String s) {
        return this.configuration.getLong(s);
    }

    @Override
    public int getInt(String s, int i) {
        return this.configuration.getInt(s, i);
    }

    @Override
    public Color getColor(String s) {
        return this.configuration.getColor(s);
    }

    @Override
    public boolean isBoolean(String s) {
        return this.configuration.isBoolean(s);
    }

    @Override
    public void addDefault(String path, Object value) {
        this.configuration.addDefault(path, value);
    }

    @Override
    public void set(String s, Object o) {
        this.configuration.set(s, o);
    }

    @Override
    public FileConfigurationOptions options() {
        return this.configuration.options();
    }

    @Override
    public void addDefaults(Configuration defaults) {
        this.configuration.addDefaults(defaults);
    }

    @Override
    public Object get(String s) {
        return this.configuration.get(s);
    }

    @Override
    public boolean isList(String s) {
        return this.configuration.isList(s);
    }

    @Override
    public boolean isColor(String s) {
        return this.configuration.isColor(s);
    }

    @Override
    public List<Long> getLongList(String s) {
        return this.configuration.getLongList(s);
    }

    @Override
    public boolean getBoolean(String s) {
        return this.configuration.getBoolean(s);
    }

    @Override
    public ItemStack getItemStack(String s, ItemStack itemStack) {
        return this.configuration.getItemStack(s, itemStack);
    }

    @Override
    public ConfigurationSection getDefaultSection() {
        return this.configuration.getDefaultSection();
    }

    @Override
    public void save(File file) throws IOException {
        this.configuration.save(file);
    }

    public void saveWithComments() {
        final String text;
        try {
            text = new String(ByteStreams.toByteArray(
                    Files.newInputStream(rootFile.toPath())), Charset.defaultCharset()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final YamlConfiguration newestConfiguration = new YamlConfiguration();
        try {
            newestConfiguration.loadFromString(text);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        YamlConfiguration.loadConfiguration(rootFile).setDefaults(newestConfiguration);
    }

    @Override
    public String getString(String s, String s1) {
        return this.configuration.getString(s, s1).replace("&","§");
    }

    @Override
    public String getName() {
        return this.configuration.getName();
    }

    @Override
    public Color getColor(String s, Color color) {
        return this.configuration.getColor(s, color);
    }

    @Override
    public String saveToString() {
        return this.configuration.saveToString();
    }

    @Override
    public String getString(String s) {
        return this.configuration.getString(s).replace("&","§");
    }

    @Override
    public boolean isString(String s) {
        return this.configuration.isString(s);
    }

    @Override
    public List<Boolean> getBooleanList(String s) {
        return this.configuration.getBooleanList(s);
    }

    @Override
    public void addDefaults(Map<String, Object> defaults) {
        this.configuration.addDefaults(defaults);
    }

    @Override
    public double getDouble(String s) {
        return this.configuration.getDouble(s);
    }

    @Override
    public List<?> getList(String s) {
        return this.configuration.getList(s);
    }

    public void loadFromString(String s) throws InvalidConfigurationException {
        this.configuration.loadFromString(s);
    }

    @Override
    protected String buildHeader() {
        return null;
    }

    @Override
    public List<Character> getCharacterList(String s) {
        return this.configuration.getCharacterList(s);
    }


    @Override
    public boolean isSet(String s) {
        return this.configuration.isSet(s);
    }

    @Override
    public boolean isDouble(String s) {
        return this.configuration.isDouble(s);
    }

    @Override
    public ConfigurationSection getParent() {
        return this.configuration.getParent();
    }

    @Override
    public long getLong(String s, long l) {
        return this.configuration.getLong(s, l);
    }

    public void reload() {
        this.configuration = new YamlConfiguration();
        try {
            this.configuration.load(rootFile);
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().warning("Nao foi possivel recarregar a config!");
        }

    }
}