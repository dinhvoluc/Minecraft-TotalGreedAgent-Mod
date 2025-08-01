package tga.MachineRecipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import tga.Items.ItemFloat;

import java.util.List;

public class OneMainAndLuck extends OneInRecipe {
    public static OneMainAndLuck[] CreateWithTagsIngredient(int power, ItemStack main, ItemStack luck, float luckBase, ItemStack... tags) {
        OneMainAndLuck[] rt = new OneMainAndLuck[tags.length];
        for (var i = 0; i < tags.length; i++)
            rt[i] = new OneMainAndLuck(tags[i], power, main, luck, luckBase);
        return rt;
    }

    public final ItemStack MainItem;
    public final ItemFloat LuckItem;

    public OneMainAndLuck(ItemStack ingredient, int power, ItemStack main, ItemStack luck, float luckBase) {
        super(ingredient, power);
        MainItem = main;
        LuckItem = new ItemFloat(luckBase, luck);
    }

    @Override
    public ItemStack RealCraft(ItemStack stack, ItemStack[] craftedItems, Random random) {
        if (!ItemStack.areItemsEqual(stack, Ingredient) || stack.getCount() < Ingredient.getCount())
            return stack;
        ItemStack rt = stack.copy();
        rt.decrement(Ingredient.getCount());
        craftedItems[0] = MainItem.copy();
        craftedItems[1] = random.nextFloat() < LuckItem.Number ? LuckItem.Item.copy() : ItemStack.EMPTY;
        return rt;
    }

    @Override
    public List<ItemFloat> CraftChanceList() {
        return List.of(new ItemFloat(10f, MainItem.copy()), LuckItem.Copy());
    }
}