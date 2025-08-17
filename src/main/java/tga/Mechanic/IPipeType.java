package tga.Mechanic;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.util.math.Direction;
import tga.Str.PipeProperty;

public interface IPipeType {
    boolean Canconnect(Direction dir);

    long PipeInsert(FluidVariant variant, PipeProperty source, Direction flowDir, long amountSource);

    long PipeInsert(FluidVariant variant, Direction flowDir, long insertAmount);

    void QueueFMIfMet(FluidVariant variant, float pipePressure, Direction dir);

    void QueueNext();
}