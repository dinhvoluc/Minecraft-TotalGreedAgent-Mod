package tga;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import tga.Screen.BoxStackScreenHandler;

public class TGAScreenHandlers {

    public static Identifier GUI_SHARE_0;
    public static ScreenHandlerType<BoxStackScreenHandler> BOX_STACK;

    public static void  Load(){
        GUI_SHARE_0 = TotalGreedyAgent.GetID("textures/gui/0.png");
//        BOX_STACK = Registry.register(Registries.SCREEN_HANDLER,
//                TotalGreedyAgent.GetID("boxstackgui"),
//                new ScreenHandlerType<>(BoxStackScreenHandler::new)
//        );
    }
}