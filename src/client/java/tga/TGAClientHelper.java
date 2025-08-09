package tga;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;

public class TGAClientHelper {
    public static boolean GUI_ButtonInrange(int bx, int by, int mouseX, int mouseY) {
        return mouseX > bx && mouseY > by && mouseX < bx + 16 && mouseY < by + 16;
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

    public static void GUI_DrawBurnFuelVol(DrawContext context, int x, int y, int left, int total) {
        if (left <= 0 || total <= 0) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y, 303, 498, 14, 14, 512, 512);
            return;
        }
        int sizeOfFrame = 1 + left * 13 / total;
        if (sizeOfFrame >= 14)
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y, 318, 498, 14, 14, 512, 512);
        else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y, 303, 498, 14, 14, 512, 512);
            int dy = 14 - sizeOfFrame;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y + dy, 318, 498 + dy, 14, 14 - dy, 512, 512);
        }
    }

    public static boolean GUI_Button12Blue(DrawContext context, int x, int y, int mouseX, int mouseY, int iconX, int iconY) {
        boolean ispointing = mouseX > x && mouseY > y && mouseX < x + 16 && mouseY < y + 16;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y, 19, ispointing ? 186 : 167, 18, 18, 512, 512);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x + 3, y + 3, iconX, iconY, 12, 12, 512, 512);
        return ispointing;
    }
}
