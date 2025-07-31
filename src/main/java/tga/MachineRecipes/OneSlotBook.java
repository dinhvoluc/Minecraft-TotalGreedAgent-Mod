package tga.MachineRecipes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tga.Items.ItemInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneSlotBook {
    private final Map<Item, OneInRecipe> RECIPE_MAP = new HashMap<>();
    private final Map<Item, List<OneInRecipe>> CAN_PRODUCE_LIST = new HashMap<>();
    public boolean CanAccept(ItemStack stack) {
        return  !stack.isEmpty() && RECIPE_MAP.containsKey(stack.getItem());
    }

    public void Register(OneInRecipe recipe){
        RECIPE_MAP.put(recipe.Ingredient.getItem(), recipe);
        List<ItemInt> canGet = recipe.CraftChanceList();
        for(ItemInt i : canGet)
            CAN_PRODUCE_LIST.computeIfAbsent(i.Item.getItem(), k -> new ArrayList<>()).add(recipe);
    }

    public @Nullable OneInRecipe CraftWith(ItemStack stack) {
        if (stack.isEmpty()) return null;
        OneInRecipe recipe = RECIPE_MAP.get(stack.getItem());
        return recipe != null && recipe.CanCraft(stack) ? recipe : null;
    }
}