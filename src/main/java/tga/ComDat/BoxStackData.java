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

import java.util.function.Consumer;

public class BoxStackData implements TooltipAppender {
    public static ComponentType<BoxStackData> COMPONET_TYPE;
    public static Codec<BoxStackData> CODEC;
    public final ItemStack HoldItem;
    public final int MaxStack;
    public final int ExCount;

    public BoxStackData(int maxStack, ItemStack item, int exCount) {
        MaxStack = maxStack;
        HoldItem = item.copy();
        ExCount = HoldItem.isEmpty() ? 0 : Math.max(0, exCount);
    }

    public int GetTotal() {
        return HoldItem.isEmpty() ? 0 : (HoldItem.getCount() + ExCount);
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
        int gTotal = GetTotal();
        if (gTotal > 0) {
            textConsumer.accept(Text.literal(gTotal + "×" + HoldItem.getName().getString()).formatted(Formatting.GOLD));
        }
    }
}