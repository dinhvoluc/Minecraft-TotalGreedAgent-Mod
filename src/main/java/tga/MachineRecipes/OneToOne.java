package tga.MachineRecipes;

import net.minecraft.item.ItemStack;
import tga.Items.ItemInt;

import java.util.List;

public class OneToOne extends OneInRecipe {

    public final ItemStack Result;

    public OneToOne(ItemStack ingredient, int power, ItemStack result) {
        super(ingredient, power);
        Result = result;
    }

    @Override
    public ItemStack RealCraft(ItemStack stack, ItemStack[] craftedItems) {
        if (!ItemStack.areItemsEqual(stack, Ingredient) || stack.getCount() < Ingredient.getCount())
            return stack;
        ItemStack rt = stack.copy();
        rt.decrement(Ingredient.getCount());
        craftedItems[0] = Result.copy();
        craftedItems[1] = ItemStack.EMPTY;
        return rt;
    }

    @Override
    public List<ItemInt> CraftChanceList() {
        return List.of(new ItemInt(FIXED_CRAFT, Result.copy()));
    }
}