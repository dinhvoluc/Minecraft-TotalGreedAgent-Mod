package tga;

import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import tga.ComDat.BoxStackData;

public class TGAToolTips {
    public static void Load(){
        ComponentTooltipAppenderRegistry.addLast(BoxStackData.COMPONET_TYPE);
    }
}
