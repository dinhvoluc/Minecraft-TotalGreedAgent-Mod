package tga;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import tga.Block.*;
import tga.Crops.CropGuayule;
import tga.Crops.CustomCropBlock;
import tga.Items.TankItem1;
import tga.Items.TheBoxRegisterReturn;
import tga.Machines.ManCracker;
import tga.Machines.MetalWorkbench;
import tga.Str.Dir64;

import java.util.function.Function;

public final class TGABlocks {
    public static final IntProperty STATE4 = IntProperty.of("state", 0, 3);
    public static final IntProperty PLUG_DIR64 = IntProperty.of("plug", 0, 63);

    public static Block BOX_WOOD;
    public static Block BOX_WOOD_FILLED;
    public static Block BOX_COPPER;
    public static Block BOX_COPPER_FILLED;
    public static Block BOX_BRONZE;
    public static Block BOX_BRONZE_FILLED;

    public static Block TANK_WOOD;
    public static Block TANK_WOOD_FILLED;
    public static Block TANK_COPPER;
    public static Block TANK_COPPER_FILLED;
    public static Block TANK_BRONZE;
    public static Block TANK_BRONZE_FILLED;

    public static Block RUBBER_SHEET;
    public static Block X_CROP_GUAYULE;
    public static Block CROP_GUAYULE_YONG;

    public static Block MAN_CRACKER;
    public static Block METAL_WORKBENCH;
    public static Block JRK_PUMP;

    public static Block PIPE_BRONZE;
    public static Block PIPE_STEEL;

