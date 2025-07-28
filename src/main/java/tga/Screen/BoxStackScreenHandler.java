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

    public static void SendUpdate(BoxStackTile tile, int exCount, ItemStack holdItem, ServerPlayerEntity player) {
        World wKey = tile.getWorld();
        if (wKey == null) return;
        BoxStackGuiSync payload = new BoxStackGuiSync(wKey.getRegistryKey().getValue().toString(), tile.getPos(), exCount, holdItem);
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return Tile.canPlayerUse(player);
    }

    public ItemStack quickMove(PlayerEntity player, int slot) {
        return switch (slot) {
            case 0 -> TryExtract();
            case 1 -> ItemStack.EMPTY;
            default -> TryImport(slot);
        };
    }

    private ItemStack TryExtract() {
        if (Tile.isEmpty()) return ItemStack.EMPTY;
        //players slot ni aitemu wo umeru
        int totalBoxHold = Tile.GetCountNow();
        ItemStack baseItem = Tile.getStack(0).copy();
        int maxCount = baseItem.getMaxCount();
        //１回目は既に配置したアイテムを埋まる
        int oldMax = totalBoxHold;
        for (var i = 2; i < slots.size(); i++) {
            Slot slot_player = getSlot(i);
            if (!slot_player.hasStack()) continue;
            ItemStack pStack = slot_player.getStack();
            if (!ItemStack.areItemsAndComponentsEqual(baseItem, pStack) || pStack.getCount() >= maxCount) continue;
            int amount = Math.min(totalBoxHold, maxCount - pStack.getCount());
            ItemStack newStack = pStack.copy();
            newStack.increment(amount);
            slot_player.setStack(newStack);
            totalBoxHold -= amount;
            if (totalBoxHold <= 0) {
                Tile.clear();
                return baseItem;
            }
        }
        //２回目は空いてるスロットに配置
        for (var i = 2; i < slots.size(); i++) {
            Slot slot_player = getSlot(i);
            if (slot_player.hasStack()) continue;
            ItemStack newStack = baseItem.copy();
            int amount = Math.min(totalBoxHold, maxCount);
            newStack.setCount(amount);
            totalBoxHold -= amount;
            slot_player.setStack(newStack);
            if (totalBoxHold <= 0) break;
        }
        Tile.SetTotalCount(totalBoxHold);
        return oldMax > totalBoxHold ? baseItem : ItemStack.EMPTY;
    }

    public ItemStack TryImport(int slot) {
        Slot original = getSlot(slot);
        if (!original.hasStack()) return ItemStack.EMPTY;
        int totalCount = Tile.GetCountNow();
        int maxInput = Tile.GetMaxHold() - totalCount;
        if (maxInput <= 0) return ItemStack.EMPTY;
        ItemStack baseItem = original.getStack().copy();
        //初期化
        if (Tile.isEmpty()) maxInput = baseItem.getMaxCount() * Tile.GetMaxHoldStack();
        //違うアイテムなのでNGを出します。
        else if (!ItemStack.areItemsAndComponentsEqual(baseItem, Tile.getStack(0))) return ItemStack.EMPTY;
        //まずはオリジナルか回収
        int curCount = baseItem.getCount();
        if (maxInput < curCount) {
            Tile.SetTotalCount(baseItem, totalCount + maxInput);
            ItemStack newStack = baseItem.copy();
            newStack.setCount(curCount - maxInput);
            original.setStack(newStack);
            return baseItem;
        } else {
            maxInput -= curCount;
            totalCount += curCount;
            original.setStack(ItemStack.EMPTY);
        }
        if (maxInput <= 0) {
            Tile.SetTotalCount(baseItem, totalCount);
            return baseItem;
        }
        //２回目は空いてるスロットに配置
        for (var i = 2; i < slots.size(); i++) {
            Slot plater_slot = getSlot(i);
            if (!plater_slot.hasStack()) continue;
            ItemStack slotNai = plater_slot.getStack();
            if (!ItemStack.areItemsAndComponentsEqual(baseItem, slotNai)) continue;
            curCount = slotNai.getCount();
            if (maxInput < curCount) {
                Tile.SetTotalCount(baseItem, totalCount + maxInput);
                ItemStack newStack = slotNai.copy();
                newStack.setCount(curCount - maxInput);
                plater_slot.setStack(newStack);
                return baseItem;
            } else {
                maxInput -= curCount;
                totalCount += curCount;
                plater_slot.setStack(ItemStack.EMPTY);
            }
            if (maxInput <= 0) break;
        }
        Tile.SetTotalCount(baseItem, totalCount);
        return baseItem;
    }

    public static final Set<ServerPlayerEntity> UsingPlayer = new HashSet<>();
    public static int UsingPlayerCount;

    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        Tile.onClose(player);
    }
}