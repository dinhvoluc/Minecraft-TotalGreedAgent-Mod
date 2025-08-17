package tga.Block.Template;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;
import tga.BlockEntity.PipeBaseEnity;
import tga.Mechanic.IPipeType;
import tga.Str.Dir64;
import tga.TGABlocks;
import tga.TGAHelper;
import tga.TotalGreedyAgent;

public class PipeBaseBlock extends Block implements BlockEntityProvider, Waterloggable {
    public static VoxelShape[] SHAPE_BY_PLUG;

    public PipeBaseBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(TGABlocks.PLUG_DIR64, 0).with(Properties.WATERLOGGED, false));
    }

    public static Block Create_Bronze(Settings settings) {
        return new PipeBaseBlock(settings);
    }

    public static Block Create_Steel(Settings settings) {
        return new PipeBaseBlock(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TGABlocks.PLUG_DIR64, Properties.WATERLOGGED);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE_BY_PLUG[state.get(TGABlocks.PLUG_DIR64, 0)];
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBaseEnity(pos, state);
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
            if (world.getBlockEntity(findPos) instanceof IPipeType target) {
                //check connect able side
                if (target.Canconnect(i.getOpposite())) plug.SetHave(i);
                else plug.SetNot(i);
            } else if (FluidStorage.SIDED.find(world, findPos, i.getOpposite()) != null)
                plug.SetHave(i);
            else plug.SetNot(i);
        }
        if (old == plug.PropValue) return;
        world.setBlockState(pos, state.with(TGABlocks.PLUG_DIR64, plug.PropValue), 3);
        if (!(world.getBlockEntity(pos) instanceof IPipeType pipe)) return;
        pipe.QueueNext();
        if (pipe instanceof PipeBaseEnity tile) tile.SetConnection(plug);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.get(Properties.WATERLOGGED))
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        if (world.getBlockEntity(pos) instanceof PipeBaseEnity pipeTest) {
            String dir = "";
            for(Direction i : pipeTest.FluidPlugDirect.GetAllHave())
                dir += i.toString().charAt(0);
            TotalGreedyAgent.broadcastDebugMessageF("P=%s(%s) V=%s F=%s Wl=%s P=%s",
                    TGAHelper.ToFluid_mB(pipeTest.Buffer.amount - pipeTest.PROPERTY.PressureLine),
                    pipeTest.PROPERTY.GetPressure(pipeTest.Buffer.amount), TGAHelper.ToFluid_mB(pipeTest.Buffer.amount),
                    pipeTest.Buffer.variant.getFluid().getBucketItem().getName().getString(),
                    state.get(Properties.WATERLOGGED), dir);
        }
        return ActionResult.SUCCESS;
    }


    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (world.isClient) return;
        PCheckConnection(state, world, pos);
    }
}