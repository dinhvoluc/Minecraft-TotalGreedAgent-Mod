package tga.ComDat;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import tga.TGAHelper;

import java.util.Optional;
import java.util.function.Consumer;

public class TankComData implements TooltipAppender {
    public static ComponentType<TankComData> COMPONET_TYPE;
    public static Codec<TankComData> CODEC;
    public final FluidVariant FType;
    public final long Count;

    public TankComData(Optional<FluidVariant> fType, long hold) {
        FType = fType.orElse(FluidVariant.blank());
        Count = FType.isBlank() ? 0 : Math.max(0, hold);
    }

    public TankComData(FluidVariant fType, long hold) {
        FType = fType == null || fType.isBlank() ? null : fType;
        Count = hold;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        if (Count <= 0 || FType == null || FType.isBlank()) return;
        textConsumer.accept(Text.literal(TGAHelper.ToFluid_mB(Count) + "mB×" + TGAHelper.GetFluidName(FType)).formatted(Formatting.GOLD));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TankComData other)) return false;
        return Count == other.Count && FType.equals(other.FType);
    }

    @Override
    public int hashCode() {
        return (int)(Count + FType.hashCode());
    }
}