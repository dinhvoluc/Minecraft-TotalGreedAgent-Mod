package tga.Items;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tga.Block.TankBlock;
import tga.ComDat.TankComData;

public class TankItem1 extends SingleFluidStorage {
    private final long VolCap;
    private final ContainerItemContext Context;
    private TankItem1(ItemStack stack, ContainerItemContext context) {
        if (stack.isEmpty() || stack.getCount() > 1) throw new IllegalCallerException("Generate-by-Unhandlable-Stack");
        Context = context;
        VolCap = TankBlock.GetVolCap(stack);
        TankComData data = stack.get(TankComData.COMPONET_TYPE);
        if (data == null) return;
        variant = data.FType;
        amount = data.Count;
    }

    @Override
    public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction) {
        long rt = super.extract(extractedVariant, maxAmount, transaction);
        //set blank
        if (rt <= 0) return rt;
        if (amount <= 0)
            Context.exchange(ItemVariant.of(TankBlock.GetEmptyTank((int) (VolCap / FluidConstants.BUCKET))), 1, transaction);
        else {
            int maxStack = (int) (VolCap / FluidConstants.BUCKET);
            ItemStack ctner = TankBlock.GetFillTank(maxStack);
            ctner.set(TankComData.COMPONET_TYPE, new TankComData(maxStack, variant, amount));
            Context.exchange(ItemVariant.of(ctner), 1, transaction);
        }
        return rt;
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        long rt = super.insert(insertedVariant, maxAmount, transaction);
        if (rt <= 0) return rt;
        int maxStack = (int) (VolCap / FluidConstants.BUCKET);
        ItemStack ctner = TankBlock.GetFillTank(maxStack);
        ctner.set(TankComData.COMPONET_TYPE, new TankComData(maxStack, variant, amount));
        Context.exchange(ItemVariant.of(ctner), 1, transaction);
        return rt;
    }

    public static @Nullable Storage<FluidVariant> of(ItemStack stack, ContainerItemContext contex) {
        if (stack.isEmpty() || stack.getCount() > 1) return null;
        return new TankItem1(stack, contex);
    }

    @Override
    public boolean supportsInsertion() {
        return amount < VolCap;
    }

    @Override
    public boolean supportsExtraction() {
        return amount > 0;
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return VolCap;
    }
}