package tga;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import tga.NetEvents.BoxStackGuiSync;
import tga.NetEvents.ManCrackerGuiSync;
import tga.Screen.BoxStackScreen;
import tga.Screen.MachineCrackerScreen;

public class ClientNet {
    public static void Load() {
        ClientPlayNetworking.registerGlobalReceiver(
                BoxStackGuiSync.PAYLOAD_ID,
                (payload, context) -> {
                    context.client().execute(() ->BoxStackScreen.HandleSync(payload));
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(
                ManCrackerGuiSync.PAYLOAD_ID,
                (payload, context) -> {
                    context.client().execute(() -> MachineCrackerScreen.HandleSync(payload));
                }
        );
    }
}
