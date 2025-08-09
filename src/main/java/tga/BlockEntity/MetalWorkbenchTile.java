package tga.BlockEntity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tga.Mechanic.IClickedIDHandler;
import tga.Mechanic.ITGAManpoweredBlock;
import tga.NetEvents.MetalWorkbenchGuiSync;
import tga.Screen.MetalWorkbenchHandler;
import tga.*;

public class MetalWorkbenchTile extends BlockEntity implements ITGAManpoweredBlock, IClickedIDHandler, SidedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    public SimpleInventory BufferSlot = new SimpleInventory(9);
    public static final int MAX_WATER_LEVEL = 16 * (int)FluidConstants.BUCKET;
    public int WaterVol;
    public int BurntimeLeft;
    public int BurntimeTotal = 10;
    public int Jinriki = 0;
    public int WorkTotal = 10;
    public int Worked = 0;
    public int WorkMode = WORK_MODE_PLATE;
    private static final int BUFFER_ID_SLOT_OUTPUT = 0;
    public ItemStack ResultSlot = ItemStack.EMPTY;
    public ItemStack Crafting = ItemStack.EMPTY;
    public int TickStep = 0;

    public static final int WORK_MODE_PLATE = 0;
    public static final int WORK_MODE_WIRE = 1;
    public static final int WORK_MODE_BAR = 2;
    public static final int WORK_MODE_PIPE = 3;
    public static final int WORK_MODE_NAIL = 4;
    public static final int WORK_MODE_SCEW = 5;
    public static final int WORK_MODE_CASING = 6;
    public static final int WORK_MODE_MESH = 7;
    public static final int WORK_MODE_SIZE = 8;
    public static final int JINRIKI_INPUT_OFF = 1_000_00;
    public static final int MAX_JINRIKI_CAP = 20_000_00;

    public MetalWorkbenchTile(BlockPos pos, BlockState state) {
        super(TGATileEnities.M_METAL_WORKBENCH, pos, state);
    }

    // <editor-fold desc="Items">
    public DefaultedList<ItemStack> GetDrops() {
        DefaultedList<ItemStack> drop = DefaultedList.of();
        drop.add(new ItemStack(TGABlocks.METAL_WORKBENCH));
        if (!ResultSlot.isEmpty()) drop.add(ResultSlot.copy());
        for (var i = 0; i < 9; i++) {
            ItemStack gotSLot = BufferSlot.heldStacks.get(i);
            if (!gotSLot.isEmpty()) drop.add(gotSLot.copy());
        }
        return drop;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return TGAShared.IntRange10;
    }

    @Override
    public void clear() {
        BufferSlot.clear();
        WaterVol = 0;
        BurntimeLeft = 0;
        Worked = 0;
        Crafting = ItemStack.EMPTY;
        ResultSlot = ItemStack.EMPTY;
        TickStep = 0;
        markDirty();
    }

    @Override
    public boolean isEmpty() {
        return ResultSlot.isEmpty() && BufferSlot.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot == BUFFER_ID_SLOT_OUTPUT ? ResultSlot : BufferSlot.getStack(slot - 1);
    }

    @Override
    public int getMaxCountPerStack() {
        return 64;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot != BUFFER_ID_SLOT_OUTPUT;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == BUFFER_ID_SLOT_OUTPUT || stack.isOf(Items.BUCKET) || stack.isOf(Items.GLASS_BOTTLE);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot == BUFFER_ID_SLOT_OUTPUT) ResultSlot = stack;
        else BufferSlot.setStack(slot - 1, stack);
        markDirty();
    }

    @Override
    public int size() {
        return 10;
    }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot == BUFFER_ID_SLOT_OUTPUT) {
            if (ResultSlot.isEmpty()) return ItemStack.EMPTY;
            ItemStack rt = ResultSlot.copy();
            ResultSlot = ItemStack.EMPTY;
            markDirty();
            return rt;
        } else {
            ItemStack rt = BufferSlot.removeStack(slot - 1);
            if (rt.isEmpty()) return ItemStack.EMPTY;
            markDirty();
            return rt;
        }
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot == BUFFER_ID_SLOT_OUTPUT) {
            ItemStack itemStack = ResultSlot.split(amount);
            if (!itemStack.isEmpty()) this.markDirty();
            return itemStack;
        } else {
            ItemStack rt = BufferSlot.removeStack(slot - 1, amount);
            if (rt.isEmpty()) return ItemStack.EMPTY;
            markDirty();
            return rt;
        }
    }

    // </editor-fold>

    // <editor-fold desc="Water">

    // </editor-fold>

    // <editor-fold desc="GUI">
    @Override
    public void ClickedID(int id) {
        if (id!=0) return;
        if (WorkMode >= 7) WorkMode = 0;
        else WorkMode++;
        markDirty();
    }

    public MetalWorkbenchGuiSync GetSyncValue() {
        if (world == null) throw new IllegalCallerException("SyncTarget-Null-of-MetalWorkbench");
        ItemStack[] sync = new ItemStack[10];
        sync[0] = ResultSlot;
        for (var i = 0; i < 9; i++) sync[i + 1] = BufferSlot.heldStacks.get(i);
        return new MetalWorkbenchGuiSync(world.getRegistryKey().getValue().toString(), pos, WorkMode, Worked, WorkTotal, BurntimeLeft, BurntimeTotal, WaterVol, sync);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null && !isRemoved() && player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 100.0;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        if (world == null || world.isClient) return null;
        MetalWorkbenchHandler.SendUpdate(this, (ServerPlayerEntity) player);
        return new MetalWorkbenchHandler(syncId, playerInventory, this);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable(TotalGreedyAgent.GetGuiLang("metalworkbench"));
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (world == null || world.isClient) return;
        MetalWorkbenchHandler.UsingPlayer.add((ServerPlayerEntity) player);
        MetalWorkbenchHandler.UsingPlayerCount = MetalWorkbenchHandler.UsingPlayer.size();
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (world == null || world.isClient) return;
        MetalWorkbenchHandler.UsingPlayer.remove((ServerPlayerEntity) player);
        MetalWorkbenchHandler.UsingPlayerCount = MetalWorkbenchHandler.UsingPlayer.size();
    }

    public void TickS(BlockState c) {
        //todo ticking logicx
    }

    public void TGAS2CSync(MetalWorkbenchGuiSync payload) {
        if (!pos.equals(payload.Pos)) return;
        if (world == null) return;
        if (!world.getRegistryKey().getValue().toString().equals(payload.World)) return;
        WorkMode = payload.WorkMode;
        Worked = payload.WorkDone;
        WorkTotal = payload.WorkTotal;
        BurntimeLeft = payload.BurnLeft;
        BurntimeTotal = payload.BurnTotal;
        WaterVol = payload.WaterLevel;
        ResultSlot = payload.ItemSlots[0];
        for (var i = 0; i < 9; i++)
            BufferSlot.heldStacks.set(i, payload.ItemSlots[i + 1]);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (MetalWorkbenchHandler.UsingPlayerCount == 0 || world == null || world.isClient) return;
        MetalWorkbenchGuiSync payload = GetSyncValue();
        for (ServerPlayerEntity player : MetalWorkbenchHandler.UsingPlayer)
            ServerPlayNetworking.send(player, payload);
    }
    // </editor-fold>

    // <editor-fold desc="Jinriki">
    @Override
    public float GetJinrikiMul() {
        return Jinriki >= JINRIKI_INPUT_OFF ? 0f : 16f;
    }

    @Override
    public void JinrikiGo(int power, ServerPlayerEntity player, World world) {
        Jinriki += power;
        if (Jinriki > MAX_JINRIKI_CAP) Jinriki = MAX_JINRIKI_CAP;
        world.playSound(null, pos, TGASounds.HAMMER, SoundCategory.BLOCKS, 1f, 1f);
    }
    // </editor-fold>
}