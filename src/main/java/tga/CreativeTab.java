package tga;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class CreativeTab {
    public static RegistryKey<ItemGroup> TGA_GROUP;

    public static void Load() {
        TGA_GROUP = RegistryKey.of(Registries.ITEM_GROUP.getKey(), TotalGreedyAgent.GetID("main"));
        Registry.register(Registries.ITEM_GROUP, TGA_GROUP, FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.tga.main"))
                .icon(() -> new ItemStack(TGABlocks.BOX_WOOD))
                .entries((context, entries) -> {
                    entries.add(TGABlocks.MAN_CRACKER);
                    entries.add(TGABlocks.BOX_WOOD);
                    entries.add(TGABlocks.BOX_COPPER);
                    entries.add(TGABlocks.RUBBER_SHEET);
                    entries.add(TGAItems.RUBBER);
                    entries.add(TGAItems.REISIN);
                    entries.add(TGAItems.COPPER_PLATE);
                    entries.add(TGAItems.NAILS);
                    entries.add(TGAItems.CACO3);
                    entries.add(TGAItems.BOW_PRE_ACETONE);
                    entries.add(TGAItems.BOW_ACETONE);
                    entries.add(TGAItems.CROP_GUAYULE_GRASS);
                    entries.add(TGABlocks.CROP_GUAYULE_YONG);
                    entries.add(TGAItems.GUAYULE_DUST);
                })
                .build());
    }
}