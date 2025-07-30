package tga.Screen;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import tga.NetEvents.ManCrackerGuiSync;
import tga.TGAClientText;
import tga.TGAScreenHandlers;
import tga.TotalGreedyAgent;

public class MachineCrackerScreen extends HandledScreen<MachineCrackerHandler> {
    public static MachineCrackerHandler LastHandler;
    public static boolean IsShowRecipes = false;

    public MachineCrackerScreen(MachineCrackerHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        width = 176;
        height = 166;
        LastHandler = handler;
    }

    @Override
    protected void init() {
        super.init();
        if (IsShowRecipes) x += 66;
    }

    public static void HandleSync(ManCrackerGuiSync payload) {
        if (LastHandler == null || LastHandler.Machine == null) return;
        LastHandler.Machine.TGAS2CSync(payload);
    }

    @Override
    public void close() {
        super.close();
        LastHandler = null;
    }

    protected int PointID;

    public static final int POINT_SYSTEM_OR_NULL = 0;
    public static final int POINT_RECIPE_BOOK_TOGLE = 1;

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        PointID = POINT_SYSTEM_OR_NULL;
        if (IsShowRecipes)
            drawWithRecipes(context, deltaTicks, mouseX, mouseY);
        else
            drawNoRecipes(context, deltaTicks, mouseX, mouseY);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x + 59, y + 35, 38, 167, 58, 18, 512, 512);
    }
    protected void DrawRecipeBookTogleButton(DrawContext context, int bx, int by, int mouseX, int mouseY) {
        boolean isPointing = mouseX >= bx + 1 && mouseY >= by + 1 && mouseX < bx + 16 && mouseY < by + 16;
        if (isPointing) {
            PointID = POINT_RECIPE_BOOK_TOGLE;
            if (IsShowRecipes) {
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
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, bx + 3, by + 3, IsShowRecipes ? 474 : 500,0, 12, 12, 512, 512);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int cvtX = (int)mouseX - x;
        int cvtY = (int)mouseY - y;
        TotalGreedyAgent.writeInfo("CLK X %s Y %s", cvtX, cvtY);
        //inrage of recipe
        if ( cvtX >=  9 && cvtY >= 36 && cvtX < 25 && cvtY < 52) {
            IsShowRecipes = !IsShowRecipes;
            init();
            return true;
        }


        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected void drawWithRecipes(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x - 132, y, 0,0, 308, 166, 512, 512);
        DrawRecipeBookTogleButton(context, x + 8, y + 35, mouseX, mouseY);


    }
    protected void drawNoRecipes(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, x, y, 132,0, 176, 166, 512, 512);
        DrawRecipeBookTogleButton(context, x + 8, y + 35, mouseX, mouseY);

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
            default:drawMouseoverTooltip(context, mouseX, mouseY);
        }
    }
}
