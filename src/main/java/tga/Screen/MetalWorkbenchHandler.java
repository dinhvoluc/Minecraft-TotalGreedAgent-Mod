package tga.Screen;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import tga.BlockEntity.MetalWorkbenchTile;
import tga.ExSlots.CheckInsertSlot;
import tga.ExSlots.TakeOnlySlot;
import tga.NetEvents.MetalWorkbenchGuiSync;
import tga.TGAScreenHandlers;

import java.util.HashSet;
import java.util.Set;

public class MetalWorkbenchHandler extends ScreenHandler {
    public static final int SLOT_COUNT = 10;
    public final MetalWorkbenchTile Machine;
    public static MetalWorkbenchTile LastWorkBlock;

    //Client
    public MetalWorkbenchHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, (MetalWorkbenchTile) playerInventory.player.getWorld().getBlockEntity(pos));
    }

    //Server
    public MetalWorkbenchHandler(int syncId, PlayerInventory playerInventory, MetalWorkbenchTile inventory) {
        super(TGAScreenHandlers.METAL_WORKBENCH, syncId);
        Machine = inventory;
        checkSize(inventory, SLOT_COUNT);
        inventory.onOpen(playerInventory.player);
        //箱のスロット
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 3; ++col)
                addSlot(new CheckInsertSlot(inventory, col + row * 3 + 1, 38 + col * 18, 18 + row * 18));
        addSlot(new TakeOnlySlot(inventory, 0, 138, 36));
        //プレイヤー
        int startX = 8;
        int startY = 84;
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                addSlot(new Slot(playerInventory, col + row * 9 + 9, startX + col * 18, startY + row * 18));
        // ホットバー
        for (int i = 0; i < 9; ++i) addSlot(new Slot(playerInventory, i, startX + i * 18, startY + 58));
    }

    public static void SendUpdate(MetalWorkbenchTile tile, ServerPlayerEntity player) {
        World wKey = tile.getWorld();
        if (wKey == null) return;
        MetalWorkbenchGuiSync payload = tile.GetSyncValue();
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

    public static Text GetModeText(int mode) {
        return Text.translatable("gui.tga.metalwork.mode." + mode);
    }

    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        Machine.onClose(player);
    }
}