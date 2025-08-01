package tga.Items;

import net.minecraft.item.ItemStack;
import tga.TotalGreedyAgent;

public class ItemFloat {
    public final float Number;
    public final ItemStack Item;

    public ItemFloat(float number, ItemStack item) {
        Number = number;
        Item = item.copy();
    }

    public ItemFloat Copy() {
        return new ItemFloat(Number, Item.copy());
    }
}