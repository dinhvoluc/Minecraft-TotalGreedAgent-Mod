package tga;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

import java.util.Random;

public class TGAHelper {
    public static Random Rnd = new Random(System.nanoTime());
    public static void WriteItem(WriteView view, String name, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        view.put(name, ItemStack.CODEC, stack);
    }
    public static ItemStack ReadItem(ReadView view, String name){
        return   view.read(name, ItemStack.CODEC).orElse(ItemStack.EMPTY);
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
}