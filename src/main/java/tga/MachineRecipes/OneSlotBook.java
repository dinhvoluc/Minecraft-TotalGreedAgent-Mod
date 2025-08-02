package tga.MachineRecipes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import tga.Items.ItemFloat;
import tga.Mechanic.IItemChecker;

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
        ItemFloat[] canGet = recipe.CraftChanceList;
        for(ItemFloat i : canGet)
            CAN_PRODUCE_LIST.computeIfAbsent(i.Item.getItem(), k -> new ArrayList<>()).add(recipe);
    }

    public void Registers(OneInRecipe... recipes) {
        for(OneInRecipe i : recipes) Register(i);
    }

    public @Nullable OneInRecipe CraftWith(ItemStack stack) {
        if (stack.isEmpty()) return null;
        OneInRecipe recipe = RECIPE_MAP.get(stack.getItem());
        return recipe != null && recipe.CanCraft(stack) ? recipe : null;
    }

    public void SearchAppend(List<List<OneInRecipe>> showingResult, String name, boolean listMode, boolean canCraft, IItemChecker checker) {


        //dummy
        if (listMode) {
            for(Map.Entry<Item, List<OneInRecipe>> rep : CAN_PRODUCE_LIST.entrySet())
            {
                showingResult.add(rep.getValue());
            }

        }
        else {
            for (Map.Entry<Item, List<OneInRecipe>> rep : CAN_PRODUCE_LIST.entrySet())
                for (OneInRecipe i : rep.getValue())
                {
                    List<OneInRecipe> a = new ArrayList<>();
                    a.add(i);
                    showingResult.add(a);
                }
        }
    }
}