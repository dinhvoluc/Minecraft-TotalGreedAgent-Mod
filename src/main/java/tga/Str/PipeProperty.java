package tga.Str;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;

public class PipeProperty {
    public long PipeCap;
    public long MaxSpeed;
    public long PressureLine;

    public PipeProperty(int mbNormalCap, int mbPressureLine, int mbMaxSpeed) {
        PipeCap = mbNormalCap * FluidConstants.BUCKET / 1000;
        PressureLine = mbPressureLine * FluidConstants.BUCKET / 1000;
        MaxSpeed = mbMaxSpeed * FluidConstants.BUCKET / 1000;
    }
}