package tga;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import tga.NetEvents.*;
import tga.Screen.BoxStackScreenHandler;
import tga.Screen.MachineCrackerHandler;
import tga.Screen.MetalWorkbenchHandler;
import tga.Screen.TankScreenHandler;

public class TGAScreenHandlers {

    public static Identifier GUI_SHARE_0;
    public static ScreenHandlerType<BoxStackScreenHandler> BOX_STACK;
    public static ScreenHandlerType<TankScreenHandler> TANK_GUI;
    public static ScreenHandlerType<MachineCrackerHandler> M_CRACKER_0;
    public static ScreenHandlerType<MetalWorkbenchHandler> METAL_WORKBENCH;

    public static void Load() {
        //GUI
        GUI_SHARE_0 = TotalGreedyAgent.GetID("textures/gui/0.png");
        //Events reg
        TotalGreedyAgent.LOGGER.info("Server=>ScreenHandler:NetEvents");
        //Storage
        BoxStackGuiSync.Load();
        TankGuiSync.Load();
        //Power
        JinrikiGogo.Load();
        //Machine
        ManCrackerGuiSync.Load();
        MetalWorkbenchGuiSync.Load();
        //GUI reg
        ClickedIDSync.Load();
        TotalGreedyAgent.LOGGER.info("Server=>ScreenHandler:GUIHandler");
        BOX_STACK = Registry.register(Registries.SCREEN_HANDLER, TotalGreedyAgent.GetID("gui_boxstack"),
                new ExtendedScreenHandlerType<>(BoxStackScreenHandler::new, BlockPos.PACKET_CODEC));
        TANK_GUI = Registry.register(Registries.SCREEN_HANDLER, TotalGreedyAgent.GetID("gui_tank"),
                new ExtendedScreenHandlerType<>(TankScreenHandler::new, BlockPos.PACKET_CODEC));
        M_CRACKER_0 = Registry.register(Registries.SCREEN_HANDLER, TotalGreedyAgent.GetID("gui_mancracker"),
                new ExtendedScreenHandlerType<>(MachineCrackerHandler::new, BlockPos.PACKET_CODEC));
        METAL_WORKBENCH = Registry.register(Registries.SCREEN_HANDLER, TotalGreedyAgent.GetID("gui_metalwb"),
                new ExtendedScreenHandlerType<>(MetalWorkbenchHandler::new, BlockPos.PACKET_CODEC));
    }
}