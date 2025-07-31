package tga.Screen;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import tga.TGAScreenHandlers;

public abstract class BasicGUISizeWithRecipe<T extends ScreenHandler> extends HandledScreen<T> {
    protected int PointID;
    protected int RecipeButtonX = 8;
    protected int RecipeButtonY = 35;

    public static final int POINT_SYSTEM_OR_NULL = 0;
    public static final int POINT_RECIPE_BOOK_TOGLE = 1;
    public static final int POINT_ERROR_FULL_INVENTORY = 2;

    public BasicGUISizeWithRecipe(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        width = 176;
        height = 166;
    }

    protected boolean IsMouseRecipeBook(int cvtX, int cvtY){
        return cvtX >=  9 && cvtY >= 36 && cvtX < 25 && cvtY < 52;
    }

    @Override
    protected void init() {
        super.init();
        if (IsRecipeOn()) x += 66;
    }

    protected boolean MouseInrange(int mX, int mY, int boxX, int boxY, int boxR, int boxB){
        return  mX >= boxX && mY >= boxY && mX < boxR && mY < boxB;
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        PointID = POINT_SYSTEM_OR_NULL;
        boolean recipeOn = IsRecipeOn();
        if (recipeOn)
            drawWithRecipes(context, deltaTicks, mouseX, mouseY);
        else
            drawNoRecipes(context, deltaTicks, mouseX, mouseY);
    }

    protected void drawWithRecipes(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x - 132, y, 0,0, 308, 166, 512, 512);
        DrawRecipeBookTogleButton(context, x + RecipeButtonX, y + RecipeButtonY, mouseX, mouseY, true);


    }
    protected void drawNoRecipes(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y, 132,0, 176, 166, 512, 512);
        DrawRecipeBookTogleButton(context, x + RecipeButtonX, y + RecipeButtonY, mouseX, mouseY, false);

    }

    protected void DrawRecipeBookTogleButton(DrawContext context, int bx, int by, int mouseX, int mouseY, boolean recipeOn) {
        boolean isPointing = mouseX > bx && mouseY > by && mouseX < bx + 16 && mouseY < by + 16;
        if (isPointing) {
            PointID = POINT_RECIPE_BOOK_TOGLE;
            if (recipeOn) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, bx, by, 19, 205, 18, 18, 512, 512);
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, bx + 3, by + 3, 474, 0, 12, 12, 512, 512);
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, bx, by, 19, 186, 18, 18, 512, 512);
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, bx + 3, by + 3, 500, 0, 12, 12, 512, 512);
            }
        }
        else
        {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, bx, by, 19,167, 18, 18, 512, 512);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, bx + 3, by + 3, recipeOn ? 474 : 500,0, 12, 12, 512, 512);
        }
    }

    protected abstract boolean IsRecipeOn();
}
