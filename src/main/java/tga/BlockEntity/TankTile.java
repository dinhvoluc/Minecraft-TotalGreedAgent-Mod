package tga.BlockEntity;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
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
import tga.ComDat.TankComData;
import tga.NetEvents.TankGuiSync;
import tga.Screen.TankScreenHandler;
import tga.TGAHelper;
import tga.TGAShared;
import tga.TGATileEnities;
import tga.TotalGreedyAgent;

public class TankTile extends BlockEntity implements SidedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    // <editor-fold desc="Fluids">
    public long VolSize;

    public final SimpleInventory BufferBox = new SimpleInventory(2);

    private final InventoryStorage InSlotHelper2 = InventoryStorage.of(BufferBox, null);

    private final ContainerItemContext InSlotHelper = ContainerItemContext.ofSingleSlot(InSlotHelper2.getSlot(0));

    public SingleVariantStorage<FluidVariant> InnerTank = new SingleVariantStorage<FluidVariant>() {
        @Override
        protected boolean canInsert(FluidVariant iType) {
            return variant.isBlank() || variant == iType;
        }

        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return VolSize;
        }

        @Override
        protected void onFinalCommit() {
            EjectItem();
        }
    };

    public void SetTankSize(int stackSize) {
        VolSize = stackSize * FluidConstants.BUCKET;
    }
    // </editor-fold>

    public TankTile(BlockPos pos, BlockState state) {
        super(TGATileEnities.TANK_TILE, pos, state);
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return BufferBox.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return BufferBox.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack rt = BufferBox.removeStack(slot, amount);
        setStack(slot, BufferBox.getStack(slot));
        if (!rt.isEmpty()) markDirty();
        return rt;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack rt = BufferBox.removeStack(slot);
        setStack(slot, ItemStack.EMPTY);
        if (!rt.isEmpty()) markDirty();
        return rt;
    }

    public long GetTankEmptyVol() {
        return VolSize - InnerTank.amount;
    }

    private void EjectItem(){
        //Commit is from pipe system
        if (!BufferBox.getStack(1).isEmpty()) {
            markDirty();
            return;
        }
        //not found nothing to do dumpout
        BufferBox.setStack(1,BufferBox.removeStack(0));
        markDirty();
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (world == null || world.isClient) return;
        BufferBox.setStack(slot, stack);
        //No process if output not empty or no input item
        if (BufferBox.getStack(0).isEmpty() || !BufferBox.getStack(1).isEmpty()) {
            markDirty();
            return;
        }
        Storage<FluidVariant> itemStorage = InSlotHelper.find(FluidStorage.ITEM);
        if (itemStorage == null) {
            EjectItem();
            return;
        }
        long tankFree = GetTankEmptyVol();
        //Check can add more
        if (tankFree > 0) {
            //when empty try add new
            if (InnerTank.variant.isBlank()) {
                for (StorageView<FluidVariant> storageView : itemStorage.nonEmptyViews()) {
                    if (storageView.isResourceBlank()) continue;
                    FluidVariant cfType = storageView.getResource();
                    try (Transaction transaction = Transaction.openOuter()) {
                        long movedVol = itemStorage.extract(cfType, tankFree, transaction);
                        if (movedVol > 0) {
                            InnerTank.insert(cfType, movedVol, transaction);
                            transaction.commit();
                            return;
                        }
                    }
                }
                EjectItem();
                return;
            }
            //Search for have same fluid
            else {
                try (Transaction transaction = Transaction.openOuter()) {
                    long movedVol = itemStorage.extract(InnerTank.variant, tankFree, transaction);
                    if (movedVol > 0) {
                        InnerTank.insert(InnerTank.variant, movedVol, transaction);
                        transaction.commit();
                        return;
                    }
                }
            }
        }
        //check if have fluid
        if (InnerTank.amount <= 0 || InnerTank.variant.isBlank()) {
            InnerTank.amount = 0;
            InnerTank.variant = FluidVariant.blank();
            EjectItem();
            return;
        }
        //try to pull out tank fluid to item
        try (Transaction transaction = Transaction.openOuter()) {
            long itemcanget = itemStorage.insert(InnerTank.variant, InnerTank.amount, transaction);
            if (itemcanget > 0) {
                InnerTank.extract(InnerTank.variant, itemcanget, transaction);
                //check tank turned empty
                if (InnerTank.amount <= 0) {
                    InnerTank.variant = FluidVariant.blank();
                    InnerTank.amount = 0;
                }
                transaction.commit();
                return;
            }
        }
        EjectItem();
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return TGAShared.IntRange2;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0 && BufferBox.getStack(0).isEmpty();
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 1;
    }

    @Override
    public void clear() {
        InnerTank.amount = 0;
        InnerTank.variant = FluidVariant.blank();
        BufferBox.clear();
    }
    // </editor-fold>

    // <editor-fold desc="Data">
    @Override
    protected void writeData(WriteView view) {
        view.putInt("S", (int)(VolSize / FluidConstants.BUCKET));
        TGAHelper.WriteItem(view,"I", BufferBox.getStack(0));
        TGAHelper.WriteItem(view,"O", BufferBox.getStack(0));
        TGAHelper.WriteFluidType(view, "L", InnerTank.variant);
        view.putLong("V", InnerTank.amount);
    }

    @Override
    protected void readData(ReadView view) {
        VolSize = view.getInt("S", 1) * FluidConstants.BUCKET;
        BufferBox.setStack(0, TGAHelper.ReadItem(view, "I"));
        BufferBox.setStack(1, TGAHelper.ReadItem(view, "O"));
        InnerTank.variant = TGAHelper.ReadFluidType(view, "L");
        InnerTank.amount = view.getLong("V", 0);
    }

    public void OnPlacedRebuild(TankComData data) {
        VolSize = data.MaxStack * FluidConstants.BUCKET;
        InnerTank.variant = data.FType == null ? FluidVariant.blank() : data.FType;
        InnerTank.amount = data.Count;
    }

    public TankComData GetDataComponent() {
        return new TankComData((int) (VolSize / FluidConstants.BUCKET), InnerTank.variant, InnerTank.amount);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (TankScreenHandler.UsingPlayerCount == 0 || world == null || world.isClient) return;
        TankGuiSync payload = new TankGuiSync(InnerTank.variant, world.getRegistryKey().getValue().toString(), pos, BufferBox.getStack(0), BufferBox.getStack(1), InnerTank.amount);
        for (ServerPlayerEntity player : TankScreenHandler.UsingPlayer)
            ServerPlayNetworking.send(player, payload);
    }
    // </editor-fold>

    // <editor-fold desc="GUI">
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return world != null && !isRemoved() && player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 100.0;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (world == null || world.isClient) return;
        TankScreenHandler.UsingPlayer.add((ServerPlayerEntity) player);
        TankScreenHandler.UsingPlayerCount = TankScreenHandler.UsingPlayer.size();
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (world == null || world.isClient) return;
        TankScreenHandler.UsingPlayer.remove((ServerPlayerEntity) player);
        TankScreenHandler.UsingPlayerCount = TankScreenHandler.UsingPlayer.size();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        if (world == null || world.isClient) return null;
        TankScreenHandler.SendUpdate(this, InnerTank.variant, BufferBox.getStack(0), BufferBox.getStack(1), InnerTank.amount, (ServerPlayerEntity) player);
        return new TankScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return pos;
    }

    @Override
    public Text getDisplayName() {
        return switch ((int) (VolSize / FluidConstants.BUCKET)) {
            case BoxStackBlock.SIZE_WOOD -> Text.translatable(TotalGreedyAgent.GetGuiLang("tank_wood"));
            case BoxStackBlock.SIZE_COPPER -> Text.translatable(TotalGreedyAgent.GetGuiLang("tank_copper"));
            case BoxStackBlock.SIZE_BRONZE -> Text.translatable(TotalGreedyAgent.GetGuiLang("tank_bronze"));
            case BoxStackBlock.SIZE_IRON -> Text.translatable(TotalGreedyAgent.GetGuiLang("tank_iron"));
            default -> Text.translatable(TotalGreedyAgent.GetGuiLang("tank_any"));
        };
    }

    public void TGAS2CSync(TankGuiSync payload) {
        InnerTank.variant = payload.FType;
        InnerTank.amount = payload.VolCount;
        BufferBox.setStack(0, payload.Slot0);
        BufferBox.setStack(1, payload.Slot1);
    }
    // </editor-fold>
}