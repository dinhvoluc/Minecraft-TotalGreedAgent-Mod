package tga.Block;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tga.BlockEntity.FluidInside;
import tga.Machines.MachineBasic;
import tga.Str.FFlSrc;
import tga.TGAHelper;
import tga.TGASounds;

public class JrkPump extends MachineBasic {
    public static final long MAX_BUFFER = 2 * FluidConstants.BUCKET;

    public JrkPump(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FluidInside(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        if (!(world.getBlockEntity(pos) instanceof FluidInside tile)) return ActionResult.PASS;
        //find attached block
        Direction dir = state.get(Properties.HORIZONTAL_FACING);
        Storage<FluidVariant> target = TGAHelper.GetFluidAttachedFC(world, pos, dir);
        if (target == null) {
            DoPumping(tile, world, pos, player);
            return ActionResult.SUCCESS;
        }
        int STEP_DB = 0;
        //Insert buffer first
        if (tile.Volume > 0) {
            try (Transaction transaction = Transaction.openOuter()) {
                long movedVol = target.insert(tile.Variant, tile.Volume, transaction);
                if (movedVol > 0) {
                    tile.Extracted(movedVol);
                    transaction.commit();
                }
            }
        }
        if (DoPumping(tile, world, pos, player)) return ActionResult.SUCCESS;
        //insert buffer next
        try (Transaction transaction = Transaction.openOuter()) {
            long movedVol = target.insert(tile.Variant, tile.Volume, transaction);
            if (movedVol > 0) {
                tile.Extracted(movedVol);
                transaction.commit();
            }
        }
        return ActionResult.SUCCESS;
    }

    private boolean DoPumping(FluidInside tile, World world, BlockPos pos, PlayerEntity player){
        //check buffer is free
        long freeBuffer = MAX_BUFFER - tile.Volume;
        //Can not pump more
        if (freeBuffer < FluidConstants.BUCKET) return true;
        //find source block

        FFlSrc sourcePos = TGAHelper.FindFluidSource(world, pos.down(), 16);
        if (sourcePos == null) sourcePos =  TGAHelper.FindFluidSource(world, pos.down(2), 16);
        if (sourcePos == null) return true;
        //compare fuild
        if (!tile.CheckInsert(sourcePos.Variant)) return true;
        tile.FluidStore(sourcePos.Variant, FluidConstants.BUCKET);
        player.addExhaustion(4f);
        sourcePos.CleanFluid(world);
        world.playSound(null, pos, TGASounds.WATER_PUMP, SoundCategory.BLOCKS, 1f, 1f);
        return false;
    }
}