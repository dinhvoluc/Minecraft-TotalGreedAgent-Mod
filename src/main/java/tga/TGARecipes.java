package tga;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import tga.BlockEntity.MetalWorkbenchTile;
import tga.WorkBook.MetalWorkBook;
import tga.WorkBook.OneSlotBook;
import tga.WorkBook.WorkRecipes.*;

public class TGARecipes {
    public static final float FIXED_CRAFT_CHANCE = 10f;
    public static MetalWorkBook MetalWorkbench = new MetalWorkBook(MetalWorkbenchTile.WORK_MODE_SIZE);
    public static OneSlotBook Cracker_LV0 = new OneSlotBook();
    public static OneSlotBook Cracker_LV1 = new OneSlotBook();
    public static OneSlotBook Cracker_LV2 = new OneSlotBook();

    public static void Load() {
        //Metal Work
        AddMetalWork(0, MetalWorkbenchTile.WORK_MODE_PLATE, new ItemStack(TGAItems.COPPER_PLATE), 5_000_00, 180, new ItemStack(Items.COPPER_INGOT));
        AddMetalWork(0, MetalWorkbenchTile.WORK_MODE_NAIL, new ItemStack(TGAItems.NAILS, 9), 900_00, 90, new ItemStack(Items.IRON_INGOT));
        AddMetalWork(0, MetalWorkbenchTile.WORK_MODE_NAIL, new ItemStack(TGAItems.NAILS), 100_00, 10, new ItemStack(Items.IRON_NUGGET));

        //CaCO3
        AddCrackerRecipes(0, OneToOne.CreateAutoBlanced(TGAItems.CACO3, 90, 1_000_00,
                Items.NAUTILUS_SHELL, Items.BONE,
                180, Items.TURTLE_SCUTE,
                270, Items.BONE_BLOCK));

        //Guayule seed/dust
        AddCrackerRecipes(0, new OneMainAndLuck(new ItemStack(TGAItems.CROP_GUAYULE_GRASS), 400_00, new ItemStack(TGAItems.GUAYULE_DUST), new ItemStack(TGABlocks.CROP_GUAYULE_YONG), 0.15f));
        AddCrackerRecipes(0, new OneMainAndLuck(new ItemStack(Items.SHORT_GRASS), 320_00, new ItemStack(TGAItems.TREE_WASTE), new ItemStack(TGABlocks.CROP_GUAYULE_YONG), 0.05f));
        AddCrackerRecipes(1, new OneToOne(new ItemStack(TGABlocks.CROP_GUAYULE_YONG, 5), 800_00, new ItemStack(TGAItems.GUAYULE_DUST)));

        //Food
        AddCrackerRecipes(0, new OneToTwo(new ItemStack(Items.WHEAT), 350_00, new ItemStack(TGAItems.WHEAT_FLOUR), new ItemStack(TGAItems.TREE_WASTE)));
        AddCrackerRecipes(0, new OneToTwo(new ItemStack(Items.HAY_BLOCK), 3150_00, new ItemStack(TGAItems.WHEAT_FLOUR, 9), new ItemStack(TGAItems.TREE_WASTE, 9)));
        AddCrackerRecipes(0, new OneToOne(new ItemStack(Items.WHEAT_SEEDS, 4), 300_00, new ItemStack(TGAItems.WHEAT_FLOUR)));

        //ORE crush
        AddCrackerRecipes(0, new OneToTwo(new ItemStack(Items.RAW_COPPER_BLOCK), 25_000_00, new ItemStack(TGAItems.DUST_COPPER, 10), new ItemStack(TGAItems.DUST_TIN)));

        //INGOT CRUSH
        AddCrackerRecipes(0, OneToOne.CreateAutoBlanced(TGAItems.DUST_TIN, 90, 1_500_00,
                TGAItems.INGOT_TIN));

        AddCrackerRecipes(0, OneToOne.CreateAutoBlanced(TGAItems.DUST_COPPER, 90, 2_000_00,
                Items.COPPER_INGOT, TGAItems.COPPER_PLATE,
                810,
                Items.COPPER_BLOCK,
                Items.CHISELED_COPPER, Items.COPPER_GRATE, Items.CUT_COPPER,
                Items.EXPOSED_CHISELED_COPPER, Items.EXPOSED_COPPER_GRATE, Items.EXPOSED_CUT_COPPER,
                Items.OXIDIZED_CHISELED_COPPER, Items.OXIDIZED_COPPER_GRATE, Items.OXIDIZED_CUT_COPPER,

                Items.WAXED_COPPER_BLOCK,
                Items.WAXED_CHISELED_COPPER, Items.WAXED_COPPER_GRATE, Items.WAXED_CUT_COPPER,
                Items.WAXED_EXPOSED_CHISELED_COPPER, Items.WAXED_EXPOSED_COPPER_GRATE, Items.WAXED_EXPOSED_CUT_COPPER,
                Items.WAXED_OXIDIZED_CHISELED_COPPER, Items.WAXED_OXIDIZED_COPPER_GRATE, Items.WAXED_OXIDIZED_CUT_COPPER,

                540,
                Items.CUT_COPPER_STAIRS, Items.EXPOSED_CUT_COPPER_STAIRS, Items.OXIDIZED_CUT_COPPER_STAIRS,

                Items.WAXED_CUT_COPPER_STAIRS, Items.WAXED_EXPOSED_CUT_COPPER_STAIRS, Items.WAXED_OXIDIZED_CUT_COPPER_STAIRS,

                405,
                Items.CUT_COPPER_SLAB, Items.EXPOSED_CUT_COPPER_SLAB, Items.OXIDIZED_CUT_COPPER_SLAB,
                Items.WAXED_CUT_COPPER_SLAB, Items.WAXED_EXPOSED_CUT_COPPER_SLAB, Items.WAXED_OXIDIZED_CUT_COPPER_SLAB,

                270,
                Items.COPPER_TRAPDOOR, Items.EXPOSED_COPPER_TRAPDOOR, Items.OXIDIZED_COPPER_TRAPDOOR,
                Items.WAXED_COPPER_TRAPDOOR, Items.WAXED_EXPOSED_COPPER_TRAPDOOR, Items.WAXED_OXIDIZED_COPPER_TRAPDOOR,

                180,
                Items.COPPER_DOOR, Items.EXPOSED_COPPER_DOOR, Items.OXIDIZED_COPPER_DOOR,
                Items.WAXED_COPPER_DOOR, Items.WAXED_EXPOSED_COPPER_DOOR, Items.WAXED_OXIDIZED_COPPER_DOOR));

        AddCrackerRecipes(0, OneToOne.CreateAutoBlanced(TGAItems.DUST_BRONZE, 90, 2_300_00,
                TGAItems.INGOT_BRONZE));
    }

    public static void AddMetalWork(int minlv, int mode, ItemStack result, long jinriki, int waterCool, ItemStack inputs)
    {
        if (inputs.isEmpty()) return;
        if (minlv <= 0) MetalWorkbench.Registers( new MetalWorkRecipe(inputs, jinriki, result, (int)(FluidConstants.BUCKET * waterCool / 1000), mode));
        //todo machine auto metalwork

    }

    public static void AddCrackerRecipes(int minlv, OneInRecipe... recipe) {
        if (minlv <= 0) Cracker_LV0.Registers(recipe);
        if (minlv <= 1) Cracker_LV1.Registers(recipe);
        if (minlv <= 2) Cracker_LV2.Registers(recipe);
    }
}