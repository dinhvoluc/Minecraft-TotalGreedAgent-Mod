package tga.Str;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public class BoxStackProperty {
    public final Supplier<Item> WhenEmpty;
    public final Supplier<Item> WhenFilled;
    public final int BoxSize;
    public final Text GUI_NAME;

    public BoxStackProperty(Supplier<Item> empty, Supplier<Item> filled, int boxSize, Text guiName) {
        WhenEmpty = empty;
        WhenFilled = filled;
        BoxSize = boxSize;
        GUI_NAME = guiName;
    }

    public ItemStack CreateEmptyStack(int count) {
        return new ItemStack(WhenEmpty.get(), count);
    }
    public ItemStack CreateFilledStack(int count) {
        return new ItemStack(WhenFilled.get(), count);
    }
}