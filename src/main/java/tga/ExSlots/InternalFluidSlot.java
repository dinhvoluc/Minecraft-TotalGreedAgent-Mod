package tga.ExSlots;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import tga.ComDat.FluidDat;
import tga.Mechanic.ITGAFluidSlotTileCallback;

public class InternalFluidSlot extends SnapshotParticipant<FluidDat> implements SingleSlotStorage<FluidVariant> {
    public final long Capacity;
    public long Amount;
    public FluidVariant LockedType;
    public final ITGAFluidSlotTileCallback CallBack;

    public InternalFluidSlot(long capacity, long amount, FluidVariant fluidVariant, ITGAFluidSlotTileCallback callback) {
        Capacity = capacity;
        Amount = amount;
        LockedType = fluidVariant;
        CallBack = callback;
    }

    public boolean CanInsert(FluidVariant insertedVariant) {
        return insertedVariant.equals(LockedType) || LockedType.isBlank();
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        if (Amount >= Capacity || !CanInsert(insertedVariant)) return 0;

        updateSnapshots(transaction);

        long space = Capacity - Amount;
        long insertedAmount = Math.min(maxAmount, space);

        if (LockedType.isBlank()) LockedType = insertedVariant;

        Amount += insertedAmount;

        return insertedAmount;
    }

    @Override
    public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction) {
        if (Amount <= 0 || !extractedVariant.equals(LockedType)) return 0;

        updateSnapshots(transaction);

        long extractedAmount = Math.min(maxAmount, Amount);

        Amount -= extractedAmount;
        if (Amount <= 0) LockedType = FluidVariant.blank();

        return extractedAmount;
    }

    @Override
    public boolean isResourceBlank() {
        return Amount <= 0;
    }

    @Override
    public FluidVariant getResource() {
        return  LockedType;
    }

    @Override
    public long getAmount() {
        return Amount;
    }

    @Override
    public long getCapacity() {
        return Capacity;
    }

    @Override
    protected FluidDat createSnapshot() {
        return new FluidDat(LockedType, Amount);
    }

    @Override
    protected void readSnapshot(FluidDat snapshot) {
        LockedType = snapshot.FType;
        Amount = snapshot.FSize;
    }

    @Override
    protected void onFinalCommit() {
        super.onFinalCommit();
        if (CallBack == null) return;
        CallBack.TankCallBack(this);
    }
}