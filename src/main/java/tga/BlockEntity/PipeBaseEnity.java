package tga.BlockEntity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import tga.Mechanic.IPipeType;
import tga.Str.Dir64;
import tga.Str.PipeProperty;
import tga.TGABlocks;
import tga.TGAHelper;
import tga.TGATileEnities;
import tga.TicksMng.FMTargetBasic;
import tga.TicksMng.IFMTarget;
import tga.TicksMng.PipeManager;

import java.util.HashMap;
import java.util.Map;

public class PipeBaseEnity extends BlockEntity implements IPipeType, FMTargetBasic.ITarget {
    public boolean TopUnlock = true;
    public PipeProperty PROPERTY;
    public IFMTarget FMTARGET;
    public Dir64 FluidPlugDirect;
    public static final Map<Identifier, PipeProperty> PIPE_SHARED_INFO = new HashMap<>();
    public SingleVariantStorage<FluidVariant> Buffer = new SingleVariantStorage<>() {
        @Override
        protected boolean canInsert(FluidVariant iType) {
            return variant.isBlank() || variant == iType;
        }

        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return PROPERTY.PipeCap;
        }

        @Override
        protected void onFinalCommit() {
            markDirty();
        }
    };

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world.isClient) return;
        FMTARGET = new FMTargetBasic(this);
        QueueNext();
    }

    public void SetConnection(Dir64 plug) {
        FluidPlugDirect.PropValue = plug.PropValue;
    }

    @Override
    public long PipeInsert(FluidVariant variant, Direction flowDir, long insertAmount) {
        //Is full
        long free = PROPERTY.PipeCap - Buffer.amount;
        if (free < 0) return 0;
        //Check fluid type
        if (!Buffer.variant.isBlank() && !variant.equals(Buffer.variant)) return 0;
        //real insert
        long size = Math.min(free, Math.min(insertAmount, PROPERTY.MaxSpeed));
        if (size <= 0) return 0;
        Buffer.variant = variant;
        SetAmount(Buffer.amount + size);
        return size;
    }

    @Override
    public long PipeInsert(FluidVariant variant, PipeProperty source, Direction flowDir, long amountSource) {
        //Is full
        long free = PROPERTY.PipeCap - Buffer.amount;
        if (free < 0) return 0;
        //Check fluid type
        if (!Buffer.variant.isBlank() && !variant.equals(Buffer.variant)) return 0;
        //Compare pressure
        long checkAmount = PipeManager.TransferCalcHelper(source, PROPERTY, flowDir, amountSource, Buffer.amount);
        if (checkAmount <= 0) return 0;
        if (flowDir == Direction.DOWN) TopUnlock = false;
        Buffer.variant = variant;
        SetAmount(Buffer.amount + checkAmount);
        return checkAmount;
    }

    public PipeBaseEnity(BlockPos pos, BlockState state) {
        super(TGATileEnities.PIPE_ENITY, pos, state);
        PROPERTY = PIPE_SHARED_INFO.get(Registries.BLOCK.getId(state.getBlock()));
        FluidPlugDirect = new Dir64(state.get(TGABlocks.PLUG_DIR64, 0));
    }

    @Override
    protected void writeData(WriteView view) {
        view.putLong("V", Buffer.amount);
        TGAHelper.WriteFluidType(view, "F", Buffer.variant);
    }

    @Override
    protected void readData(ReadView view) {
        Buffer.variant = TGAHelper.ReadFluidType(view, "F");
        Buffer.amount = view.getLong("V", 0);
    }

    private void SetAmount(long value) {
        long old = Buffer.amount;
        if (value <= 0) {
            Buffer.amount = 0;
            Buffer.variant = FluidVariant.blank();
        } else Buffer.amount = value;
        if (old != value) markDirty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world.isClient) return;
        QueueNext();
    }

    private long InsertToStorage(BlockPos pos, Direction dir) {
        if (Buffer.amount <= 0) return 0;
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, pos, dir);
        long move = 0;
        if (storage != null) {
            try (Transaction transaction = Transaction.openOuter()) {
                move = storage.insert(Buffer.variant, Math.min(Buffer.amount, PROPERTY.MaxSpeed), transaction);
                if (move > 0) {
                    transaction.commit();
                    SetAmount(Buffer.amount - move);
                }
            }
            //connected to storage so push to update
            if (Buffer.amount > 0) QueueNext();
        }
        return move;
    }
    private void NoStockLeft(){
        if (FluidPlugDirect.HaveNorth()) {
            BlockPos north = pos.north();
            if (world.getBlockEntity(north) instanceof IPipeType pipe) pipe.QueueFMIfMet(FluidVariant.blank(), PipeManager.ACTIVE_PRESSURE_GAP, Direction.SOUTH);
        }
        if (FluidPlugDirect.HaveSouth()) {
            BlockPos south = pos.south();
            if (world.getBlockEntity(south) instanceof IPipeType pipe) pipe.QueueFMIfMet(FluidVariant.blank(), PipeManager.ACTIVE_PRESSURE_GAP, Direction.NORTH);
        }
        if (FluidPlugDirect.HaveEast()) {
            BlockPos east = pos.east();
            if (world.getBlockEntity(east) instanceof IPipeType pipe) pipe.QueueFMIfMet(FluidVariant.blank(), PipeManager.ACTIVE_PRESSURE_GAP, Direction.WEST);
        }
        if (FluidPlugDirect.HaveWest()) {
            BlockPos west = pos.west();
            if (world.getBlockEntity(west) instanceof IPipeType pipe) pipe.QueueFMIfMet(FluidVariant.blank(), PipeManager.ACTIVE_PRESSURE_GAP, Direction.EAST);
        }
        if (FluidPlugDirect.HaveUp()) {
            BlockPos up = pos.up();
            if (world.getBlockEntity(up) instanceof IPipeType pipe) pipe.QueueFMIfMet(FluidVariant.blank(), -1f, Direction.DOWN);
        }
    }

    private long FMUDir(BlockPos pos, Direction flow) {
        if (world.getBlockEntity(pos) instanceof IPipeType pipe) {
            long move = pipe.PipeInsert(Buffer.variant, PROPERTY, flow, Buffer.amount);
            if (move > 0) SetAmount(Buffer.amount - move);
            else pipe.QueueFMIfMet(Buffer.variant, PROPERTY.GetPressure(Buffer.amount) + PipeManager.ACTIVE_PRESSURE_GAP, flow.getOpposite());
            return move;
        } else return InsertToStorage(pos, flow.getOpposite());
    }

    @Override
    public void PipeUpdate(PipeManager mng) {
        if (removed) return;
        boolean pushUp = TopUnlock;
        TopUnlock = true;
        if (Buffer.amount <= 0) {
            NoStockLeft();
            return;
        }
        long canPushUp = PROPERTY.MaxSpeed;
        //update per side
        if (FluidPlugDirect.HaveDown()) canPushUp -= FMUDir(pos.down(), Direction.DOWN);
        if (Buffer.amount <= 0) {
            NoStockLeft();
            return;
        }
        if (FluidPlugDirect.HaveNorth()) canPushUp -= FMUDir(pos.north(), Direction.NORTH);
        if (Buffer.amount <= 0) {
            NoStockLeft();
            return;
        }
        if (FluidPlugDirect.HaveSouth()) canPushUp -= FMUDir(pos.south(), Direction.SOUTH);
        if (Buffer.amount <= 0) {
            NoStockLeft();
            return;
        }
        if (FluidPlugDirect.HaveEast()) canPushUp -= FMUDir(pos.east(), Direction.EAST);
        if (Buffer.amount <= 0) {
            NoStockLeft();
            return;
        }
        if (FluidPlugDirect.HaveWest()) canPushUp -= FMUDir(pos.west(), Direction.WEST);
        if (Buffer.amount <= 0) {
            NoStockLeft();
            return;
        }
        if (FluidPlugDirect.HaveUp()) {
            BlockPos up = pos.up();
            float pressure = PROPERTY.GetPressure(Buffer.amount);
            if (world.getBlockEntity(up) instanceof IPipeType pipe) {
                if (pushUp && canPushUp > 0 && pressure > 1f) {
                    long move = pipe.PipeInsert(Buffer.variant, PROPERTY, Direction.UP, Buffer.amount);
                    if (move > 0) SetAmount(Buffer.amount - move);
                    else
                        pipe.QueueFMIfMet(Buffer.variant, PROPERTY.GetPressure(Buffer.amount) + PipeManager.ACTIVE_PRESSURE_GAP, Direction.DOWN);
                } else
                    pipe.QueueFMIfMet(Buffer.variant, PROPERTY.GetPressure(Buffer.amount) + PipeManager.ACTIVE_PRESSURE_GAP, Direction.DOWN);
            } else if (pressure > 1f) InsertToStorage(up, Direction.DOWN);
        }
        if (Buffer.amount <= 0) NoStockLeft();
    }

    @Override
    public void QueueNext() {
        FMTARGET.QueQueNext(PipeManager.INTANCE);
    }

    @Override
    public void QueueFMIfMet(FluidVariant variant, float pipePressure, Direction dir) {
        if (Buffer.amount <= 0) return;
        if (!variant.isBlank() && !variant.equals(Buffer.variant)) return;
        float localPressure = PROPERTY.GetPressure(Buffer.amount);
        if (localPressure < pipePressure) return;
        if (dir == Direction.UP) {
            if (localPressure < 1f + PipeManager.ACTIVE_PRESSURE_GAP) return;
        }
        else if (dir == Direction.DOWN) {
            if (localPressure < pipePressure) return;
        }
        QueueNext();
    }

    @Override
    public boolean Canconnect(Direction dir) {
        return true;
    }
}