package tga.RecipeViewer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public interface IRecipeViewer {
    int DrawViewer(DrawContext context, TextRenderer textRenderer, float deltaTicks, int mouseX, int mouseY);

    void SetAnchor(int x, int y);
}