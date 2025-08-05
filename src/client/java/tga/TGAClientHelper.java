package tga;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;

public class TGAClientHelper {
    public static boolean GUI_ButtonInrange(int bx, int by, int mouseX, int mouseY) {
        return  mouseX > bx && mouseY > by && mouseX < bx + 16 && mouseY < by + 16;
    }

    public static void GUI_DrawRoundEFMetter(DrawContext context, int x, int y, long count, long max) {
        int level = (int)(count * 10 / max);
        if (level < 0) level = 0;
        else if (level > 10) level = 10;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y, 348 + 15 * level, 498, 14, 14, 512, 512);
    }

    public static void GUI_DrawRoundEFMetterE(DrawContext context, int x, int y) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y, 333, 498, 14, 14, 512, 512);
    }
}
