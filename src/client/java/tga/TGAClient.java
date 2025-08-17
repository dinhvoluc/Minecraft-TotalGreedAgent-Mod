package tga;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import tga.BlockEntity.MachineTiles.ManCrackerTile;
import tga.BlockEntity.MachineTiles.MetalWorkbenchTile;
import tga.ClUpdate.JinrikiWork;
import tga.Screen.BoxStackScreen;
import tga.Screen.MachineCrackerScreen;
import tga.Screen.MetalWorkbenchScreen;
import tga.Screen.TankScreen;
import tga.TicksMng.ManMachineManager;
import tga.TicksMng.PipeManager;

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
			if (TotalGreedyAgent.TGA_CLIENT_UPDATE_GLOBAL_TICK > 0x7f_ff_ff_ef)
				TotalGreedyAgent.TGA_CLIENT_UPDATE_GLOBAL_TICK = 1;
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
		LOGGER.info("Client=>ExtraEvents");
		HudElementRegistry.addLast(TotalGreedyAgent.GetID("debug_info"), (context, tickCounter) -> {
			if (TotalGreedyAgent.SHOW_DEBUG == 0) return;
			MinecraftClient client = MinecraftClient.getInstance();
			TextRenderer textRenderer = client.textRenderer;
			context.drawText(textRenderer,
					"[TGA_Mng] M=" + ManMachineManager.SERVER_INTANCE.TurnUpdated + "+" + ManMachineManager.CLIENT_INTANCE.TurnUpdated +
							" P=" + PipeManager.INTANCE.TurnUpdated +
							" Tc=" + TotalGreedyAgent.TGA_CLIENT_UPDATE_GLOBAL_TICK + "/" + TotalGreedyAgent.TGA_SERVER_UPDATE_GLOBAL_TICK, 3, 3, 0xffffffff, true);
		});
		ClientCommandRegistrationCallback.EVENT.register(TotalGreedyAgent.GetID("debug_cmd"), (a, b) -> AddClientCmd(a,b));
		LOGGER.info("Client=>Init-Ended");
	}

	private void AddClientCmd(CommandDispatcher<FabricClientCommandSource> cd, CommandRegistryAccess cra) {
		cd.register(ClientCommandManager.literal("tga-db")
				.then(ClientCommandManager.literal("info")
						.executes(ctx -> {
							TotalGreedyAgent.SHOW_DEBUG = TotalGreedyAgent.SHOW_DEBUG == 0 ? 1 : 0;
							return 1;
						})));
	}
}