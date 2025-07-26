package tga.Block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tga.BlockEntity.BoxStackTile;
import tga.ComDat.BoxStackData;
import tga.TGABlocks;
import tga.TGATileEnities;

public class BoxStackBlock extends Block implements BlockEntityProvider {
    public final int MaxStack;
    public static final int SIZE_WOOD = 8;
    public static final int SIZE_COPPER = 16;

    public BoxStackBlock(AbstractBlock.Settings settings, int maxStack, Block insideNo, Block insideYes) {
        super(settings);
        MaxStack = maxStack;
    }

    public static BoxStackBlock Create_Wooden(AbstractBlock.Settings settings) {
        return new BoxStackBlock(settings, SIZE_WOOD, TGABlocks.BOX_WOOD, TGABlocks.BOX_WOOD_FILLED);
    }

    public static BoxStackBlock Create_Copper(AbstractBlock.Settings settings) {
        return new BoxStackBlock(settings, SIZE_COPPER, TGABlocks.BOX_COPPER, TGABlocks.BOX_COPPER_FILLED);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        BoxStackTile rt = TGATileEnities.BOX_STACK_TILE.instantiate(pos, state);
        rt.SetMaxHoldStack(MaxStack);
        return rt;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("（GUIは未実装です）"), false);
            if (!(world.getBlockEntity(pos) instanceof BoxStackTile tile)) return ActionResult.PASS;
            player.sendMessage(Text.literal(String.format("Tile S0=%s S1=%s, TC=%s (Mx= %s)", tile.getStack(0), tile.getStack(1), tile.GetCountNow(), tile.GetMaxHold())), false);

        }
        return ActionResult.SUCCESS;
    }

    private static ItemStack GetEmptyBox(int maxStack) {
        return switch (maxStack) {
            case SIZE_WOOD -> new ItemStack(TGABlocks.BOX_WOOD);
            case SIZE_COPPER -> new ItemStack(TGABlocks.BOX_COPPER);
            default -> ItemStack.EMPTY;
        };
    }

    private static ItemStack GetFillBox(int maxStack) {
       return switch (maxStack) {
           case SIZE_WOOD -> new ItemStack(TGABlocks.BOX_WOOD_FILLED);
           case SIZE_COPPER -> new ItemStack(TGABlocks.BOX_COPPER_FILLED);
           default -> ItemStack.EMPTY;
       };
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);

        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof BoxStackTile boxTile) {
                BoxStackData data = stack.get(BoxStackData.COMPONET_TYPE);
                if (data != null) boxTile.SetDataComponent(data);
            }
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient || (player != null && player.isCreative())) return super.onBreak(world, pos, state, player);
        BlockEntity bTile = world.getBlockEntity(pos);
        if (bTile instanceof BoxStackTile info) {
            if (info.isEmpty()) {
                ItemStack drop = GetEmptyBox(MaxStack);
                if (!drop.isEmpty()) Block.dropStack(world, pos, drop);
            } else {
                ItemStack drop = GetFillBox(MaxStack);
                if (!drop.isEmpty()) {
                    drop.set(BoxStackData.COMPONET_TYPE, info.GetDataComponent());
                    Block.dropStack(world, pos, drop);
                }
            }
            world.removeBlockEntity(pos);
        }
        return super.onBreak(world, pos, state, player);
    }
}