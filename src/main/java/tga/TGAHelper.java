package tga;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import tga.Items.CraftOutputPatch;
import tga.MachineRecipes.OneInRecipe;

import java.util.List;

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

    public static <T> T GetOrNull(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) return null;
        return list.get(index);
    }
    public static <T> T GetOrNull(T[] array, int index) {
        if (array == null || index < 0 || index >= array.length) return null;
        return array[index];
    }

    public static boolean GUI_ButtonInrange(int bx, int by, int mouseX, int mouseY) {
        return  mouseX > bx && mouseY > by && mouseX < bx + 16 && mouseY < by + 16;
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
}