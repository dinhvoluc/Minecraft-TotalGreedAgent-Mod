package tga.NetEvents;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import tga.TotalGreedyAgent;

public class ManCrackerGuiSync implements CustomPayload {
    public static PacketCodec<RegistryByteBuf, ManCrackerGuiSync> PACKET_CODEC;
    public static CustomPayload.Id<ManCrackerGuiSync> PAYLOAD_ID;

    public static void Load() {
        PAYLOAD_ID = new Id<>(TotalGreedyAgent.GetID("e_mancracker_syc"));
        PACKET_CODEC = new PacketCodec<>() {
            private static final int FLAG_NO_ITEM = 0;
            private static final int FLAG_ONLY_INPUT = 1;
            private static final int FLAG_ONLY_OUTPUT = 2;
            private static final int FLAG_ALL_SLOT = 3;
            @Override
            public ManCrackerGuiSync decode(RegistryByteBuf buf) {
                int flags = buf.readInt();
                String wrl = buf.readString();
                BlockPos pos = buf.readBlockPos();
                int workDone = buf.readInt();
                int workTotal = buf.readInt();
                ItemStack inputSlot;
                ItemStack outputSlot;
                switch (flags) {
                    case FLAG_NO_ITEM:
                        inputSlot = ItemStack.EMPTY;
                        outputSlot = ItemStack.EMPTY;
                        break;
                    case  FLAG_ONLY_INPUT:
                        inputSlot = ItemStack.PACKET_CODEC.decode(buf);
                        outputSlot = ItemStack.EMPTY;
                        break;
                    case FLAG_ONLY_OUTPUT:
                        inputSlot = ItemStack.EMPTY;
                        outputSlot = ItemStack.PACKET_CODEC.decode(buf);
                        break;
                    case FLAG_ALL_SLOT:
                        inputSlot = ItemStack.PACKET_CODEC.decode(buf);
                        outputSlot = ItemStack.PACKET_CODEC.decode(buf);
                        break;
                    default:
                        throw new IllegalStateException("no-flags-match-for-packet-ManCrackerGuiSync");
                }
                return new ManCrackerGuiSync(wrl, pos, workDone, workTotal, inputSlot, outputSlot);
            }

            @Override
            public void encode(RegistryByteBuf buf, ManCrackerGuiSync value) {
                int flags = value.SlotIn.isEmpty() ? FLAG_NO_ITEM : FLAG_ONLY_INPUT;
                if (!value.SlotOut.isEmpty()) flags += FLAG_ONLY_OUTPUT;
                buf.writeInt(flags);
                buf.writeString(value.World);
                buf.writeBlockPos(value.Pos);
                buf.writeInt(value.WorkDone);
                buf.writeInt(value.WorkTotal);
                if (!value.SlotIn.isEmpty()) ItemStack.PACKET_CODEC.encode(buf, value.SlotIn);
                if (!value.SlotOut.isEmpty()) ItemStack.PACKET_CODEC.encode(buf, value.SlotOut);
            }
        };
        PayloadTypeRegistry.playS2C().register(PAYLOAD_ID, PACKET_CODEC);
    }

    public ManCrackerGuiSync(String world, BlockPos pos, int workDone, int workTotal, ItemStack slotIn, ItemStack slotOut) {
        World = world;
        Pos = pos;
        WorkDone = workDone;
        WorkTotal = workTotal;
        SlotIn = slotIn;
        SlotOut = slotOut;
    }

    public final String World;
    public final BlockPos Pos;
    public final int WorkDone;
    public final int WorkTotal;
    public final ItemStack SlotIn;
    public final ItemStack SlotOut;

    @Override
    public Id<ManCrackerGuiSync> getId() {
        return PAYLOAD_ID;
    }
}