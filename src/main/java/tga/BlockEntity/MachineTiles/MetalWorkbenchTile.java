package tga.BlockEntity.MachineTiles;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
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
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import tga.Mechanic.IClickedIDHandler;
import tga.Mechanic.ICraftProvider;
import tga.Mechanic.ITGAManpoweredBlock;
import tga.NetEvents.MetalWorkbenchGuiSync;
import tga.Screen.MetalWorkbenchHandler;
import tga.*;
import tga.TicksMng.IMMMTarget;
import tga.TicksMng.MMMTargetBasic;
import tga.TicksMng.ManMachineManager;
import tga.WorkBook.WorkRecipes.MetalWorkRecipe;

import java.util.function.Function;

public class MetalWorkbenchTile extends BlockEntity implements MMMTargetBasic.ITarget, ICraftProvider, ITGAManpoweredBlock, IClickedIDHandler, SidedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    public IMMMTarget Ticker;
    public SimpleInventory BufferSlot = new SimpleInventory(9);
    public static final int MAX_WATER_LEVEL = 4 * (int)FluidConstants.BUCKET;
    public int BurntimeLeft;
    public static final int BURN_TIME_BAR_MAX = 400;
    public int Jinriki = 0;
    public int WorkTotal = 10;
    public int Worked = 0;
    public int WorkMode = WORK_MODE_PLATE;
    private static final int BUFFER_ID_SLOT_OUTPUT = 0;
    public ItemStack ResultSlot = ItemStack.EMPTY;
    public ItemStack Crafting = ItemStack.EMPTY;
    private final ContainerItemContext[] FluidSlotHelper = new ContainerItemContext[9];
    public SingleVariantStorage<FluidVariant> InnerTank = new SingleVariantStorage<>() {
        @Override
        protected boolean canInsert(FluidVariant iType) {
            return variant == iType;
        }

        @Override
        protected FluidVariant getBlankVariant() {
            return TGAShared.VARIANT_WATER;
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return MAX_WATER_LEVEL;
        }

        @Override
        protected void onFinalCommit() {
            super.onFinalCommit();
            markDirty();
        }
    };

    public static Function<MetalWorkbenchTile, IMMMTarget> TICKER_BUILDER_CLIENT;

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

    public MetalWorkbenchTile(BlockPos pos, BlockState state) {
        super(TGATileEnities.M_METAL_WORKBENCH, pos, state);
        InventoryStorage inSlotHelper2 = InventoryStorage.of(BufferSlot, null);
        for(var i = 0; i < 9; i++)
            FluidSlotHelper[i] = ContainerItemContext.ofSingleSlot(inSlotHelper2.getSlot(i));
    }

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
        InnerTank.amount = 0;
        BurntimeLeft = 0;
        Worked = 0;
        Crafting = ItemStack.EMPTY;
        ResultSlot = ItemStack.EMPTY;
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
        return slot == BUFFER_ID_SLOT_OUTPUT || TGAHelper.IsEmptyFluidHolder(stack);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot == BUFFER_ID_SLOT_OUTPUT) ResultSlot = stack;
        else BufferSlot.setStack(slot - 1, stack);
        CheckTankInput();
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
            if (!itemStack.isEmpty()) markDirty();
            return itemStack;
        } else {
            ItemStack rt = BufferSlot.removeStack(slot - 1, amount);
            if (rt.isEmpty()) return ItemStack.EMPTY;
            markDirty();
            return rt;
        }
    }

    @Override
    public ItemStack GetCraftInputStack(int i) {
        return BufferSlot.getStack(i);
    }

    @Override
    public void SetCraftLeft(ItemStack[] setSlot) {
        for(int i =0; i < 9; i++)
        {
            ItemStack setter = setSlot[i];
            if (setter == null) continue;
            BufferSlot.setStack(i, setter);
        }
        markDirty();
    }

    @Override
    public int GetCraftInputSize() {
        return 9;
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world.isClient) Ticker = TICKER_BUILDER_CLIENT.apply(this);
        else {
            Ticker = new MMMTargetBasic(this);
            Ticker.QueQueNext(ManMachineManager.SERVER_INTANCE);
        }
    }

    @Override
    public void MachineUpdate(ManMachineManager mng) {
        if (removed) return;
        BlockState state = world.getBlockState(pos);
        boolean isBurn = BurntimeLeft > 0;
        if (isBurn) BurntimeLeft--;
        //have nenergy for crafting
        if (Jinriki < 20) {
            Jinriki = 0;
            UpdateExit(isBurn, state);
            return;
        }
        Ticker.QueQueNext(mng);
        Jinriki -= 5;
        //find for new recipe
        if (Crafting.isEmpty())
            for(MetalWorkRecipe canCraft : TGARecipes.MetalWorkbench.GetNextCraft(this, this.WorkMode)) {
                if (canCraft.WaterToCool > InnerTank.amount || !TGAHelper.ItemCanStackTo(canCraft.Result, ResultSlot)) continue;
                if (TGARecipes.MetalWorkbench.RealCraft(canCraft, this)) {
                    Crafting = canCraft.Result;
                    WorkTotal = (int) canCraft.NeedPower;
                    Worked = 0;
                    InnerTank.amount -= canCraft.WaterToCool;
                    CheckTankInput();
                    break;
                }
            }
        //fuel ticks
        if (BurntimeLeft <= 0) {
            //get fuel for heat
            for (int i = 0; i < 9; i++) {
                ItemStack fuel = BufferSlot.getStack(i);
                if (fuel.isOf(Items.COAL)) {
                    BurntimeLeft = BURN_TIME_BAR_MAX;
                    if (fuel.getCount() == 1) BufferSlot.heldStacks.set(i, ItemStack.EMPTY);
                    else fuel.decrement(1);
                    break;
                } else if (fuel.isOf(Items.CHARCOAL)) {
                    BurntimeLeft = BURN_TIME_BAR_MAX / 2;
                    if (fuel.getCount() == 1) BufferSlot.heldStacks.set(i, ItemStack.EMPTY);
                    else fuel.decrement(1);
                    break;
                }
            }
            //no heat no work
            if (BurntimeLeft <= 0) {
                UpdateExit(true, state);
                return;
            }
        }
        //Crafting tick
        int amount = Math.min(Jinriki / 10, 20_00);
        Jinriki -= amount;
        Worked += amount + 5;
        //Crafted
        if (Worked >= WorkTotal) {
            ResultSlot = TGAHelper.ItemStackTo(Crafting, ResultSlot);
            Crafting = ItemStack.EMPTY;
            Worked = 0;
        }
        UpdateExit(true, state);
    }

    private void UpdateExit(boolean isDirty, BlockState state) {
        int newState = (BurntimeLeft > 0 ? 1 : 0) + (InnerTank.amount > FluidConstants.BOTTLE ? 2 : 0);
        if (state.get(TGABlocks.STATE4, -1) != newState)
            world.setBlockState(pos, state.with(TGABlocks.STATE4, newState), Block.NOTIFY_ALL);
        if (isDirty) markDirty();
    }

    private void CheckTankInput() {
        long tankFree = MAX_WATER_LEVEL - InnerTank.amount;
        if (tankFree <= 0) return;
        int freeSlot = -1;
        boolean freeCheck = true;
        for (int i = 0; i < 9; i++) {
            if (tankFree <= 0) return;
            Storage<FluidVariant> itemStorage = FluidSlotHelper[i].find(FluidStorage.ITEM);
            if (itemStorage == null) continue;
            //Check can add more
            try (Transaction transaction = Transaction.openOuter()) {
                long movedVol = itemStorage.extract(TGAShared.VARIANT_WATER, tankFree, transaction);
                if (movedVol > 0) {
                    InnerTank.insert(InnerTank.variant, movedVol, transaction);
                    transaction.commit();
                    return;
                }
            }
            //check canbe split
            ItemStack original = BufferSlot.heldStacks.get(i);
            if (original.getCount() <= 1) continue;
            if (freeCheck) {
                for (int sle = 0; sle < 9; sle++)
                    if (BufferSlot.heldStacks.get(sle).isEmpty()) {
                        freeSlot = sle;
                        break;
                    }
                freeCheck = false;
            }
            //No empty slot
            if (freeSlot < 0) return;
            ItemStack tmpSingle = original.copy();
            tmpSingle.setCount(1);
            Storage<FluidVariant> itemPortableStorage = ContainerItemContext.withConstant(tmpSingle).find(FluidStorage.ITEM);
            if (itemPortableStorage == null) continue;
            boolean canDoExchance;
            try (Transaction transaction = Transaction.openOuter()) {
                long movedVol = itemPortableStorage.extract(TGAShared.VARIANT_WATER, tankFree, transaction);
                canDoExchance = movedVol > 0;
            }
            if (canDoExchance) {
                //do the real input
                BufferSlot.heldStacks.set(freeSlot, original.split(1));
                Storage<FluidVariant> splitedItemStorage = FluidSlotHelper[freeSlot].find(FluidStorage.ITEM);
                try (Transaction transaction = Transaction.openOuter()) {
                    if (splitedItemStorage == null) throw new IllegalCallerException("Item-Not-Stabled-Ouput");
                    long movedVol = splitedItemStorage.extract(TGAShared.VARIANT_WATER, tankFree, transaction);
                    InnerTank.insert(InnerTank.variant, movedVol, transaction);
                    transaction.commit();
                }
                return;
            }
        }
    }

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
        return new MetalWorkbenchGuiSync(world.getRegistryKey().getValue().toString(), pos, WorkMode, Worked, WorkTotal, BurntimeLeft, (int)InnerTank.amount, sync);
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
        return TotalGreedyAgent.GetGuiLang("metalwb");
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

    public void TGAS2CSync(MetalWorkbenchGuiSync payload) {
        if (!pos.equals(payload.Pos)) return;
        if (world == null) return;
        if (!world.getRegistryKey().getValue().toString().equals(payload.World)) return;
        WorkMode = payload.WorkMode;
        Worked = payload.WorkDone;
        WorkTotal = payload.WorkTotal;
        BurntimeLeft = payload.BurnLeft;
        InnerTank.amount = payload.WaterLevel;
        ResultSlot = payload.ItemSlots[0];
        for (var i = 0; i < 9; i++)
            BufferSlot.heldStacks.set(i, payload.ItemSlots[i + 1]);
    }

    public void markDirty() {
        super.markDirty();
        if (world.isClient) return;
        Ticker.QueQueNext(ManMachineManager.SERVER_INTANCE);
        if (MetalWorkbenchHandler.UsingPlayerCount == 0) return;
        MetalWorkbenchGuiSync payload = GetSyncValue();
        for (ServerPlayerEntity player : MetalWorkbenchHandler.UsingPlayer)
            ServerPlayNetworking.send(player, payload);
    }

    @Override
    protected void readData(ReadView view) {
        Jinriki = view.getInt("J", 0);
        BurntimeLeft = view.getInt("B", 0);
        WorkMode = view.getInt("M", 0);
        Worked = view.getInt("W", 0);
        WorkTotal = view.getInt("T", 10);
        InnerTank.amount = view.getInt("F", 0);
        TGAHelper.ReadStacks( view ,"I", BufferSlot.heldStacks, 0, 9);
        Crafting = TGAHelper.ReadItem(view, "C");
        ResultSlot = TGAHelper.ReadItem(view, "R");
    }
    @Override
    protected void writeData(WriteView view) {
        view.putInt("J", Jinriki);
        view.putInt("M", WorkMode);
        view.putInt("B", BurntimeLeft);
        view.putInt("W", Worked);
        view.putInt("T", WorkTotal);
        view.putInt("F", (int)InnerTank.amount);
        TGAHelper.WriteStacks( view ,"I", BufferSlot.heldStacks, 0, 9);
        TGAHelper.WriteItem(view, "C", Crafting);
        TGAHelper.WriteItem(view, "R", ResultSlot);
    }

    @Override
    public float GetJinrikiMul() {
        return Jinriki >= JINRIKI_INPUT_OFF ? 0f : 16f;
    }

    @Override
    public void JinrikiGo(int power, ServerPlayerEntity player, World world) {
        Jinriki += power;
        world.playSound(null, pos, TGASounds.HAMMER, SoundCategory.BLOCKS, 1f, 1f);
        Ticker.QueQueNext(ManMachineManager.SERVER_INTANCE);
    }
}