package tga.WorkBook;

import tga.Items.CraftOutputPatch;
import tga.Mechanic.IItemChecker;

import java.util.List;

public interface ICommonBook {
    void SearchAppend(List<CraftOutputPatch<ACommonRecipe>> showingResult, String name, boolean showOnlyCanCraft, IItemChecker itemChecker);
}