package tga.Str;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

public class PipeProperty {
    public final long PipeCap;
    public final long MaxSpeed;
    public final long PressureLine;

    public PipeProperty(int mbNormalCap, int mbPressureLine, int mbMaxSpeed) {
        PipeCap = mbNormalCap * FluidConstants.BUCKET / 1000;
        PressureLine = mbPressureLine * FluidConstants.BUCKET / 1000;
        MaxSpeed = mbMaxSpeed * FluidConstants.BUCKET / 1000;
    }

    public float GetPressure(long contain) {
        if (contain <= 0) return 0;
        if (contain > PressureLine) return 1f + (contain - PressureLine) / (float) (PipeCap - PressureLine);
        return contain / (float) PressureLine;
    }

    public long GetPressurVol(long contain) {
        return contain > PressureLine ? contain - PressureLine : 0;
    }
}