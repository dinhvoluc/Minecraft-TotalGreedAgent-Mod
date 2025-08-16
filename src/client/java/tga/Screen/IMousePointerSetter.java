package tga.Screen;

import net.minecraft.client.font.TextRenderer;

public interface IMousePointerSetter {
    void SetPointID(int id);
    TextRenderer GetTextRenderer();
}