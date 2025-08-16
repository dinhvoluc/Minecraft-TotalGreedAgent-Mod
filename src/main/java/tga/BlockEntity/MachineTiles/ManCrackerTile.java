package tga.BlockEntity.MachineTiles;

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
import net.minecraft.sound.SoundCategory;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tga.Mechanic.ITGAManpoweredBlock;
import tga.Mechanic.ManMachineManager;
import tga.NetEvents.ManCrackerGuiSync;
import tga.Screen.MachineCrackerHandler;
import tga.*;
import tga.Str.IMMMTarget;
import tga.Str.MMMTargetBasic;
import tga.WorkBook.WorkRecipes.OneInRecipe;

import java.util.Arrays;
import java.util.function.Function;

public class ManCrackerTile extends BlockEntity implements MMMTargetBasic.ITarget, ITGAManpoweredBlock, SidedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    public IMMMTarget Ticker;
    private final ItemStack[] ItemBuffer = new ItemStack[BUFFER_SIZE];
    private static final int BUFFER_ID_SLOT_INPUT = 0;
    private static final int BUFFER_ID_SLOT_OUTPUT = 1;
    private static final int BUFFER_SIZE = 2;

    private ItemStack SubOutput = ItemStack.EMPTY;
    private ItemStack CraftMain = ItemStack.EMPTY;
    private ItemStack CraftSub = ItemStack.EMPTY;
    public int Worked;
    public int WorkTotal = 1;
    public int Jinriki;

    public static final int JINRIKI_INPUT_OFF = 20_000_00;

    @Override
    public float GetJinrikiMul() {
        return Jinriki >= JINRIKI_INPUT_OFF ? 0f : 4f;
    }

    @Override
    public void JinrikiGo(int power, ServerPlayerEntity player, World world) {
        Jinriki += power;
        world.playSound(null, pos, TGASounds.GRINDER, SoundCategory.BLOCKS, 1f, 1f);
        Ticker.QueQueNext(ManMachineManager.SERVER_INTANCE);
    }

    public static Function<ManCrackerTile, IMMMTarget> TICKER_BUILDER_CLIENT;

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world.isClient) Ticker = TICKER_BUILDER_CLIENT.apply(this);
        else {
            Ticker = new MMMTargetBasic(this);
            Ticker.QueQueNext(ManMachineManager.SERVER_INTANCE);
        }
    }

    public void MachineUpdate(ManMachineManager mng) {
        if (removed) return;
        //No power or no crafting require
        if (Jinriki < 20){
            Jinriki = 0;
            return;
        }
        Ticker.QueQueNext(mng);
        Jinriki -= 5;
        if (!ItemBuffer[1].isEmpty()) return;
        if (CraftMain.isEmpty() && ItemBuffer[0].isEmpty()) return;
        //Crafting
        int amount = Math.min(Jinriki / 10, 25_00);
        Jinriki -= amount;
        Worked += amount + 5;
        //Crafted
        if (Worked >= WorkTotal) {
            ItemBuffer[1] = CraftMain;
            SubOutput = CraftSub;
            Worked = 0;
            WorkTotal = 1;
            CraftMain = ItemStack.EMPTY;
            CraftSub = ItemStack.EMPTY;
            TryCraft();
        }
        markDirty();
    }

    @Override
    protected void writeData(WriteView view) {
        view.putInt("W", Worked);
        view.putInt("J", Jinriki);
        view.putInt("T", WorkTotal);
        TGAHelper.WriteItem(view, "SI", ItemBuffer[0]);
        TGAHelper.WriteItem(view, "SO", ItemBuffer[1]);
        TGAHelper.WriteItem(view, "Ss", SubOutput);
        TGAHelper.WriteItem(view, "Cr", CraftMain);
        TGAHelper.WriteItem(view, "Lk", CraftSub);
    }

    @Override
    protected void readData(ReadView view) {
        Worked = view.getInt("W", 0);
        Jinriki = view.getInt("J", 0);
        WorkTotal = view.getInt("T", 1_000_000);
        ItemBuffer[0] = TGAHelper.ReadItem(view, "SI");
        ItemBuffer[1] = TGAHelper.ReadItem(view, "SO");
        SubOutput = TGAHelper.ReadItem(view, "Ss");
        CraftMain = TGAHelper.ReadItem(view, "Cr");
        CraftSub = TGAHelper.ReadItem(view, "Lk");
    }

    public ManCrackerTile(BlockPos pos, BlockState state) {
        super(TGATileEnities.M_CRACKER_LV0, pos, state);
        Arrays.fill(ItemBuffer, ItemStack.EMPTY);
    }

    private void TryCraft() {
        if (!ItemBuffer[1].isEmpty()) return;
        if (!CraftMain.isEmpty()) return;
        //Locking for next craft
        OneInRecipe next_recipe = TGARecipes.Cracker_LV0.CraftWith(ItemBuffer[0]);
        if (next_recipe != null) {
            ItemStack[] getCrafted = new ItemStack[2];
            ItemBuffer[0] = next_recipe.RealCraft(ItemBuffer[0], getCrafted, world.getRandom());
            WorkTotal = (int)next_recipe.NeedPower;
            //Get only 2
            CraftMain = getCrafted[0];
            CraftSub = getCrafted[1];
        }
        else {
            //reset craft
            CraftMain = ItemStack.EMPTY;
            CraftSub = ItemStack.EMPTY;
        }
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return TGAShared.IntRange2;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        if (slot != BUFFER_ID_SLOT_INPUT) return false;
        return TGARecipes.Cracker_LV0.CanAccept(stack);
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
        return ItemBuffer[0].isEmpty() && ItemBuffer[1].isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return ItemBuffer[slot];
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack stack = ItemBuffer[slot];
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack rt = stack.copy();
        if (amount >= stack.getCount())
            if (slot == BUFFER_ID_SLOT_OUTPUT) {
                ItemBuffer[slot] = SubOutput;
                SubOutput = ItemStack.EMPTY;
            } else ItemBuffer[slot] = ItemStack.EMPTY;
        else {
            stack.decrement(amount);
            rt.setCount(amount);
        }
        markDirty();
        return rt;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack rtStack = ItemBuffer[slot].copy();
        if (slot == BUFFER_ID_SLOT_OUTPUT) {
            ItemBuffer[BUFFER_ID_SLOT_OUTPUT] = SubOutput;
            SubOutput = ItemStack.EMPTY;
        }
        else ItemBuffer[slot] = ItemStack.EMPTY;
        markDirty();
        return rtStack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemBuffer[slot] = stack;
        TryCraft();
        markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null && !isRemoved() && player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 100.0;
    }

    @Override
    public Text getDisplayName() {
        return TotalGreedyAgent.GetGuiLang("mancracker");
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
        if (world.isClient || MachineCrackerHandler.UsingPlayerCount == 0) return;
        ManCrackerGuiSync payload = new ManCrackerGuiSync(world.getRegistryKey().getValue().toString(), pos, Worked, WorkTotal, ItemBuffer[0], ItemBuffer[1]);
        for (ServerPlayerEntity player : MachineCrackerHandler.UsingPlayer)
            ServerPlayNetworking.send(player, payload);
    }

    public ManCrackerGuiSync GetSyncValue() {
        return new ManCrackerGuiSync(world.getRegistryKey().getValue().toString(), pos, Worked, WorkTotal, ItemBuffer[0], ItemBuffer[1]);
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

    public DefaultedList<ItemStack> GetDrops() {
        DefaultedList<ItemStack> drop = DefaultedList.of();
        drop.add(new ItemStack(TGABlocks.MAN_CRACKER));
        if (!ItemBuffer[0].isEmpty()) drop.add(ItemBuffer[0].copy());
        if (!ItemBuffer[1].isEmpty()) drop.add(ItemBuffer[1].copy());
        if (!SubOutput.isEmpty()) drop.add(SubOutput.copy());
        return drop;
    }
}