package tga.Block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import tga.TGABlocks;

public class TankBlock extends Block {
    public final int MaxStack;
    public TankBlock(AbstractBlock.Settings settings, int maxStack) {
        super(settings);
        MaxStack = maxStack;
    }

    public static TankBlock Create_Wooden(AbstractBlock.Settings settings) {
        return new TankBlock(settings, BoxStackBlock.SIZE_WOOD);
    }

    public static TankBlock Create_Copper(AbstractBlock.Settings settings) {
        return new TankBlock(settings, BoxStackBlock.SIZE_COPPER);
    }

    private static ItemStack GetEmptyBox(int maxStack) {
        return switch (maxStack) {
            case BoxStackBlock.SIZE_WOOD -> new ItemStack(TGABlocks.TANK_WOOD);
            case BoxStackBlock.SIZE_COPPER -> new ItemStack(TGABlocks.TANK_COPPER);
            default -> ItemStack.EMPTY;
        };
    }

    private static ItemStack GetFillBox(int maxStack) {
        return switch (maxStack) {
            case BoxStackBlock.SIZE_WOOD -> new ItemStack(TGABlocks.TANK_WOOD_FILLED);
            case BoxStackBlock.SIZE_COPPER -> new ItemStack(TGABlocks.TANK_COPPER_FILLED);
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return GetEmptyBox(MaxStack);
    }
}