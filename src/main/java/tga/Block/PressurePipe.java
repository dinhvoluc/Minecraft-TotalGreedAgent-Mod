package tga.Block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import tga.BlockEntity.PressurePipeTile;
import tga.ComDat.TankComData;
import tga.TGABlocks;
import tga.TGAItems;

public class PressurePipe extends Block implements BlockEntityProvider  {
    public PressurePipe(Settings settings) {
        super(settings);
    }

    public static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(2, 4, 2, 14, 8, 14),
            Block.createCuboidShape(0, 8, 0, 16, 16, 16),
            Block.createCuboidShape(4, 0, 4, 12, 4, 12)
    );

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PressurePipeTile(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (!(world.getBlockEntity(pos) instanceof PressurePipeTile tile)) return ActionResult.PASS;
            player.openHandledScreen(tile);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return new ItemStack(TGAItems.PIPE_HOPPER);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        if (world.isClient) return;
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof PressurePipeTile boxTile) {
            TankComData data = stack.get(TankComData.COMPONET_TYPE);
            if (data != null) boxTile.OnPlacedRebuild(data);
            boxTile.QueueNext();
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient) return super.onBreak(world, pos, state, player);
        if (player != null && player.isCreative()) {
            world.removeBlockEntity(pos);
            return super.onBreak(world, pos, state, player);
        }
        BlockEntity bTile = world.getBlockEntity(pos);
        if (bTile instanceof PressurePipeTile info) {
            if (info.InnerTank.variant == null || info.InnerTank.variant.isBlank()) {
                ItemStack drop = new ItemStack(TGABlocks.PIPE_HOPPER);
                if (!drop.isEmpty()) Block.dropStack(world, pos, drop);
            } else {
                ItemStack drop = new ItemStack(TGABlocks.PIPE_HOPPER_FILLED);
                if (!drop.isEmpty()) {
                    drop.set(TankComData.COMPONET_TYPE, info.GetDataComponent());
                    Block.dropStack(world, pos, drop);
                }
            }
            ItemScatterer.spawn(world, pos, info.BufferBox);
            world.removeBlockEntity(pos);
        }
        return super.onBreak(world, pos, state, player);
    }
}