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
    public static Item CACO3;
    public static Item BOW_PRE_ACETONE;
    public static Item BOW_ACETONE;
    public static Item TREE_WASTE;
    public static Item DUST_COPPER;
    public static Item DUST_TIN;
    public static Item DUST_BRONZE;
    public static Item INGOT_TIN;
    public static Item INGOT_BRONZE;

    public static Item GUAYULE_DUST;
    public static Item CROP_GUAYULE_GRASS;

    public static Item WHEAT_FLOUR;
    public static Item BREAD_DOUGH;

    public static Item PART_BRONZE_TANK;
    public static Item PART_BRONZE_BOX;
    public static Item PART_BRONZE_TURBIN;
    public static Item PART_BRONZE_BOIL;

    public static void Load(boolean isClientSide) {
        SetBurnTime(RUBBER = Register("rubber", Item::new, new Item.Settings()), 2000);
        SetBioBurnTime(REISIN = Register("resin", Item::new, new Item.Settings()), 0.2f, 1000);
        COPPER_PLATE = Register("plate_copper", Item::new, new Item.Settings());
        NAILS = Register("nails", Item::new, new Item.Settings());
        CACO3 = Register("c_caco3", Item::new, new Item.Settings());
        BOW_PRE_ACETONE = Register("c_caco3_vinegar", Item::new, new Item.Settings());
        SetBurnTime(BOW_ACETONE = Register("bow_acetone", Item::new, new Item.Settings()), 800);
        SetBioBurnTime(CROP_GUAYULE_GRASS = Register("guayule_grass", Item::new, new Item.Settings()), 0.1f, 50);
        SetBioBurnTime(GUAYULE_DUST = Register("guayule_dust", Item::new, new Item.Settings()), 0.8f, 400);
        SetBioBurnTime(TREE_WASTE = Register("treewaste", Item::new, new Item.Settings()), 0.1f, 45);
        DUST_COPPER = Register("d_copper", Item::new, new Item.Settings());
        DUST_TIN = Register("d_tin", Item::new, new Item.Settings());
        DUST_BRONZE = Register("d_bronze", Item::new, new Item.Settings());
        INGOT_TIN = Register("ingot_tin", Item::new, new Item.Settings());
        INGOT_BRONZE = Register("ingot_bronze", Item::new, new Item.Settings());

        SetBurnTime(WHEAT_FLOUR = Register("wheatflour", Item::new, new Item.Settings()), 50);
        BREAD_DOUGH = Register("dough", Item::new, new Item.Settings());


        //BRONZE PARTS
        PART_BRONZE_TANK = Register("p_tank_bronze", Item::new, new Item.Settings());
        PART_BRONZE_BOX = Register("p_box_bronze", Item::new, new Item.Settings());
        PART_BRONZE_TURBIN = Register("p_turbin_bronze", Item::new, new Item.Settings());
        PART_BRONZE_BOIL = Register("p_boil_bronze", Item::new, new Item.Settings());
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

    private static Item Register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final Identifier identifier = TotalGreedyAgent.GetID(path);
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, identifier);
        return Items.register(registryKey, factory, settings);
    }
}