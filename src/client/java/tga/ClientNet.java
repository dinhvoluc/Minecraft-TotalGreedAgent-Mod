package tga;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import tga.NetEvents.BoxStackGuiSync;
import tga.Screen.BoxStackScreen;

public class ClientNet {
    public static void Load() {
        ClientPlayNetworking.registerGlobalReceiver(
                BoxStackGuiSync.PAYLOAD_ID,
                (payload, context) -> {
                    context.client().execute(() ->BoxStackScreen.HandleSync(payload));
                }
        );
    }
}
