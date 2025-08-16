package tga.Items;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class TheBoxRegisterReturn<T extends Item> {
    public final Block BlockEmpty;
    public final Block BlockFilled;
    public final T ItemEmpty;
    public final T ItemFilled;

    public TheBoxRegisterReturn(Block blockEmpty, Block blockFilled, T itemEmpty, T itemFilled) {
        BlockEmpty = blockEmpty;
        BlockFilled = blockFilled;
        ItemEmpty = itemEmpty;
        ItemFilled = itemFilled;
    }
}