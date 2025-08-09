package tga.WorkBook.WorkRecipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import tga.Items.ItemFloat;

public class OneToTwo extends OneInRecipe {
    public static OneToTwo[] CreateWithTagsIngredient(int power, ItemStack result, ItemStack result2, ItemStack... tags) {
        OneToTwo[] rt = new OneToTwo[tags.length];
        for (var i = 0; i < tags.length; i++)
            rt[i] = new OneToTwo(tags[i], power, result, result2);
        return rt;
    }

    public OneToTwo(ItemStack ingredient, int power, ItemStack result, ItemStack result2) {
        super(ingredient, power);
        CraftChanceList = new ItemFloat[] {ItemFloat.of(result), ItemFloat.of(result2)};
    }

    @Override
    public ItemStack RealCraft(ItemStack stack, ItemStack[] craftedItems, Random random) {
        if (!ItemStack.areItemsEqual(stack, Ingredient) || stack.getCount() < Ingredient.getCount())
            return stack;
        ItemStack rt = stack.copy();
        rt.decrement(Ingredient.getCount());
        craftedItems[0] = CraftChanceList[0].Item.copy();
        craftedItems[1] = CraftChanceList[1].Item.copy();
        return rt;
    }
}