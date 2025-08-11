package tga.Block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;
import tga.Str.Dir64;
import tga.TGABlocks;
import tga.TotalGreedyAgent;

public class PipeBaseBlock extends Block {
    public PipeBaseBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(TGABlocks.PLUG_DIR64, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TGABlocks.PLUG_DIR64);
    }

    //todo PIPE system, onload and unload


    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isClient) return;
        Dir64 plug = new Dir64(state.get(TGABlocks.PLUG_DIR64, 0));
        for (Direction i : Direction.values())
            if (world.getBlockState(pos.offset(i)).getBlock() == TGABlocks.PIPE_COPPER)
                plug.SetHave(i);
            else plug.SetNot(i);
        world.setBlockState(pos, state.with(TGABlocks.PLUG_DIR64, plug.PropValue), 3);
        TotalGreedyAgent.broadcastDebugMessageF("%s S=%s", world.getBlockState(pos), sourceBlock);

        //todo connect pipe
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) return;


        //todo connect pipe
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        //todo delete this test code only
        if (player.isSneaking()) {
            int val = state.get(TGABlocks.PLUG_DIR64, 0) - 1;
            if (val < 0) val = 63;
            String dir = "";
            for(Direction i : new Dir64(val).GetAllHave())
                dir += " " + i;
            TotalGreedyAgent.broadcastDebugMessageF("VAL=%s DIR=%s", val, dir);
            world.setBlockState(pos, state.with(TGABlocks.PLUG_DIR64, val), 3);
        } else {
            int val = state.get(TGABlocks.PLUG_DIR64, 0) + 1;
            if (val > 63) val = 0;
            String dir = "";
            for(Direction i : new Dir64(val).GetAllHave())
                dir += " " + i;
            TotalGreedyAgent.broadcastDebugMessageF("VAL=%s DIR=%s", val, dir);
            world.setBlockState(pos, state.with(TGABlocks.PLUG_DIR64, val), 3);
        }
        return ActionResult.SUCCESS;
    }
}