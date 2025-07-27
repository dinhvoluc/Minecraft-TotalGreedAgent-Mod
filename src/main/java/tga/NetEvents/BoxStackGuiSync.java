package tga.NetEvents;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import tga.TotalGreedyAgent;

public class BoxStackGuiSync implements CustomPayload {
    public static PacketCodec<RegistryByteBuf, BoxStackGuiSync> PACKET_CODEC;
    public static CustomPayload.Id<BoxStackGuiSync> PAYLOAD_ID;

    public static void Load() {
        PAYLOAD_ID = new Id<>(TotalGreedyAgent.GetID("e_box_stack_syc"));
        PACKET_CODEC = new PacketCodec<>() {
            @Override
            public BoxStackGuiSync decode(RegistryByteBuf buf) {
                BlockPos pos = buf.readBlockPos();
                int exCount = buf.readInt();
                ItemStack hold;
                if (exCount == -128) {
                    exCount = 0;
                    hold = ItemStack.EMPTY;
                } else hold = ItemStack.PACKET_CODEC.decode(buf);
                return new BoxStackGuiSync(pos, exCount, hold);
            }

            @Override
            public void encode(RegistryByteBuf buf, BoxStackGuiSync value) {
                buf.writeBlockPos(value.Pos);
                if (value.HoldItem.isEmpty()) buf.writeInt(-128);
                else {
                    buf.writeInt(value.ExCount);
                    ItemStack.PACKET_CODEC.encode(buf, value.HoldItem);
                }
            }
        };
        PayloadTypeRegistry.playS2C().register(BoxStackGuiSync.PAYLOAD_ID, BoxStackGuiSync.PACKET_CODEC);
    }

    public BoxStackGuiSync(BlockPos pos, int exCount, ItemStack holdItem) {
        Pos = pos;
        ExCount = exCount;
        HoldItem = holdItem;
    }

    public final BlockPos Pos;
    public final int ExCount;
    public final ItemStack HoldItem;

    @Override
    public Id<BoxStackGuiSync> getId() {
        return PAYLOAD_ID;
    }
}