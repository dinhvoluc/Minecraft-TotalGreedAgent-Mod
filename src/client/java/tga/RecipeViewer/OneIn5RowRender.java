package tga.RecipeViewer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import tga.Items.CraftOutputPatch;
import tga.Items.ItemFloat;
import tga.Mechanic.IItemChecker;
import tga.Screen.BasicGUISizeWithRecipe;
import tga.Screen.IMousePointerSetter;
import tga.*;
import tga.WorkBook.OneSlotBook;
import tga.WorkBook.WorkRecipes.OneInRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OneIn5RowRender extends IRecipeViewer {
    public boolean ShowOnlyCanCraft = true;
    public ItemStack SetTarget = ItemStack.EMPTY;
    public List<OneInRecipe> TartgetRecipes;
    public final List<CraftOutputPatch<OneInRecipe>> ShowingResult = new ArrayList<>();
    public OneSlotBook Recipes;
    public int CurrentPage = 0;
    public int SubViewPage = 0;
    public IItemChecker ItemChecker;
    public String LastSearch;
    public int X;
    public int Y;
    private int BT0X, BT0Y;
    private int BT1X, BT1Y;
    private int BT2X, BT2Y;
    private int BT3X, BT3Y;
    private int RBX, RBY;
    private int SubPointID;
    private TextFieldWidget SearchInput;
    public boolean IsForcusing = false;

    public void SetAnchor(int x, int y, IMousePointerSetter mpset) {
        X = x - 66;
        Y = y;
        //Togle craft ablity
        BT0X = X + 15;
        BT0Y = Y + 14;
        //Search or return
        BT1X = X + 98;
        BT1Y = Y + 14;
        //Prev
        BT2X = X + 15;
        BT2Y = Y + 135;
        //Next
        BT3X = X + 98;
        BT3Y = Y + 135;
        //VIEW BAR
        RBX = X + 15;
        RBY = Y + 34;
        //textbox setup
        SearchInput = new TextFieldWidget(mpset.GetTextRenderer(), X + 34, Y + 14, 63, 18, Text.literal("search"));
        SearchInput.setPlaceholder(TGATexts.TXT_SearchInput);
        SearchInput.setText(LastSearch);
        SearchInput.setMaxLength(16);
    }

    public void SetTarget(OneSlotBook recipes, IItemChecker itemChecker) {
        boolean needReScan = ShowOnlyCanCraft || Recipes != recipes;
        Recipes = recipes;
        ItemChecker = itemChecker;
        if (needReScan) {
            InvokeSearch(LastSearch);
            SetTarget = ItemStack.EMPTY;
            TartgetRecipes = null;
        }
    }

    public void InvokeSearch(String name) {
        ShowingResult.clear();
        LastSearch = name;
        Recipes.SearchAppend(ShowingResult, name, ShowOnlyCanCraft, ItemChecker);
        CurrentPage = 0;
    }

    // <editor-fold desc="Drawwing">
    @Override
    public void DrawViewer(DrawContext context, TextRenderer textRenderer, float deltaTicks, int mouseX, int mouseY, IMousePointerSetter mpset) {
        DrawButToggleCheckCanCraft(context, mouseX, mouseY, mpset);
        if (SetTarget.isEmpty()) {
            //All output mode
            int startOffset = CurrentPage * 25;
            int maxOffset = Math.min(ShowingResult.size(), CurrentPage * 5 + 25);
            for (int showOffset = startOffset; showOffset < maxOffset; showOffset++)
                DrawRecipeGrid(context, textRenderer, ShowingResult.get(showOffset - startOffset), showOffset, mouseX, mouseY, mpset);
            int maxPage = (ShowingResult.size() + 24) / 25;
            if (maxPage > 0) {
                if (maxPage > 1) {
                    DrawButonPagePrev(context, mouseX, mouseY, mpset);
                    DrawButtonPageNext(context, mouseX, mouseY, mpset);
                }
                Text txt = Text.literal((CurrentPage + 1) + "/" + maxPage);
                context.drawText(textRenderer, txt, X + 65 - textRenderer.getWidth(txt) / 2, Y + 141, 0xffffffff, false);
            }
            DrawButtonSearch(context, mouseX, mouseY, mpset, deltaTicks);
        } else {
            //display recipe
            if (TartgetRecipes != null) {
                int startOffset = SubViewPage * 5;
                int maxOffset = Math.min(TartgetRecipes.size(), SubViewPage * 5 + 5);
                for (int showOffset = startOffset; showOffset < maxOffset; showOffset++)
                    DrawRecipeSlot(context, textRenderer, TartgetRecipes.get(showOffset), showOffset, mouseX, mouseY, mpset);
                int maxPage = (TartgetRecipes.size() + 4) / 5;
                if (maxPage > 0) {
                    if (maxPage > 1) {
                        DrawButonPagePrev(context, mouseX, mouseY, mpset);
                        DrawButtonPageNext(context, mouseX, mouseY, mpset);
                    }
                    Text txt = Text.literal((SubViewPage + 1) + "/" + maxPage);
                    context.drawText(textRenderer, txt, X + 65 - textRenderer.getWidth(txt) / 2, Y + 141, 0xffffffff, false);
                }
            }
            DrawReturnButton(context, mouseX, mouseY, mpset);
        }
    }

    private void DrawRecipeGrid(DrawContext context, TextRenderer textRenderer, CraftOutputPatch<OneInRecipe> result, int index, int mouseX, int mouseY, IMousePointerSetter mpset) {
        int rby = RBY + ((index % 25) / 5) * 20;
        int rbx = RBX + 1 + (index % 5) * 20;
        boolean isPointing = mouseX > rbx + 1 && mouseY > rby + 1 && mouseX < rbx + 18 && mouseY < rby + 18;
        if (isPointing) {
            mpset.SetPointID(BasicGUISizeWithRecipe.POINT_RECIPE_GRID);
            SubPointID = index;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, rbx, rby, 57, 206, 19, 19, 512, 512);
        } else
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, rbx, rby, 138, 82, 19, 19, 512, 512);
        context.drawItem(result.Target, rbx + 2, rby + 2);
        int size = result.Recipes.size();
        if (size != 1)
            context.drawText(textRenderer, Text.literal(String.valueOf(size)), rbx + 1, rby + 1, 0xffffff00, true);
    }

    public void DrawToolTip(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY) {
        if (SetTarget.isEmpty()) {
            CraftOutputPatch<OneInRecipe> gotRep = TGAHelper.GetOrNull(ShowingResult, SubPointID);
            if (gotRep == null) return;
            TGAClientText.GUI_ToolTipItemAddFirst(context, textRenderer, mouseX, mouseY, gotRep.Target,
                    Text.translatable(TGATexts.RAW_HACE_X_RECIPES, gotRep.Recipes.size()).formatted(Formatting.YELLOW));
        } else {
            int indexOfPoint = SubPointID / 16;
            int pointingSlot = SubPointID % 16;
            OneInRecipe gotRep = TGAHelper.GetOrNull(TartgetRecipes, indexOfPoint);
            if (gotRep == null) return;
            switch (pointingSlot) {
                case  0:
                    TGAClientText.GUI_ToolTipItem(context, textRenderer, mouseX, mouseY, gotRep.Ingredient);
                    break;
                case 1:
                    List<Text> tooltips = new ArrayList<>();
                    gotRep.GetExtraCostHint(tooltips);
                    if (!tooltips.isEmpty()) context.drawTooltip(textRenderer, tooltips, mouseX, mouseY);
                    break;
                case 2:
                    ItemFloat c0Item = TGAHelper.GetOrNull(gotRep.CraftChanceList,0);
                    if (c0Item == null) break;
                    if (c0Item.Number > 2f)
                        TGAClientText.GUI_ToolTipItem(context, textRenderer, mouseX, mouseY, c0Item.Item);
                    else
                        TGAClientText.GUI_ToolTipItemAddFirst(context, textRenderer, mouseX, mouseY, c0Item.Item,
                                Text.translatable(TGATexts.RAW_CRAFT_CHANCE, TGAHelper.ToPercent(c0Item.Number)).formatted(Formatting.RED));
                    break;
                case 3:
                    ItemFloat c1Item = TGAHelper.GetOrNull(gotRep.CraftChanceList,1);
                    if (c1Item == null) break;
                    if (c1Item.Number > 1.00001f)
                        TGAClientText.GUI_ToolTipItem(context, textRenderer, mouseX, mouseY, c1Item.Item);
                    else
                        TGAClientText.GUI_ToolTipItemAddFirst(context, textRenderer, mouseX, mouseY, c1Item.Item,
                                Text.translatable(TGATexts.RAW_CRAFT_CHANCE, TGAHelper.ToPercent(c1Item.Number)).formatted(Formatting.RED));
                    break;
            }
        }
    }

    private void DrawRecipeSlot(DrawContext context, TextRenderer textRenderer, OneInRecipe recipe, int index, int mouseX, int mouseY, IMousePointerSetter mpset) {
        int rby = RBY + (index % 5) * 20;
        int rbx = RBX +2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, RBX, rby, 57, 186, 101, 19, 512, 512);
        int rbyItem = rby + 2;
        if(TGAHelper.InRangeXY(mouseX, mouseY, rbx, rbyItem, 16, 16))
        {
            mpset.SetPointID(BasicGUISizeWithRecipe.POINT_RECIPE_ROW);
            SubPointID = index * 16;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, rbx, rbyItem, 79, 168, 16, 16, 512, 512);
        }
        if (TGAHelper.InRangeXY(mouseX, mouseY, RBX + 20, rby + 4, 18, 12)) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, RBX + 23, rby + 4, 435, 0, 12, 12, 512, 512);
            mpset.SetPointID(BasicGUISizeWithRecipe.POINT_RECIPE_ROW);
            SubPointID = index * 16 + 1;
        }
        else context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, RBX + 20, rby + 4, 58, 170, 18, 12, 512, 512);
        context.drawItem(recipe.Ingredient, RBX + 2, rbyItem);
        int count = recipe.Ingredient.getCount();
        if (count != 1)
            context.drawText(textRenderer, Text.literal(String.valueOf(count)), RBX + 1, rby + 1, 0xffff0000, true);
        rbx = RBX + 40;
        for (int i = 0; i < recipe.CraftChanceList.length; i++) {
            ItemFloat resultPart = recipe.CraftChanceList[i];
            if (TGAHelper.InRangeXY(mouseX, mouseY, rbx, rbyItem, 16, 16)) {
                mpset.SetPointID(BasicGUISizeWithRecipe.POINT_RECIPE_ROW);
                SubPointID = index * 16 + i + 2;
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, rbx, rbyItem, 79, 168, 16, 16, 512, 512);
            }
            context.drawItem(resultPart.Item, rbx, rbyItem);
            count = resultPart.Item.getCount();
            if (count != 1)
                context.drawText(textRenderer, Text.literal(String.valueOf(count)), rbx, rby + 1, 0xff00ff00, true);
            rbx += 16;
        }
    }

    protected void DrawButToggleCheckCanCraft(DrawContext context, int mouseX, int mouseY, IMousePointerSetter mpset) {
        boolean isPointing = TGAClientHelper.GUI_ButtonInrange(BT0X, BT0Y, mouseX, mouseY);
        if (isPointing) {
            if (ShowOnlyCanCraft) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT0X, BT0Y, 19, 205, 18, 18, 512, 512);
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT0X + 3, BT0Y + 3, 461, 0, 12, 12, 512, 512);
                mpset.SetPointID(BasicGUISizeWithRecipe.POINT_RECIPE_SET_ALLCRAFT_BUTTON);
            } else {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT0X, BT0Y, 19, 186, 18, 18, 512, 512);
                context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT0X + 3, BT0Y + 3, 448, 0, 12, 12, 512, 512);
                mpset.SetPointID(BasicGUISizeWithRecipe.POINT_RECIPE_SET_CANCRAFT_BUTTON);
            }
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT0X, BT0Y, 19, 167, 18, 18, 512, 512);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT0X + 3, BT0Y + 3, ShowOnlyCanCraft ? 461 : 448, 0, 12, 12, 512, 512);
        }
    }

    protected void DrawButtonSearch(DrawContext context, int mouseX, int mouseY, IMousePointerSetter mpset, float deltaTicks) {
        boolean isPointing = TGAClientHelper.GUI_ButtonInrange(BT1X, BT1Y, mouseX, mouseY);
        if (isPointing) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT1X, BT1Y, 19, 186, 18, 18, 512, 512);
            mpset.SetPointID(BasicGUISizeWithRecipe.POINT_RECIPE_SEARCH_BUTTON);
        } else
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT1X, BT1Y, 19, 167, 18, 18, 512, 512);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT1X + 3, BT1Y + 3, 487, 0, 12, 12, 512, 512);
        SearchInput.render(context, mouseX, mouseY, deltaTicks);
    }

    protected void DrawReturnButton(DrawContext context, int mouseX, int mouseY, IMousePointerSetter mpset) {
        boolean isPointing = TGAClientHelper.GUI_ButtonInrange(BT1X, BT1Y, mouseX, mouseY);
        if (isPointing) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT1X, BT1Y, 19, 186, 18, 18, 512, 512);
            mpset.SetPointID(BasicGUISizeWithRecipe.POINT_RECIPE_RETURN_TO_ALL_BUTTON);
        } else
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT1X, BT1Y, 19, 167, 18, 18, 512, 512);
        context.drawItem(SetTarget, BT1X + 1, BT1Y + 1);
    }

    protected void DrawButonPagePrev(DrawContext context, int mouseX, int mouseY, IMousePointerSetter mpset) {
        boolean isPointing = TGAClientHelper.GUI_ButtonInrange(BT2X, BT2Y, mouseX, mouseY);
        if (isPointing) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT2X, BT2Y, 19, 186, 18, 18, 512, 512);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT2X + 3, BT2Y + 5, 500, 22, 12, 8, 512, 512);
            mpset.SetPointID(BasicGUISizeWithRecipe.POINT_RECIPE_PREV_BUTTON);
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT2X, BT2Y, 19, 167, 18, 18, 512, 512);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT2X + 3, BT2Y + 5, 500, 22, 12, 8, 512, 512);
        }
    }

    protected void DrawButtonPageNext(DrawContext context, int mouseX, int mouseY, IMousePointerSetter mpset) {
        boolean isPointing = TGAClientHelper.GUI_ButtonInrange(BT3X, BT3Y, mouseX, mouseY);
        if (isPointing) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT3X, BT3Y, 19, 186, 18, 18, 512, 512);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT3X + 3, BT3Y + 5, 500, 13, 12, 8, 512, 512);
            mpset.SetPointID(BasicGUISizeWithRecipe.POINT_RECIPE_NEXT_BUTTON);
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT3X, BT3Y, 19, 167, 18, 18, 512, 512);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TGAScreenHandlers.GUI_SHARE_0, BT3X + 3, BT3Y + 5, 500, 13, 12, 8, 512, 512);
        }
    }
