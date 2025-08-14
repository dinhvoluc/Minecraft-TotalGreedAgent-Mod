package tga.Mechanic;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.util.math.Direction;
import tga.Str.Dir64;
import tga.Str.FMTarget;
import tga.Str.PipeProperty;

import java.util.Queue;

public interface IPipeType {
    void FluidManagerUpdate();

    void SetConnection(Dir64 plug);

    long FluidInsert(FluidVariant variant, long pressure, long amount, Direction dir);

    void QueuFMIfNotNull(Direction dir);

    void QueuFMIfMet(FluidVariant variant, long minPressure, long minVol, Direction dir);
}