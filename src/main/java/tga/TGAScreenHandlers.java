package tga;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import tga.Screen.BoxStackScreenHandler;

public class TGAScreenHandlers {

    public static Identifier GUI_SHARE_0;
    public static ScreenHandlerType<BoxStackScreenHandler> BOX_STACK;

    public static void Load() {
        GUI_SHARE_0 = TotalGreedyAgent.GetID("textures/gui/0.png");
        //BOX_STACK = register("gui_boxstack", BoxStackScreenHandler::new, BlockPos.PACKET_CODEC);
        BOX_STACK = Registry.register(Registries.SCREEN_HANDLER, TotalGreedyAgent.GetID("gui_boxstack"),
                new ExtendedScreenHandlerType<>(BoxStackScreenHandler::new, BlockPos.PACKET_CODEC));
    }
}