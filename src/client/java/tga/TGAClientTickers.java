package tga;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import tga.BlockEntity.ManCrackerTile;
import tga.BlockEntity.MetalWorkbenchTile;
import tga.NetEvents.JinrikiGogo;
import tga.Screen.MachineCrackerHandler;
import tga.Screen.MetalWorkbenchHandler;

public class TGAClientTickers {
    public static void ManCrakerTick(ManCrackerTile target) {
        if (MachineCrackerHandler.LastWorkBlock != target) return;
        if (++target.Jinriki > 20) {
            target.Jinriki = 0;
            ClientPlayNetworking.send(new JinrikiGogo(target.getPos()));
        }
    }
    public static void MetalWorkbenchTick(MetalWorkbenchTile target) {
        if (MetalWorkbenchHandler.LastWorkBlock != target) return;
        if (++target.Jinriki > 20) {
            target.Jinriki = 0;
            ClientPlayNetworking.send(new JinrikiGogo(target.getPos()));
        }
    }
}
