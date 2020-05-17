package de.thedodo24.commonPackage.utils;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    static ItemStack item;
    static ItemMeta itemmeta;


    public ItemBuilder(ItemStack item){
        this.item = item;
        this.itemmeta = item.getItemMeta();
    }


    /**
     * Modify a {@link ItemStack} from the current contents of this
     * builder.
     */
    public static Builder modify() {
        return new Builder();
    }


    public static final class Builder {
        /**
         * Modify the lore from the current {@link ItemStack}
         * @param lore
         * @return null
         */
        public Builder setLore(String... lore){
            List<String> loreList = new ArrayList<String>();
            for(String lores : lore){
                loreList.add(lores);
            }
            itemmeta.setLore(loreList);
            return this;
        }

        /**
         * Add an Enchantment to the current {@link ItemStack}
         * @param entchantment
         * @return null
         */
        public Builder addEnchantment(Enchantment entchantment, int level){
            itemmeta.addEnchant(entchantment, level, false);
            return this;
        }

        /**
         * Remove an Enchantment from the current {@link ItemStack}
         * @param entchantment
         * @return null
         */
        public Builder removeEnchantment(Enchantment entchantment){
            itemmeta.removeEnchant(entchantment);
            return this;
        }

        /**
         * Set the Displayname to the current {@link ItemStack}
         * @param display
         * @return null
         */
        public Builder setDisplayName(String display){
            itemmeta.setDisplayName(display);
            return this;
        }

        /**
         * Set Unbreakable to the current {@link ItemStack}
         * @param unbreakable
         * @return null
         */
        public Builder setUnbreakable(boolean unbreakable){
            itemmeta.setUnbreakable(unbreakable);
            return this;
        }


        /**
         * Hide this Flag(NBT) to the current {@link ItemStack}
         * @param itemFlag
         * @return null
         */
        public Builder hideFlag(ItemFlag itemFlag){
            itemmeta.addItemFlags(itemFlag);
            return this;
        }

        /**
         * Show this Flag(NBT) to the current {@link ItemStack}
         * @param itemFlag
         * @return null
         */
        public Builder showFlag(ItemFlag itemFlag){
            itemmeta.removeItemFlags(itemFlag);
            return this;
        }

        /**
         * Hide all Flags exept this to the current {@link ItemStack}
         * @param itemFlag
         * @return null
         */
        public Builder hideFlagsExcept(ItemFlag itemFlag){
            hideFlags();
            showFlag(itemFlag);
            return this;
        }

        /**
         * Show all Flags exept this to the current {@link ItemStack}
         * @param itemFlag
         * @return null
         */
        public Builder showFlagsExcept(ItemFlag itemFlag){
            showFlags();
            hideFlag(itemFlag);
            return this;
        }

        /**
         * Hide all Flags to the current {@link ItemStack}
         * @return null
         */
        public Builder hideFlags(){
            itemmeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemmeta.addItemFlags(ItemFlag.HIDE_DESTROYS);
            itemmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemmeta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
            itemmeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            itemmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            return this;
        }

        /**
         * Show all Flags to the current {@link ItemStack}
         * @return null
         */
        public Builder showFlags(){
            itemmeta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemmeta.removeItemFlags(ItemFlag.HIDE_DESTROYS);
            itemmeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemmeta.removeItemFlags(ItemFlag.HIDE_PLACED_ON);
            itemmeta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            itemmeta.removeItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            return this;
        }

        /**
         * Build the modified {@link ItemStack}
         * @return {@link ItemStack}
         */
        public ItemStack build(){
            item.setItemMeta(itemmeta);
            return item;
        }
    }
}