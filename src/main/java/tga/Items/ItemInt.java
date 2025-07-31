package tga.Items;

import net.minecraft.item.ItemStack;

public class ItemInt {
    public int Number;
    public ItemStack Item;

    public ItemInt(int number, ItemStack item) {
        Number = number;
        Item = item;
    }

    public ItemInt Copy() {
        return new ItemInt(Number, Item.copy());
    }
}