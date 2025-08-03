package tga.Items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CraftOutputPatch<T> {
    public final List<T> Recipes;
    public final ItemStack Target;

    public CraftOutputPatch(Item target, List<T> recipes) {
        Recipes = recipes;
        Target = new ItemStack(target);
    }
}