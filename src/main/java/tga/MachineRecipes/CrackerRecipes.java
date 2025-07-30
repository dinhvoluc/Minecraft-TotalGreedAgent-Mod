package tga.MachineRecipes;

import net.minecraft.item.ItemStack;
import tga.Items.ItemFloat;

import java.util.List;

public class CrackerRecipes {
    public interface ICrackerRecipe {
        public ItemStack GetRadomInput(int index);
        public boolean TryToCraft(ItemStack stack);
        public int GetEnergyNeed(ItemStack stack);
        public ItemStack GetMainOutput(ItemStack stack);
        public ItemStack OnStartCrafting(ItemStack stack);
        public List<ItemFloat> GetCraftBonus(ItemStack stack);
        public void RegistryInOut(CrackerRecipes host);
    }
}