package tga.BlockEntity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import tga.Mechanic.PipeManager;
import tga.Mechanic.IPipeType;
import tga.Str.Dir64;
import tga.Str.FMTarget;
import tga.Str.PipeProperty;
import tga.TGABlocks;
import tga.TGAHelper;
import tga.TGATileEnities;

import java.util.HashMap;
import java.util.Map;

public class PipeBaseEnity extends BlockEntity implements IPipeType {
    public PipeProperty PROPERTY;
    public FMTarget FMTARGET;
    public Identifier PipeType;
    public Dir64 FluidPlugDirect;
    public static Map<Identifier, PipeProperty> PIPE_SHARED_INFO = new HashMap<>();
    public SingleVariantStorage<FluidVariant> Buffer = new SingleVariantStorage<FluidVariant>() {
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
        FMTARGET = PipeManager.INTANCE.Register(this, pos);
    }

    @Override
    public void SetConnection(Dir64 plug) {
        FluidPlugDirect.PropValue = plug.PropValue;
    }

    public PipeBaseEnity(BlockPos pos, BlockState state) {
        super(TGATileEnities.PIPE_ENITY, pos, state);
        FluidPlugDirect = new Dir64(state.get(TGABlocks.PLUG_DIR64, 0));
    }

    public PipeBaseEnity(BlockPos pos, BlockState state, Identifier pipeType) {
        this(pos, state);
        PROPERTY = PIPE_SHARED_INFO.get(PipeType = pipeType);
    }

    @Override
    protected void writeData(WriteView view) {
        if (PipeType == null) return;
        view.putString("T", PipeType.toString());
        view.putLong("V", Buffer.amount);
        TGAHelper.WriteFluidType(view, "F", Buffer.variant);
    }

    @Override
    protected void readData(ReadView view) {
        String id = view.getString("T", null);
        if (id == null) return;
        PipeType = Identifier.tryParse(id);
        PROPERTY = PIPE_SHARED_INFO.get(PipeType);
        Buffer.variant = TGAHelper.ReadFluidType(view, "F");
        Buffer.amount = view.getLong("V", 0);
    }

    private void SetAmount(long value) {
        if (Buffer.amount == value) return;
        if (value <= 0) {
            Buffer.amount = 0;
            Buffer.variant = FluidVariant.blank();
        } else Buffer.amount = value;
        markDirty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (world.isClient) return;
        FMTARGET.MarkDirty();
    }

