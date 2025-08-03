package tga.Screen;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import tga.*;
import tga.Machines.ManCrackerTile;
import tga.Mechanic.IItemChecker;
import tga.NetEvents.ManCrackerGuiSync;
import tga.RecipeViewer.OneIn5RowRender;

public class MachineCrackerScreen extends BasicGUISizeWithRecipe<MachineCrackerHandler> implements IItemChecker {
    public static OneIn5RowRender Viewer = new OneIn5RowRender();

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
            if (TGAHelper.InRangeXY(mouseX - x, mouseY - y, 82, 38, 94, 50))
                PointID = POINT_ERROR_FULL_INVENTORY;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (DrawBasicToolTip(context, mouseX, mouseY, delta)) return;
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public boolean HaveEnough(ItemStack stack) {
        int totalAmount = 0;
        for (Slot sl : handler.slots) {
            if (!sl.hasStack()) continue;
            ItemStack getStack = sl.getStack();
            if (ItemStack.areItemsEqual(stack, getStack)) {
                totalAmount += getStack.getCount();
                if (totalAmount >= stack.getCount()) return true;
            }
        }
        return false;
    }

    @Override
    public boolean HaveAll(ItemStack[] stacks) {
        for(ItemStack stack : stacks)
            if (!HaveEnough(stack)) return false;
        return true;
    }
}
