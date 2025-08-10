package tga.BlockEntity;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import tga.TGAHelper;
import tga.TGATileEnities;

public class FluidInside extends BlockEntity {
    public long Volume;
    public FluidVariant Variant = FluidVariant.blank();

    public FluidInside(BlockPos pos, BlockState state) {
        super(TGATileEnities.FLUID_INSIDE, pos, state);
    }

    @Override
    protected void writeData(WriteView view) {
        view.putLong("V", Volume);
        TGAHelper.WriteFluidType(view, "F", Variant);
    }

    @Override
    protected void readData(ReadView view) {
        Volume = view.getLong("V", 0);
        Variant = TGAHelper.ReadFluidType(view, "F");
    }

    public void Extracted(long vol) {
        if (Volume == 0) return;
        if (Volume <= vol)
        {
            Volume = 0;
            Variant = FluidVariant.blank();
            return;
        }
        Volume -= (int)vol;
        markDirty();
    }

    public void FluidStore(FluidVariant variant, long amount) {
        Variant = variant;
        Volume += amount;
        markDirty();
    }

    public boolean CheckInsert(FluidVariant variant) {
        return Variant.isBlank() || Variant.equals(variant);
    }
}