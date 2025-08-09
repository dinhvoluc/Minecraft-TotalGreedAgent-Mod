package tga;

import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.sound.BlockSoundGroup;
import tga.Generator.JinrikiGenerator;

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