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
import tga.Block.BoxStackBlock;
import tga.Block.RubberSheetBlock;

import java.util.function.Function;

public final class TGABlocks {
    public static Block BOX_WOOD;
    public static Block BOX_WOOD_FILLED;
    public static Block BOX_COPPER;
    public static Block BOX_COPPER_FILLED;
    public static Block RUBBER_SHEET;
    public static Block MAN_CRACKER;
    public static Block CROP_GUAYULE;

    public static void Load(boolean isClientSide) {
        BOX_WOOD_FILLED = register("box_wood_filled", BoxStackBlock::Create_Wooden, Block.Settings.create()
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .strength(0.2f, 3.5f)
                .sounds(BlockSoundGroup.WOOD));
        TGAItems.SetBurnTime((BOX_WOOD = register("box_wood", BoxStackBlock::Create_Wooden, Block.Settings.create()
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .strength(0.2f, 3.5f)
                .sounds(BlockSoundGroup.WOOD))).asItem(), 800);
        BOX_COPPER = register("box_copper", BoxStackBlock::Create_Copper, Block.Settings.create()
                .mapColor(MapColor.BROWN)
                .strength(0.2f, 2.5f)
                .sounds(BlockSoundGroup.METAL));
        BOX_COPPER_FILLED = register("box_copper_filled", BoxStackBlock::Create_Copper, Block.Settings.create()
                .mapColor(MapColor.BROWN)
                .strength(0.2f, 2.5f)
                .sounds(BlockSoundGroup.METAL));
        TGAItems.SetBurnTime((RUBBER_SHEET = register("rubber_sheet", RubberSheetBlock::new, Block.Settings.create()
                .mapColor(MapColor.BLACK)
                .strength(0.2f, 0.2f)
                .sounds(BlockSoundGroup.WOOL)
                .nonOpaque())).asItem(), 600);
        MAN_CRACKER = register("m_cracker_lv0", Block::new, AbstractBlock.Settings.create()
                .mapColor(MapColor.DEEPSLATE_GRAY)
                .strength(4f, 6f)
                .sounds(BlockSoundGroup.STONE)
                .requiresTool());
        //CROP_GUAYULE = noDirectItem(TotalGreedyAgent.GetCropID(GuayuleCrop.MC_ID), GuayuleCrop::new, AbstractBlock.Settings.copy(Blocks.WHEAT));
    }

    private static Block noDirectItem(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        final Identifier identifier = TotalGreedyAgent.GetID(path);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);
        return Blocks.register(registryKey, factory, settings);
    }

    private static Block register(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        Block block = noDirectItem(path, factory, settings);
        Items.register(block);
        return block;
    }
}