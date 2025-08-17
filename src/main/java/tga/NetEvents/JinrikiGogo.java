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
import tga.Mechanic.ITGAManpoweredBlock;
import tga.TGAHelper;
import tga.TotalGreedyAgent;

public class JinrikiGogo implements CustomPayload {
    public static PacketCodec<RegistryByteBuf, JinrikiGogo> PACKET_CODEC;
    public static CustomPayload.Id<JinrikiGogo> PAYLOAD_ID;

    public static void Load() {
        PAYLOAD_ID = new Id<>(TotalGreedyAgent.GetID("e_jinriki_now"));
        PACKET_CODEC = new PacketCodec<>() {
            @Override
            public JinrikiGogo decode(RegistryByteBuf buf) {
                return new JinrikiGogo( buf.readBlockPos());
            }

            @Override
            public void encode(RegistryByteBuf buf, JinrikiGogo value) {
                buf.writeBlockPos(value.Pos);
            }
        };
        PayloadTypeRegistry.playC2S().register(PAYLOAD_ID, PACKET_CODEC);
    }

    public JinrikiGogo(BlockPos pos) {
        Pos = pos;
    }

    public final BlockPos Pos;

    public void Handle(ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        ServerWorld world = player.getWorld();
        if (world == null) return;
        BlockEntity tile = world.getBlockEntity(Pos);
        if (!(tile instanceof ITGAManpoweredBlock man)) return;
        float speed = man.GetJinrikiMul();
        if (speed < 0.1f) return;
        //Man power code
        int jinriki = 100 + player.getHungerManager().getFoodLevel() + player.experienceLevel * 6;
        player.addExhaustion(speed);
        if (TGAHelper.drainExperience(player, 1f + (int)(speed * 0.18f))) jinriki += 25 + (int)(5f * speed);
        man.JinrikiGo((int)(jinriki * 40f * speed) + 10, player, world);
    }

    @Override
    public Id<JinrikiGogo> getId() {
        return PAYLOAD_ID;
    }
}