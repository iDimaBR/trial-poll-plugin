package com.github.idimabr.util;

import com.google.common.collect.Maps;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemBuilder {

	private ItemStack is;
	private final Map<String, ItemMeta> cacheSkull = Maps.newHashMap();

	public ItemBuilder(Material m) {

		this(m, 1);

	}

	public ItemBuilder(String material){
		is = new ItemStack(Material.valueOf(material), 1);
	}

	public ItemBuilder(ItemStack is) {
		this.is = is;

	}

	public ItemBuilder(Material m, int quantia) {
		is = new ItemStack(m, quantia);

	}

	public ItemBuilder(Material m, int quantia, byte durabilidade) {
		is = new ItemStack(m, quantia, durabilidade);

	}

	public ItemBuilder clone() {
		return new ItemBuilder(is);

	}

	public ItemBuilder setDurability(short durabilidade) {

		is.setDurability(durabilidade);

		return this;

	}

	public ItemBuilder setQuantity(int number){
		is.setAmount(number);
		return this;
	}


	public ItemBuilder setPotion(PotionEffectType type, int duration, int amplifier) {
		PotionMeta im = (PotionMeta) is.getItemMeta();
		im.addCustomEffect(new PotionEffect(type, duration, amplifier), true);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder setName(String nome) {

		ItemMeta im = is.getItemMeta();

		im.setDisplayName(nome);

		is.setItemMeta(im);

		return this;

	}

	public ItemBuilder addUnsafeEnchantment(Enchantment ench, int level) {

		is.addUnsafeEnchantment(ench, level);

		return this;

	}

	public ItemBuilder removeEnchantment(Enchantment ench) {

		is.removeEnchantment(ench);

		return this;

	}

	public ItemBuilder setSkullOwner(String dono) {

		try {

			SkullMeta im = (SkullMeta) is.getItemMeta();

			im.setOwner(dono);

			is.setItemMeta(im);
			is.setDurability((short) 3);

		} catch (ClassCastException expected) {
		}

		return this;

	}

	public ItemBuilder addEnchant(Enchantment ench, int level) {

		ItemMeta im = is.getItemMeta();

		im.addEnchant(ench, level, true);

		is.setItemMeta(im);

		return this;

	}

	public ItemBuilder setAmount(int amount){
		is.setAmount(amount);
		return this;
	}

	public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {

		is.addEnchantments(enchantments);

		return this;

	}

	public ItemBuilder setInfinityDurability() {

		is.setDurability(Short.MAX_VALUE);

		return this;

	}

	public ItemBuilder addItemFlag(ItemFlag flag) {

		is.getItemMeta().addItemFlags(flag);

		return this;

	}

	public ItemBuilder setLore(String... lore) {

		ItemMeta im = is.getItemMeta();

		im.setLore(Arrays.asList(lore));

		is.setItemMeta(im);

		return this;

	}

	public ItemBuilder replaceLore(String key, String value){
		ItemMeta im = is.getItemMeta();

		final List<String> lore = im.getLore()
				.stream()
				.map($ -> $.replace(key, value))
				.collect(Collectors.toList());

		im.setLore(lore);

		is.setItemMeta(im);

		return this;
	}

	public ItemBuilder setLore(List<String> lore) {

		ItemMeta im = is.getItemMeta();

		im.setLore(lore);

		is.setItemMeta(im);

		return this;

	}

	public ItemBuilder setLoreIf(boolean condition, String... lore) {
		if(!condition) return this;
		ItemMeta im = is.getItemMeta();

		im.setLore(Arrays.asList(lore));

		is.setItemMeta(im);

		return this;

	}

	public ItemBuilder setLoreIf(boolean condition, List<String> lore) {
		if(!condition) return this;
		ItemMeta im = is.getItemMeta();

		im.setLore(lore);

		is.setItemMeta(im);

		return this;

	}

	public ItemBuilder removeLoreLine(String linha) {

		ItemMeta im = is.getItemMeta();

		List<String> lore = new ArrayList<>(im.getLore());

		if (!lore.contains(linha))
			return this;

		lore.remove(linha);

		im.setLore(lore);

		is.setItemMeta(im);

		return this;

	}

	public ItemBuilder removeLoreLine(int index) {

		ItemMeta im = is.getItemMeta();

		List<String> lore = new ArrayList<>(im.getLore());

		if (index < 0 || index > lore.size())
			return this;

		lore.remove(index);

		im.setLore(lore);

		is.setItemMeta(im);

		return this;

	}

	public ItemBuilder addLoreIf(boolean condition, String string) {
		if(!condition) return this;
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>();
		if (im.hasLore())
			lore = new ArrayList<>(im.getLore());
		lore.add(string);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder addLoreLine(String string) {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>();
		if (im.hasLore())
			lore = new ArrayList<>(im.getLore());
		lore.add(string);
		im.setLore(lore);
		is.setItemMeta(im);
		return this;
	}

	public ItemBuilder addLoreLine(int pos, String string) {

		ItemMeta im = is.getItemMeta();

		List<String> lore = new ArrayList<>(im.getLore());

		lore.set(pos, string);

		im.setLore(lore);

		is.setItemMeta(im);

		return this;

	}

	@SuppressWarnings("deprecation")

	public ItemBuilder setDyeColor(DyeColor cor) {

		this.is.setDurability(cor.getDyeData());

		return this;

	}

	@Deprecated

	public ItemBuilder setWoolColor(DyeColor cor) {

		if (!is.getType().toString().contains("WOOL"))
			return this;

		this.is.setDurability(cor.getWoolData());

		return this;

	}

	public ItemBuilder setLeatherArmorColor(Color color) {
		if (is.getType() == Material.LEATHER_HELMET ||
				is.getType() == Material.LEATHER_CHESTPLATE ||
				is.getType() == Material.LEATHER_LEGGINGS ||
				is.getType() == Material.LEATHER_BOOTS) {
			LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
			if (meta != null) {
				meta.setColor(color);
				is.setItemMeta(meta);
			}
		}
		return this;
	}

	public ItemStack build() {

		return is;

	}

	public ItemBuilder removeLastLoreLine() {
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>(im.getLore());
		lore.remove(lore.size()-1);
		im.setLore(lore);
		is.setItemMeta(im);

		return this;
	}

	public ItemBuilder addStringNBT(String root, String key, String value) {
		if (is == null || is.getItemMeta() == null) return this;

		ItemMeta meta = is.getItemMeta();
		NamespacedKey nbtKey = new NamespacedKey(root, key);
		meta.getPersistentDataContainer().set(nbtKey, PersistentDataType.STRING, value);
		is.setItemMeta(meta);

		return this;
	}

	public ItemBuilder addIntNBT(String root, String key, int value) {
		if (is == null || is.getItemMeta() == null) return this;

		ItemMeta meta = is.getItemMeta();
		NamespacedKey nbtKey = new NamespacedKey(root, key);
		meta.getPersistentDataContainer().set(nbtKey, PersistentDataType.INTEGER, value);
		is.setItemMeta(meta);

		return this;
	}

	public ItemBuilder addBooleanNBT(String root, String key, boolean value) {
		if (is == null || is.getItemMeta() == null) return this;

		ItemMeta meta = is.getItemMeta();
		NamespacedKey nbtKey = new NamespacedKey(root, key);
		meta.getPersistentDataContainer().set(nbtKey, PersistentDataType.BYTE, (byte) (value ? 1 : 0));
		is.setItemMeta(meta);

		return this;
	}
}