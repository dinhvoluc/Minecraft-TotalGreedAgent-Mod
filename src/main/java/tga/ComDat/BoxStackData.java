package tga.ComDat;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.function.Consumer;

public class BoxStackData implements TooltipAppender {
    public static ComponentType<BoxStackData> COMPONET_TYPE;
    public static Codec<BoxStackData> CODEC;
    public final ItemStack LockedType;
    public final int MaxStack;
    public final int Count;

    public BoxStackData(int maxStack, Optional<ItemStack> item, int exCount) {
        MaxStack = maxStack;
        LockedType = item.isEmpty() ? ItemStack.EMPTY : item.get().copy();
        Count = LockedType.isEmpty() ? 0 : Math.max(0, exCount);
    }

    public BoxStackData(int maxStack, ItemStack item, int count) {
        MaxStack = maxStack;
        LockedType = item.copy();
        Count = count;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        if (Count <= 0 || LockedType.isEmpty()) return;
        textConsumer.accept(Text.literal(Count + "×" + LockedType.getName().getString()).formatted(Formatting.GOLD));
    }
}