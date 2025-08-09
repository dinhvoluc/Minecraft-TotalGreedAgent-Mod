package tga;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

import java.util.List;

public class TGAHelper {
    // <editor-fold desc="Fluids">
    public static void WriteFluidType(WriteView view, String name, FluidVariant fType) {
        if (fType.isBlank()) return;
        view.put(name, FluidVariant.CODEC, fType);
    }

    public static String GetFluidName(FluidVariant fType) {
        Item fItem = fType.getFluid().getBucketItem();
        if (fItem == Items.AIR) return "";
        return fItem.getName().getString();
    }

    public static FluidVariant ReadFluidType(ReadView view, String name) {
        return view.read(name, FluidVariant.CODEC).orElse(FluidVariant.blank());
    }
    // </editor-fold>

    // <editor-fold desc="Items">
    public static void WriteItem(WriteView view, String name, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        view.put(name, ItemStack.CODEC, stack);
    }

    public static ItemStack ReadItem(ReadView view, String name){
        return view.read(name, ItemStack.CODEC).orElse(ItemStack.EMPTY);
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
    // </editor-fold>

    // <editor-fold desc="Player">
    public static boolean drainExperience(ServerPlayerEntity player, float amount) {
        float exp = player.experienceProgress;
        float tmp = exp - amount / player.getNextLevelExperience();
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
    // </editor-fold>

    // <editor-fold desc="Numbers">
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

    public static boolean InRangeXY(int posX, int posY, int x, int y, int w, int h) {
        return posX >= x && posY >= y && posX < x + w && posY < y + h;
    }

    public static String ToPercent(float v) {
        long fmt = (long) (v * 10000);
        long lefOver = fmt % 100;
        return lefOver < 10 ? ((fmt / 100) + ".0" + lefOver + "%") : ((fmt / 100) + "." + lefOver + "%");
    }

    public static String JinrikiToPower10String(long val) {
        if (val < 1_000_00) return String.valueOf(val / 100);
        if (val < 1_000_000_00) {
            val /= 1_000;
            long lefOver = val % 100;
            return lefOver < 10 ? ((val / 100) + ".0" + lefOver + "K") : ((val / 100) + "." + lefOver + "K");
        }
        if (val < 1_000_000_000_00L) {
            val /= 1_000_000;
            long lefOver = val % 100;
            return lefOver < 10 ? ((val / 100) + ".0" + lefOver + "M") : ((val / 100) + "." + lefOver + "M");
        }
        if (val < 1_000_000_000_000_00L) {
            val /= 1_000_000_000;
            long lefOver = val % 100;
            return lefOver < 10 ? ((val / 100) + ".0" + lefOver + "G") : ((val / 100) + "." + lefOver + "G");
        }
        val /= 1_000_000_000_000L;
        long lefOver = val % 100;
        return lefOver < 10 ? ((val / 100) + ".0" + lefOver + "T") : ((val / 100) + "." + lefOver + "T");
    }

    public static String ToFluid_mB(long rawVol) {
        long upper = rawVol / (FluidConstants.BUCKET / 1000);
        if (upper < 1000 && upper > -1000) return  String.valueOf(upper);
        StringBuilder rt = new StringBuilder();
        long sub = Math.abs(upper % 1000);
        upper /= 1000;
        while (upper != 0) {
            if (sub < 10) rt.insert(0, ",00" + sub);
            else if (sub < 100) rt.insert(0, ",0" + sub);
            else rt.insert(0, "," + sub);
            sub = Math.abs(upper % 1000);
            upper /= 1000;
        }
        rt.insert(0, sub);
        return rt.toString();
    }
    // </editor-fold>

    // <editor-fold desc="Collections">
    public static <T> T GetOrNull(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) return null;
        return list.get(index);
    }
    public static <T> T GetOrNull(T[] array, int index) {
        if (array == null || index < 0 || index >= array.length) return null;
        return array[index];
    }
    // </editor-fold>
}