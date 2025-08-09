package tga.WorkBook.WorkRecipes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import tga.Items.ItemFloat;
import tga.TGAHelper;

import java.util.Arrays;

public class OneToOne extends OneInRecipe {
    public static OneToOne[] CreateAutoBlanced(Item result, int baseValue, int power, Object... tags) {
        OneToOne[] rt = new OneToOne[tags.length];
        int mapped = 0;
        int value = baseValue;
        int maxStackOfResult = result.getMaxCount();
        int powerCost = power;
        int rsCount = 1;
        int idCount = 1;
        for (Object i : tags) {
            if (i instanceof Item item) {
                if (idCount <= 0 || rsCount <= 0 || rsCount > maxStackOfResult || idCount > item.getMaxCount()) continue;
                rt[mapped++] = new OneToOne(new ItemStack(item, idCount), powerCost, new ItemStack(result, rsCount));
                continue;
            }
            value = (int) i;
            int minMul = TGAHelper.Num_MinMul(value, baseValue);
            rsCount = minMul / baseValue;
            idCount = minMul / value;
            powerCost = power * rsCount;
        }
        return mapped == rt.length ? rt : Arrays.copyOfRange(rt, 0, mapped);
    }

    public static OneToOne[] CreateWithTagsIngredient(int power, ItemStack result, ItemStack... tags) {
        OneToOne[] rt = new OneToOne[tags.length];
        for (var i = 0; i < tags.length; i++)
            rt[i] = new OneToOne(tags[i], power, result);
        return rt;
    }

    public OneToOne(ItemStack ingredient, int power, ItemStack result) {
        super(ingredient, power);
        CraftChanceList = new ItemFloat[] {ItemFloat.of(result)};
    }

    @Override
    public ItemStack RealCraft(ItemStack stack, ItemStack[] craftedItems, Random random) {
        if (!ItemStack.areItemsEqual(stack, Ingredient) || stack.getCount() < Ingredient.getCount())
            return stack;
        ItemStack rt = stack.copy();
        rt.decrement(Ingredient.getCount());
        craftedItems[0] = CraftChanceList[0].Item.copy();
        craftedItems[1] = ItemStack.EMPTY;
        return rt;
    }
}