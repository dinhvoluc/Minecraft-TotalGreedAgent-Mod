package tga.Mechanic;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.util.math.Direction;
import tga.Str.Dir64;

public interface IPipeType {
    void FluidManagerUpdate();

    void SetConnection(Dir64 plug);

    long FluidInsert(FluidVariant variant, long fillRate, long pressure, long amount, Direction dir);

    void QueueFMIfNotNull(Direction dir);

    void QueueFMIfMet(FluidVariant variant, long minPressure, long minVol, Direction dir);

    void QueueNext();
}