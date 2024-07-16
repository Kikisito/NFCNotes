/*
 * Copyright (C) 2020  Kikisito (Kyllian)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package es.kikisito.nfcnotes;

import es.kikisito.nfcnotes.enums.NFCConfig;
import es.kikisito.nfcnotes.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NFCNote {
    private final ItemStack itemStack;
    private final String name;
    private final List<String> lore;
    private final Double value;

    public NFCNote(Main plugin, ItemStack itemStack){
        this.itemStack = itemStack;
        this.name = itemStack.getItemMeta().getDisplayName();
        this.lore = itemStack.getItemMeta().getLore();

        NamespacedKey noteValue = new NamespacedKey(plugin, "noteValue");
        this.value = itemStack.getItemMeta().getPersistentDataContainer().get(noteValue, PersistentDataType.DOUBLE);
    }

    public ItemStack getItemStack(){ return this.itemStack; }

    public String getDisplayName(){ return this.name; }

    public List<String> getLore(){ return this.lore; }

    public Double getValue(){ return this.value; }

    public static ItemStack createNFCNoteItem(Main plugin, String name, List<String> lore, String material, String playername, DecimalFormat decimalFormat, Double money, Integer amount){
        // Note value as string
        String formattedMoney = decimalFormat.format(money);

        // Note item
        ItemStack is = new ItemStack(Material.valueOf(material.toUpperCase()), amount);
        ItemMeta im = is.getItemMeta();

        // Note display name
        im.setDisplayName(Utils.parseMessage(name).replace("{money}", formattedMoney).replace("{issuer}", playername));

        // Parse lore
        List<String> loreList = new ArrayList<>();
        for(String s : lore) loreList.add(Utils.parseMessage(s).replace("{money}", formattedMoney).replace("{issuer}", playername));
        im.setLore(loreList);

        // Note value is stored using the item's persistent data container, so its internal data its hidden and all note's public values can be modified
        NamespacedKey noteIdentifier = new NamespacedKey(plugin, "noteIdentifier");
        NamespacedKey noteValue = new NamespacedKey(plugin, "noteValue");
        im.getPersistentDataContainer().set(noteIdentifier, PersistentDataType.STRING, NFCConfig.NOTE_UUID.getString());
        im.getPersistentDataContainer().set(noteValue, PersistentDataType.DOUBLE, money);

        // Glint
        if(NFCConfig.NOTE_GLINT_ENABLED.getBoolean()){
            Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(NFCConfig.NOTE_GLINT_ENCHANTMENT.getString().toLowerCase()));
            int enchantLevel = NFCConfig.NOTE_GLINT_ENCHANTMENT_LEVEL.getInt();
            im.addEnchant(enchant, enchantLevel, true);

            // If set, hide enchant flag
            if(NFCConfig.NOTE_GLINT_HIDE_ENCHANTMENT_FLAG.getBoolean()){
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }

        // Custom Model Data for texture packs
        im.setCustomModelData(NFCConfig.NOTE_CUSTOM_MODEL_DATA_INTEGER.getInt());

        // Set ItemMeta
        is.setItemMeta(im);
        return is;
    }

    public static boolean isNFCNote(Main plugin, ItemStack itemStack){
        NamespacedKey noteIdentifier = new NamespacedKey(plugin, "noteIdentifier");
        NamespacedKey noteValue = new NamespacedKey(plugin, "noteValue");

        // If the item has no meta, it's not a note
        // If itemStack is null, the user has clicked something with its hand empty
        if(itemStack == null || itemStack.getItemMeta() == null) return false;

        // Get item's persistent data container and check its values
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        if(!pdc.has(noteIdentifier) || !pdc.has(noteValue)) {
            return false;
        }

        // It's a note, but it's an updated note? (User may have changed notes uuid)
        return pdc.get(noteIdentifier, PersistentDataType.STRING).equals(NFCConfig.NOTE_UUID.getString());
    }

    public static boolean isLegacyNFCNote(ItemStack itemStack){
        // No hay item o no tiene meta
        if(itemStack == null || !itemStack.hasItemMeta()) return false;

        ItemMeta im = itemStack.getItemMeta();
        if(!im.hasAttributeModifiers() || im.getAttributeModifiers(Attribute.GENERIC_LUCK) == null || im.getAttributeModifiers(Attribute.GENERIC_LUCK).iterator().next() == null) {
            return false;
        }

        return im.getAttributeModifiers(Attribute.GENERIC_LUCK).iterator().next().getName().equalsIgnoreCase(NFCConfig.NOTE_UUID.getString());
    }
}
