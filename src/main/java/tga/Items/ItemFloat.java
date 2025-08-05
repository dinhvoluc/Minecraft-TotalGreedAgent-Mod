package tga.Items;

import net.minecraft.item.ItemStack;
import tga.TGARecipes;

public class ItemFloat {
    public final float Number;
    public final ItemStack Item;

    public ItemFloat(float number, ItemStack item) {
        Number = number;
        Item = item.copy();
    }

    public static ItemFloat of(float number, ItemStack item) {
        return new ItemFloat(number, item);
    }

    public static ItemFloat of(ItemStack item) {
        return new ItemFloat(TGARecipes.FIXED_CRAFT_CHANCE, item);
    }

    public ItemFloat Copy() {
        return new ItemFloat(Number, Item.copy());
    }
}