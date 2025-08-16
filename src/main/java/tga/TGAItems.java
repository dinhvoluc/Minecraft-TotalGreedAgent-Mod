package tga;

import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import tga.Items.EFItemTank;

import java.util.function.Function;

public final class TGAItems {
    public static Item RUBBER;
    public static Item REISIN;

    public static Item CACO3;
    public static Item BOW_PRE_ACETONE;
    public static Item BOW_ACETONE;
    public static Item TREE_WASTE;

    public static Item DUST_TIN;
    public static Item DUST_COPPER;
    public static Item DUST_BRONZE;
    public static Item DUST_ALUMINUM;
    public static Item DUST_GOLD;
    public static Item DUST_SILVER;
    public static Item DUST_IRON;
    public static Item DUST_STEEL;
    public static Item DUST_TITAN;

    public static Item INGOT_TIN;
    public static Item INGOT_BRONZE;
    public static Item INGOT_ALUMIUM;
    public static Item INGOT_SILVER;
    public static Item INGOT_STEEL;
    public static Item INGOT_TITAN;

    public static Item PLATE_COPPER;
    public static Item PLATE_BRONZE;
    public static Item PLATE_ALUMINUM;
    public static Item PLATE_GOLD;
    public static Item PLATE_IRON;
    public static Item PLATE_STEEL;
    public static Item PLATE_TITAN;

    public static Item GUAYULE_DUST;
    public static Item CROP_GUAYULE_GRASS;

    public static Item WHEAT_FLOUR;
    public static Item BREAD_DOUGH;

    public static Item NAILS;
    public static Item SCREW;
    public static Item PART_BRONZE_TANK;
    public static Item PART_BRONZE_BOX;
    public static Item PART_BRONZE_TURBIN;
    public static Item PART_BRONZE_BOIL;

    public static Item BOX_WOOD;
    public static Item BOX_COPPER;
    public static Item BOX_BRONZE;

    public static Item BOX_WOOD_FILLED;
    public static Item BOX_COPPER_FILLED;
    public static Item BOX_BRONZE_FILLED;

    public static EFItemTank TANK_WOOD;
    public static EFItemTank TANK_COPPER;
    public static EFItemTank TANK_BRONZE;

    public static EFItemTank TANK_WOOD_FILLED;
    public static EFItemTank TANK_COPPER_FILLED;
    public static EFItemTank TANK_BRONZE_FILLED;

    public static EFItemTank PIPE_HOPPER;
    public static EFItemTank PIPE_HOPPER_FILLED;

    public static EFItemTank JRK_PUMP;
    public static EFItemTank JRK_PUMP_FILLED;

    public static void Load(boolean isClientSide) {
        SetBurnTime(RUBBER = Register("rubber"), 2000);
        SetBioBurnTime(REISIN = Register("resin"), 0.2f, 1000);

        CACO3 = Register("c_caco3");
        BOW_PRE_ACETONE = Register("c_caco3_vinegar");
        SetBurnTime(BOW_ACETONE = Register("bow_acetone"), 800);
        SetBioBurnTime(CROP_GUAYULE_GRASS = Register("guayule_grass"), 0.1f, 50);
        SetBioBurnTime(GUAYULE_DUST = Register("guayule_dust"), 0.8f, 400);
        SetBioBurnTime(TREE_WASTE = Register("treewaste"), 0.1f, 45);

        DUST_TIN = Register("d_tin");
        DUST_COPPER = Register("d_copper");
        DUST_BRONZE = Register("d_bronze");
        DUST_ALUMINUM = Register("d_alumium");
        DUST_GOLD = Register("d_gold");
        DUST_SILVER = Register("d_silver");
        DUST_IRON = Register("d_iron");
        DUST_STEEL = Register("d_steel");
        DUST_TITAN = Register("d_titan");

        INGOT_TIN = Register("ingot_tin");
        INGOT_BRONZE = Register("ingot_bronze");
        INGOT_ALUMIUM = Register("ingot_alumium");
        INGOT_SILVER = Register("ingot_silver");
        INGOT_STEEL = Register("ingot_steel");
        INGOT_TITAN = Register("ingot_titan");

        PLATE_COPPER = Register("plate_copper");
        PLATE_BRONZE = Register("plate_bronze");
        PLATE_ALUMINUM = Register("plate_alumium");
        PLATE_GOLD = Register("plate_gold");
        PLATE_IRON = Register("plate_iron");
        PLATE_STEEL = Register("plate_steel");
        PLATE_TITAN = Register("plate_titan");

        SetBurnTime(WHEAT_FLOUR = Register("wheatflour"), 50);
        BREAD_DOUGH = Register("dough");

        //IRON PARTS
        NAILS = Register("nails");
        SCREW = Register("screw");

        //BRONZE PARTS
        PART_BRONZE_TANK = Register("p_tank_bronze");
        PART_BRONZE_BOX = Register("p_box_bronze");
        PART_BRONZE_TURBIN = Register("p_turbin_bronze");
        PART_BRONZE_BOIL = Register("p_boil_bronze");
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

    public static Item Register(int maxStack, String path) {
        return Register(path, Item::new, new Item.Settings().maxCount(1));
    }

    public static Item Register(String path) {
        return Register(path, Item::new, new Item.Settings());
    }

    public static Item Register(String path, Function<Item.Settings, Item> factory, Item.Settings settings) {
        return Register(TotalGreedyAgent.GetID(path), factory, settings);
    }
    public static Item Register(Identifier id, Function<Item.Settings, Item> factory, Item.Settings settings) {
        final RegistryKey<Item> registryKey = RegistryKey.of(RegistryKeys.ITEM, id);
        return Items.register(registryKey, factory, settings);
    }
}