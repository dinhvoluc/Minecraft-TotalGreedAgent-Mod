package tga.NetEvents;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import tga.Mechanic.IClickedIDHandler;
import tga.TotalGreedyAgent;

public class ClickedIDSync implements CustomPayload {
    public static PacketCodec<RegistryByteBuf, ClickedIDSync> PACKET_CODEC;
    public static Id<ClickedIDSync> PAYLOAD_ID;

    public static void Load() {
        PAYLOAD_ID = new Id<>(TotalGreedyAgent.GetID("e_clicked_id"));
        PACKET_CODEC = new PacketCodec<>() {
            @Override
            public ClickedIDSync decode(RegistryByteBuf buf) {
                BlockPos pos = buf.readBlockPos();
                int id = buf.readInt();
                return new ClickedIDSync(pos, id);
            }

            @Override
            public void encode(RegistryByteBuf buf, ClickedIDSync value) {
                buf.writeBlockPos(value.Pos);
                buf.writeInt(value.ID);
            }
        };
        PayloadTypeRegistry.playC2S().register(PAYLOAD_ID, PACKET_CODEC);
    }

    public ClickedIDSync(BlockPos pos, int id) {
        Pos = pos;
        ID = id;
    }

    public final BlockPos Pos;
    public final int ID;

    public void Handle(ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        ServerWorld world = player.getWorld();
        if (world == null) return;
        BlockEntity tile = world.getBlockEntity(Pos);
        if (!(tile instanceof IClickedIDHandler man)) return;
        man.ClickedID(ID);
    }

    @Override
    public Id<ClickedIDSync> getId() {
        return PAYLOAD_ID;
    }
}