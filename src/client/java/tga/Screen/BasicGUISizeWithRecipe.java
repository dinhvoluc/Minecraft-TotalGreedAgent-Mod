package tga.Screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import tga.RecipeViewer.IRecipeViewer;
import tga.TGAClientText;
import tga.TGAScreenHandlers;

public abstract class BasicGUISizeWithRecipe<T extends ScreenHandler> extends HandledScreen<T> implements IMousePointerSeeter {
    public static int BlinkerSwap;
    public static long BlinkerTimeTick = Long.MIN_VALUE;
    protected int PointID;
    protected int PointIDPrev;
    protected int RecipeButtonX = 8;
    protected int RecipeButtonY = 35;
    protected IRecipeViewer Viewer;

    public static final int POINT_SYSTEM_OR_NULL = 0;
    public static final int POINT_RECIPE_BOOK_TOGLE = 1;
    public static final int POINT_ERROR_FULL_INVENTORY = 2;
    public static final int POINT_RECIPE_SEARCH_BUTTON = 3;
    public static final int POINT_RECIPE_NEXT_BUTTON = 4;
    public static final int POINT_RECIPE_PREV_BUTTON = 5;
    public static final int POINT_RECIPE_RETURN_TO_ALL_BUTTON = 6;
    public static final int POINT_RECIPE_SET_CANCRAFT_BUTTON = 7;
    public static final int POINT_RECIPE_SET_ALLCRAFT_BUTTON = 8;
    public static final int POINT_RECIPE_GRID = 9;
    public static final int POINT_RECIPE_ROW = 10;

    @Override
    public void SetPointID(int id) {
        PointID = id;
    }

    @Override
    public TextRenderer GetTextRenderer() {
        return textRenderer;
    }

    public BasicGUISizeWithRecipe(T handler, PlayerInventory inventory, Text title, IRecipeViewer viewer) {
        super(handler, inventory, title);
        Viewer = viewer;
        width = 176;
        height = 166;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        switch (PointIDPrev) {
            case POINT_RECIPE_BOOK_TOGLE:
                Viewer.IsShow = !Viewer.IsShow;
                init();
                return true;
            case  POINT_RECIPE_SEARCH_BUTTON:
                //todo Import textbox
                Viewer.ActionSearch();
                return true;
            case POINT_RECIPE_NEXT_BUTTON:
                Viewer.ActionNextPage();
                return true;
            case POINT_RECIPE_PREV_BUTTON:
                Viewer.ActionPrevPage();
                return true;
            case POINT_RECIPE_RETURN_TO_ALL_BUTTON:
                Viewer.ActionBackButton();
                return true;
            case POINT_RECIPE_SET_CANCRAFT_BUTTON:
                Viewer.ActionSetCanCraft();
                return true;
            case POINT_RECIPE_SET_ALLCRAFT_BUTTON:
                Viewer.ActionSetAllCraft();
                return true;
            case POINT_RECIPE_GRID:
                Viewer.ActionGridClick();
                return true;
            case POINT_RECIPE_ROW:
                return true;
        }
        if (Viewer.MouseClicked(mouseX, mouseY, button)) return true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected boolean IsMouseRecipeBook(int cvtX, int cvtY) {
        return cvtX >= 9 && cvtY >= 36 && cvtX < 25 && cvtY < 52;
    }

    @Override
    protected void init() {
        super.init();
        Viewer.SetAnchor(x, y, this);
        if (Viewer.IsShow) x += 66;
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        PointIDPrev = PointID;
        PointID = POINT_SYSTEM_OR_NULL;
        if (Viewer.IsShow)
            drawWithRecipes(context, deltaTicks, mouseX, mouseY);
        else
            drawNoRecipes(context, deltaTicks, mouseX, mouseY);
    }

    protected void drawWithRecipes(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        long sysTime = System.currentTimeMillis();
        if (sysTime > BlinkerTimeTick) {
            BlinkerSwap++;
            BlinkerTimeTick = sysTime + 2500;
            if (BlinkerSwap > 0x1fffffff) BlinkerSwap = 0;
        }
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x - 132, y, 0, 0, 308, 166, 512, 512);
        DrawRecipeBookTogleButton(context, x + RecipeButtonX, y + RecipeButtonY, mouseX, mouseY, true);
        Viewer.DrawViewer(context, textRenderer, deltaTicks, mouseX, mouseY, this);
    }

    protected void drawNoRecipes(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y, 132, 0, 176, 166, 512, 512);
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
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, bx, by, 19, 167, 18, 18, 512, 512);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, bx + 3, by + 3, recipeOn ? 474 : 500, 0, 12, 12, 512, 512);
        }
    }

    public boolean DrawBasicToolTip(DrawContext context, int mouseX, int mouseY, float delta) {
        switch (PointID) {
            case POINT_RECIPE_BOOK_TOGLE:
                context.drawTooltip(TGAClientText.TOGLE_RECIPE_BOOK, mouseX, mouseY);
                return true;
            case POINT_ERROR_FULL_INVENTORY:
                context.drawTooltip(TGAClientText.WARN_NO_SLOT_FOR_OUTPUT, mouseX, mouseY);
                return true;
            case  POINT_RECIPE_SEARCH_BUTTON:
                context.drawTooltip(TGAClientText.BTN_SEARCH, mouseX, mouseY);
                return true;
            case POINT_RECIPE_NEXT_BUTTON:
            case POINT_RECIPE_PREV_BUTTON:
                return true;
            case POINT_RECIPE_RETURN_TO_ALL_BUTTON:
                context.drawTooltip(TGAClientText.BTN_RETURN_TO_ALL_RECIPE, mouseX, mouseY);
                return true;
            case POINT_RECIPE_SET_CANCRAFT_BUTTON:
                context.drawTooltip(TGAClientText.TOGLE_CAN_CRAFT, mouseX, mouseY);
                return true;
            case POINT_RECIPE_SET_ALLCRAFT_BUTTON:
                context.drawTooltip(TGAClientText.TOGLE_ALL_CRAFT, mouseX, mouseY);
                return true;
            case POINT_RECIPE_GRID:
            case POINT_RECIPE_ROW:
                Viewer.DrawToolTip(context, textRenderer, mouseX, mouseY);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Viewer.KeyPressed(keyCode, scanCode, modifiers)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (Viewer.CharTyped(chr, modifiers)) return true;
        return super.charTyped(chr, modifiers);
    }
}