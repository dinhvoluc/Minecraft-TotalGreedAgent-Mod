package tga.Screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import tga.BlockEntity.MetalWorkbenchTile;
import tga.Mechanic.IItemChecker;
import tga.NetEvents.ClickedIDSync;
import tga.NetEvents.MetalWorkbenchGuiSync;
import tga.RecipeViewer.OneOut5RowRender;
import tga.TGAClientHelper;
import tga.TGAHelper;
import tga.TGARecipes;
import tga.TGAScreenHandlers;

public class MetalWorkbenchScreen extends BasicGUISizeWithRecipe<MetalWorkbenchHandler> implements IItemChecker {
    public static OneOut5RowRender Viewer = new OneOut5RowRender();

    public MetalWorkbenchScreen(MetalWorkbenchHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title, Viewer);
        Viewer.SetTarget(TGARecipes.MetalWorkbench, this);
        MetalWorkbenchHandler.LastWorkBlock = handler.Machine;
    }

    public static void HandleSync(MetalWorkbenchGuiSync payload) {
        if (MetalWorkbenchHandler.LastWorkBlock == null) return;
        MetalWorkbenchHandler.LastWorkBlock.TGAS2CSync(payload);
    }

    @Override
    public void close() {
        super.close();
        MetalWorkbenchHandler.LastWorkBlock = null;
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        super.drawBackground(context, deltaTicks, mouseX, mouseY);
        //slots
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x + 37, y + 17, 139, 83, 54, 54, 512, 512);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x + 117, y + 35, 58, 167, 38, 18, 512, 512);
        //draw tank level
        if (TGAClientHelper.GUI_ButtonInrange(x+117, y + 17 , mouseX, mouseY))
            PointID =POINT_RECIPE_CRFAT_INFO_1;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x + 117, y + 17, 0, 211, 18, 18, 512, 512);
        if (handler.Machine.WaterVol <= 0) TGAClientHelper.GUI_DrawRoundEFMetterE(context, x + 119, y + 19);
        else
            TGAClientHelper.GUI_DrawRoundEFMetter(context, x + 119, y + 19, handler.Machine.WaterVol, MetalWorkbenchTile.MAX_WATER_LEVEL);
        //draw fuel bar
        TGAClientHelper.GUI_DrawBurnFuelVol(context, x + 119, y + 55, handler.Machine.BurntimeLeft, handler.Machine.BurntimeTotal);
        //draw mode
        if (TGAClientHelper.GUI_Button12Blue(context, x + 97, y+35, mouseX, mouseY, 500, 31 + handler.Machine.WorkMode * 13))
            PointID = POINT_RECIPE_CRFAT_INFO_0;


        //todo draw logic

    }

    @Override
    public boolean OnClickCraftInfo(double mouseX, double mouseY, int button) {
        if (PointIDPrev == POINT_RECIPE_CRFAT_INFO_0){
            ClientPlayNetworking.send(new ClickedIDSync(handler.Machine.getPos(), 0));
            return true;
        }
        return false;
    }

    @Override
    public boolean DrawCraftInfo(DrawContext context, int mouseX, int mouseY, float delta) {
        switch (PointID) {
            case POINT_RECIPE_CRFAT_INFO_0:
                context.drawTooltip(MetalWorkbenchHandler.GetModeText(handler.Machine.WorkMode), mouseX, mouseY);
                return true;
            case POINT_RECIPE_CRFAT_INFO_1:
                context.drawTooltip(Text.translatable("gui.tga.waterlevel", TGAHelper.ToFluid_mB(handler.Machine.WaterVol), TGAHelper.ToFluid_mB(MetalWorkbenchTile.MAX_WATER_LEVEL)), mouseX, mouseY);
                return true;
            default:
                return false;
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
