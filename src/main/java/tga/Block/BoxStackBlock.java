package tga.Block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
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
import tga.TotalGreedyAgent;

public class BoxStackBlock extends Block implements BlockEntityProvider {
    public BoxStackBlock(AbstractBlock.Settings settings) {
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
            if ( !( world.getBlockEntity(pos) instanceof  BoxStackTile tile)) return ActionResult.PASS;
            player.sendMessage(Text.literal(String.format("Tile S0=%s S1=%s, TC=%s (Mx= %s)", tile.getStack(0), tile.getStack(1), tile.GetCountNow(), tile.GetMaxHold())), false);

        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack tool) {
        TotalGreedyAgent.broadcastDebugMessage("GotBreak");
        if (!world.isClient && blockEntity instanceof BoxStackTile boxTile) {
            TotalGreedyAgent.broadcastDebugMessage("OKTile=" + boxTile.GetCountNow());
            ItemStack drop;
            if (boxTile.isEmpty()) {
                drop = new ItemStack(TGABlocks.BOX_WOOD);
            } else {
                drop = new ItemStack(TGABlocks.BOX_WOOD_FILLED);
                drop.set(BoxStackData.COMPONET_TYPE, boxTile.GetDataComponent());
            }
            Block.dropStack(world, pos, drop);
        }

        super.afterBreak(world, player, pos, state, blockEntity, tool);
    }

}