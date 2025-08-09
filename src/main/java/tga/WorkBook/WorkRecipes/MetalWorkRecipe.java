package tga.WorkBook.WorkRecipes;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import tga.Screen.MetalWorkbenchHandler;
import tga.TGAHelper;
import tga.WorkBook.ACommonRecipe;

import java.util.List;

public class MetalWorkRecipe extends ACommonRecipe {
    public int WaterToCool;
    public int MachineMode;

    public MetalWorkRecipe(ItemStack[] ingredient, long power, ItemStack result, int water, int mode) {
        super(ingredient, power, result);
        WaterToCool = water;
        MachineMode = mode;
    }

    @Override
    public void GetExtraCostHint(List<Text> tooltips) {
        tooltips.add(MetalWorkbenchHandler.GetModeText(MachineMode));
        super.GetExtraCostHint(tooltips);
        tooltips.add(Text.translatable("gui.tga.metalwb.needwater.tocool", TGAHelper.ToFluid_mB(WaterToCool)));
    }

    @Override
    public int GetCraftyModeIcon() {
        return (500 * Short.MAX_VALUE) + 31 + MachineMode * 13;
    }
}