// </editor-fold>

    // <editor-fold desc="GUI Actions">
    @Override
    public void ActionSearch() {
        String name = SearchInput.getText();
        if (!ShowOnlyCanCraft && name.toLowerCase(Locale.ROOT).equalsIgnoreCase(LastSearch)) {
            LastSearch = name;
            return;
        }
        InvokeSearch(SearchInput.getText());
    }

    @Override
    public void ActionNextPage() {
        if (SetTarget.isEmpty()) {
            int maxPage = (ShowingResult.size() - 1) / 25;
            if (CurrentPage >= maxPage) CurrentPage = 0;
            else CurrentPage++;
        } else {
            if (TartgetRecipes == null) return;
            int maxPage = (TartgetRecipes.size() + -1) / 5;
            if (SubViewPage >= maxPage) SubViewPage = 0;
            else SubViewPage++;
        }
    }

    @Override
    public void ActionPrevPage() {
        if (SetTarget.isEmpty()) {
            if (CurrentPage <= 0) CurrentPage = (ShowingResult.size() - 1) / 25;
            else CurrentPage--;
        } else if (SubViewPage <= 0) SubViewPage = (TartgetRecipes.size() - 1) / 5;
        else SubViewPage--;
    }

    @Override
    public void ActionBackButton() {
        SetTarget = ItemStack.EMPTY;
    }

    @Override
    public void ActionSetCanCraft() {
        if (ShowOnlyCanCraft) return;
        ShowOnlyCanCraft = true;
        InvokeSearch(LastSearch);
        SetTarget = ItemStack.EMPTY;
    }

    @Override
    public void ActionSetAllCraft() {
        if (!ShowOnlyCanCraft) return;
        ShowOnlyCanCraft = false;
        InvokeSearch(LastSearch);
        SetTarget = ItemStack.EMPTY;
    }

    @Override
    public void ActionGridClick() {
        CraftOutputPatch<OneInRecipe> ima = TGAHelper.GetOrNull(ShowingResult, SubPointID);
        if (ima == null) return;
        SetTarget = ima.Target;
        TartgetRecipes = ima.Recipes;
        SubViewPage = 0;
    }
    // </editor-fold>

    // <editor-fold desc="KeyBoard and Mouse">
    @Override
    public boolean KeyPressed(int keyCode, int scanCode, int modifiers) {
        if (!IsShow || !IsForcusing) return false;
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            ActionSearch();
            return true;
        }
        return (SearchInput.keyPressed(keyCode, scanCode, modifiers) || MinecraftClient.getInstance().options.inventoryKey.matchesKey(keyCode, scanCode));
    }

    @Override
    public boolean CharTyped(char chr, int modifiers) {
        return IsShow && IsForcusing && SearchInput.charTyped(chr, modifiers);
    }

    @Override
    public boolean MouseClicked(double mouseX, double mouseY, int button) {
        if (!IsShow) return false;
        IsForcusing = SearchInput.mouseClicked(mouseX, mouseY, button);
        SearchInput.setFocused(IsForcusing);
        return IsForcusing;
    }
    // </editor-fold>
}