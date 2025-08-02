package tga.Screen;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import tga.Machines.ManCrackerTile;
import tga.Mechanic.IItemChecker;
import tga.NetEvents.ManCrackerGuiSync;
import tga.RecipeViewer.OneIn5RowRender;
import tga.TGAClientText;
import tga.TGARecipes;
import tga.TGAScreenHandlers;
import tga.TotalGreedyAgent;

public class MachineCrackerScreen extends BasicGUISizeWithRecipe<MachineCrackerHandler> implements IItemChecker {
    public static boolean IsShowRecipes = false;
    public static OneIn5RowRender Viewer = new OneIn5RowRender();

    @Override
    protected boolean IsRecipeOn() { return IsShowRecipes; }

    public MachineCrackerScreen(MachineCrackerHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, Viewer);
        Viewer.SetTarget(TGARecipes.Cracker_LV0, this);
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
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (DrawBasicToolTip(context, mouseX, mouseY, delta)) return;
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public boolean HaveEnough(ItemStack stack) {
        return false;
    }

    @Override
    public boolean HaveAll(ItemStack[] stacks) {
        return false;
    }
}
