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
import tga.ExSlots.CheckInsertSlot;
import tga.ExSlots.TakeOnlySlot;
import tga.Machines.ManCrackerTile;
import tga.NetEvents.ManCrackerGuiSync;
import tga.TGAScreenHandlers;

import java.util.HashSet;
import java.util.Set;

public class MachineCrackerHandler extends ScreenHandler {
    public static final int SLOT_COUNT = 2;
    public final ManCrackerTile Machine;
    public static ManCrackerTile LastWorkBlock;

    //Client
    public MachineCrackerHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, (ManCrackerTile) playerInventory.player.getWorld().getBlockEntity(pos));
    }

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
    public ItemStack quickMove(PlayerEntity player, int slot){
        return  slot > 1 ? TryPlayerInput(slot) : TryExtract(slot);
    }
    private ItemStack TryExtract(int slot) {
        ItemStack baseItem = Machine.getStack(slot);
        if (baseItem.isEmpty()) return ItemStack.EMPTY;
        //players slot ni aitemu wo umeru
        int leftOver = baseItem.getCount();
        baseItem = baseItem.copy();
        int maxCount = baseItem.getMaxCount();
        //１回目は既に配置したアイテムを埋まる
        int oldValue = leftOver;
        for (var i = 2; i < slots.size(); i++) {
            Slot slot_player = getSlot(i);
            if (!slot_player.hasStack()) continue;
            ItemStack pStack = slot_player.getStack();
            if (!ItemStack.areItemsAndComponentsEqual(baseItem, pStack) || pStack.getCount() >= maxCount) continue;
            int amount = Math.min(leftOver, maxCount - pStack.getCount());
            ItemStack newStack = pStack.copy();
            newStack.increment(amount);
            slot_player.setStack(newStack);
            leftOver -= amount;
            if (leftOver <= 0) {
                Machine.setStack(slot, ItemStack.EMPTY);
                return baseItem;
            }
        }
        //２回目は空いてるスロットに配置
        for (var i = 2; i < slots.size(); i++) {
            Slot slot_player = getSlot(i);
            if (slot_player.hasStack()) continue;
            ItemStack newStack = baseItem.copy();
            int amount = Math.min(leftOver, maxCount);
            newStack.setCount(amount);
            leftOver -= amount;
            slot_player.setStack(newStack);
            if (leftOver <= 0) {
                Machine.setStack(slot, ItemStack.EMPTY);
                return baseItem;
            }
        }
        if (leftOver != oldValue) {
            ItemStack setNew = baseItem.copy();
            baseItem.setCount(leftOver);
            Machine.setStack(slot, setNew);
            return baseItem;
        }
        return ItemStack.EMPTY;
    }
    private ItemStack TryPlayerInput(int slot) {
        Slot original = getSlot(slot);
        if (!original.hasStack()) return ItemStack.EMPTY;
        ItemStack baseItem = original.getStack();
        if (Machine.CanPush(baseItem) <= 0) return ItemStack.EMPTY;
        original.setStack(Machine.PushItem(baseItem));
        return  baseItem;
    }

    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        Machine.onClose(player);
    }
}