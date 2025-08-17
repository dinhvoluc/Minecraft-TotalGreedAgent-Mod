package tga.BlockEntity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import tga.Mechanic.IPipeType;
import tga.Str.FHopperProperty;
import tga.TGATileEnities;
import tga.TicksMng.FMTarget;
import tga.TicksMng.PipeManager;

public class PressurePipeTile extends TankTile implements IPipeType {
    public final FHopperProperty PROPERTY2;
    public FMTarget FMTARGET;
    public Direction PushDirection;

    public PressurePipeTile(BlockPos pos, BlockState state) {
        super(TGATileEnities.PIPE_HOPPER, pos, state);
        PROPERTY2 = (FHopperProperty) PROPERTY;
    }

    public PressurePipeTile(BlockPos pos, BlockState state, Direction pushDirection) {
        super(TGATileEnities.PIPE_HOPPER, pos, state);
        PROPERTY2 = (FHopperProperty) PROPERTY;
        PushDirection = pushDirection;
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world.isClient) return;
        FMTARGET = PipeManager.INTANCE.Register(this);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world.isClient) return;
        FMTARGET.MarkDirty();
    }

    public void SetAmount(long value) {
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
        if (PushDirection == null) return;
        if (InnerTank.amount <= 0) return;
        //check target
        BlockPos target = pos.offset(PushDirection);
        if (world.getBlockEntity(target) instanceof IPipeType pipe) {
            long maxSpeed = Math.min(InnerTank.amount, PROPERTY2.PumpRate);
            long move = pipe.FluidInsert(InnerTank.variant, 10f, maxSpeed, maxSpeed, PushDirection.getOpposite());
            if (move > 0) SetAmount(InnerTank.amount - move);
        } else {
            if (InnerTank.amount <= 0) return;
            Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, target, PushDirection.getOpposite());
            if (storage != null) {
                try (Transaction transaction = Transaction.openOuter()) {
                    long move = storage.insert(InnerTank.variant, Math.min(InnerTank.amount, PROPERTY2.PumpRate), transaction);
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
        if (dir == PushDirection) return 0;
        long free = PROPERTY.TankCap - InnerTank.amount;
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

    @Override
    public boolean Canconnect(Direction dir) {
        return true;
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        PushDirection = view.read("D", Direction.CODEC).orElse(null);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (PushDirection != null) view.put("D", Direction.CODEC, PushDirection);
    }
}