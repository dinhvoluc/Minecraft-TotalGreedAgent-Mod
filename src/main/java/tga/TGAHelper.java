package tga;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import tga.Str.FFlSrc;

import java.util.*;

public class TGAHelper {
    // <editor-fold desc="Fluids">
    public static FFlSrc FindFluidSource(World world, BlockPos start, int maxRange) {
        FluidState target = world.getBlockState(start).getFluidState();
        if (target.isEmpty()) return null;
        return FindFluidSource(world, start, target, maxRange);
    }

    public static FFlSrc FindFluidSource(World world, BlockPos start, FluidState startState, int maxRange) {
        double radius = maxRange * maxRange;
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        //for search
        queue.add(start);
        visited.add(start);
        Fluid targetFluid = startState.getFluid();
        //finding
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            BlockState state = world.getBlockState(current);
            FluidState fs = state.getFluidState();
            if (targetFluid.matchesType(fs.getFluid())) {
                // 水源を発見
                if (fs.isStill()) return new FFlSrc(current, targetFluid, state);
                // 周囲を探索（水平4方向＋上）
                BlockPos up = current.up();
                if (!visited.contains(up) && start.getSquaredDistance(up) <= radius) {
                    visited.add(up);
                    queue.add(up);
                }
                BlockPos n = current.north();
                if (!visited.contains(n) && start.getSquaredDistance(n) <= radius) {
                    visited.add(n);
                    queue.add(n);
                }
                BlockPos s = current.south();
                if (!visited.contains(s) && start.getSquaredDistance(s) <= radius) {
                    visited.add(s);
                    queue.add(s);
                }
                BlockPos w = current.west();
                if (!visited.contains(w) && start.getSquaredDistance(w) <= radius) {
                    visited.add(w);
                    queue.add(w);
                }
                BlockPos e = current.east();
                if (!visited.contains(e) && start.getSquaredDistance(e) <= radius) {
                    visited.add(e);
                    queue.add(e);
                }
            }
        }
        return null;
    }

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
    public static void ReadStacks(ReadView view, String prefix, DefaultedList<ItemStack> heldStacks, int offset, int endIndex) {
        for (int i = offset; i < endIndex; i++)
            heldStacks.set(i, ReadItem(view, prefix + i));
    }

    public static void WriteStacks(WriteView view, String prefix, DefaultedList<ItemStack> heldStacks, int offset, int endIndex) {
        for (int i = offset; i < endIndex; i++)
            WriteItem(view, prefix + i, heldStacks.get(i));
    }

    public static ItemStack ItemStackTo(ItemStack item, ItemStack slot){
        if (slot.isEmpty()) return item.copy();
        if (!ItemStack.areItemsAndComponentsEqual(item, slot)) return slot;
        slot.increment(item.getCount());
        return slot;
    }

    public static boolean ItemCanStackTo(ItemStack item, ItemStack slot){
        if (slot.isEmpty()) return true;
        if (!ItemStack.areItemsAndComponentsEqual(item, slot)) return false;
        return item.getCount() + slot.getCount() <= slot.getMaxCount();
    }

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

    public static Storage<FluidVariant> GetFluidAttachedFC(World world, BlockPos pos, Direction dir) {
       return switch (dir) {
            case Direction.NORTH -> FluidStorage.SIDED.find(world, pos.north(), Direction.SOUTH);
            case Direction.EAST -> FluidStorage.SIDED.find(world, pos.east(), Direction.WEST);
            case Direction.SOUTH -> FluidStorage.SIDED.find(world, pos.south(), Direction.NORTH);
            case Direction.WEST -> FluidStorage.SIDED.find(world, pos.west(), Direction.EAST);
            case Direction.UP -> FluidStorage.SIDED.find(world, pos.up(), Direction.DOWN);
           case Direction.DOWN -> FluidStorage.SIDED.find(world, pos.down(), Direction.UP);
        };
    }
    // </editor-fold>
}