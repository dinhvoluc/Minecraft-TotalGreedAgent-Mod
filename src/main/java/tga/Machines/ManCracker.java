package tga.Machines;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tga.BlockEntity.BoxStackTile;
import tga.TGATileEnities;

public class ManCracker extends MachineBasic {
    public static MapCodec<ManCracker> CODEC;

    public ManCracker(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return TGATileEnities.M_CRACKER_LV0.instantiate(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            if (!(world.getBlockEntity(pos) instanceof ManCrackerTile tile)) return ActionResult.PASS;
            player.openHandledScreen(tile);
        }
        return ActionResult.SUCCESS;
    }
}