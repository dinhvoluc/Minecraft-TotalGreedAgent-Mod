package tga.ComDat;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

public class FluidDat {
    public final FluidVariant FType;
    public final long FSize;

    public FluidDat(FluidVariant FType, long FSize) {
        this.FType = FType;
        this.FSize = FSize;
    }
}