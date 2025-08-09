package tga.WorkBook;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import tga.TGAHelper;
import tga.TGATexts;

import java.util.List;

public class ACommonRecipe {
    public ItemStack[] Ingredient;
    public ItemStack Result;
    public long NeedPower;

    public ACommonRecipe(ItemStack[] ingredient, long power, ItemStack result) {
        Ingredient = ingredient;
        NeedPower = power;
        Result = result;
    }

    public ItemStack RealCraft(Inventory holder) {
        return ItemStack.EMPTY;
        //todo recipe work

    }

    public void GetExtraCostHint(List<Text> tooltips) {
        tooltips.add(Text.translatable(TGATexts.RAW_NEED_POWER, TGAHelper.JinrikiToPower10String(NeedPower)));
    }

    public int GetCraftyModeIcon() {
        return -1;
    }
}