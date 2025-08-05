package tga.Screen;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import tga.NetEvents.TankGuiSync;
import tga.TGAClientHelper;
import tga.TGAHelper;
import tga.TGAScreenHandlers;

public class TankScreen extends HandledScreen<TankScreenHandler> {
    public static TankScreenHandler LastHandler;

    public TankScreen(TankScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        width = 176;
        height = 166;
        LastHandler = handler;
    }

    public static void HandleSync(TankGuiSync payload) {
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
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y, 132, 0, 176, 166, 512, 512);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x + 19, y + 20, 0, 167, 18, 43, 512, 512);
        //Draw metter back
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x + 41, y + 45, 0, 211, 18, 18, 512, 512);
        if (handler.Tile.InnerTank.amount > 0) {
            TGAClientHelper.GUI_DrawRoundEFMetter(context, x + 43, y + 47, handler.Tile.InnerTank.amount, handler.Tile.VolSize);
            context.drawText(textRenderer, TGAHelper.GetFluidName(handler.Tile.InnerTank.variant), x + 50, y + 24, 0xff404040, false);
        } else TGAClientHelper.GUI_DrawRoundEFMetterE(context, x + 43, y + 47);
        context.drawText(textRenderer, Text.literal(TGAHelper.ToFluid_mB(handler.Tile.InnerTank.amount) + "/" + TGAHelper.ToFluid_mB(handler.Tile.VolSize) + "mB"), x + 72, y + 49, 0xff704040, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
