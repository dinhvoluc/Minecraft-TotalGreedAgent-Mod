package tga.BlockEntity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import tga.ComDat.BoxStackData;
import tga.TGATileEnities;

import java.util.Optional;

public class BoxStackTile extends BlockEntity implements SidedInventory {

    @Override
    protected void writeData(WriteView view) {
        view.put("Info", BoxStackData.CODEC, GetDataComponent());
    }

    @Override
    protected void readData(ReadView view) {
        Optional<BoxStackData> opt = view.read("Info", BoxStackData.CODEC);
        if (opt.isEmpty()) return;
        BoxStackData info = opt.get();
        MaxHoldStack = info.MaxStack;
        HoldItem = info.HoldItem;
        ExCount = info.ExCount;
    }

    private int ExCount;
    private int MaxHoldStack;
    private ItemStack HoldItem = ItemStack.EMPTY;

    public void SetDataComponent(BoxStackData data){
        MaxHoldStack = data.MaxStack;
        HoldItem = data.HoldItem.copy();
        ExCount = data.ExCount;
    }
    public BoxStackData GetDataComponent()
    {
        return new BoxStackData(MaxHoldStack, HoldItem, ExCount);
    }

    public void SetMaxHoldStack(int max) {
        MaxHoldStack = max;
    }
    public BoxStackTile(BlockPos pos, net.minecraft.block.BlockState state) {
        super(TGATileEnities.BOX_STACK_TILE, pos, state);
    }

    public int GetMaxHold() {
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
        return ItemStack.areItemsAndComponentsEqual(HoldItem, stack) && (ExCount + HoldItem.getCount()) < GetMaxHold();
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
        return slot == 0 ? HoldItem : ItemStack.EMPTY;
    }

    public int GetCountNow() {
        return HoldItem.isEmpty() ? 0 : (HoldItem.getCount() + ExCount);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (amount <= 0 || HoldItem.isEmpty()) return ItemStack.EMPTY;
        int cNow = GetCountNow();
        if (amount > cNow) {
            ItemStack rt = HoldItem.copy();
            HoldItem = ItemStack.EMPTY;
            ExCount = 0;
            markDirty();
            return rt;
        }
        ItemStack rt = HoldItem.copy();
        int fastCount = HoldItem.getCount();
        amount = Math.min(amount, fastCount);
        rt.setCount(amount);
        Merging(cNow - amount);
        markDirty();
        return rt;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (HoldItem.isEmpty()) return ItemStack.EMPTY;
        ItemStack rt = HoldItem.copy();
        if (ExCount > 0) {
            int maxPerStack = HoldItem.getMaxCount();
            if (ExCount >= maxPerStack) {
                HoldItem.setCount(maxPerStack);
                ExCount -= maxPerStack;
            } else {
                HoldItem.setCount(ExCount);
                ExCount = 0;
            }
        } else HoldItem = ItemStack.EMPTY;
        markDirty();
        return rt;
    }

    private void Merging(int total) {
        if (total <= 0) {
            HoldItem = ItemStack.EMPTY;
            ExCount = 0;
            return;
        }
        int maxPerStack = HoldItem.getMaxCount();
        int sendBack = total - GetMaxHold();
        if (sendBack > 0) total -= sendBack;
        if (total > maxPerStack) {
            ExCount = total - maxPerStack;
            HoldItem.setCount(maxPerStack);
        } else {
            ExCount = 0;
            HoldItem.setCount(total);
        }
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
            Merging(GetCountNow() + stack.getCount());
            return;
        }
        markDirty();
        //Extract slot only
        if (stack.isEmpty()) {
            //swap amaru
            if (ExCount == 0) {
                HoldItem = ItemStack.EMPTY;
                return;
            }
            int maxPerStack = getMaxCountPerStack();
            if (ExCount >= maxPerStack) {
                HoldItem.setCount(maxPerStack);
                ExCount -= maxPerStack;
                return;
            }
            HoldItem.setCount(ExCount);
            ExCount = 0;
            return;
        }
        Merging(ExCount + stack.getCount());
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.world != null && !this.isRemoved() && player.squaredDistanceTo(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 100.0;
    }

    @Override
    public void clear() {
        ExCount = 0;
        HoldItem = ItemStack.EMPTY;
        markDirty();
    }
}