package tga.Str;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.item.Item;
import java.util.function.Supplier;

public class FHopperProperty extends TankProperty {
    public final long PumpRate;

    public FHopperProperty(Supplier<Item> empty, Supplier<Item> filled, int mbcap, String guiName, long pumpRate) {
        super(empty, filled, mbcap, guiName);
        PumpRate = pumpRate * FluidConstants.BUCKET / 1000;
    }
}