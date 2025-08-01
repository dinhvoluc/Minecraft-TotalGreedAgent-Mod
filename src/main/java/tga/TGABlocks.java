package tga;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import tga.Block.BoxStackBlock;
import tga.Block.RubberSheetBlock;
import tga.Crops.CropGuayule;
import tga.Crops.CustomCropBlock;
import tga.Machines.ManCracker;
import tga.Machines.ManCrackerTile;

import java.util.function.Function;

public final class TGABlocks {
    public static Block BOX_WOOD;
    public static Block BOX_WOOD_FILLED;
    public static Block BOX_COPPER;
    public static Block BOX_COPPER_FILLED;
    public static Block RUBBER_SHEET;
    public static Block MAN_CRACKER;
    public static Block X_CROP_GUAYULE;
    public static Block CROP_GUAYULE_YONG;

    public static void Load(boolean isClientSide) {
        CustomCropBlock.SHAPES_BY_AGE = Block.createShapeArray(6, (age) -> Block.createColumnShape(16.0F, 0.0F, 2 + age * 2));
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
        MAN_CRACKER = register("m_cracker_lv0", ManCracker::new, AbstractBlock.Settings.create()
                .mapColor(MapColor.DEEPSLATE_GRAY)
                .strength(4f, 6f)
                .sounds(BlockSoundGroup.STONE));
        X_CROP_GUAYULE = noDirectItem("crop_guayule", CropGuayule::new, AbstractBlock.Settings.create()
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .noCollision()
                .breakInstantly()
                .sounds(BlockSoundGroup.GRASS)
                .nonOpaque()
                .pistonBehavior(PistonBehavior.DESTROY));
        TGAItems.SetBioValue((CROP_GUAYULE_YONG = register("sd_guayule", CustomCropBlock::CropGuayule, AbstractBlock.Settings.create()
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .noCollision()
                .breakInstantly()
                .sounds(BlockSoundGroup.GRASS)
                .nonOpaque()
                .pistonBehavior(PistonBehavior.DESTROY))).asItem(), 0.3f);
        //Load server side ticker
        if (isClientSide) return;
        ManCracker.TICKER_SERVER = (a, b, c, d) -> d.TickS();
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