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
import net.minecraft.util.Identifier;
import tga.Block.BoxStackBlock;
import tga.Block.RubberSheetBlock;
import tga.Block.TankBlock;
import tga.Crops.CropGuayule;
import tga.Crops.CustomCropBlock;
import tga.Generator.JinrikiGenerator;
import tga.Machines.ManCracker;

import java.util.function.Function;

public final class TGAElectricBlocks {
    public static Block GEN_JINRIKI;

    public static void Load(boolean isClientSide) {
        //BOX
        GEN_JINRIKI = TGABlocks.Register("g_jrk", JinrikiGenerator::new, Block.Settings.create()
                .mapColor(MapColor.TERRACOTTA_BROWN)
                .strength(0.2f, 3.5f)
                .sounds(BlockSoundGroup.WOOD));
    }
}