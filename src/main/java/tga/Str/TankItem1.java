package tga.Str;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import tga.ComDat.TankComData;
import tga.Items.EFItemTank;

public class TankItem1 extends SingleFluidStorage {
    private final long VolCap;
    private final ContainerItemContext Context;
    private final EFItemTank TargetItemType;

    private TankItem1(ItemStack stack, ContainerItemContext context) {
        if (stack.isEmpty() || stack.getCount() > 1 || !(stack.getItem() instanceof EFItemTank item)) throw new IllegalCallerException("Generate-by-Unhandlable-Stack");
        Context = context;
        VolCap = item.PROPERTY.TankCap;
        TankComData data = stack.get(TankComData.COMPONET_TYPE);
        TargetItemType = item;
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
            Context.exchange(ItemVariant.of(TargetItemType.PROPERTY.WhenEmpty.get()), 1, transaction);
        else {
            ItemStack ctner = new ItemStack(TargetItemType.PROPERTY.WhenFilled.get());
            ctner.set(TankComData.COMPONET_TYPE, new TankComData(variant, amount));
            Context.exchange(ItemVariant.of(ctner), 1, transaction);
        }
        return rt;
    }

    @Override
    public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
        long rt = super.insert(insertedVariant, maxAmount, transaction);
        if (rt <= 0) return rt;
        ItemStack ctner = new ItemStack(TargetItemType.PROPERTY.WhenFilled.get());
        ctner.set(TankComData.COMPONET_TYPE, new TankComData(variant, amount));
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