    private void InsertToStorage(BlockPos pos, Direction dir) {
        if (Buffer.amount <= 0) return;
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(world, pos, dir);
        if (storage != null) {
            try (Transaction transaction = Transaction.openOuter()) {
                long move = storage.insert(Buffer.variant, Math.min(Buffer.amount, PROPERTY.MaxSpeed), transaction);
                if (move > 0) {
                    SetAmount(Buffer.amount - move);
                    transaction.commit();
                }
            }
            //connected to storage so push to update
            if (Buffer.amount > 0) FMTARGET.MarkDirty();
        }
    }
    private void NoStockLeft(){
        //No check down
        if (FluidPlugDirect.HaveNorth()) {
            BlockPos north = pos.north();
            if (world.getBlockEntity(north) instanceof IPipeType pipe) pipe.QueuFMIfNotNull(Direction.SOUTH);
        }
        if (FluidPlugDirect.HaveSouth()) {
            BlockPos south = pos.south();
            if (world.getBlockEntity(south) instanceof IPipeType pipe) pipe.QueuFMIfNotNull(Direction.NORTH);
        }
        if (FluidPlugDirect.HaveEast()) {
            BlockPos east = pos.east();
            if (world.getBlockEntity(east) instanceof IPipeType pipe) pipe.QueuFMIfNotNull(Direction.WEST);
        }
        if (FluidPlugDirect.HaveWest()) {
            BlockPos west = pos.west();
            if (world.getBlockEntity(west) instanceof IPipeType pipe) pipe.QueuFMIfNotNull(Direction.EAST);
        }
        if (FluidPlugDirect.HaveUp()) {
            BlockPos up = pos.up();
            if (world.getBlockEntity(up) instanceof IPipeType pipe) pipe.QueuFMIfNotNull(Direction.DOWN);
        }
    }
    @Override
    public void FluidManagerUpdate() {
        if (Buffer.amount <= 0) {
            NoStockLeft();
            return;
        }
        //update per side
        if (FluidPlugDirect.HaveDown()) {
            BlockPos down = pos.down();
            if (world.getBlockEntity(down) instanceof IPipeType pipe) {
                long pressure = GetLocalPressure();
                long move = pipe.FluidInsert(Buffer.variant, pressure, Math.min(Buffer.amount, PROPERTY.MaxSpeed), Direction.UP);
                if (move > 0) SetAmount(Buffer.amount - move);
                else
                    pipe.QueuFMIfMet(Buffer.variant, pressure + PipeManager.MIN_ACTIVE_PRESSURE_GAP, Buffer.amount + PipeManager.MIN_ACTIVE_PRESSURE_GAP, Direction.UP);
            } else InsertToStorage(down, Direction.UP);
        }
        if (Buffer.amount <= 0) {
            NoStockLeft();
            return;
        }
        if (FluidPlugDirect.HaveNorth()) {
            BlockPos north = pos.north();
            if (world.getBlockEntity(north) instanceof IPipeType pipe) {
                long pressure = GetLocalPressure();
                long move = pipe.FluidInsert(Buffer.variant, pressure, Math.min(Buffer.amount, PROPERTY.MaxSpeed), Direction.SOUTH);
                if (move > 0) SetAmount(Buffer.amount - move);
                else
                    pipe.QueuFMIfMet(Buffer.variant, pressure + PipeManager.MIN_ACTIVE_PRESSURE_GAP, Buffer.amount + PipeManager.MIN_ACTIVE_PRESSURE_GAP, Direction.SOUTH);
            } else InsertToStorage(north, Direction.SOUTH);
        }
        if (Buffer.amount <= 0) {
            NoStockLeft();
            return;
        }
        if (FluidPlugDirect.HaveSouth()) {
            BlockPos south = pos.south();
            if (world.getBlockEntity(south) instanceof IPipeType pipe) {
                long pressure = GetLocalPressure();
                long move = pipe.FluidInsert(Buffer.variant, pressure, Math.min(Buffer.amount, PROPERTY.MaxSpeed), Direction.NORTH);
                if (move > 0) SetAmount(Buffer.amount - move);
                else
                    pipe.QueuFMIfMet(Buffer.variant, pressure + PipeManager.MIN_ACTIVE_PRESSURE_GAP, Buffer.amount + PipeManager.MIN_ACTIVE_PRESSURE_GAP, Direction.NORTH);
            } else InsertToStorage(south, Direction.NORTH);
        }
        if (Buffer.amount <= 0) {
            NoStockLeft();
            return;
        }
        if (FluidPlugDirect.HaveEast()) {
            BlockPos east = pos.east();
            if (world.getBlockEntity(east) instanceof IPipeType pipe) {
                long pressure = GetLocalPressure();
                long move = pipe.FluidInsert(Buffer.variant, pressure, Math.min(Buffer.amount, PROPERTY.MaxSpeed), Direction.WEST);
                if (move > 0) SetAmount(Buffer.amount - move);
                else
                    pipe.QueuFMIfMet(Buffer.variant, pressure + PipeManager.MIN_ACTIVE_PRESSURE_GAP, Buffer.amount + PipeManager.MIN_ACTIVE_PRESSURE_GAP, Direction.WEST);
            } else InsertToStorage(east, Direction.WEST);
        }
        if (Buffer.amount <= 0) {
            NoStockLeft();
            return;
        }
        if (FluidPlugDirect.HaveWest()) {
            BlockPos west = pos.west();
            if (world.getBlockEntity(west) instanceof IPipeType pipe) {
                long pressure = GetLocalPressure();
                long move = pipe.FluidInsert(Buffer.variant, pressure, Math.min(Buffer.amount, PROPERTY.MaxSpeed), Direction.EAST);
                if (move > 0) SetAmount(Buffer.amount - move);
                else
                    pipe.QueuFMIfMet(Buffer.variant, pressure + PipeManager.MIN_ACTIVE_PRESSURE_GAP, Buffer.amount + PipeManager.MIN_ACTIVE_PRESSURE_GAP, Direction.EAST);
            } else InsertToStorage(west, Direction.EAST);
        }
        if (Buffer.amount <= 0) {
            NoStockLeft();
            return;
        }
        if (FluidPlugDirect.HaveUp()) {
            BlockPos up = pos.up();
            if (world.getBlockEntity(up) instanceof IPipeType pipe) {
                long pressure = GetLocalPressure();
                long move = pipe.FluidInsert(Buffer.variant, pressure, Math.min(Buffer.amount, PROPERTY.MaxSpeed), Direction.DOWN);
                if (move > 0) SetAmount(Buffer.amount - move);
                else
                    pipe.QueuFMIfMet(Buffer.variant, pressure, Buffer.amount + PipeManager.MIN_ACTIVE_PRESSURE_GAP, Direction.DOWN);
            } else if (GetLocalPressure() > 0) InsertToStorage(up, Direction.DOWN);
        }
        if (Buffer.amount <= 0) NoStockLeft();
    }

