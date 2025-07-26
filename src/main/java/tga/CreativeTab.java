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
                    entries.add(TGABlocks.BOX_WOOD);
                    entries.add(TGABlocks.BOX_COPPER);
                    entries.add(TGAItems.RUBBER);
                    entries.add(TGABlocks.RUBBER_SHEET);
                    entries.add(TGAItems.REISIN);
                })
                .build());
    }
}