package tga.NetEvents;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import tga.TGAHelper;
import tga.TotalGreedyAgent;

public class MetalWorkbenchGuiSync implements CustomPayload {
    public static PacketCodec<RegistryByteBuf, MetalWorkbenchGuiSync> PACKET_CODEC;
    public static Id<MetalWorkbenchGuiSync> PAYLOAD_ID;

    public static void Load() {
        PAYLOAD_ID = new Id<>(TotalGreedyAgent.GetID("e_metalwb_syc"));
        PACKET_CODEC = new PacketCodec<>() {
            @Override
            public MetalWorkbenchGuiSync decode(RegistryByteBuf buf) {
                String wrl = buf.readString();
                BlockPos pos = buf.readBlockPos();
                int workMode = buf.readInt();
                int workDone = buf.readInt();
                int workTotal = buf.readInt();
                int burnLeft = buf.readInt();
                int burnTotal = buf.readInt();
                int waterLevel = buf.readInt();
                int slotsCount = buf.readInt();
                ItemStack[] slots = new ItemStack[slotsCount];
                for (int i = 0; i < 10; i++)
                    slots[i] = TGAHelper.DecodeItem(buf);
                return new MetalWorkbenchGuiSync(wrl, pos, workMode, workDone, workTotal, burnLeft, burnTotal, waterLevel, slots);
            }

            @Override
            public void encode(RegistryByteBuf buf, MetalWorkbenchGuiSync value) {
                buf.writeString(value.World);
                buf.writeBlockPos(value.Pos);
                buf.writeInt(value.WorkMode);
                buf.writeInt(value.WorkDone);
                buf.writeInt(value.WorkTotal);
                buf.writeInt(value.BurnLeft);
                buf.writeInt(value.BurnTotal);
                buf.writeInt(value.WaterLevel);
                buf.writeInt(value.ItemSlots.length);
                for(var i = 0; i < value.ItemSlots.length; i++)
                    TGAHelper.EncodeItem(buf, value.ItemSlots[i]);
            }
        };
        PayloadTypeRegistry.playS2C().register(PAYLOAD_ID, PACKET_CODEC);
    }

    public MetalWorkbenchGuiSync(String world, BlockPos pos, int workMode, int workDone, int workTotal, int burnLeft, int burnTotal, int waterLevel, ItemStack[] slots) {
        World = world;
        Pos = pos;
        WorkMode = workMode;
        WorkDone = workDone;
        WorkTotal = workTotal;
        ItemSlots = slots;
        BurnLeft = burnLeft;
        BurnTotal = burnTotal;
        WaterLevel = waterLevel;
    }

    public final String World;
    public final BlockPos Pos;
    public final int WorkMode;
    public final int WorkDone;
    public final int WorkTotal;
    public final int BurnLeft;
    public final int BurnTotal;
    public final int WaterLevel;
    public final ItemStack[] ItemSlots;

    @Override
    public Id<MetalWorkbenchGuiSync> getId() {
        return PAYLOAD_ID;
    }
}