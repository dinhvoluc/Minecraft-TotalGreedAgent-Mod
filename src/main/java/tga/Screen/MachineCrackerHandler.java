package tga.Screen;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tga.BlockEntity.MachineTiles.ManCrackerTile;
import tga.ExSlots.CheckInsertSlot;
import tga.ExSlots.TakeOnlySlot;
import tga.NetEvents.ManCrackerGuiSync;
import tga.TGAScreenHandlers;

import java.util.HashSet;
import java.util.Set;

public class MachineCrackerHandler extends ScreenHandler {
    public static final int SLOT_COUNT = 2;
    public final ManCrackerTile Machine;

    //Client
    public MachineCrackerHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, (ManCrackerTile) playerInventory.player.getWorld().getBlockEntity(pos));
    }

    //Server
    public MachineCrackerHandler(int syncId, PlayerInventory playerInventory, ManCrackerTile inventory) {
        super(TGAScreenHandlers.M_CRACKER_0, syncId);
        Machine = inventory;
        checkSize(inventory, SLOT_COUNT);
        inventory.onOpen(playerInventory.player);
        //箱のスロット
        addSlot(new CheckInsertSlot(inventory, 0, 60, 36));
        addSlot(new TakeOnlySlot(inventory, 1, 100, 36));
        //プレイヤー
        int startX = 8;
        int startY = 84;
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                addSlot(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
        // ホットバー
        for (int i = 0; i < 9; ++i) addSlot(new Slot(playerInventory, i, startX + i * 18, startY + 58));
    }

    public static void SendUpdate(ManCrackerTile tile, ServerPlayerEntity player) {
        World wKey = tile.getWorld();
        if (wKey == null) return;
        ManCrackerGuiSync payload = tile.GetSyncValue();
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return Machine.canPlayerUse(player);
    }

    public static final Set<ServerPlayerEntity> UsingPlayer = new HashSet<>();
    public static int UsingPlayerCount;

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = slots.get(invSlot);
        if (slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < SLOT_COUNT) {
                if (!insertItem(originalStack, SLOT_COUNT, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!insertItem(originalStack, 0, SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }

    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        Machine.onClose(player);
    }
}