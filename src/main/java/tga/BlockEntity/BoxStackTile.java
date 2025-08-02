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
import org.jetbrains.annotations.Nullable;
import tga.Block.BoxStackBlock;
import tga.ComDat.BoxStackData;
import tga.NetEvents.BoxStackGuiSync;
import tga.Screen.BoxStackScreenHandler;
import tga.TGAHelper;
import tga.TGATileEnities;
import tga.TotalGreedyAgent;

public class BoxStackTile extends BlockEntity implements SidedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    public int InfoMaxStack;
    public ItemStack LockedType = ItemStack.EMPTY;
    public int NoInStackCount;
    private final ItemStack[] VirtualSlot = new ItemStack[3];
    private static final int HIDEN_FUNCTION_SLOT = 1;

    public int GetTotalCount() {
        return LockedType.isEmpty() ? 0 : NoInStackCount + VirtualSlot[0].getCount() + VirtualSlot[2].getCount();
    }

    public BoxStackTile(BlockPos pos, net.minecraft.block.BlockState state) {
        super(TGATileEnities.BOX_STACK_TILE, pos, state);
        VirtualSlot[0] = ItemStack.EMPTY;
        VirtualSlot[1] = ItemStack.EMPTY;
        VirtualSlot[2] = ItemStack.EMPTY;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (BoxStackScreenHandler.UsingPlayerCount == 0 || world == null || world.isClient) return;
        BoxStackGuiSync payload = new BoxStackGuiSync(world.getRegistryKey().getValue().toString(), pos, LockedType, GetTotalCount());
        for (ServerPlayerEntity player : BoxStackScreenHandler.UsingPlayer)
            ServerPlayNetworking.send(player, payload);
    }

    public BoxStackData GetDataComponent() {
        return new BoxStackData(InfoMaxStack, LockedType, GetTotalCount());
    }

    public int GetMaxSpace() {
        return (LockedType.isEmpty() ? 64 : LockedType.getMaxCount()) * InfoMaxStack;
    }

    @Override
    protected void writeData(WriteView view) {
        view.putInt("M", InfoMaxStack);
        view.putInt("C", GetTotalCount());
        TGAHelper.WriteItem(view, "I", LockedType);
    }

    @Override
    protected void readData(ReadView view) {
        InfoMaxStack = view.getInt("M", 1);
        int total = view.getInt("C", 0);
        LockedType = TGAHelper.ReadItem(view, "I");
        UpdateTempSlot(total);
    }

    public void OnPlacedRebuild(BoxStackData data) {
        InfoMaxStack = data.MaxStack;
        LockedType = data.LockedType;
        UpdateTempSlot(data.Count);
    }

    @Override
    public int getMaxCountPerStack() {
        return LockedType.isEmpty() ? 64 : LockedType.getMaxCount();
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0, 1};
    }

    public static int Ticker = 0;

    @Override
    public void setStack(int slot, ItemStack stack) {
        //no action
        if (ItemStack.areEqual(VirtualSlot[slot], stack)) return;
        //check empty
        if (LockedType.isEmpty()) {
            //no action
            if (stack.isEmpty()) return;
            //copy
            LockedType = stack.copy();
            //check max count
            stack.capCount(stack.getMaxCount());
            VirtualSlot[0] = stack;
            markDirty();
            return;
        }
        if (slot == HIDEN_FUNCTION_SLOT) {
            //for hopper or insert
            UpdateTempSlot(GetTotalCount() + stack.getCount());
            markDirty();
            return;
        }
        int maxSize = LockedType.getMaxCount();
        VirtualSlot[slot] = stack;
        //check max count
        stack.capCount(stack.getMaxCount());
        UpdateTempSlot(GetTotalCount());
        markDirty();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return switch (slot) {
            case 1 -> LockedType.isEmpty() ||
                    (stack.getCount() + GetTotalCount() <= GetMaxSpace() && ItemStack.areItemsAndComponentsEqual(LockedType, stack));
            case 2 -> LockedType.isEmpty() || ItemStack.areItemsAndComponentsEqual(LockedType, stack);
            default -> false;
        };
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    @Override
    public int size() {
        return 3;
    }

    @Override
    public ItemStack getStack(int slot) {
        return VirtualSlot[slot];
    }

    public void SetBoxInfo(ItemStack item, int count) {
        if (GetTotalCount() == count && ItemStack.areItemsAndComponentsEqual(LockedType, item)) return;
        if (count <= 0) {
            clear();
            return;
        }
        LockedType = item.copy();
        UpdateTempSlot(count);
    }

    public void UpdateTempSlot(int total) {
        if (total <= 0 || LockedType.isEmpty()) {
            NoInStackCount = 0;
            LockedType = ItemStack.EMPTY;
            VirtualSlot[0] = ItemStack.EMPTY;
            VirtualSlot[2] = ItemStack.EMPTY;
        } else {
            int sizePerStack = LockedType.getMaxCount();
            int exMaxSize = sizePerStack * (InfoMaxStack - 2);
            if (!ItemStack.areItemsAndComponentsEqual(VirtualSlot[0], LockedType)) VirtualSlot[0] = LockedType.copy();
            int maxSpace = InfoMaxStack * sizePerStack;
            if (total > maxSpace) total = maxSpace;
            int forSlot0 = Math.min(sizePerStack, total);
            VirtualSlot[0].setCount(forSlot0);
            total -= forSlot0;
            if (total > exMaxSize) {
                if (!ItemStack.areItemsAndComponentsEqual(VirtualSlot[2], LockedType)) VirtualSlot[2] = LockedType.copy();
                NoInStackCount = exMaxSize;
                VirtualSlot[2].setCount(total - exMaxSize);
            } else {
                NoInStackCount = total;
                if (!VirtualSlot[2].isEmpty()) VirtualSlot[2] = ItemStack.EMPTY;
            }
        }
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (amount <= 0 || slot == 1) return ItemStack.EMPTY;
        ItemStack slotUpdateing = VirtualSlot[slot];
        if (slotUpdateing.isEmpty()) return ItemStack.EMPTY;
        ItemStack rt = slotUpdateing.copy();
        int slotHave = slotUpdateing.getCount();
        if (slotHave <= amount) {
            VirtualSlot[slot] = ItemStack.EMPTY;
            //Only update when slot is 0
            if (slot == 0) UpdateTempSlot(GetTotalCount());
            rt.setCount(slotHave);
        } else {
            slotUpdateing.decrement(amount);
            //Only update when slot is 0
            if (slot == 0) UpdateTempSlot(GetTotalCount());
            rt.setCount(amount);
        }
        markDirty();
        return rt;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return removeStack(slot, 1);
    }

    @Override
    public boolean isEmpty() {
        return LockedType.isEmpty();
    }

    @Override
    public void clear() {
        NoInStackCount = 0;
        LockedType = ItemStack.EMPTY;
        VirtualSlot[0] = ItemStack.EMPTY;
        VirtualSlot[2] = ItemStack.EMPTY;
        markDirty();
    }

    //
    //SCREENN
    //

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null && !isRemoved() && player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 100.0;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    @Override
    public Text getDisplayName() {
        return switch (InfoMaxStack) {
            case BoxStackBlock.SIZE_WOOD -> Text.translatable(TotalGreedyAgent.GetGuiLang("box_wood"));
            case BoxStackBlock.SIZE_COPPER -> Text.translatable(TotalGreedyAgent.GetGuiLang("box_copper"));
            case BoxStackBlock.SIZE_BRONZE -> Text.translatable(TotalGreedyAgent.GetGuiLang("box_bronze"));
            case BoxStackBlock.SIZE_IRON -> Text.translatable(TotalGreedyAgent.GetGuiLang("box_iron"));
            default -> Text.translatable(TotalGreedyAgent.GetGuiLang("box_any"));
        };
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        if (world == null || world.isClient) return null;
        BoxStackScreenHandler.SendUpdate(this, LockedType, GetTotalCount(), (ServerPlayerEntity) player);
        return new BoxStackScreenHandler(syncId, playerInventory, this);
    }

    public void TGAS2CSync(BoxStackGuiSync payload) {
        if (!pos.equals(payload.Pos)) return;
        if (world == null) return;
        if (!world.getRegistryKey().getValue().toString().equals(payload.World)) return;
        boolean typeSame = ItemStack.areItemsAndComponentsEqual(LockedType, payload.LockedType);
        if (typeSame && GetTotalCount() == payload.Count) return;
        LockedType = payload.LockedType;
        VirtualSlot[0] = ItemStack.EMPTY;
        VirtualSlot[2] = ItemStack.EMPTY;
        UpdateTempSlot(payload.Count);
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