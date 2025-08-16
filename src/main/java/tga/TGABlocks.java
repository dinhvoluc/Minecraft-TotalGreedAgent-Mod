package tga;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import tga.Block.FluidHopper;
import tga.Block.JrkPump;
import tga.Block.RubberSheetBlock;
import tga.Block.Template.BoxStackBlock;
import tga.Block.Template.PipeBaseBlock;
import tga.Block.Template.TankBlock;
import tga.Crops.CropGuayule;
import tga.Crops.CustomCropBlock;
import tga.Items.EFItemTank;
import tga.Items.TheBoxRegisterReturn;
import tga.Machines.ManCracker;
import tga.Machines.MetalWorkbench;
import tga.Str.Dir64;
import tga.Str.FHopperProperty;
import tga.Str.TankItem1;
import tga.Str.TankProperty;

import java.util.function.Function;
import java.util.function.Supplier;

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

    public static Block PIPE_BRONZE;
    public static Block PIPE_STEEL;

    public static Block PIPE_HOPPER;
    public static Block PIPE_HOPPER_FILLED;

    public static Block JRK_PUMP;
    public static Block JRK_PUMP_FILLED;

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

        //BOX
        TheBoxRegisterReturn<Item> woodenBox = RegisterBox("box_wood", BoxStackBlock::new,
                Bs(0.2f, 1.5f, MapColor.TERRACOTTA_BROWN, BlockSoundGroup.WOOD),
                8, ()->TGAItems.BOX_WOOD, ()-> TGAItems.BOX_WOOD_FILLED);
        BOX_WOOD = woodenBox.BlockEmpty;
        TGAItems.SetBurnTime(TGAItems.BOX_WOOD = woodenBox.ItemEmpty, 800);
        BOX_WOOD_FILLED = woodenBox.BlockFilled;
        TGAItems.BOX_WOOD_FILLED = woodenBox.ItemFilled;

        TheBoxRegisterReturn<Item> copperBox = RegisterBox("box_copper", BoxStackBlock::new,
                Bs(0.2f, 2.5f, MapColor.BROWN, BlockSoundGroup.METAL),
                16, ()->TGAItems.BOX_COPPER, ()->TGAItems.BOX_COPPER_FILLED);
        BOX_COPPER = copperBox.BlockEmpty;
        BOX_COPPER_FILLED = copperBox.BlockFilled;
        TGAItems.BOX_COPPER = copperBox.ItemEmpty;
        TGAItems.BOX_COPPER_FILLED = copperBox.ItemFilled;

        TheBoxRegisterReturn<Item> bronzeBox = RegisterBox("box_bronze", BoxStackBlock::new,
                Bs(0.2f, 3f, MapColor.BROWN, BlockSoundGroup.METAL),
                32, ()-> TGAItems.BOX_BRONZE, () -> TGAItems.BOX_BRONZE_FILLED);
        BOX_BRONZE = bronzeBox.BlockEmpty;
        BOX_BRONZE_FILLED = bronzeBox.BlockFilled;
        TGAItems.BOX_BRONZE = bronzeBox.ItemEmpty;
        TGAItems.BOX_BRONZE_FILLED = bronzeBox.ItemFilled;

        //TANK
        TheBoxRegisterReturn<EFItemTank> woodTank = RegisterTank( "tank_wood", TankBlock::new,
                Bs(0.2f, 1.5f, MapColor.TERRACOTTA_BROWN, BlockSoundGroup.WOOD),
               new TankProperty(()->TGAItems.TANK_WOOD, ()->TGAItems.TANK_WOOD_FILLED, 8_000, "tank_wood"));
        TANK_WOOD = woodTank.BlockEmpty;
        TGAItems.SetBurnTime(TGAItems.TANK_WOOD = woodTank.ItemEmpty, 800);
        TANK_WOOD_FILLED = woodTank.BlockFilled;
        TGAItems.TANK_WOOD_FILLED = woodTank.ItemFilled;

        TheBoxRegisterReturn<EFItemTank> copperTank = RegisterTank( "tank_copper", TankBlock::new,
                Bs(0.2f, 2.5f, MapColor.BROWN, BlockSoundGroup.METAL),
                new TankProperty( ()->TGAItems.TANK_COPPER, ()->TGAItems.TANK_COPPER_FILLED, 16_000, "tank_copper"));
        TANK_COPPER = copperTank.BlockEmpty;
        TGAItems.TANK_COPPER = copperTank.ItemEmpty;
        TANK_COPPER_FILLED = copperTank.BlockFilled;
        TGAItems.TANK_COPPER_FILLED = copperTank.ItemFilled;

        TheBoxRegisterReturn<EFItemTank> bronzeTank = RegisterTank( "tank_bronze", TankBlock::new,
                Bs(0.2f, 3f, MapColor.BROWN, BlockSoundGroup.METAL),
                new TankProperty( ()->TGAItems.TANK_BRONZE, ()->TGAItems.TANK_BRONZE_FILLED, 32_000, "tank_bronze"));
        TANK_BRONZE = bronzeTank.BlockEmpty;
        TGAItems.TANK_BRONZE = bronzeTank.ItemEmpty;
        TANK_BRONZE_FILLED = bronzeTank.BlockFilled;
        TGAItems.TANK_BRONZE_FILLED = bronzeTank.ItemFilled;
        //Workbench
        MAN_CRACKER = Register("m_cracker_lv0", ManCracker::new,
                Bs(4f, 6f, MapColor.DEEPSLATE_GRAY ,BlockSoundGroup.STONE));
        METAL_WORKBENCH = Register("metal_workbench", MetalWorkbench::new,
                Bs(4f, 6f, MapColor.STONE_GRAY, BlockSoundGroup.STONE).nonOpaque());

        //PIPE
        PIPE_BRONZE = Register(TGAID.ID_PIPE_BRONZE = TotalGreedyAgent.GetID("pipe_bronze"), PipeBaseBlock::new,
                Bs(0.3f, 2f, MapColor.BROWN, BlockSoundGroup.METAL).nonOpaque());

        PIPE_STEEL = Register(TGAID.ID_PIPE_STEEL = TotalGreedyAgent.GetID("pipe_steel"), PipeBaseBlock::new,
                Bs(0.3f, 2f, MapColor.BROWN, BlockSoundGroup.METAL).nonOpaque());

        TheBoxRegisterReturn<EFItemTank> fhopper = RegisterTank("fhopper", FluidHopper::new,
                Bs(1f, 3f, MapColor.IRON_GRAY, BlockSoundGroup.METAL).nonOpaque(),
                new FHopperProperty(()->TGAItems.PIPE_HOPPER, ()->TGAItems.PIPE_HOPPER_FILLED, 16_000, "fhopper", 20));
        PIPE_HOPPER = fhopper.BlockEmpty;
        TGAItems.PIPE_HOPPER = fhopper.ItemEmpty;
        PIPE_HOPPER_FILLED = fhopper.BlockFilled;
        TGAItems.PIPE_HOPPER_FILLED = fhopper.ItemFilled;

        TheBoxRegisterReturn<EFItemTank> jrkpump = RegisterTank("jrkpump", JrkPump::new,
                Bs(1f, 2f, MapColor.DEEPSLATE_GRAY, BlockSoundGroup.STONE).nonOpaque(),
                new FHopperProperty(()->TGAItems.JRK_PUMP, ()->TGAItems.JRK_PUMP_FILLED, 16_000, "jrkpump", 5));
        JRK_PUMP = jrkpump.BlockEmpty;
        TGAItems.JRK_PUMP = jrkpump.ItemEmpty;
        JRK_PUMP_FILLED = jrkpump.BlockFilled;
        TGAItems.JRK_PUMP_FILLED = jrkpump.ItemFilled;

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
    }

    private static AbstractBlock.Settings Bs(float hardness, float resistance, MapColor color, BlockSoundGroup sound) {
        return AbstractBlock.Settings.create().strength(hardness, resistance).mapColor(color).sounds(sound);
    }

    public static TheBoxRegisterReturn<Item> RegisterBox(String path, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings blockSetting, int size, Supplier<Item> itemEmpty, Supplier<Item> itemFilled) {
        Block bE = Register(path, blockFactory, blockSetting);
        Block bF = Register(1, path + "_filled", blockFactory, blockSetting);
        BoxStackBlock.SetupType(size, Registries.BLOCK.getId(bE), itemEmpty, Registries.BLOCK.getId(bF), itemFilled, path);
        return new TheBoxRegisterReturn<>(bE, bF, bE.asItem(), bF.asItem());
    }

    public static TheBoxRegisterReturn<EFItemTank> RegisterTank(String path, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings blockSetting, TankProperty prop) {
        Identifier idE = TotalGreedyAgent.GetID(path);
        Identifier idF = TotalGreedyAgent.GetID(path + "_filled");
        TankBlock.SHARED_TANK_PROPERTY.put(idE, prop);
        TankBlock.SHARED_TANK_PROPERTY.put(idF, prop);

        Block bE = NoDirectItem(idE, blockFactory, blockSetting);
        Block bF = NoDirectItem(idF, blockFactory, blockSetting);

        EFItemTank iE = (EFItemTank) TGAItems.Register(idE, (s) -> new EFItemTank(bE, s), new Item.Settings());
        EFItemTank iF = (EFItemTank) TGAItems.Register(idF, (s) -> new EFItemTank(bF, s), new Item.Settings());
        FluidStorage.ITEM.registerForItems(TankItem1::of, iE, iF);
        return new TheBoxRegisterReturn<>(bE, bF, iE, iF);
    }

    public static Block NoDirectItem(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return NoDirectItem(TotalGreedyAgent.GetID(path), factory, settings);
    }
    public static Block NoDirectItem(Identifier id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        RegistryKey<Block> registryKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        return Blocks.register(registryKey, factory, settings);
    }

    public static Block Register(int maxStack, String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return Register(maxStack, TotalGreedyAgent.GetID(path), factory, settings);
    }

    public static Block Register(int maxStack, Identifier id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        Block block = NoDirectItem(id, factory, settings);
        Items.register(block, new Item.Settings().maxCount(maxStack));
        return block;
    }

    public static Block Register(Identifier id, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        Block block = NoDirectItem(id, factory, settings);
        Items.register(block);
        return block;
    }
    public static Block Register(String path, Function<AbstractBlock.Settings, Block> factory, AbstractBlock.Settings settings) {
        return Register(TotalGreedyAgent.GetID(path), factory, settings);
    }
}