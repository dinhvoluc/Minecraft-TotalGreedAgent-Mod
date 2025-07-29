package tga.Crops;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import tga.TGABlocks;
import tga.TGAItems;

public class CropGuayule extends Block {
    public CropGuayule(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(TGABlocks.CROP_GUAYULE_YONG);
    }

    @Override
    protected boolean isTransparent(BlockState state) { return true; }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return !world.getBlockState(pos.down()).isOf(Blocks.FARMLAND) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient) return super.onBreak(world, pos, state, player);
        if (player != null && player.isCreative()) return super.onBreak(world, pos, state, player);
        Random rnd = world.getRandom();
        //drop seed
        if (rnd.nextFloat() < 0.8f)
            Block.dropStack(world, pos, new ItemStack(TGABlocks.CROP_GUAYULE_YONG, rnd.nextBetween(1, 3)));
        //drop grass
        Block.dropStack(world, pos, new ItemStack(TGAItems.CROP_GUAYULE_GRASS));
        return super.onBreak(world, pos, state, player);
    }
}