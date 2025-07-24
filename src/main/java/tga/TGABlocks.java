package tga;

import net.minecraft.sound.BlockSoundGroup;
import tga.Block.BoxWood;

public class TGABlocks {
    public static BoxWood BOX_WOOD = new BoxWood(FabricBlockSettings.of(Material.WOOD).strength(2.0f).sounds(BlockSoundGroup.WOOD));
}