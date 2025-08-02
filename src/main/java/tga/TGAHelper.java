package tga;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;

public class TGAHelper {
    public static void WriteItem(WriteView view, String name, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        view.put(name, ItemStack.CODEC, stack);
    }
    public static ItemStack DecodeItem(RegistryByteBuf buf){
        return buf.readBoolean() ? ItemStack.PACKET_CODEC.decode(buf) : ItemStack.EMPTY;
    }
    public static void EncodeItem(RegistryByteBuf buf, ItemStack stack) {
        if (stack.isEmpty()) {
            buf.writeBoolean(false);
            return;
        }
        buf.writeBoolean(true);
        ItemStack.PACKET_CODEC.encode(buf, stack);
    }
    public static ItemStack ReadItem(ReadView view, String name){
        return view.read(name, ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
    public static boolean drainExperience(ServerPlayerEntity player) {
        float exp = player.experienceProgress;
        float tmp = exp - 1f / player.getNextLevelExperience();
        boolean rt = exp >= 0.0f;
        //レベルを落とす
        if (tmp <= 0f) {
            if (player.experienceLevel <= 0)
            {
                player.experienceProgress = 0f;
                player.setExperienceLevel(0);
            }
            else {
                player.experienceLevel--;
                player.setExperiencePoints(player.getNextLevelExperience() - 1);
                rt = true;
            }
        } else {
            player.experienceProgress = tmp;
            player.setExperienceLevel(player.experienceLevel);
            rt = true;
        }
        return rt;
    }
    public static int Num_MinMul(int a, int b) {
        return a * b / Num_MaxSub(a, b);
    }
    public static int Num_MaxSub(int a, int b)
    {
        while (b != 0)
        {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static Text GetFluidName(FluidVariant obj) {
        if (obj.isOf(Fluids.EMPTY)) return Text.translatable("fluid.tga.none");
        if (obj.isOf(Fluids.WATER)) return Text.translatable("fluid.tga.water");
        if (obj.isOf(Fluids.LAVA)) return Text.translatable("fluid.tga.lava");
        return Text.literal(obj.toString());
    }
}