package tga.WorkBook;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tga.BlockEntity.MetalWorkbenchTile;
import tga.Items.CraftOutputPatch;
import tga.Mechanic.IItemChecker;
import tga.WorkBook.WorkRecipes.MetalWorkRecipe;

import java.util.*;

public class MetalWorkBook implements ICommonBook {
    private final Map<Item, List<ACommonRecipe>>[] RECIPE_MAP;
    private final Map<Item, List<ACommonRecipe>> CAN_PRODUCE_LIST = new HashMap<>();

    public MetalWorkBook(int modeSize) {
        RECIPE_MAP = new Map[modeSize];
        for(int i = 0; i < modeSize; i++)
            RECIPE_MAP[i] = new HashMap<>();
    }

    @Override
    public void SearchAppend(List<CraftOutputPatch<ACommonRecipe>> showingResult, String name, boolean canCraft, IItemChecker checker) {
        if (name == null || name.isEmpty()) {
            //No search mode
            if (canCraft)
                for (Map.Entry<Item, List<ACommonRecipe>> rep : CAN_PRODUCE_LIST.entrySet()) {
                    List<ACommonRecipe> chunkedList = new ArrayList<>();
                    for (ACommonRecipe i : rep.getValue())
                        if (checker.HaveAll(i.Ingredient)) chunkedList.add(i);
                    if (chunkedList.isEmpty()) continue;
                    showingResult.add(new CraftOutputPatch<>(rep.getKey(), chunkedList));
                }
            else
                for (Map.Entry<Item, List<ACommonRecipe>> rep : CAN_PRODUCE_LIST.entrySet())
                    showingResult.add(new CraftOutputPatch<>(rep.getKey(), rep.getValue()));
        } else {
            //Search mode
            String match = name.toLowerCase(Locale.ROOT);
            if (canCraft)
                for (Map.Entry<Item, List<ACommonRecipe>> rep : CAN_PRODUCE_LIST.entrySet()) {
                    if (!rep.getKey().getName().getString().toLowerCase(Locale.ROOT).contains(match)) continue;
                    List<ACommonRecipe> chunkedList = new ArrayList<>();
                    for (ACommonRecipe i : rep.getValue())
                        if (checker.HaveAll(i.Ingredient)) chunkedList.add(i);
                    if (chunkedList.isEmpty()) continue;
                    showingResult.add(new CraftOutputPatch<>(rep.getKey(), chunkedList));
                }
            else
                for (Map.Entry<Item, List<ACommonRecipe>> rep : CAN_PRODUCE_LIST.entrySet()) {
                    if (!rep.getKey().getName().getString().toLowerCase(Locale.ROOT).contains(match)) continue;
                    showingResult.add(new CraftOutputPatch<>(rep.getKey(), rep.getValue()));
                }
        }
    }

    public void Registers(MetalWorkRecipe recipe) {
        CAN_PRODUCE_LIST.computeIfAbsent(recipe.Result.getItem(), k -> new ArrayList<>()).add(recipe);
        for (ItemStack inItem : recipe.Ingredient)
            RECIPE_MAP[recipe.MachineMode].computeIfAbsent(inItem.getItem(), k -> new ArrayList<>()).add(recipe);
    }
}