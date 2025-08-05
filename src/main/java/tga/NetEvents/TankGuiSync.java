package tga.NetEvents;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import tga.TGAHelper;
import tga.TotalGreedyAgent;

public class TankGuiSync implements CustomPayload {
    public static PacketCodec<RegistryByteBuf, TankGuiSync> PACKET_CODEC;
    public static Id<TankGuiSync> PAYLOAD_ID;

    public static void Load() {
        PAYLOAD_ID = new Id<>(TotalGreedyAgent.GetID("e_tank_syc"));
        PACKET_CODEC = new PacketCodec<>() {
            @Override
            public TankGuiSync decode(RegistryByteBuf buf) {
                String wrl = buf.readString();
                BlockPos pos = buf.readBlockPos();
                ItemStack slot0 = TGAHelper.DecodeItem(buf);
                ItemStack slot1 = TGAHelper.DecodeItem(buf);
                long volC = buf.readLong();
                FluidVariant ftype = volC > 0 ? FluidVariant.PACKET_CODEC.decode(buf) : FluidVariant.blank();
                return new TankGuiSync(ftype, wrl, pos, slot0, slot1, volC);
            }

            @Override
            public void encode(RegistryByteBuf buf, TankGuiSync value) {
                buf.writeString(value.World);
                buf.writeBlockPos(value.Pos);
                TGAHelper.EncodeItem(buf, value.Slot0);
                TGAHelper.EncodeItem(buf, value.Slot1);
                if (value.FType == null || value.FType.isBlank() || value.VolCount <= 0) buf.writeLong(0);
                else {
                    buf.writeLong(value.VolCount);
                    FluidVariant.PACKET_CODEC.encode(buf, value.FType);
                }
            }
        };
        PayloadTypeRegistry.playS2C().register(PAYLOAD_ID, PACKET_CODEC);
    }

    public TankGuiSync(FluidVariant fType, String world, BlockPos pos, ItemStack slot0, ItemStack slot1, long volUsing) {
        FType = fType;
        World = world;
        Pos = pos;
        Slot0 = slot0;
        Slot1 = slot1;
        VolCount = volUsing;
    }

    public final String World;
    public final BlockPos Pos;
    public final ItemStack Slot0, Slot1;
    public final long VolCount;
    public final FluidVariant FType;

    @Override
    public Id<TankGuiSync> getId() {
        return PAYLOAD_ID;
    }
}