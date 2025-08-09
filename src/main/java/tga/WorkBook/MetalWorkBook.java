package tga.WorkBook;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tga.Items.CraftOutputPatch;
import tga.Mechanic.ICraftProvider;
import tga.Mechanic.IItemChecker;
import tga.WorkBook.WorkRecipes.MetalWorkRecipe;

import java.util.*;

public class MetalWorkBook implements ICommonBook {
    private final Map<Item, MetalWorkRecipe>[] RECIPE_MAP;
    private final Map<Item, List<ACommonRecipe>> CAN_PRODUCE_LIST = new HashMap<>();

    public MetalWorkBook(int modeSize) {
        RECIPE_MAP = new Map[modeSize];
        for(int i = 0; i < modeSize; i++)
            RECIPE_MAP[i] = new HashMap<>();
    }

    public MetalWorkRecipe GetNextCraft(ICraftProvider inputSlots, int mode) {
        //search for first recipe exist
        int slotCount = inputSlots.GetCraftInputSize();
        Map<Item, MetalWorkRecipe> mode_book = RECIPE_MAP[mode];
        for(var i = 0; i < slotCount; i++)
        {
            ItemStack stackInSlot = inputSlots.GetCraftInputStack(i);
            if (stackInSlot.isEmpty()) continue;
            MetalWorkRecipe recipe = mode_book.get(stackInSlot.getItem());
            if (recipe == null) continue;
            //check enough
            Item needItem = recipe.Ingredient[0].getItem();
            int needAmounth = recipe.Ingredient[0].getCount() - stackInSlot.getCount();
            while (needAmounth > 0)
                for(int j = i + 1; j < slotCount; j++){
                    ItemStack checkMergerSlot = inputSlots.GetCraftInputStack(j);
                    if (checkMergerSlot.isOf(needItem)) needAmounth -= checkMergerSlot.getCount();
                }
            if (needAmounth <= 0) return recipe;
        }
        return null;
    }
    public boolean RealCraft(MetalWorkRecipe recipe, ICraftProvider inputSlots) {
        //search for first recipe exist
        int slotCount = inputSlots.GetCraftInputSize();
        ItemStack[] setSlot = new ItemStack[slotCount];
        Item needItem = recipe.Ingredient[0].getItem();
        int needCount = recipe.Ingredient[0].getCount();
        for(var i = 0; i < slotCount; i++)
        {
            ItemStack stackInSlot = inputSlots.GetCraftInputStack(i);
            if (!stackInSlot.isOf(needItem)) continue;
            int stackHave = stackInSlot.getCount();
            if (stackHave >= needCount) {
                if (stackHave == needCount)
                    setSlot[i] = ItemStack.EMPTY;
                else {
                    setSlot[i] = stackInSlot.copy();
                    setSlot[i].setCount(stackHave - needCount);
                }
                needCount = 0;
                break;
            }
            needCount -= stackHave;
            setSlot[i] = ItemStack.EMPTY;
        }
        if (needCount <= 0) {
            inputSlots.SetCraftLeft(setSlot);
            return true;
        }
        return false;
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
        RECIPE_MAP[recipe.MachineMode].put(recipe.Ingredient[0].getItem(), recipe);
        CAN_PRODUCE_LIST.computeIfAbsent(recipe.Result.getItem(), k -> new ArrayList<>()).add(recipe);
    }
}