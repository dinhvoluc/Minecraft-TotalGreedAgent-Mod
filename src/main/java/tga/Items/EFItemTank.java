package tga.Items;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import tga.Block.Template.TankBlock;
import tga.Str.TankProperty;

import java.util.function.Supplier;

public class EFItemTank extends BlockItem {
    public final TankProperty PROPERTY;

    public EFItemTank(Block block, Settings settings) {
        super(block, settings);
        PROPERTY = TankBlock.SHARED_TANK_PROPERTY.get(Registries.BLOCK.getId(block));
    }

    public ItemStack CreateEmptyStack(int count) {
        return new ItemStack(PROPERTY.WhenEmpty.get(), count);
    }
    public ItemStack CreateFilledStack(int count) {
        return new ItemStack(PROPERTY.WhenFilled.get(), count);
    }
}