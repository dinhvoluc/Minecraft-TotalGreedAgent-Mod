package tga.Block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tga.TGABlocks;

public class PipeBaseBlock extends Block {
    public PipeBaseBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(TGABlocks.DIR32, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TGABlocks.DIR32);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        //todo delete this test code only
        int val = state.get(TGABlocks.DIR32, 0) + 1;
        if (val > 31) val = 0;
        world.setBlockState(pos, state.with(TGABlocks.DIR32, val), 3);
        return ActionResult.SUCCESS;
    }
}