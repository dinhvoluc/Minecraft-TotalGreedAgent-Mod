package tga.Block.Template;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tga.BlockEntity.TankTile;
import tga.ComDat.TankComData;
import tga.Str.TankProperty;

import java.util.HashMap;
import java.util.Map;

public class TankBlock extends Block implements BlockEntityProvider {
    public static final Map<Identifier, TankProperty> SHARED_TANK_PROPERTY = new HashMap<>();

    public TankBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TankTile(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (!(world.getBlockEntity(pos) instanceof TankTile tile)) return ActionResult.PASS;
            player.openHandledScreen(tile);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        if (world.isClient) return;
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TankTile boxTile) {
            TankComData data = stack.get(TankComData.COMPONET_TYPE);
            if (data != null) boxTile.OnPlacedRebuild(data);
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
                ItemStack drop = SHARED_TANK_PROPERTY.get(Registries.BLOCK.getId(state.getBlock())).CreateEmptyStack(1);
                if (!drop.isEmpty()) Block.dropStack(world, pos, drop);
            } else {
                ItemStack drop = SHARED_TANK_PROPERTY.get(Registries.BLOCK.getId(state.getBlock())).CreateFilledStack(1);
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