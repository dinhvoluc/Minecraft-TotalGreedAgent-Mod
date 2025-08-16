package tga.RecipeViewer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import tga.Screen.IMousePointerSetter;

public abstract class IRecipeViewer {
    public boolean IsShow = false;

    public abstract void DrawViewer(DrawContext context, TextRenderer textRenderer, float deltaTicks, int mouseX, int mouseY, IMousePointerSetter mpset);

    public abstract void SetAnchor(int x, int y, IMousePointerSetter mpset);

    public abstract void DrawToolTip(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY);

    public abstract void ActionSearch();

    public abstract void ActionNextPage();

    public abstract void ActionPrevPage();

    public abstract void ActionBackButton();

    public abstract void ActionSetCanCraft();

    public abstract void ActionSetAllCraft();

    public abstract void ActionGridClick();

    public abstract boolean KeyPressed(int keyCode, int scanCode, int modifiers);

    public abstract boolean CharTyped(char chr, int modifiers);

    public abstract boolean MouseClicked(double mouseX, double mouseY, int button);
}