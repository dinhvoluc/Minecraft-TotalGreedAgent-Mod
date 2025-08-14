package tga.Block;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;
import tga.BlockEntity.PipeBaseEnity;
import tga.Mechanic.IPipeType;
import tga.Str.Dir64;
import tga.TGABlocks;
import tga.TGAHelper;
import tga.TotalGreedyAgent;
import tga.TGAID;

public class PipeBaseBlock extends Block implements BlockEntityProvider {
    public static VoxelShape[] SHAPE_BY_PLUG;

    public PipeBaseBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(TGABlocks.PLUG_DIR64, 0));
    }

    public static Block Create_Bronze(Settings settings) {
        return new PipeBaseBlock(settings);
        //todo add tile varibale
    }

    public static Block Create_Steel(Settings settings) {
        return new PipeBaseBlock(settings);
        //todo add tile varibale
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TGABlocks.PLUG_DIR64);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE_BY_PLUG[state.get(TGABlocks.PLUG_DIR64, 0)];
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        Block me = state.getBlock();
        if (me == TGABlocks.PIPE_BRONZE) return new PipeBaseEnity(pos, state, TGAID.ID_PIPE_BRONZE);
        if (me == TGABlocks.PIPE_STEEL) return new PipeBaseEnity(pos, state, TGAID.ID_PIPE_STEEL);
        return null;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isClient) return;
        PCheckConnection(state, world, pos);
    }

    protected void PCheckConnection(BlockState state, World world, BlockPos pos){
        int old = state.get(TGABlocks.PLUG_DIR64, -1);
        Dir64 plug = new Dir64(old);
        for (Direction i : Direction.values()) {
            BlockPos findPos = pos.offset(i);
            if (world.getBlockState(findPos).getBlock() instanceof PipeBaseBlock || FluidStorage.SIDED.find(world, findPos, i.getOpposite()) != null)
                plug.SetHave(i);
            else plug.SetNot(i);
        }
        if (old == plug.PropValue) return;
        world.setBlockState(pos, state.with(TGABlocks.PLUG_DIR64, plug.PropValue), 3);
        if (!(world.getBlockEntity(pos) instanceof IPipeType pipe)) return;
        pipe.SetConnection(plug);
        pipe.QueueNext();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) return;
        PCheckConnection(state, world, pos);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        int val = state.get(TGABlocks.PLUG_DIR64, 0);
        String dir = "";
        for (Direction i : new Dir64(val).GetAllHave())
            dir += " " + i;
        TotalGreedyAgent.broadcastDebugMessageF("VAL=%s DIR=%s", val, dir);
        if (world.getBlockEntity(pos) instanceof PipeBaseEnity pipeTest) {
            TotalGreedyAgent.broadcastDebugMessageF("Pressure=%s Vol=%s of %s", TGAHelper.ToFluid_mB(pipeTest.GetLocalPressure()), TGAHelper.ToFluid_mB(pipeTest.Buffer.amount), pipeTest.Buffer.variant.getFluid().getBucketItem().getName());
        }
        return ActionResult.SUCCESS;
    }
}