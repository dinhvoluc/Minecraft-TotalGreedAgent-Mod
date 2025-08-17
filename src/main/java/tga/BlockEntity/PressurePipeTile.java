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
import tga.Str.PipeProperty;
import tga.TGATileEnities;
import tga.TicksMng.FMTargetBasic;
import tga.TicksMng.IFMTarget;
import tga.TicksMng.PipeManager;

public class PressurePipeTile extends TankTile implements IPipeType, FMTargetBasic.ITarget {
    public final FHopperProperty PROPERTY2;
    public IFMTarget FMTARGET;
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
        FMTARGET = new FMTargetBasic(this);
        QueueNext();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world.isClient) return;
        QueueNext();
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
    public void PipeUpdate(PipeManager mng) {
        if (removed) return;
        if (PushDirection == null) return;
        if (InnerTank.amount <= 0) return;
        //check target
        BlockPos target = pos.offset(PushDirection);
        if (world.getBlockEntity(target) instanceof IPipeType pipe) {
            long move = pipe.PipeInsert(InnerTank.variant, PushDirection, Math.min(InnerTank.amount, PROPERTY2.PumpRate));
            if (move > 0) SetAmount(InnerTank.amount - move);
        } else {
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
                if (InnerTank.amount > 0) QueueNext();
            }
        }
        if (GetTankEmptyVol() <= 0) return;
        //notify side if is pipe
        if (PushDirection != Direction.NORTH) {
            BlockPos north = pos.north();
            if (world.getBlockEntity(north) instanceof IPipeType pipe)
                pipe.QueueFMIfMet(FluidVariant.blank(), PipeManager.ACTIVE_PRESSURE_GAP, Direction.SOUTH);
        }
        if (PushDirection != Direction.SOUTH) {
            BlockPos south = pos.south();
            if (world.getBlockEntity(south) instanceof IPipeType pipe)
                pipe.QueueFMIfMet(FluidVariant.blank(), PipeManager.ACTIVE_PRESSURE_GAP, Direction.NORTH);
        }
        if (PushDirection != Direction.EAST) {
            BlockPos east = pos.east();
            if (world.getBlockEntity(east) instanceof IPipeType pipe)
                pipe.QueueFMIfMet(FluidVariant.blank(), PipeManager.ACTIVE_PRESSURE_GAP, Direction.WEST);
        }
        if (PushDirection != Direction.WEST) {
            BlockPos west = pos.west();
            if (world.getBlockEntity(west) instanceof IPipeType pipe)
                pipe.QueueFMIfMet(FluidVariant.blank(), PipeManager.ACTIVE_PRESSURE_GAP, Direction.EAST);
        }
        if (PushDirection != Direction.UP) {
            BlockPos up = pos.up();
            if (world.getBlockEntity(up) instanceof IPipeType pipe)
                pipe.QueueFMIfMet(FluidVariant.blank(), -1f, Direction.DOWN);
        }
    }

    @Override
    public long PipeInsert(FluidVariant variant, Direction flowDir, long insertAmount) {
        //Is full
        long free = GetTankEmptyVol();
        if (free < 0) return 0;
        //Check fluid type
        if (!InnerTank.variant.isBlank() && !variant.equals(InnerTank.variant)) return 0;
        //real insert
        long size = Math.min(free, insertAmount);
        if (size <= 0) return 0;
        InnerTank.variant = variant;
        SetAmount(InnerTank.amount + size);
        return size;
    }

    @Override
    public long PipeInsert(FluidVariant variant, PipeProperty source, Direction flowDir, long amountSource) {
        if (flowDir == PushDirection.getOpposite()) return 0;
        long free = PROPERTY.TankCap - InnerTank.amount;
        if (free <= 0) return 0;
        long move = Math.min(free, source.MaxSpeed);
        if (move <= 0) return 0;
        InnerTank.variant = variant;
        SetAmount(InnerTank.amount + move);
        return move;
    }

    @Override
    public void QueueFMIfMet(FluidVariant variant, float pipePressure, Direction dir) {
        if (InnerTank.amount <= 0 || dir != PushDirection) return;
        if (!variant.isBlank() && !variant.equals(InnerTank.variant)) return;
        QueueNext();
    }

    @Override
    public void QueueNext() {
        FMTARGET.QueQueNext(PipeManager.INTANCE);
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