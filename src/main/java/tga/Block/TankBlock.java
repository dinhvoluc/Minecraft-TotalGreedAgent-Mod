package tga.Block;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import tga.BlockEntity.TankTile;
import tga.ComDat.TankComData;
import tga.TGAItems;
import tga.TGATileEnities;

public class TankBlock extends Block implements BlockEntityProvider {
    public final int MaxStack;
    public TankBlock(AbstractBlock.Settings settings, int maxStack) {
        super(settings);
        MaxStack = maxStack;
    }

    public static TankBlock Create_Wooden(AbstractBlock.Settings settings) {
        return new TankBlock(settings, BoxStackBlock.SIZE_WOOD);
    }

    public static TankBlock Create_Copper(AbstractBlock.Settings settings) {
        return new TankBlock(settings, BoxStackBlock.SIZE_COPPER);
    }

    public static TankBlock Create_Bronze(AbstractBlock.Settings settings) {
        return new TankBlock(settings, BoxStackBlock.SIZE_BRONZE);
    }

    public static ItemStack GetEmptyTank(int maxStack) {
        return switch (maxStack) {
            case BoxStackBlock.SIZE_WOOD -> new ItemStack(TGAItems.TANK_WOOD);
            case BoxStackBlock.SIZE_COPPER -> new ItemStack(TGAItems.TANK_COPPER);
            case BoxStackBlock.SIZE_BRONZE -> new ItemStack(TGAItems.TANK_BRONZE);
            default -> ItemStack.EMPTY;
        };
    }

    public static long GetVolCap(ItemStack stack) {
        if (stack.isOf(TGAItems.TANK_WOOD) || stack.isOf(TGAItems.TANK_WOOD_FILLED))
            return BoxStackBlock.SIZE_WOOD * FluidConstants.BUCKET;
        if (stack.isOf(TGAItems.TANK_COPPER) || stack.isOf(TGAItems.TANK_COPPER_FILLED))
            return BoxStackBlock.SIZE_COPPER * FluidConstants.BUCKET;
        if (stack.isOf(TGAItems.TANK_BRONZE) || stack.isOf(TGAItems.TANK_BRONZE_FILLED))
            return BoxStackBlock.SIZE_BRONZE * FluidConstants.BUCKET;
        return 0;
    }

    public static ItemStack GetFillTank(int maxStack) {
        return switch (maxStack) {
            case BoxStackBlock.SIZE_WOOD -> new ItemStack(TGAItems.TANK_WOOD_FILLED);
            case BoxStackBlock.SIZE_COPPER -> new ItemStack(TGAItems.TANK_COPPER_FILLED);
            case BoxStackBlock.SIZE_BRONZE -> new ItemStack(TGAItems.TANK_BRONZE_FILLED);
            default -> ItemStack.EMPTY;
        };
    }

    public static boolean IsEmptyTank(ItemStack stack) {
        return stack.isOf(Items.BUCKET) || stack.isOf(Items.GLASS_BOTTLE) ||
                stack.isOf(TGAItems.TANK_WOOD) || stack.isOf(TGAItems.TANK_COPPER) || stack.isOf(TGAItems.TANK_BRONZE);
    }

    @Override
    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return GetEmptyTank(MaxStack);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        TankTile rt = TGATileEnities.TANK_TILE.instantiate(pos, state);
        rt.SetTankSize(MaxStack);
        return rt;
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
                ItemStack drop = GetEmptyTank(MaxStack);
                if (!drop.isEmpty()) Block.dropStack(world, pos, drop);
            } else {
                ItemStack drop = GetFillTank(MaxStack);
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