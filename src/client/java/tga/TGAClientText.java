package tga;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TGAClientText {

    public static TooltipType GetToolTipType() {
       return MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipType.ADVANCED : TooltipType.BASIC;
    }

    public static void GUI_ToolTipItemAddFirst(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY, ItemStack target, Text... first) {
        List<Text> tooltip = new ArrayList<>();
        tooltip.addAll(Arrays.asList(first));
        tooltip.addAll(target.getTooltip(Item.TooltipContext.DEFAULT, null, TGAClientText.GetToolTipType()));
        context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
    }

    public static void GUI_ToolTipItem(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY, ItemStack target) {
        List<Text> tooltip = new ArrayList<>(target.getTooltip(Item.TooltipContext.DEFAULT, null, TGAClientText.GetToolTipType()));
        context.drawTooltip(textRenderer, tooltip, mouseX, mouseY);
    }
}