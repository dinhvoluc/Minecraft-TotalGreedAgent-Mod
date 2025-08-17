package tga.Str;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import tga.TicksMng.PipeManager;

public class PipeProperty {
    public final long PipeCap;
    public final long MaxSpeed;
    public final long PressureLine;
    public final float PressureCap;

    public PipeProperty(int mbNormalCap, int mbPressureLine, int mbMaxSpeed) {
        PipeCap = mbNormalCap * FluidConstants.BUCKET / 1000;
        PressureLine = mbPressureLine * FluidConstants.BUCKET / 1000;
        MaxSpeed = mbMaxSpeed * FluidConstants.BUCKET / 1000;
        PressureCap = (float) PipeCap / PressureLine - PipeManager.ACTIVE_GAP;
    }

    public float GetPressure(long contain) {
        return contain / (float) PressureLine;
    }
}