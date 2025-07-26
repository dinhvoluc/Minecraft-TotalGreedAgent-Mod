package tga;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import tga.Screen.BoxStackScreenHandler;

public class TGAScreenHandlers {
    public static ScreenHandlerType<BoxStackScreenHandler> BOX_STACK;

    public static void  Load(){
//        BOX_STACK = Registry.register(Registries.SCREEN_HANDLER,
//                TotalGreedyAgent.GetID("boxstackgui"),
//                new ScreenHandlerType<>(BoxStackScreenHandler::new)
//        );
    }
}