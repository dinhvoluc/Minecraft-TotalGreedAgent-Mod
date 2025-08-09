package tga;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import tga.Block.BoxStackBlock;
import tga.Block.RubberSheetBlock;
import tga.Block.TankBlock;
import tga.Crops.CropGuayule;
import tga.Crops.CustomCropBlock;
import tga.Machines.ManCracker;
import tga.Machines.MetalWorkbench;

import java.util.function.Function;

public final class TGABlocks {
    public static final IntProperty STATE4 = IntProperty.of("state", 0, 3);

    public static Block BOX_WOOD;
    public static Block BOX_WOOD_FILLED;
    public static Block BOX_COPPER;
    public static Block BOX_COPPER_FILLED;

    public static Block TANK_WOOD;
    public static Block TANK_WOOD_FILLED;
    public static Block TANK_COPPER;
    public static Block TANK_COPPER_FILLED;

    public static Block RUBBER_SHEET;
    public static Block X_CROP_GUAYULE;
    public static Block CROP_GUAYULE_YONG;

    public static Block MAN_CRACKER;
    public static Block METAL_WORKBENCH;

    public static void Load(boolean isClientSide) {
        CustomCropBlock.SHAPES_BY_AGE = Block.createShapeArray(6, (age) -> Block.createColumnShape(16.0F, 0.0F, 2 + age * 2));
        //Workbench
        MAN_CRACKER = Register("m_cracker_lv0", ManCracker::new, AbstractBlock.Settings.create()
                .mapColor(MapColor.DEEPSLATE_GRAY)
                .strength(4f, 6f)
                .sounds(BlockSoundGroup.STONE));
        METAL_WORKBENCH = Register("metal_workbench", MetalWorkbench::new, AbstractBlock.Settings.create()
                .mapColor(MapColor.STONE_GRAY)
                .strength(4f, 6f)
                .sounds(BlockSoundGroup.STONE)
                .nonOpaque());
        //BOX
        BOX_WOOD_FILLED = Register("box_wood_filled", BoxStackBlock::Create_Wooden, Block.Settings.create()
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .strength(0.2f, 3.5f)
                .sounds(BlockSoundGroup.WOOD));
        TGAItems.SetBurnTime((BOX_WOOD = Register("box_wood", BoxStackBlock::Create_Wooden, Block.Settings.create()
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .strength(0.2f, 3.5f)
                .sounds(BlockSoundGroup.WOOD))).asItem(), 800);
        BOX_COPPER = Register("box_copper", BoxStackBlock::Create_Copper, Block.Settings.create()
                .mapColor(MapColor.BROWN)
                .strength(0.2f, 2.5f)
                .sounds(BlockSoundGroup.METAL));
        BOX_COPPER_FILLED = Register("box_copper_filled", BoxStackBlock::Create_Copper, Block.Settings.create()
                .mapColor(MapColor.BROWN)
                .strength(0.2f, 2.5f)
                .sounds(BlockSoundGroup.METAL));
        //TANK
        TANK_WOOD_FILLED = Register("tank_wood_filled", TankBlock::Create_Wooden, Block.Settings.create()
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .strength(0.2f, 3.5f)
                .sounds(BlockSoundGroup.WOOD));
        TGAItems.SetBurnTime((TANK_WOOD = Register("tank_wood", TankBlock::Create_Wooden, Block.Settings.create()
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .strength(0.2f, 3.5f)
                .sounds(BlockSoundGroup.WOOD))).asItem(), 400);
        TANK_COPPER = Register("tank_copper", TankBlock::Create_Copper, Block.Settings.create()
                .mapColor(MapColor.BROWN)
                .strength(0.2f, 2.5f)
                .sounds(BlockSoundGroup.METAL));
        TANK_COPPER_FILLED = Register("tank_copper_filled", TankBlock::Create_Copper, Block.Settings.create()
                .mapColor(MapColor.BROWN)
                .strength(0.2f, 2.5f)
                .sounds(BlockSoundGroup.METAL));
        //OTHER
        TGAItems.SetBurnTime((RUBBER_SHEET = Register("rubber_sheet", RubberSheetBlock::new, Block.Settings.create()
                .mapColor(MapColor.BLACK)
                .strength(0.2f, 0.2f)
                .sounds(BlockSoundGroup.WOOL)
                .nonOpaque())).asItem(), 600);
        X_CROP_GUAYULE = NoDirectItem("crop_guayule", CropGuayule::new, AbstractBlock.Settings.create()
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .noCollision()
                .breakInstantly()
                .sounds(BlockSoundGroup.GRASS)
                .nonOpaque()
                .pistonBehavior(PistonBehavior.DESTROY));
        TGAItems.SetBioValue((CROP_GUAYULE_YONG = Register("sd_guayule", CustomCropBlock::CropGuayule, AbstractBlock.Settings.create()
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .noCollision()
                .breakInstantly()
                .sounds(BlockSoundGroup.GRASS)
                .nonOpaque()
                .pistonBehavior(PistonBehavior.DESTROY))).asItem(), 0.3f);
        //Load server side ticker
        if (isClientSide) return;
        ManCracker.TICKER_SERVER = (a, b, c, d) -> d.TickS();
        MetalWorkbench.TICKER_SERVER = (a, b, c, d) -> d.TickS(c);
    }

    public static Block NoDirectItem(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        final Identifier identifier = TotalGreedyAgent.GetID(path);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);
        return Blocks.register(registryKey, factory, settings);
    }

    public static Block Register(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        Block block = NoDirectItem(path, factory, settings);
        Items.register(block);
        return block;
    }
}