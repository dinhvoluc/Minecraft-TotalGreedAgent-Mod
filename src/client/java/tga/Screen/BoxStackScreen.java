package tga.Screen;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import tga.NetEvents.BoxStackGuiSync;
import tga.TGAScreenHandlers;
import tga.TotalGreedyAgent;

public class BoxStackScreen extends HandledScreen<BoxStackScreenHandler> {
    public static BoxStackScreenHandler LastHandler;

    public BoxStackScreen(BoxStackScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        width = 176;
        height = 166;
        LastHandler = handler;
    }

    public static void HandleSync(BoxStackGuiSync payload) {
        if (LastHandler == null || LastHandler.Tile == null) return;
        LastHandler.Tile.TGAS2CSync(payload);
    }

    @Override
    public void close() {
        super.close();
        LastHandler = null;
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y, 0,0, 176, 166, 256, 256);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x + 19, y + 20, 23,167, 18, 43, 256, 256);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        if (!handler.Tile.isEmpty())
            context.drawText(textRenderer, handler.Tile.getStack(0).getName(), 50, 24, 0xff404040, false);
        context.drawText(textRenderer, Text.literal(handler.Tile.GetCountNow() + "/" + handler.Tile.GetMaxHold()), 50, 49, 0xff704040, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
