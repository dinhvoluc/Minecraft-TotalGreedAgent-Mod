package tga.Str;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FFlSrc {
    public final BlockPos Pos;
    public final FluidVariant Variant;
    public final BlockState State;

    public FFlSrc(BlockPos pos, Fluid fluid, BlockState state) {
        Pos = pos;
        Variant = FluidVariant.of(fluid);
        State = state;
    }

    public void CleanFluid(World world) {
        if (State.contains(Properties.WATERLOGGED)) {
            // 水だけ除去
            world.setBlockState(Pos, State.with(Properties.WATERLOGGED, false), Block.NOTIFY_ALL);
        } else {
            // ブロックごと除去
            world.setBlockState(Pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
        }
    }
}