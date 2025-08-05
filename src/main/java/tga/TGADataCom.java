package tga;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import tga.ComDat.BoxStackData;
import tga.ComDat.TankComData;
import tga.NetEvents.JinrikiGogo;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class TGADataCom {
    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, TotalGreedyAgent.GetID(name), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void Load() {
        BoxStackData.CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("m").forGetter((dat) -> dat.MaxStack),
                        ItemStack.CODEC.optionalFieldOf("i").forGetter((dat) -> dat.LockedType.isEmpty() ? Optional.empty() : Optional.of(dat.LockedType)),
                        Codec.INT.fieldOf("c").forGetter((dat) -> dat.Count)
                ).apply(instance, BoxStackData::new));
        TankComData.CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.INT.fieldOf("m").forGetter((dat) -> dat.MaxStack),
                        FluidVariant.CODEC.optionalFieldOf("f").forGetter((dat) -> dat.FType == null || dat.FType.isBlank() ? Optional.empty() : Optional.of(dat.FType)),
                        Codec.LONG.fieldOf("c").forGetter((dat) -> dat.Count)
                ).apply(instance, TankComData::new));
        BoxStackData.COMPONET_TYPE = register("boxstackdata", builder -> builder.codec(BoxStackData.CODEC));
        TankComData.COMPONET_TYPE = register("tankcomdata", builder -> builder.codec(TankComData.CODEC));

        ServerPlayNetworking.registerGlobalReceiver(
                JinrikiGogo.PAYLOAD_ID,
                (payload, context) -> {
                    context.server().execute(() -> payload.Handle(context));
                }
        );
    }
}