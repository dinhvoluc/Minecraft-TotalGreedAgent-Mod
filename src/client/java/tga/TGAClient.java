package tga;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.RenderLayer;
import tga.Screen.BoxStackScreen;

public class TGAClient extends TotalGreedyAgent implements ClientModInitializer {
	@Override
	public void onInitialize() {
		IsClientSize = true;
		super.onInitialize();
	}

	@Override
	public void onInitializeClient() {
		LOGGER.info("Client=>NetEvents");
		ClientNet.Load();
		LOGGER.info("Client=>Screens");
		HandledScreens.register(TGAScreenHandlers.BOX_STACK, BoxStackScreen::new);
		LOGGER.info("Client=>ToolTips");
		TGAToolTips.Load();
		LOGGER.info("Client=>RenderFix");
		BlockRenderLayerMap.putBlocks(BlockRenderLayer.CUTOUT, TGABlocks.CROP_GUAYULE_YONG, TGABlocks.CROP_GUAYULE);
		LOGGER.info("Client=>Init-Ended");
	}
}