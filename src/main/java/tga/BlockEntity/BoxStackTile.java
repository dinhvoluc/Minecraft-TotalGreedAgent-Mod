package tga.BlockEntity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import tga.Block.BoxStackBlock;
import tga.ComDat.BoxStackData;
import tga.Screen.BoxStackScreenHandler;
import tga.TGATileEnities;
import tga.TotalGreedyAgent;

public class BoxStackTile extends BlockEntity implements SidedInventory, ExtendedScreenHandlerFactory<BlockPos> {

    @Override
    protected void writeData(WriteView view) {
        view.putInt("M", MaxHoldStack);
        view.putInt("E", ExCount);
        if (HoldItem == null || HoldItem.isEmpty()) return;
        view.put("I", ItemStack.CODEC, HoldItem);
    }

    @Override
    protected void readData(ReadView view) {
        MaxHoldStack = view.getInt("M", 1);
        ExCount = view.getInt("E", 0);
        HoldItem = view.read("T", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    private int ExCount;
    private int MaxHoldStack;
    private ItemStack HoldItem = ItemStack.EMPTY;

    public void SetDataComponent(BoxStackData data) {
        MaxHoldStack = data.MaxStack;
        HoldItem = data.HoldItem.copy();
        ExCount = data.ExCount;
    }

    public BoxStackData GetDataComponent() {
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

        TotalGreedyAgent.broadcastDebugMessage("removeStack=>" + slot + "/" + amount);

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

        TotalGreedyAgent.broadcastDebugMessage("removeStack=>" + slot);

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
            markDirty();
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
        markDirty();
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

        TotalGreedyAgent.broadcastDebugMessage("setStack=>" + stack);

        if (slot == 1) {
            //Input slot
            if (stack.isEmpty() || !canInsert(slot, stack, null)) return;
            if (HoldItem.isEmpty()) {
                HoldItem = stack.copy();
                stack.setCount(0);
                return;
            }
            Merging(GetCountNow() + stack.getCount());
            return;
        }
        //Extract slot only
        if (stack.isEmpty()) {
            //swap amaru
            if (ExCount == 0) {
                HoldItem = ItemStack.EMPTY;
                markDirty();
                return;
            }
            int maxPerStack = getMaxCountPerStack();
            if (ExCount >= maxPerStack) {
                HoldItem.setCount(maxPerStack);
                ExCount -= maxPerStack;
                markDirty();
                return;
            }
            HoldItem.setCount(ExCount);
            ExCount = 0;
            markDirty();
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

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    @Override
    public Text getDisplayName() {
        return switch (MaxHoldStack) {
            case BoxStackBlock.SIZE_WOOD -> Text.translatable(TotalGreedyAgent.GetGuiLang("box_wood"));
            case BoxStackBlock.SIZE_COPPER -> Text.translatable(TotalGreedyAgent.GetGuiLang("box_copper"));
            default -> Text.translatable(TotalGreedyAgent.GetGuiLang("box_any"));
        };
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BoxStackScreenHandler(syncId, playerInventory, this);
    }
}