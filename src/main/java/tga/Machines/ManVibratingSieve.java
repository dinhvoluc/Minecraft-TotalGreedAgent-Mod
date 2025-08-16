package tga.Machines;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tga.BlockEntity.MachineTiles.ManCrackerTile;
import tga.BlockEntity.MachineTiles.ManVbrSieveTile;
import tga.TGATileEnities;

public class ManVibratingSieve extends MachineBasic {
    public static MapCodec<ManVibratingSieve> CODEC;

    public ManVibratingSieve(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
        //todo tile
        //return new ManVbrSieveTile(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (!(world.getBlockEntity(pos) instanceof ManCrackerTile tile)) return ActionResult.PASS;
            player.openHandledScreen(tile);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient) return super.onBreak(world, pos, state, player);
        if (player != null && player.isCreative()) {
            world.removeBlockEntity(pos);
            return super.onBreak(world, pos, state, player);
        }
        BlockEntity bTile = world.getBlockEntity(pos);
        if (bTile instanceof ManCrackerTile info) {
            ItemScatterer.spawn(world, pos, info.GetDrops());
            world.removeBlockEntity(pos);
        }
        return super.onBreak(world, pos, state, player);
    }
}