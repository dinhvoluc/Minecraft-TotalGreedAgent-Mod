package tga;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import tga.NetEvents.BoxStackGuiSync;
import tga.NetEvents.ManCrackerGuiSync;
import tga.Screen.BoxStackScreenHandler;
import tga.Screen.MachineCrackerHandler;

public class TGAScreenHandlers {

    public static Identifier GUI_SHARE_0;
    public static ScreenHandlerType<BoxStackScreenHandler> BOX_STACK;
    public static ScreenHandlerType<MachineCrackerHandler> M_CRACKER_0;

    public static void Load() {
        //GUI
        GUI_SHARE_0 = TotalGreedyAgent.GetID("textures/gui/0.png");
        //Events reg
        TotalGreedyAgent.LOGGER.info("Server=>ScreenHandler:NetEvents");
        BoxStackGuiSync.Load();
        ManCrackerGuiSync.Load();
        //GUI reg
        TotalGreedyAgent.LOGGER.info("Server=>ScreenHandler:GUIHandler");
        BOX_STACK = Registry.register(Registries.SCREEN_HANDLER, TotalGreedyAgent.GetID("gui_boxstack"),
                new ExtendedScreenHandlerType<>(BoxStackScreenHandler::new, BlockPos.PACKET_CODEC));
        M_CRACKER_0 = Registry.register(Registries.SCREEN_HANDLER, TotalGreedyAgent.GetID("gui_mancracker"),
                new ExtendedScreenHandlerType<>(MachineCrackerHandler::new, BlockPos.PACKET_CODEC));
    }
}