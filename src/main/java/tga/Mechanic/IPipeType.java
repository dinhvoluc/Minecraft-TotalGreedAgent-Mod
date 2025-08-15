package tga.Mechanic;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.util.math.Direction;
import tga.Str.Dir64;

public interface IPipeType {
    void FluidManagerUpdate();

    void SetConnection(Dir64 plug);

    long FluidInsert(FluidVariant variant, float pipePressure, long maxAmount, long pressureAmount, Direction dir);

    void QueueFMIfMet(FluidVariant variant, float pipePressure, Direction dir);

    void QueueNext();
}