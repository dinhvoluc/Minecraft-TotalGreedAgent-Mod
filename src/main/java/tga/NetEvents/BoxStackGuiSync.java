package tga.NetEvents;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import tga.TGAHelper;
import tga.TotalGreedyAgent;

public class BoxStackGuiSync implements CustomPayload {
    public static PacketCodec<RegistryByteBuf, BoxStackGuiSync> PACKET_CODEC;
    public static CustomPayload.Id<BoxStackGuiSync> PAYLOAD_ID;

    public static void Load() {
        PAYLOAD_ID = new Id<>(TotalGreedyAgent.GetID("e_box_stack_syc"));
        PACKET_CODEC = new PacketCodec<>() {
            @Override
            public BoxStackGuiSync decode(RegistryByteBuf buf) {
                String wrl = buf.readString();
                BlockPos pos = buf.readBlockPos();
                ItemStack lockedType = TGAHelper.DecodeItem(buf);
                int count = buf.readInt();
                return new BoxStackGuiSync(wrl, pos, lockedType, count);
            }

            @Override
            public void encode(RegistryByteBuf buf, BoxStackGuiSync value) {
                buf.writeString(value.World);
                buf.writeBlockPos(value.Pos);
                TGAHelper.EncodeItem(buf, value.LockedType);
                buf.writeInt(value.Count);
            }
        };
        PayloadTypeRegistry.playS2C().register(PAYLOAD_ID, PACKET_CODEC);
    }

    public BoxStackGuiSync(String world, BlockPos pos, ItemStack lockedType, int count) {
        World = world;
        Pos = pos;
        LockedType = lockedType;
        Count = count;
    }

    public final String World;
    public final BlockPos Pos;
    public final ItemStack LockedType;
    public final int Count;

    @Override
    public Id<BoxStackGuiSync> getId() {
        return PAYLOAD_ID;
    }
}