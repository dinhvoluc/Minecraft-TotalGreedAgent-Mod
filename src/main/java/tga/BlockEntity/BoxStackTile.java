package tga.BlockEntity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tga.Block.BoxStackBlock;
import tga.ComDat.BoxStackData;
import tga.NetEvents.BoxStackGuiSync;
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
        HoldItem = view.read("I", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    private int ExCount;
    private int MaxHoldStack;
    private ItemStack HoldItem = ItemStack.EMPTY;

    public void SetDataComponent(BoxStackData data) {
        MaxHoldStack = data.MaxStack;
        HoldItem = data.HoldItem.copy();
        ExCount = data.ExCount;
        markDirty();
    }

    @Override
    public int getMaxCount(ItemStack stack) {
        return Math.min(stack.getCount(), getMaxCountPerStack());
    }

    @Override
    public int getMaxCountPerStack() {
        return HoldItem.isEmpty() ? 64 : Math.min(HoldItem.getMaxCount(), GetMaxHold() - GetCountNow());
    }

    public BoxStackData GetDataComponent() {
        return new BoxStackData(MaxHoldStack, HoldItem, ExCount);
    }

    public int GetMaxHoldStack() {
        return MaxHoldStack;
    }

    public void SetMaxHoldStack(int max) {
        MaxHoldStack = max;
    }

    public BoxStackTile(BlockPos pos, net.minecraft.block.BlockState state) {
        super(TGATileEnities.BOX_STACK_TILE, pos, state);
    }

    public int GetMaxHold() {
        return MaxHoldStack * (HoldItem.isEmpty() ? 64 : HoldItem.getMaxCount());
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
        return ItemStack.areItemsAndComponentsEqual(HoldItem, stack) && (ExCount + HoldItem.getCount() + stack.getCount()) <= GetMaxHold();
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
        SetTotalCount(cNow - amount);
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

    public void SetTotalCount(ItemStack item, int total) {
        if (total <= 0 || item.isEmpty()) {
            HoldItem = ItemStack.EMPTY;
            ExCount = 0;
            markDirty();
            return;
        }
        HoldItem = item.copy();
        int maxPerStack = HoldItem.getMaxCount();
        int maxHold = maxPerStack * MaxHoldStack;
        if (total > maxHold) total = maxHold;
        if (total > maxPerStack) {
            ExCount = total - maxPerStack;
            HoldItem.setCount(maxPerStack);
        } else {
            ExCount = 0;
            HoldItem.setCount(total);
        }
        markDirty();
    }

    public void SetTotalCount(int total) {
        if (total <= 0) {
            HoldItem = ItemStack.EMPTY;
            ExCount = 0;
            markDirty();
            return;
        }
        int maxPerStack = HoldItem.getMaxCount();
        int maxHold = maxPerStack * MaxHoldStack;
        if (total > maxHold) total = maxHold;
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
        if (slot == 1) {
            //Input slot
            if (stack.isEmpty() || !canInsert(slot, stack, null)) return;
            if (HoldItem.isEmpty()) {
                HoldItem = stack.copy();
                stack.setCount(0);
                markDirty();
                return;
            }
            SetTotalCount(GetCountNow() + stack.getCount());
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
        SetTotalCount(ExCount + stack.getCount());
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null && !isRemoved() && player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 100.0;
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
    public void markDirty() {
        super.markDirty();
        if (BoxStackScreenHandler.UsingPlayerCount == 0 || world == null || world.isClient) return;
        BoxStackGuiSync payload = new BoxStackGuiSync(world.getRegistryKey().getValue().toString(), pos, ExCount, HoldItem);
        for (ServerPlayerEntity player : BoxStackScreenHandler.UsingPlayer)
            ServerPlayNetworking.send(player, payload);
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        if (world == null || world.isClient) return null;
        BoxStackScreenHandler.SendUpdate(this, ExCount, HoldItem, (ServerPlayerEntity) player);
        return new BoxStackScreenHandler(syncId, playerInventory, this);
    }

    public void TGAS2CSync(BoxStackGuiSync payload) {
        if (!pos.equals(payload.Pos)) return;
        if (world == null) return;
        if (!world.getRegistryKey().getValue().toString().equals(payload.World)) return;
        ExCount = payload.ExCount;
        HoldItem = payload.HoldItem;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (world == null || world.isClient) return;
        BoxStackScreenHandler.UsingPlayer.add((ServerPlayerEntity) player);
        BoxStackScreenHandler.UsingPlayerCount = BoxStackScreenHandler.UsingPlayer.size();
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (world == null || world.isClient) return;
        BoxStackScreenHandler.UsingPlayer.remove((ServerPlayerEntity) player);
        BoxStackScreenHandler.UsingPlayerCount = BoxStackScreenHandler.UsingPlayer.size();
    }
}