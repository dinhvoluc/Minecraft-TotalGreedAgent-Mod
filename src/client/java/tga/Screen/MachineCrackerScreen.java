package tga.Screen;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import tga.Machines.ManCrackerTile;
import tga.NetEvents.ManCrackerGuiSync;
import tga.TGAClientText;
import tga.TGAScreenHandlers;
import tga.TotalGreedyAgent;

public class MachineCrackerScreen extends BasicGUISizeWithRecipe<MachineCrackerHandler> {
    public static boolean IsShowRecipes = false;

    @Override
    protected boolean IsRecipeOn() { return IsShowRecipes; }

    public MachineCrackerScreen(MachineCrackerHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        MachineCrackerHandler.LastWorkBlock = handler.Machine;
    }

    public static void HandleSync(ManCrackerGuiSync payload) {
        if (MachineCrackerHandler.LastWorkBlock == null) return;
        MachineCrackerHandler.LastWorkBlock.TGAS2CSync(payload);
    }

    @Override
    public void close() {
        super.close();
        MachineCrackerHandler.LastWorkBlock = null;
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        super.drawBackground(context, deltaTicks, mouseX, mouseY);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x + 59, y + 35, 38, 167, 58, 18, 512, 512);
        if (handler.Machine.getStack(1).isEmpty()) {
            //draw working bar
            int workLevel = handler.Machine.Worked * 19 / handler.Machine.WorkTotal;
            if (workLevel > 0)
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x + 79, y + 37, 38, 214, Math.min(18, workLevel), 14, 512, 512);
        }
        else {
            //draw stuck warnings
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x + 79, y + 37, 38, 242, 18, 14, 512, 512);
            if (MouseInrange(mouseX - x, mouseY - y, 82, 38, 94, 50))
                PointID = POINT_ERROR_FULL_INVENTORY;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int cvtX = (int)mouseX - x;
        int cvtY = (int)mouseY - y;
        //inrage of recipe
        if (IsMouseRecipeBook(cvtX, cvtY)) {
            IsShowRecipes = !IsShowRecipes;
            init();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);



    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        switch (PointID){
            case POINT_RECIPE_BOOK_TOGLE: context.drawTooltip(TGAClientText.TOGLE_RECIPE_BOOK, mouseX, mouseY);
            case POINT_ERROR_FULL_INVENTORY: context.drawTooltip(TGAClientText.TOGLE_NO_SLOT_FOR_OUTPUT, mouseX, mouseY);
            default:drawMouseoverTooltip(context, mouseX, mouseY);
        }
    }
}
