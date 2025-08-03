package tga.Screen;

import net.minecraft.client.font.TextRenderer;

public interface IMousePointerSeeter {
    void SetPointID(int id);
    TextRenderer GetTextRenderer();
}