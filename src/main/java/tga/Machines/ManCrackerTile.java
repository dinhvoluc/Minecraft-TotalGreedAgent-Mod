package tga.Machines;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import tga.NetEvents.ManCrackerGuiSync;
import tga.Screen.MachineCrackerHandler;
import tga.TGATileEnities;
import tga.TotalGreedyAgent;

import java.util.Arrays;

public class ManCrackerTile extends BlockEntity implements SidedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    private final ItemStack[] ItemBuffer = new ItemStack[BUFFER_SIZE];
    private static final int BUFFER_ID_SLOT_INPUT = 0;
    private static final int BUFFER_ID_SLOT_OUTPUT = 1;
    private static final int BUFFER_SIZE = 2;

    private ItemStack SubOutput = ItemStack.EMPTY;
    private int Worked;
    private int WorkTotal;
    private int Jinriki;

    public ManCrackerTile(BlockPos pos, BlockState state) {
        super(TGATileEnities.M_CRACKER_LV0, pos, state);
        Arrays.fill(ItemBuffer, ItemStack.EMPTY);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return new int[]{0, 1};
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (slot != BUFFER_ID_SLOT_INPUT) return false;
        ItemStack slotIN = ItemBuffer[BUFFER_ID_SLOT_INPUT];
        if (slotIN.isEmpty()) return false;


        //tempory
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
       return  slot == BUFFER_ID_SLOT_OUTPUT;
    }

    @Override
    public int size() {
        return BUFFER_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return ItemBuffer[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack rtStack = ItemBuffer[slot].copy();
        if (slot == BUFFER_ID_SLOT_OUTPUT) {
            ItemBuffer[BUFFER_ID_SLOT_OUTPUT] = SubOutput;
            SubOutput = ItemStack.EMPTY;
        }
        else ItemBuffer[slot] = ItemStack.EMPTY;
        return rtStack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null && !isRemoved() && player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 100.0;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(TotalGreedyAgent.GetGuiLang("mancracker"));
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        if (world == null || world.isClient) return null;
        MachineCrackerHandler.SendUpdate(this, (ServerPlayerEntity) player);
        return new MachineCrackerHandler(syncId, playerInventory, this);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (MachineCrackerHandler.UsingPlayerCount == 0 || world == null || world.isClient) return;
        ManCrackerGuiSync payload = new ManCrackerGuiSync(world.getRegistryKey().getValue().toString(), pos, Worked, WorkTotal, ItemBuffer[0], ItemBuffer[1]);
        for (ServerPlayerEntity player : MachineCrackerHandler.UsingPlayer)
            ServerPlayNetworking.send(player, payload);
    }

    public ManCrackerGuiSync GetSyncValue() {
        if (world == null) throw new IllegalCallerException("SyncTarget-Null-of-ManCracker");
        return new ManCrackerGuiSync(world.getRegistryKey().getValue().toString(), pos, Worked, WorkTotal, ItemBuffer[0], ItemBuffer[1]);
    }

    public boolean IsNormal() {
        return ItemBuffer[1].isEmpty();
    }

    public void TGAS2CSync(ManCrackerGuiSync payload) {
        if (!pos.equals(payload.Pos)) return;
        if (world == null) return;
        if (!world.getRegistryKey().getValue().toString().equals(payload.World)) return;
        Worked = payload.WorkDone;
        WorkTotal = payload.WorkTotal;
        ItemBuffer[0] = payload.SlotIn;
        ItemBuffer[1] = payload.SlotOut;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (world == null || world.isClient) return;
        MachineCrackerHandler.UsingPlayer.add((ServerPlayerEntity) player);
        MachineCrackerHandler.UsingPlayerCount = MachineCrackerHandler.UsingPlayer.size();
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (world == null || world.isClient) return;
        MachineCrackerHandler.UsingPlayer.remove((ServerPlayerEntity) player);
        MachineCrackerHandler.UsingPlayerCount = MachineCrackerHandler.UsingPlayer.size();
    }

    @Override
    public void clear() {
        SubOutput = ItemStack.EMPTY;
        ItemBuffer[0] = ItemStack.EMPTY;
        ItemBuffer[1] = ItemStack.EMPTY;
        markDirty();
    }
}