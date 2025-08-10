package tga.Items;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class TheBoxRegisterReturn {
    public final Block BlockEmpty;
    public final Block BlockFilled;
    public final Item ItemEmpty;
    public final Item ItemFilled;

    public TheBoxRegisterReturn(Block blockEmpty, Block blockFilled) {
        BlockEmpty = blockEmpty;
        BlockFilled = blockFilled;
        ItemEmpty = blockEmpty.asItem();
        ItemFilled = blockFilled.asItem();
    }

    public TheBoxRegisterReturn(Block blockEmpty, Block blockFilled, Item itemEmpty, Item itemFilled) {
        BlockEmpty = blockEmpty;
        BlockFilled = blockFilled;
        ItemEmpty = itemEmpty;
        ItemFilled = itemFilled;
    }
}