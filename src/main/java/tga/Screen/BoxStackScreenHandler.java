package tga.Screen;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import tga.BlockEntity.BoxStackTile;
import tga.ExSlots.CheckInsertSlot;
import tga.ExSlots.TakeOnlySlot;
import tga.NetEvents.BoxStackGuiSync;
import tga.TGAScreenHandlers;

import java.util.HashSet;
import java.util.Set;

public class BoxStackScreenHandler extends ScreenHandler {
    public static final int SLOT_COUNT = 2;
    public final BoxStackTile Tile;

    //Client
    public BoxStackScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, (BoxStackTile) playerInventory.player.getWorld().getBlockEntity(pos));
    }

    public BoxStackScreenHandler(int syncId, PlayerInventory playerInventory, BoxStackTile inventory) {
        super(TGAScreenHandlers.BOX_STACK, syncId);
        Tile = inventory;
        checkSize(inventory, SLOT_COUNT);
        inventory.onOpen(playerInventory.player);
        //箱のスロット
        addSlot(new TakeOnlySlot(Tile, 0, 20, 21));
        addSlot(new CheckInsertSlot(Tile, 1, 20, 46));
        //プレイヤー
        int startX = 8;
        int startY = 84;
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                addSlot(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
        // ホットバー
        for (int i = 0; i < 9; ++i) addSlot(new Slot(playerInventory, i, startX + i * 18, startY + 58));
    }

    public static void SendUpdate(BlockPos pos, int exCount, ItemStack holdItem) {
        BoxStackGuiSync payload = new BoxStackGuiSync(pos, exCount, holdItem);
        for (ServerPlayerEntity player : UsingPlayer)
            ServerPlayNetworking.send(player, payload);
    }
    public static void SendUpdate(BlockPos pos, int exCount, ItemStack holdItem, ServerPlayerEntity player) {
        BoxStackGuiSync payload = new BoxStackGuiSync(pos, exCount, holdItem);
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return Tile.canPlayerUse(player);
    }

    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < SLOT_COUNT) {
                if (!insertItem(itemStack2, SLOT_COUNT, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!insertItem(itemStack2, 1, SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }

        return itemStack;
    }
    public static final Set<ServerPlayerEntity> UsingPlayer = new HashSet<>();
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        Tile.onClose(player);
    }
}