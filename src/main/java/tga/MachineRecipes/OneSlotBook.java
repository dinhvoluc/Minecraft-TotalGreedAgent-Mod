package tga.MachineRecipes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tga.Items.CraftOutputPatch;
import tga.Items.ItemFloat;
import tga.Mechanic.IItemChecker;

import java.util.*;

public class OneSlotBook {
    private final Map<Item, OneInRecipe> RECIPE_MAP = new HashMap<>();
    private final Map<Item, List<OneInRecipe>> CAN_PRODUCE_LIST = new HashMap<>();

    public boolean CanAccept(ItemStack stack) {
        return !stack.isEmpty() && RECIPE_MAP.containsKey(stack.getItem());
    }

    public void Register(OneInRecipe recipe) {
        RECIPE_MAP.put(recipe.Ingredient.getItem(), recipe);
        ItemFloat[] canGet = recipe.CraftChanceList;
        for (ItemFloat i : canGet)
            CAN_PRODUCE_LIST.computeIfAbsent(i.Item.getItem(), k -> new ArrayList<>()).add(recipe);
    }

    public void Registers(OneInRecipe... recipes) {
        for (OneInRecipe i : recipes) Register(i);
    }

    public @Nullable OneInRecipe CraftWith(ItemStack stack) {
        if (stack.isEmpty()) return null;
        OneInRecipe recipe = RECIPE_MAP.get(stack.getItem());
        return recipe != null && recipe.CanCraft(stack) ? recipe : null;
    }

    public void SearchAppend(List<CraftOutputPatch<OneInRecipe>> showingResult, String name, boolean canCraft, IItemChecker checker) {
        if (name == null || name.isEmpty()) {
            //No search mode
            if (canCraft)
                for (Map.Entry<Item, List<OneInRecipe>> rep : CAN_PRODUCE_LIST.entrySet()) {
                    List<OneInRecipe> chunkedList = new ArrayList<>();
                    for (OneInRecipe i : rep.getValue())
                        if (checker.HaveEnough(i.Ingredient)) chunkedList.add(i);
                    if (chunkedList.isEmpty()) continue;
                    showingResult.add(new CraftOutputPatch<>(rep.getKey(), chunkedList));
                }
            else
                for (Map.Entry<Item, List<OneInRecipe>> rep : CAN_PRODUCE_LIST.entrySet())
                    showingResult.add(new CraftOutputPatch<>(rep.getKey(), rep.getValue()));
        } else {
            //Search mode
            String match = name.toLowerCase(Locale.ROOT);
            if (canCraft)
                for (Map.Entry<Item, List<OneInRecipe>> rep : CAN_PRODUCE_LIST.entrySet()) {
                    if (!rep.getKey().getName().getString().toLowerCase(Locale.ROOT).contains(match)) continue;
                    List<OneInRecipe> chunkedList = new ArrayList<>();
                    for (OneInRecipe i : rep.getValue())
                        if (checker.HaveEnough(i.Ingredient)) chunkedList.add(i);
                    if (chunkedList.isEmpty()) continue;
                    showingResult.add(new CraftOutputPatch<>(rep.getKey(), chunkedList));
                }
            else
                for (Map.Entry<Item, List<OneInRecipe>> rep : CAN_PRODUCE_LIST.entrySet()) {
                    if (!rep.getKey().getName().getString().toLowerCase(Locale.ROOT).contains(match)) continue;
                    showingResult.add(new CraftOutputPatch<>(rep.getKey(), rep.getValue()));
                }
        }
    }
}