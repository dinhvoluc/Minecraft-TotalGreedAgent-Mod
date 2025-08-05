package tga.MachineRecipes;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import tga.Items.ItemFloat;

public abstract class OneInRecipe {
    public ItemStack Ingredient;
    public long NeedPower;

    public OneInRecipe(ItemStack ingredient, long power) {
        Ingredient = ingredient;
        NeedPower = power;
    }

    /**
     * @param stack 素材のインプット
     * @param craftedItems クラフト結果、ランダムと列のサイズご注意
     * @return 素材を取った後のItemStackです
     */
    public abstract ItemStack RealCraft(ItemStack stack, ItemStack[] craftedItems, Random random);

    /** レシピ本を登録用です。
     * 可能性のクラフト結果、10.0f。メインとなる結果。ランダムで何が追加がある場合は1.0f以下で対応
     */
    public ItemFloat[] CraftChanceList;

    /** RealCraft()を実行出来るかのチェック段階です。
     * @param stack チェック素材のインプット
     * @return クラフト可能かどうか。
     */
    public boolean CanCraft(ItemStack stack) {
        return  ItemStack.areItemsAndComponentsEqual(stack, Ingredient) && stack.getCount() >= Ingredient.getCount();
    }
}