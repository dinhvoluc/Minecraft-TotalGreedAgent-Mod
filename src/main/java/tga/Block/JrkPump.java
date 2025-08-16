package tga.Block;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.block.WireOrientation;
import org.jetbrains.annotations.Nullable;
import tga.Block.Template.TankBlock;
import tga.BlockEntity.PressurePipeTile;
import tga.BlockEntity.TankTile;
import tga.ComDat.TankComData;
import tga.Machines.MachineBasic;
import tga.Str.FFlSrc;
import tga.TGAHelper;
import tga.TGASounds;

public class JrkPump extends MachineBasic {
    public static final int TANK_SIZE = 10;
    public static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0, 0, 0, 16, 2, 16),
            Block.createCuboidShape(5, -16, 5, 11, 16, 11)
    );
    public static final VoxelShape GUI_STAND = Block.createCuboidShape(0, 0, 0, 16, 2, 16);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public JrkPump(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        if (world.isClient) return;
        if (world.getBlockEntity(pos) instanceof PressurePipeTile boxTile) boxTile.QueueNext();
    }

    @Override
    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return TankBlock.SHARED_TANK_PROPERTY.get(Registries.BLOCK.getId(state.getBlock())).CreateEmptyStack(1);
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
        if (bTile instanceof TankTile info) {
            if (info.InnerTank.variant == null || info.InnerTank.variant.isBlank()) {
                ItemStack drop = TankBlock.SHARED_TANK_PROPERTY.get(Registries.BLOCK.getId(state.getBlock())).CreateEmptyStack(1);
                if (!drop.isEmpty()) Block.dropStack(world, pos, drop);
            } else {
                ItemStack drop = TankBlock.SHARED_TANK_PROPERTY.get(Registries.BLOCK.getId(state.getBlock())).CreateFilledStack(1);
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

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PressurePipeTile(pos, state, state.get(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        //Check open GUI
        if (hit.getPos().y - pos.getY() < 0.3D){
            if (!(world.getBlockEntity(pos) instanceof PressurePipeTile tile)) return ActionResult.PASS;
            player.openHandledScreen(tile);
            return ActionResult.SUCCESS;
        }
        if (!(world.getBlockEntity(pos) instanceof PressurePipeTile tile)) return ActionResult.PASS;
        //find attached block
        //check buffer is free
        long freeBuffer = tile.PROPERTY.TankCap - tile.InnerTank.amount;
        //Can not pump more
        if (freeBuffer < FluidConstants.BUCKET) return ActionResult.SUCCESS;
        //find source block
        FFlSrc sourcePos = TGAHelper.FindFluidSource(world, pos.down(), 64);
        if (sourcePos == null) sourcePos =  TGAHelper.FindFluidSource(world, pos.down(2), 64);
        if (sourcePos == null) return ActionResult.SUCCESS;
        //compare fuild
        if (!tile.InnerTank.variant.isBlank() && !tile.InnerTank.variant.equals(sourcePos.Variant)) return ActionResult.SUCCESS;
        tile.InnerTank.variant = sourcePos.Variant;
        tile.SetAmount(tile.InnerTank.amount + FluidConstants.BUCKET);
        player.addExhaustion(4f);
        sourcePos.CleanFluid(world);
        world.playSound(null, pos, TGASounds.WATER_PUMP, SoundCategory.BLOCKS, 1f, 1f);
        return ActionResult.SUCCESS;
    }
}