    @Override
    public void QueuFMIfNotNull(Direction dir) {
        if (Buffer.amount > PipeManager.MIN_ACTIVE_PRESSURE_GAP) FMTARGET.MarkDirty();
    }

    @Override
    public void QueuFMIfMet(FluidVariant variant, long minPressure, long minVol, Direction dir) {
        if (variant.isBlank()) return;
        if (dir == Direction.UP) {
            long localPress = GetLocalPressure();
            if (localPress > 0 && localPress < minPressure) return;
        } else if (minPressure == 0) {
            if (dir != Direction.DOWN && minVol > Buffer.amount) return;
        } else if (minPressure > GetLocalPressure()) return;
        if (Buffer.variant.equals(variant)) FMTARGET.MarkDirty();
    }

    public long GetLocalPressure() {
        long rt = Buffer.amount - PROPERTY.PressureLine;
        return rt < PipeManager.MIN_ACTIVE_PRESSURE_GAP ? 0 : rt;
    }

    @Override
    public long FluidInsert(FluidVariant variant, long pressure, long amount, Direction dir) {
        //check valid fluid
        if (!Buffer.variant.isBlank())
            if (!variant.equals(Buffer.variant)) return 0;
        //Is full
        long free = PROPERTY.PipeCap - Buffer.amount;
        if (free < PipeManager.MIN_ACTIVE_PRESSURE_GAP) return 0;
        long localPress = GetLocalPressure();
        long pressGap = pressure - localPress;
        //Up is diff
        if (dir == Direction.UP) {
            if (localPress > 0 && pressGap < PipeManager.MIN_ACTIVE_PRESSURE_GAP) return 0;
            long moveSize = Math.min(free, Math.min(PROPERTY.MaxSpeed, pressGap < PipeManager.MIN_ACTIVE_PRESSURE_GAP ? amount : Math.min(pressGap / 2, amount)));
            if (moveSize <= 0) return 0;
            Buffer.variant = variant;
            SetAmount(Buffer.amount + moveSize);
            return moveSize;
        }
        if (pressGap > PipeManager.MIN_ACTIVE_PRESSURE_GAP) {
            //blancing pressure
            long moveSize = Math.min(free, Math.min(PROPERTY.MaxSpeed, Math.min(amount, pressGap / 2)));
            if (moveSize <= 0) return 0;
            Buffer.variant = variant;
            SetAmount(Buffer.amount + moveSize);
            return moveSize;
        }
        //no down
        if (dir == Direction.DOWN) return 0;
        //spay arround
        long noPressureFree = PROPERTY.PressureLine - Buffer.amount;
        if (localPress > 0 && noPressureFree <= PipeManager.MIN_ACTIVE_PRESSURE_GAP) return 0;
        long moveSize = Math.min(noPressureFree, Math.min(PROPERTY.MaxSpeed, amount));
        if (moveSize <= 0) return 0;
        Buffer.variant = variant;
        SetAmount(Buffer.amount + moveSize);
        return moveSize;
    }
}