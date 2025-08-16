package tga.Block.Template;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import tga.BlockEntity.BoxStackTile;
import tga.ComDat.BoxStackData;
import tga.Str.BoxStackProperty;
import tga.TotalGreedyAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BoxStackBlock extends Block implements BlockEntityProvider {
    public static final Map<Identifier, BoxStackProperty> SHARED_STACK_PROPERTY = new HashMap<>();

    public BoxStackBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    public static void SetupType(int size, Identifier emptyID, Supplier<Item> itemEmpty, Identifier filledID, Supplier<Item> itemFilled, String guiName) {
        BoxStackProperty prop = new BoxStackProperty(itemEmpty, itemFilled, size, TotalGreedyAgent.GetGuiLang(guiName));
        SHARED_STACK_PROPERTY.put(emptyID, prop);
        SHARED_STACK_PROPERTY.put(filledID, prop);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BoxStackTile(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (!(world.getBlockEntity(pos) instanceof BoxStackTile tile)) return ActionResult.PASS;
            player.openHandledScreen(tile);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);

        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof BoxStackTile boxTile) {
                BoxStackData data = stack.get(BoxStackData.COMPONET_TYPE);
                if (data != null) boxTile.OnPlacedRebuild(data);
            }
        }
    }

    @Override
    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        return SHARED_STACK_PROPERTY.get(Registries.BLOCK.getId(state.getBlock())).CreateEmptyStack(1);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient) return super.onBreak(world, pos, state, player);
        if (player != null && player.isCreative()) {
            world.removeBlockEntity(pos);
            return super.onBreak(world, pos, state, player);
        }
        BlockEntity bTile = world.getBlockEntity(pos);
        if (bTile instanceof BoxStackTile info) {
            if (info.isEmpty()) {
                ItemStack drop = info.PROPERTY.CreateEmptyStack(1);
                if (!drop.isEmpty()) Block.dropStack(world, pos, drop);
            } else {
                ItemStack drop = info.PROPERTY.CreateFilledStack(1);
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