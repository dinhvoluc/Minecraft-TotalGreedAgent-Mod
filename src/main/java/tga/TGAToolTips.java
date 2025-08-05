package tga;

import net.fabricmc.fabric.api.item.v1.ComponentTooltipAppenderRegistry;
import tga.ComDat.BoxStackData;
import tga.ComDat.TankComData;

public class TGAToolTips {
    public static void Load(){
        ComponentTooltipAppenderRegistry.addLast(BoxStackData.COMPONET_TYPE);
        ComponentTooltipAppenderRegistry.addLast(TankComData.COMPONET_TYPE);
    }
}
