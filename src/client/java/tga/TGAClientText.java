package tga;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TGAClientText {
    public static final Text TOGLE_RECIPE_BOOK = Text.translatable("gui.tga.recipebook.togle");
    public static final Text WARN_NO_SLOT_FOR_OUTPUT = Text.translatable("gui.tga.machine.noslotforout");
    public static final Text BTN_RETURN_TO_ALL_RECIPE = Text.translatable("gui.tga.recipebook.toallview");
    public static final Text TOGLE_CAN_CRAFT = Text.translatable("gui.tga.recipebook.tocaftable");
    public static final Text TOGLE_ALL_CRAFT = Text.translatable("gui.tga.recipebook.toall");
    public static final Text BTN_SEARCH = Text.translatable("gui.tga.recipebook.search");
    public static final Text TXT_SearchInput = Text.translatable("gui.tga.recipebook.searchinput");
    public static final String RAW_NEED_POWER = "gui.tga.power_need";
    public static final String RAW_CRAFT_CHANCE = "gui.tga.craft_chance";
    public static final String RAW_HACE_X_RECIPES = "gui.tga.have_x_recipe";

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