package tga.Block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tga.BlockEntity.BoxStackTile;
import tga.TGABlocks;
import tga.TGATileEnities;

public class BoxStack extends Block implements BlockEntityProvider {
    public BoxStack(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        BoxStackTile rt = TGATileEnities.BOX_STACK_TILE.instantiate(pos, state);
        rt.SetMaxHoldStack(16);
        return rt;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            player.sendMessage(Text.literal("（GUIは未実装です）"), false);


        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
        if (!world.isClient && blockEntity instanceof BoxStackTile boxTile) {
            ItemStack drop;
            if (boxTile.isEmpty()) {
                drop = new ItemStack(TGABlocks.BOX_WOOD);
            } else {
                drop = new ItemStack(TGABlocks.BOX_WOOD_FILLED);
                drop.set(DataComponentTypes.BLOCK_ENTITY_DATA, boxTile)
                NbtCompound tag = new NbtCompound();
                boxTile.writeNbt(tag);
                drop.setSubNbt("BlockEntityTag", tag);
            }
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), drop);
        }

        super.afterBreak(world, player, pos, state, blockEntity, tool);
    }

}