    public static void Load(boolean isClientSide) {
        CustomCropBlock.SHAPES_BY_AGE = Block.createShapeArray(6, (age) -> Block.createColumnShape(16.0D, 0.0D, 2 + age * 2));
        PipeBaseBlock.SHAPE_BY_PLUG = new VoxelShape[64];
        for(var i =0; i < 64; i++) {
            double x = 4;
            double y = 4;
            double z = 4;
            double xS = 12;
            double yS = 12;
            double zS = 12;
            for (Direction checker : new Dir64(i).GetAllHave())
                switch (checker) {
                    case Direction.UP -> yS = 16;
                    case Direction.DOWN -> y = 0;
                    case Direction.NORTH -> z = 0;
                    case Direction.SOUTH -> zS = 16;
                    case Direction.WEST -> x = 0;
                    case Direction.EAST -> xS = 16;
                }
            PipeBaseBlock.SHAPE_BY_PLUG[i] = Block.createCuboidShape(x, y, z, xS, yS, zS);
        }
        //Workbench
        MAN_CRACKER = Register("m_cracker_lv0", ManCracker::new,
                Bs(4f, 6f, MapColor.DEEPSLATE_GRAY ,BlockSoundGroup.STONE));
        METAL_WORKBENCH = Register("metal_workbench", MetalWorkbench::new,
                Bs(4f, 6f, MapColor.STONE_GRAY, BlockSoundGroup.STONE).nonOpaque());
        JRK_PUMP = Register("jrkpump", JrkPump::new,
                Bs(1f, 2f, MapColor.DEEPSLATE_GRAY, BlockSoundGroup.STONE).nonOpaque());
        //PIPE
        PIPE_BRONZE = Register("pipe_bronze", PipeBaseBlock::Create_Bronze,
                Bs(1f, 2f, MapColor.BROWN, BlockSoundGroup.METAL).nonOpaque());
        PIPE_STEEL = Register("pipe_steel", PipeBaseBlock::Create_Steel,
                Bs(1f, 2f, MapColor.BROWN, BlockSoundGroup.METAL).nonOpaque());
        //BOX
        TheBoxRegisterReturn woodenBox = RegisterBox("box_wood", BoxStackBlock::Create_Wooden,
                Bs(0.2f, 1.5f, MapColor.TERRACOTTA_BROWN, BlockSoundGroup.WOOD));
        BOX_WOOD = woodenBox.BlockEmpty;
        TGAItems.SetBurnTime(woodenBox.ItemEmpty, 800);
        BOX_WOOD_FILLED = woodenBox.BlockFilled;
        TheBoxRegisterReturn copperBox = RegisterBox("box_copper", BoxStackBlock::Create_Copper,
                Bs(0.2f, 2.5f, MapColor.BROWN, BlockSoundGroup.METAL));
        BOX_COPPER = copperBox.BlockEmpty;
        BOX_COPPER_FILLED = copperBox.BlockFilled;
        TheBoxRegisterReturn bronzeBox = RegisterBox("box_bronze", BoxStackBlock::Create_Bronze,
                Bs(0.2f, 3f, MapColor.BROWN, BlockSoundGroup.METAL));
        BOX_BRONZE = bronzeBox.BlockEmpty;
        BOX_BRONZE_FILLED = bronzeBox.BlockFilled;
        //TANK
        TheBoxRegisterReturn woodTank = RegisterTank( "tank_wood", TankBlock::Create_Wooden,
                Bs(0.2f, 1.5f, MapColor.TERRACOTTA_BROWN, BlockSoundGroup.WOOD));
        TANK_WOOD = woodTank.BlockEmpty;
        TGAItems.SetBurnTime(TGAItems.TANK_WOOD = woodTank.ItemEmpty, 800);
        TANK_WOOD_FILLED = woodTank.BlockFilled;
        TGAItems.TANK_WOOD_FILLED = woodTank.ItemFilled;

        TheBoxRegisterReturn copperTank = RegisterTank( "tank_copper", TankBlock::Create_Copper,
                Bs(0.2f, 2.5f, MapColor.BROWN, BlockSoundGroup.METAL));
        TANK_COPPER = copperTank.BlockEmpty;
        TGAItems.TANK_COPPER = copperTank.ItemEmpty;
        TANK_COPPER_FILLED = copperTank.BlockFilled;
        TGAItems.TANK_COPPER_FILLED = copperTank.ItemFilled;

        TheBoxRegisterReturn bronzeTank = RegisterTank( "tank_bronze", TankBlock::Create_Bronze,
                Bs(0.2f, 3f, MapColor.BROWN, BlockSoundGroup.METAL));
        TANK_BRONZE = bronzeTank.BlockEmpty;
        TGAItems.TANK_BRONZE = bronzeTank.ItemEmpty;
        TANK_BRONZE_FILLED = bronzeTank.BlockFilled;
        TGAItems.TANK_BRONZE_FILLED = bronzeTank.ItemFilled;
        //OTHER
        TGAItems.SetBurnTime((RUBBER_SHEET = Register("rubber_sheet", RubberSheetBlock::new,
                Bs(0.2f, 0.2f, MapColor.BLACK, BlockSoundGroup.WOOL).nonOpaque())).asItem(), 600);
        AbstractBlock.Settings cropSetting = AbstractBlock.Settings.create()
                .noCollision().breakInstantly().sounds(BlockSoundGroup.GRASS).nonOpaque().pistonBehavior(PistonBehavior.DESTROY);
        X_CROP_GUAYULE = NoDirectItem("crop_guayule", CropGuayule::new, cropSetting.mapColor(MapColor.GREEN));
        TGAItems.SetBioValue((CROP_GUAYULE_YONG = Register("sd_guayule",
                CustomCropBlock::CropGuayule, cropSetting.mapColor(MapColor.BROWN))).asItem(), 0.3f);
        //Load server side ticker
        if (isClientSide) return;
        ManCracker.TICKER_SERVER = (a, b, c, d) -> d.TickS();
        MetalWorkbench.TICKER_SERVER = (a, b, c, d) -> d.TickS(c);
    }

    private static AbstractBlock.Settings Bs(float hardness, float resistance, MapColor color, BlockSoundGroup sound) {
        return AbstractBlock.Settings.create().strength(hardness, resistance).mapColor(color).sounds(sound);
    }

    public static TheBoxRegisterReturn RegisterBox(String path, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings blockSetting) {
        Block bE = Register(path, blockFactory, blockSetting);
        Block bF = Register(1,path + "_filled", blockFactory, blockSetting);
        return new TheBoxRegisterReturn(bE, bF);
    }

    public static TheBoxRegisterReturn RegisterTank(String path, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings blockSetting) {
        Block bE = Register(path, blockFactory, blockSetting);
        Block bF = Register(1,path + "_filled", blockFactory, blockSetting);
        Item iE = bE.asItem();
        Item iF = bF.asItem();
        FluidStorage.ITEM.registerForItems(TankItem1::of, iE, iF);
        return new TheBoxRegisterReturn(bE, bF, iE, iF);
    }

    public static Block NoDirectItem(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        final Identifier identifier = TotalGreedyAgent.GetID(path);
        final RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, identifier);
        return Blocks.register(registryKey, factory, settings);
    }

    public static Block Register(int maxStack, String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        Block block = NoDirectItem(path, factory, settings);
        Items.register(block, new Item.Settings().maxCount(1));
        return block;
    }

    public static Block Register(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        Block block = NoDirectItem(path, factory, settings);
        Items.register(block);
        return block;
    }
}