package tga.RecipeViewer;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import tga.Items.ItemFloat;
import tga.MachineRecipes.OneInRecipe;
import tga.MachineRecipes.OneSlotBook;
import tga.Mechanic.IItemChecker;
import tga.Screen.BasicGUISizeWithRecipe;
import tga.TGAScreenHandlers;

import java.util.ArrayList;
import java.util.List;

public class OneIn5RowRender implements IRecipeViewer {
    public boolean ShowOnlyCanCraft = true;
    public boolean CollapView = true;
    public final List<List<OneInRecipe>> ShowingResult = new ArrayList<>();
    public OneSlotBook Recipes;
    public int CurrentPage = 0;
    public IItemChecker ItemChecker;
    public String LastSearch;
    public int X;
    public int Y;
    private int BT0X, BT0Y, BT0R, BT0B;
    private int BT1X, BT1Y, BT1R, BT1B;
    private int RBX, RBY;

    public void SetAnchor(int x, int y) {
        X = x - 66;
        Y = y;
        BT0X = X + 15; BT0R = X + 31; BT0Y = Y + 14; BT0B = Y + 30;
        BT1X = X + 98; BT1R = X + 114; BT1Y = Y + 14; BT1B = Y + 30;
        RBX = X + 15; RBY = Y + 34;
    }

    public void SetTarget(OneSlotBook recipes, IItemChecker itemChecker) {
        boolean needReScan = ShowOnlyCanCraft || Recipes != recipes;
        Recipes = recipes;
        ItemChecker = itemChecker;
        if (needReScan) InvokeSearch(LastSearch);
    }

    public void InvokeSearch(String name) {
        ShowingResult.clear();
        LastSearch = name;
        Recipes.SearchAppend(ShowingResult, name, CollapView, ShowOnlyCanCraft, ItemChecker);
        CurrentPage = 0;
    }

    @Override
    public int DrawViewer(DrawContext context, TextRenderer textRenderer, float deltaTicks, int mouseX, int mouseY) {
        int rt = ButToggleCheckCanCraft(context, mouseX, mouseY);
        int tmp = ButtonSearch(context, mouseX, mouseY);
        if (tmp != BasicGUISizeWithRecipe.POINT_SYSTEM_OR_NULL) rt = tmp;

        //display recipe
        int startOffset = CurrentPage * 5;
        int maxOffset = Math.min(ShowingResult.size(), CurrentPage * 5 + 5);
        for(int showOffset = startOffset; showOffset < maxOffset; showOffset++) {
            tmp = DrawRecipeSlot(context, ShowingResult.get(showOffset), showOffset - startOffset, mouseX, mouseY);
            if (tmp != BasicGUISizeWithRecipe.POINT_SYSTEM_OR_NULL) rt = tmp;
        }
        context.drawText(textRenderer, Text.literal((CurrentPage + 1) + "/" + ((ShowingResult.size() + 4) / 5)), X + 34, Y + 139, 0xffffffff, false);
        return rt;
    }

    private int DrawRecipeSlot(DrawContext context, List<OneInRecipe> oneInRecipes, int index, int mouseX, int mouseY) {
        int rby = RBY + index * 20;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, RBX, rby, 57, 186, 101, 19, 512, 512);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, RBX + 20, rby + 4, 58, 170, 18, 12, 512, 512);
        OneInRecipe randomRecipe = oneInRecipes.get(BasicGUISizeWithRecipe.BlinkerSwap % oneInRecipes.size());
        int rbyItem =rby + 2;
        //pointing code


        context.drawItem(randomRecipe.Ingredient, RBX + 2, rbyItem);
        int rbx = RBX + 40;
        for(ItemFloat i : randomRecipe.CraftChanceList) {
            //pointing code

            context.drawItem(i.Item, rbx, rbyItem);
            rbx += 16;
        }
        return BasicGUISizeWithRecipe.POINT_SYSTEM_OR_NULL;
    }

    protected int ButToggleCheckCanCraft(DrawContext context, int mouseX, int mouseY) {
        boolean isPointing = mouseX > BT0X && mouseY > BT0Y && mouseX < BT0R && mouseY < BT0B;
        if (isPointing) {
            if (ShowOnlyCanCraft) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT0X, BT0Y, 19, 205, 18, 18, 512, 512);
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT0X + 3, BT0Y + 3, 461, 0, 12, 12, 512, 512);
                return BasicGUISizeWithRecipe.POINT_RECIPE_SET_ALLCRAFT_BUTTON;
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT0X, BT0Y, 19, 186, 18, 18, 512, 512);
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT0X + 3, BT0Y + 3, 448, 0, 12, 12, 512, 512);
                return BasicGUISizeWithRecipe.POINT_RECIPE_SET_CANCRAFT_BUTTON;
            }
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT0X, BT0Y, 19, 167, 18, 18, 512, 512);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT0X + 3, BT0Y + 3, ShowOnlyCanCraft ? 461 : 448, 0, 12, 12, 512, 512);
            return BasicGUISizeWithRecipe.POINT_SYSTEM_OR_NULL;
        }
    }

    protected int ButtonSearch(DrawContext context, int mouseX, int mouseY) {
        boolean isPointing = mouseX > BT1X && mouseY > BT1Y && mouseX < BT1R && mouseY < BT1B;
        if (isPointing) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT1X, BT1Y, 19, 186, 18, 18, 512, 512);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT1X + 3, BT1Y + 3, 487, 0, 12, 12, 512, 512);
            return BasicGUISizeWithRecipe.POINT_RECIPE_SEARCH_BUTTON;
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT1X, BT1Y, 19, 167, 18, 18, 512, 512);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT1X + 3, BT1Y + 3, 487, 0, 12, 12, 512, 512);
            return BasicGUISizeWithRecipe.POINT_SYSTEM_OR_NULL;
        }
    }
}