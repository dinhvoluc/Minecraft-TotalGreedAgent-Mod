package tga;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

public class CreativeTab {
    public static RegistryKey<ItemGroup> TGA_GROUP;

    public static void Load() {
        TGA_GROUP = RegistryKey.of(Registries.ITEM_GROUP.getKey(), TotalGreedyAgent.GetID("main"));
        Registry.register(Registries.ITEM_GROUP, TGA_GROUP, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.tga.main"))
                .icon(() -> new ItemStack(TGABlocks.MAN_CRACKER))
                .entries((context, entries) -> {
                    //Machine
                    entries.add(TGABlocks.MAN_CRACKER);
                    entries.add(TGABlocks.METAL_WORKBENCH);
                    entries.add(TGABlocks.JRK_PUMP);
                    //entries.add(TGAElectricBlocks.GEN_JINRIKI);
                    //BOX
                    entries.add(TGABlocks.BOX_WOOD);
                    entries.add(TGABlocks.BOX_COPPER);
                    entries.add(TGABlocks.BOX_BRONZE);
                    //TANK
                    entries.add(TGAItems.TANK_WOOD);
                    entries.add(TGAItems.TANK_COPPER);
                    entries.add(TGAItems.TANK_BRONZE);
                    //Rubber
                    entries.add(TGABlocks.RUBBER_SHEET);
                    entries.add(TGAItems.RUBBER);
                    entries.add(TGAItems.REISIN);
                    entries.add(TGAItems.CACO3);
                    entries.add(TGAItems.BOW_PRE_ACETONE);
                    entries.add(TGAItems.BOW_ACETONE);
                    entries.add(TGAItems.CROP_GUAYULE_GRASS);
                    entries.add(TGABlocks.CROP_GUAYULE_YONG);
                    entries.add(TGAItems.GUAYULE_DUST);
                    entries.add(TGAItems.TREE_WASTE);
                    //Food
                    entries.add(TGAItems.WHEAT_FLOUR);
                    entries.add(TGAItems.BREAD_DOUGH);
                    //Sozai
                    entries.add(TGAItems.NAILS);
                    //Dust
                    entries.add(TGAItems.DUST_TIN);
                    entries.add(TGAItems.DUST_COPPER);
                    entries.add(TGAItems.DUST_BRONZE);
                    entries.add(TGAItems.DUST_ALUMINUM);
                    entries.add(TGAItems.DUST_GOLD);
                    entries.add(TGAItems.DUST_SILVER);
                    entries.add(TGAItems.DUST_IRON);
                    entries.add(TGAItems.DUST_STEEL);
                    entries.add(TGAItems.DUST_TITAN);
                    //Ingot
                    entries.add(TGAItems.INGOT_TIN);
                    entries.add(TGAItems.INGOT_BRONZE);
                    entries.add(TGAItems.INGOT_ALUMIUM);
                    entries.add(TGAItems.INGOT_SILVER);
                    entries.add(TGAItems.INGOT_STEEL);
                    entries.add(TGAItems.INGOT_TITAN);
                    //Plate
                    entries.add(TGAItems.PLATE_COPPER);
                    entries.add(TGAItems.PLATE_BRONZE);
                    entries.add(TGAItems.PLATE_ALUMINUM);
                    entries.add(TGAItems.PLATE_GOLD);
                    entries.add(TGAItems.PLATE_IRON);
                    entries.add(TGAItems.PLATE_STEEL);
                    entries.add(TGAItems.PLATE_TITAN);
                    //BRONZE
                    entries.add(TGAItems.PART_BRONZE_TANK);
                    entries.add(TGAItems.PART_BRONZE_BOX);
                    entries.add(TGAItems.PART_BRONZE_TURBIN);
                    entries.add(TGAItems.PART_BRONZE_BOIL);
                    //PIPE
                    entries.add(TGABlocks.PIPE_BRONZE);
                    entries.add(TGABlocks.PIPE_STEEL);
                    entries.add(TGABlocks.PIPE_HOPPER);
                })
                .build());
    }
}