package tga.Mechanic;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.util.math.Direction;

public interface IPipeType {
    void FluidManagerUpdate();

    boolean Canconnect(Direction dir);

    long FluidInsert(FluidVariant variant, float pipePressure, long maxAmount, long pressureAmount, Direction dir);

    void QueueFMIfMet(FluidVariant variant, float pipePressure, Direction dir);

    void QueueNext();
}