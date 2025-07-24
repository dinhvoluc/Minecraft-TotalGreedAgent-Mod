package tga;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import tga.Block.BoxWood;

import java.util.function.Function;

public final class TGABlocks {
    public static Block BOX_WOOD;

    public static void register() {
        BOX_WOOD = register("box_wood", BoxWood::new, Block.Settings.create().mapColor(MapColor.TERRACOTTA_BROWN).strength(2.0f).sounds(BlockSoundGroup.WOOD));
    }
    private static Block register(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        final Identifier identifier = TotalGreedyAgent.GetID(path);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);

        final Block block = Blocks.register(registryKey, factory, settings);
        Items.register(block);
        return block;
    }
}