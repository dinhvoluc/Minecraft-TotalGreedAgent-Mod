package tga.Crops;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import tga.TGABlocks;

public class CustomCropBlock extends Block implements Fertilizable {
    public static final IntProperty AGE = IntProperty.of("age", 0, 6);

    public CustomCropBlock(Block.Settings settings, Block matureAs){
        super(settings);
        GrownUpStage = matureAs;
        setDefaultState(getStateManager().getDefaultState().with(AGE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    public final Block GrownUpStage;
    public static VoxelShape[] SHAPES_BY_AGE;
    private int GrowTicker;

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES_BY_AGE[state.get(AGE)];
    }

    private void OKMatured(ServerWorld world, BlockPos pos) {
        world.setBlockState(pos, GrownUpStage.getDefaultState(), NOTIFY_LISTENERS);
    }

    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        GrowMul(state, world, pos, 1);
    }
    private void GrowMul(BlockState state, ServerWorld world, BlockPos pos, int bonus) {
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            int i = state.get(AGE);
            if (i > 6) {
                //matured
                OKMatured(world, pos);
                return;
            }
            GrowTicker += bonus;
            BlockState blockState = world.getBlockState(pos.down());
            if (blockState.isOf(Blocks.FARMLAND)) {
                GrowTicker += 2 + bonus;
                if ((Integer) blockState.get(FarmlandBlock.MOISTURE) > 0) {
                    GrowTicker += 5 + bonus;
                }
            }
            if (GrowTicker > 100) {
                if (i > 5) {
                    //matured
                    OKMatured(world, pos);
                    return;
                }
                world.setBlockState(pos, getDefaultState().with(AGE, i + 1), NOTIFY_LISTENERS);
                GrowTicker -= 100;
            }
        }
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    protected boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }

    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return !state.canPlaceAt(world, pos) ? Blocks.AIR.getDefaultState() : super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockBelow = world.getBlockState(pos.down());
        return blockBelow.isOf(Blocks.FARMLAND);
    }

    @Override
    protected boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) { return true; }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        GrowMul(state, world, pos, 20);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient) return super.onBreak(world, pos, state, player);
        if (player != null && player.isCreative()) return super.onBreak(world, pos, state, player);
        //drop seed
        int age = state.get(AGE);
        if (world.getRandom().nextFloat() < 0.3f + 0.1f * age)
            Block.dropStack(world, pos, new ItemStack(this, age < 5 ? 1 : world.getRandom().nextBetween(1, age - 3)));
        return super.onBreak(world, pos, state, player);
    }

    public static CustomCropBlock CropGuayule(Settings settings) {
        return new CustomCropBlock(settings, TGABlocks.X_CROP_GUAYULE);
    }
}