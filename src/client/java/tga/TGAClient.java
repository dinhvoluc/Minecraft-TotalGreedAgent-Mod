package tga;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;
import tga.BlockEntity.MachineTiles.ManCrackerTile;
import tga.BlockEntity.MachineTiles.MetalWorkbenchTile;
import tga.ClUpdate.JinrikiWork;
import tga.TicksMng.ManMachineManager;
import tga.Screen.BoxStackScreen;
import tga.Screen.MachineCrackerScreen;
import tga.Screen.MetalWorkbenchScreen;
import tga.Screen.TankScreen;

public class TGAClient extends TotalGreedyAgent implements ClientModInitializer {
	@Override
	public void onInitialize() {
		IsClientSize = true;
		super.onInitialize();
	}

	@Override
	public void onInitializeClient() {
		LOGGER.info("Client=>LocalTicker");
		//Ticker
		ManCrackerTile.TICKER_BUILDER_CLIENT = JinrikiWork::new;
		MetalWorkbenchTile.TICKER_BUILDER_CLIENT = JinrikiWork::new;
		//Manager
		ManMachineManager.CLIENT_INTANCE = new ManMachineManager();
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (TotalGreedyAgent.TGA_CLIENT_UPDATE_GLOBAL_TICK > 0x7f_ff_ff_ef) TotalGreedyAgent.TGA_CLIENT_UPDATE_GLOBAL_TICK = 1;
			else TotalGreedyAgent.TGA_CLIENT_UPDATE_GLOBAL_TICK++;
			ManMachineManager.CLIENT_INTANCE.Ticking();
		});
		ClientPlayConnectionEvents.DISCONNECT.register((a,b) -> {
			ManMachineManager.CLIENT_INTANCE.ClearQueue();
		});
		LOGGER.info("Client=>NetEvents");
		ClientNet.Load();
		LOGGER.info("Client=>Screens");
		HandledScreens.register(TGAScreenHandlers.BOX_STACK, BoxStackScreen::new);
		HandledScreens.register(TGAScreenHandlers.M_CRACKER_0, MachineCrackerScreen::new);
		HandledScreens.register(TGAScreenHandlers.METAL_WORKBENCH, MetalWorkbenchScreen::new);
		HandledScreens.register(TGAScreenHandlers.TANK_GUI, TankScreen::new);
		LOGGER.info("Client=>ToolTips");
		TGAToolTips.Load();
		LOGGER.info("Client=>RenderFix");
		BlockRenderLayerMap.putBlocks(BlockRenderLayer.CUTOUT, TGABlocks.CROP_GUAYULE_YONG, TGABlocks.X_CROP_GUAYULE);
		LOGGER.info("Client=>Init-Ended");
	}
}