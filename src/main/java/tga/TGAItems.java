package tga;

import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public final class TGAItems {
    public static Item RUBBER;
    public static Item REISIN;
    public static Item COPPER_PLATE;
    public static Item NAILS;
    public static Item CROP_GUAYULE_SEED;
    public static Item BUCKET_VINEGAR;

    public static void Load(boolean isClientSide) {
        SetBioBurnTime(RUBBER = register("rubber", Item::new, new Item.Settings()), 0.1f, 200);
        SetBioBurnTime(REISIN = register("resin", Item::new, new Item.Settings()), 0.2f, 100);
        COPPER_PLATE = register("plate_copper", Item::new, new Item.Settings());
        NAILS = register("nails", Item::new, new Item.Settings());
        Item.Settings bucket_vinegar = new Item.Settings();
        bucket_vinegar.maxCount(4);
        BUCKET_VINEGAR = register("bucket_vinegar", Item::new, bucket_vinegar);
        //SetBioBurnTime(CROP_GUAYULE_SEED = register(TotalGreedyAgent.GetSeedID(GuayuleCrop.MC_ID), Item::new, new Item.Settings()), 0.1f, 30);
    }

    public static void SetBioValue(Item item, float rate) {
        CompostingChanceRegistry.INSTANCE.add(item, rate);
    }

    public static void SetBioBurnTime(Item item, float bioRate, int burnTicks) {
        CompostingChanceRegistry.INSTANCE.add(item, bioRate);
        FuelRegistryEvents.BUILD.register((builder, context) -> {
            builder.add(item, burnTicks);
        });
    }

    public static void SetBurnTime(Item item, int ticks) {
        FuelRegistryEvents.BUILD.register((builder, context) -> {
            builder.add(item, ticks);
        });
    }

    private static Item register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final Identifier identifier = TotalGreedyAgent.GetID(path);
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, identifier);
        return Items.register(registryKey, factory, settings);
    }
}