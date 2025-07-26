package tga.Screen;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import tga.TGAScreenHandlers;

public class BoxStackScreen extends HandledScreen<BoxStackScreenHandler> {
    public BoxStackScreen(BoxStackScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        width = 176;
        height = 166;
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y, 0,0, 176, 166, 256, 256);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x + 19, y + 20, 23,167, 18, 43, 256, 256);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (!handler.Tile.isEmpty()) {
            ItemStack ctn = handler.Tile.getStack(0);
            Text name = ctn.getCustomName();
            String asStringName;
            if (name != null) asStringName = name.getString();
            else asStringName = ctn.toString();
            context.drawText(textRenderer, asStringName == null ? ctn.toString() : asStringName, x + 40, y + 22, 0x404040, false);
            context.drawText(textRenderer, handler.Tile.GetCountNow() + "/" + handler.Tile.GetMaxHold(), x + 40, y + 47, 0x704040, false);
        }
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
