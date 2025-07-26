package tga.Screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import tga.BlockEntity.BoxStackTile;
import tga.TGAScreenHandlers;

public class BoxStackScreenHandler extends ScreenHandler {
    public final BoxStackTile Tile;
    //Server
    public BoxStackScreenHandler(int syncId, PlayerInventory playerInventory, BoxStackTile tile) {
        super(TGAScreenHandlers.BOX_STACK, syncId);
        Tile = tile;
        int startX = 8;
        int startY = 84;
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                addSlot(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
        // ホットバー
        for (int i = 0; i < 9; ++i) addSlot(new Slot(playerInventory, i, startX + i * 18, startY + 58));
        //箱のスロット
        addSlot(new Slot(Tile, 0, 20,21));
        addSlot(new Slot(Tile, 1, 20, 46));
    }
    //Client
    public BoxStackScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, (BoxStackTile) playerInventory.player.getWorld().getBlockEntity(pos));

    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return Tile.canPlayerUse(player);
    }
}