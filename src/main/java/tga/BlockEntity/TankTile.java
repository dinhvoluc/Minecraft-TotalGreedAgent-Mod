package tga.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import tga.ExSlots.InternalFluidSlot;
import tga.Mechanic.ITGAFluidSlotTileCallback;
import tga.TGATileEnities;

public class TankTile extends BlockEntity implements SidedInventory, ITGAFluidSlotTileCallback {
    public InternalFluidSlot Tank;
    public final ItemStack[] ItemSlot = new ItemStack[2];

    public TankTile(BlockPos pos, BlockState state) {
        super(TGATileEnities.TANK_TILE, pos, state);
        ItemSlot[0] = ItemStack.EMPTY;
        ItemSlot[1] = ItemStack.EMPTY;
    }

    @Override
    public void TankCallBack(InternalFluidSlot slot) {
        markDirty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        //sync code


    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return ItemSlot[0].isEmpty() && ItemSlot[1].isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return ItemSlot[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = ItemSlot[slot];
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack rt = stack.copy();
        if (amount >= stack.getCount()) ItemSlot[slot] = ItemStack.EMPTY;
        else {
            stack.decrement(amount);
            rt.setCount(amount);
        }
        markDirty();
        return rt;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack rtStack = ItemSlot[slot].copy();
        ItemSlot[slot] = ItemStack.EMPTY;
        markDirty();
        return rtStack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null && !isRemoved() && player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 100.0;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        SidedInventory.super.onOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        SidedInventory.super.onClose(player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return SidedInventory.super.isValid(slot, stack);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[0];
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return false;
    }

    @Override
    public void clear() {

    }
}