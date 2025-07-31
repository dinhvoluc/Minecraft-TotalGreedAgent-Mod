package tga;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import tga.MachineRecipes.OneMainAndLuck;
import tga.MachineRecipes.OneSlotBook;
import tga.MachineRecipes.OneToOne;

public class TGARecipes {
    public static OneSlotBook Cracker_LV0 = new OneSlotBook();
    public static void Load(){
        Cracker_LV0.Register(new OneToOne(new ItemStack(Items.NAUTILUS_SHELL), 3_000_00, new ItemStack(TGAItems.CACO3)));
        Cracker_LV0.Register(new OneToOne(new ItemStack(Items.TURTLE_SCUTE), 5_000_00, new ItemStack(TGAItems.CACO3, 2)));
        Cracker_LV0.Register(new OneToOne(new ItemStack(Items.BONE_BLOCK), 10_000_00, new ItemStack(TGAItems.CACO3)));
        Cracker_LV0.Register(new OneMainAndLuck(new ItemStack(TGAItems.CROP_GUAYULE_GRASS), 500_00, new ItemStack(TGAItems.GUAYULE_DUST), new ItemStack(TGABlocks.CROP_GUAYULE), 15_000_000));
        Cracker_LV0.Register(new OneMainAndLuck(new ItemStack(Items.SHORT_GRASS), 200_00, new ItemStack(TGAItems.TREE_WASTE), new ItemStack(TGABlocks.CROP_GUAYULE), 5_000_000));
    }
}