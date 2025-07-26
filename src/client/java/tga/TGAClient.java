package tga;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import tga.Screen.BoxStackScreen;

public class TGAClient extends TotalGreedyAgent implements ClientModInitializer {
	@Override
	public void onInitialize() {
		IsClientSize = true;
		super.onInitialize();
	}

	@Override
	public void onInitializeClient() {
		LOGGER.info("Client=>LoadScreen");
		HandledScreens.register(TGAScreenHandlers.BOX_STACK, BoxStackScreen::new);
		LOGGER.info("Client=>ToolTips");
		TGAToolTips.Load();
		LOGGER.info("Client=>Init-Ended");
	}
}