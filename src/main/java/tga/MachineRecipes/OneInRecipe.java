package tga.MachineRecipes;

import net.minecraft.item.ItemStack;
import tga.Items.ItemInt;

import java.util.List;

public abstract class OneInRecipe {
    public static final int FIXED_CRAFT = 2_000_000_000;
    public static final int RANDOM_SPACE = 100_000_000;
    public ItemStack Ingredient;
    public int NeedPower;

    public OneInRecipe(ItemStack ingredient, int power) {
        Ingredient = ingredient;
        NeedPower = power;
    }

    /**
     * @param stack 素材のインプット
     * @param craftedItems クラフト結果、ランダムと列のサイズご注意
     * @return 素材を取った後のItemStackです
     */
    public abstract ItemStack RealCraft(ItemStack stack, ItemStack[] craftedItems);

    /** レシピ本を登録用です。
     * @return 可能性のクラフト結果、2e9。メインとなる結果。ランダムで何が追加がある場合は100e6以下で対応
     */
    public abstract List<ItemInt> CraftChanceList();

    /** RealCraft()を実行出来るかのチェック段階です。
     * @param stack チェック素材のインプット
     * @return クラフト可能かどうか。
     */
    public boolean CanCraft(ItemStack stack) {
        return  ItemStack.areItemsAndComponentsEqual(stack, Ingredient) && stack.getCount() >= Ingredient.getCount();
    }
}