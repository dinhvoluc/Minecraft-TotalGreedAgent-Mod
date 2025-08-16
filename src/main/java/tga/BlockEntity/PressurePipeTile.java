package tga.BlockEntity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import tga.Mechanic.IPipeType;
import tga.Mechanic.PipeManager;
import tga.Str.FMTarget;
import tga.TGATileEnities;

public class PressurePipeTile extends TankTile implements IPipeType {
    public static final long PUMP_RATE = FluidConstants.BUCKET / 100;
    public FMTarget FMTARGET;
    public static final int VOL_STACK_SIZE = 24;

    public PressurePipeTile(BlockPos pos, BlockState state) {
        super(TGATileEnities.PIPE_HOPPER, pos, state);
        SetTankSize(VOL_STACK_SIZE);
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world.isClient) return;
        FMTARGET = PipeManager.INTANCE.Register(this, pos);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world.isClient) return;
        FMTARGET.MarkDirty();
    }

    private void SetAmount(long value) {
        long old = InnerTank.amount;
        if (value <= 0) {
            InnerTank.amount = 0;
            InnerTank.variant = FluidVariant.blank();
        } else InnerTank.amount = value;
        if (old != value) markDirty();
    }

    @Override
    public void FluidManagerUpdate() {
        if (removed) return;
        if (InnerTank.amount <= 0) return;
        //check down
        BlockPos down = pos.down();
        if (world.getBlockEntity(down) instanceof IPipeType pipe) {
            long maxSpeed = Math.min(InnerTank.amount, PUMP_RATE);
            long move = pipe.FluidInsert(InnerTank.variant, 10f, maxSpeed, maxSpeed, Direction.UP);
            if (move > 0) SetAmount(InnerTank.amount - move);
        } else {
            if (InnerTank.amount <= 0) return;
            Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, down, Direction.UP);
            if (storage != null) {
                try (Transaction transaction = Transaction.openOuter()) {
                    long move = storage.insert(InnerTank.variant, Math.min(InnerTank.amount, PUMP_RATE), transaction);
                    if (move > 0) {
                        transaction.commit();
                        SetAmount(InnerTank.amount - move);
                    }
                }
                //connected to storage so push to update
                if (InnerTank.amount > 0) FMTARGET.MarkDirty();
            }
        }
    }

    @Override
    public long FluidInsert(FluidVariant variant, float pipePressure, long maxAmount, long pressureAmount, Direction dir) {
        if (dir == Direction.DOWN) return 0;
        long free = VolSize - InnerTank.amount;
        if (free <= 0) return 0;
        long move = Math.min(free, maxAmount);
        if (move <= 0) return 0;
        InnerTank.variant = variant;
        SetAmount(InnerTank.amount + move);
        return move;
    }

    @Override
    public void QueueFMIfMet(FluidVariant variant, float pipePressure, Direction dir) {
        if (InnerTank.amount <= 0) return;
        if (!variant.isBlank() && !variant.equals(InnerTank.variant)) return;
        FMTARGET.MarkDirty();
    }

    @Override
    public void QueueNext() {
        FMTARGET.MarkDirty();
    }
}