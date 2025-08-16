package tga.Str;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import tga.TotalGreedyAgent;

import java.util.function.Supplier;

public class TankProperty {
    public final Supplier<Item> WhenEmpty;
    public final Supplier<Item> WhenFilled;
    public final long TankCap;
    public final Text GUI_NAME;

    public TankProperty(Supplier<Item> empty, Supplier<Item> filled, int mbcap, String guiName) {
        WhenEmpty = empty;
        WhenFilled = filled;
        TankCap = mbcap * FluidConstants.BUCKET / 1000;
        GUI_NAME = TotalGreedyAgent.GetGuiLang(guiName);
    }

    public ItemStack CreateEmptyStack(int count) {
        return new ItemStack(WhenEmpty.get(), count);
    }

    public ItemStack CreateFilledStack(int count) {
        return new ItemStack(WhenFilled.get(), count);
    }
}