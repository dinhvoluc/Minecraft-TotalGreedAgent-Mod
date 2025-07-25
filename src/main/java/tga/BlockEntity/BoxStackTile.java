package tga.BlockEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.StackWithSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import tga.TGATileEnities;

public class BoxStackTile extends BlockEntity implements SidedInventory {

    @Override
    protected void writeData(WriteView view) {
        view.putInt("C", ExHold);
        view.getListAppender("I", StackWithSlot.CODEC).add(new StackWithSlot(0, HoldItem));
    }

    @Override
    protected void readData(ReadView view) {
        ExHold = view.getInt("C", 0);
        for(StackWithSlot stackWithSlot : view.getTypedListView("Items", StackWithSlot.CODEC)) {
            HoldItem = stackWithSlot.stack();
            break;
        }
    }

    private int ExHold;
    private int MaxHoldStack;
    private ItemStack HoldItem = ItemStack.EMPTY;

    public void SetMaxHoldStack(int max) {
        MaxHoldStack = max;
    }
    public BoxStackTile(BlockPos pos, net.minecraft.block.BlockState state) {
        super(TGATileEnities.BOX_STACK_TILE, pos, state);
    }

    private int GetMaxHold() {
        return MaxHoldStack * HoldItem.getMaxCount();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0, 1};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (slot != 1) return false;
        if (HoldItem.isEmpty()) return true;
        //スタック出来るかを確認する
        return ItemStack.areItemsAndComponentsEqual(HoldItem, stack) && (ExHold + HoldItem.getCount()) < GetMaxHold();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 0 && !HoldItem.isEmpty();
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return HoldItem.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return HoldItem;
    }

    public int GetCountNow() {
        return HoldItem.isEmpty() ? 0 : (HoldItem.getCount() + ExHold);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (HoldItem.isEmpty()) return ItemStack.EMPTY;
        if (amount > GetCountNow()) {
            ItemStack rt = HoldItem.copy();
            HoldItem = ItemStack.EMPTY;
            ExHold = 0;
            markDirty();
            return rt;
        }
        ItemStack rt = HoldItem.copy();
        if (ExHold > 0) {
            int maxPerStack = HoldItem.getMaxCount();
            if (ExHold >= maxPerStack) {
                HoldItem.setCount(maxPerStack);
                ExHold -= maxPerStack;
            } else {
                HoldItem.setCount(ExHold);
                ExHold = 0;
            }
        } else HoldItem = ItemStack.EMPTY;
        markDirty();
        return rt;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (HoldItem.isEmpty()) return ItemStack.EMPTY;
        ItemStack rt = HoldItem.copy();
        if (ExHold > 0) {
            int maxPerStack = HoldItem.getMaxCount();
            if (ExHold >= maxPerStack) {
                HoldItem.setCount(maxPerStack);
                ExHold -= maxPerStack;
            } else {
                HoldItem.setCount(ExHold);
                ExHold = 0;
            }
        } else HoldItem = ItemStack.EMPTY;
        markDirty();
        return rt;
    }

    private int Merging(int total) {
        int maxPerStack = HoldItem.getMaxCount();
        int sendBack = total - GetMaxHold();
        if (sendBack > 0) total -= sendBack;
        if (total > maxPerStack) {
            ExHold = total - maxPerStack;
            HoldItem.setCount(maxPerStack);
        } else {
            ExHold = 0;
            HoldItem.setCount(total);
        }
        return Math.max(0, sendBack);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot == 1) {
            //Input slot
            if (stack.isEmpty() || !canInsert(slot, stack, null)) return;
            markDirty();
            if (HoldItem.isEmpty()) {
                HoldItem = stack.copy();
                stack.setCount(0);
                return;
            }
            stack.setCount(Merging(GetCountNow() + stack.getCount()));
            return;
        }
        markDirty();
        //Extract slot only
        if (stack.isEmpty()) {
            //swap amaru
            if (ExHold == 0) {
                HoldItem = ItemStack.EMPTY;
                return;
            }
            int maxPerStack = getMaxCountPerStack();
            if (ExHold >= maxPerStack) {
                HoldItem.setCount(maxPerStack);
                ExHold -= maxPerStack;
                return;
            }
            HoldItem.setCount(ExHold);
            ExHold = 0;
            return;
        }
        stack.setCount(Merging(ExHold + stack.getCount()));
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.world != null && !this.isRemoved() && player.squaredDistanceTo(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 100.0;
    }

    @Override
    public void clear() {
        ExHold = 0;
        HoldItem = ItemStack.EMPTY;
        markDirty();
    }
}