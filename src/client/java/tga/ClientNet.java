package tga;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import tga.NetEvents.*;
import tga.Screen.*;

public class ClientNet {
    public static void Load() {
        ClientPlayNetworking.registerGlobalReceiver(
                BoxStackGuiSync.PAYLOAD_ID,
                (payload, context) -> {
                    context.client().execute(() ->BoxStackScreen.HandleSync(payload));
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(
                TankGuiSync.PAYLOAD_ID,
                (payload, context) -> {
                    context.client().execute(() -> TankScreen.HandleSync(payload));
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(
                ManCrackerGuiSync.PAYLOAD_ID,
                (payload, context) -> {
                    context.client().execute(() -> MachineCrackerScreen.HandleSync(payload));
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(
                MetalWorkbenchGuiSync.PAYLOAD_ID,
                (payload, context) -> {
                    context.client().execute(() -> MetalWorkbenchScreen.HandleSync(payload));
                }
        );
    }
}
