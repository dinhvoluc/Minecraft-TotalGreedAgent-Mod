package tga.MachineRecipes;

import net.minecraft.item.ItemStack;
import tga.Items.ItemInt;
import tga.TGAHelper;

import java.util.List;

public class OneMainAndLuck extends OneInRecipe {

    public final ItemStack MainItem;
    public final ItemInt LuckItem;

    public OneMainAndLuck(ItemStack ingredient, int power, ItemStack main, ItemStack luck, int luckBase) {
        super(ingredient, power);
        MainItem = main;
        LuckItem = new ItemInt(luckBase, luck);
    }

    @Override
    public ItemStack RealCraft(ItemStack stack, ItemStack[] craftedItems) {
        if (!ItemStack.areItemsEqual(stack, Ingredient) || stack.getCount() < Ingredient.getCount())
            return stack;
        ItemStack rt = stack.copy();
        rt.decrement(Ingredient.getCount());
        craftedItems[0] = MainItem.copy();
        craftedItems[1] = TGAHelper.Rnd.nextInt(RANDOM_SPACE) < LuckItem.Number ? LuckItem.Item.copy() : ItemStack.EMPTY;
        return rt;
    }

    @Override
    public List<ItemInt> CraftChanceList() {
        return List.of(new ItemInt(FIXED_CRAFT, MainItem.copy()), LuckItem.Copy());
    }
}