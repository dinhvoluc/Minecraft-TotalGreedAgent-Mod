package tga;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayer;
import tga.Machines.ManCracker;
import tga.Screen.BoxStackScreen;
import tga.Screen.MachineCrackerScreen;

public class TGAClient extends TotalGreedyAgent implements ClientModInitializer {
	@Override
	public void onInitialize() {
		IsClientSize = true;
		super.onInitialize();
	}

	@Override
	public void onInitializeClient() {
		LOGGER.info("Client=>LocalTicker");
		ManCracker.TICKER_CLIENT = (a,b,c,d) -> TGAClientTickers.ManCrakerTick(d);
		LOGGER.info("Client=>NetEvents");
		ClientNet.Load();
		LOGGER.info("Client=>Screens");
		HandledScreens.register(TGAScreenHandlers.BOX_STACK, BoxStackScreen::new);
		HandledScreens.register(TGAScreenHandlers.M_CRACKER_0, MachineCrackerScreen::new);
		LOGGER.info("Client=>ToolTips");
		TGAToolTips.Load();
		LOGGER.info("Client=>RenderFix");
		BlockRenderLayerMap.putBlocks(BlockRenderLayer.CUTOUT, TGABlocks.CROP_GUAYULE_YONG, TGABlocks.CROP_GUAYULE);
		LOGGER.info("Client=>Init-Ended");
	}
}