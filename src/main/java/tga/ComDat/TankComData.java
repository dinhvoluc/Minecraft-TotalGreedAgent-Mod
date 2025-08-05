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
    public final int MaxStack;
    public final long Count;

    public TankComData(int maxStack, Optional<FluidVariant> fType, long hold) {
        MaxStack = maxStack;
        FType = fType.orElse(FluidVariant.blank());
        Count = FType.isBlank() ? 0 : Math.max(0, hold);
    }

    public TankComData(int maxStack, FluidVariant fType, long hold) {
        MaxStack = maxStack;
        FType = fType == null || fType.isBlank() ? null : fType;
        Count = hold;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        if (Count <= 0 || FType == null || FType.isBlank()) return;
        textConsumer.accept(Text.literal((Count / FluidConstants.BUCKET) + "×" + TGAHelper.GetFluidName(FType)).formatted(Formatting.GOLD));
    }
}