package tga;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TotalGreedyAgent implements ModInitializer {
	public static int TGA_SERVER_UPDATE_GLOBAL_TICK;
	public static int TGA_CLIENT_UPDATE_GLOBAL_TICK;
	public static final String MOD_ID = "tga";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean IsClientSize = false;
	public static MinecraftServer SERVER;
	public static int SHOW_DEBUG = 1;

    @Override
	public void onInitialize() {
		LOGGER.info("Server=>Init...");
		ServerLifecycleEvents.SERVER_STARTED.register(s -> {
			SERVER = s;
			TGA_SERVER_UPDATE_GLOBAL_TICK = 1;
		});
		ServerLifecycleEvents.SERVER_STOPPED.register(s -> SERVER = null);
		LOGGER.info("Server=>Items");
		TGAItems.Load(IsClientSize);
		LOGGER.info("Server=>Blocks");
		TGABlocks.Load(IsClientSize);
		LOGGER.info("Server=>ElectricBlocks");
		TGAElectricBlocks.Load(IsClientSize);
		LOGGER.info("Server=>Creative list");
		CreativeTab.Load();
		LOGGER.info("Server=>BLocks logics");
		TGATileEnities.Load(IsClientSize);
		LOGGER.info("Server=>Sounds");
		TGASounds.Load();
		LOGGER.info("Server=>GuiHandler");
		TGAScreenHandlers.Load();
		LOGGER.info("Server=>Custom data");
		TGADataCom.Load();
		LOGGER.info("Server=>WorkBook");
		TGARecipes.Load();
		LOGGER.info("Server=>Init-Ended");
	}

	public static Text GetGuiLang(String name) {
		return Text.translatable("gui.tga." + name + ".title");
	}

	public static Identifier GetID(String name) {
		return Identifier.of(TGARef.Mod_ID, name);
	}

	public static void writeInfo(String message, Object... ars) {
		LOGGER.info(ars.length == 0 ? message : String.format(message, ars));
	}

	public static void broadcastDebugMessageF(String message, Object... ars) {
		broadcastDebugMessage(ars.length == 0 ? message : String.format(message, ars), false);
	}

	public static void broadcastDebugMessage(String message, boolean overlay) {
		if (SERVER == null) return;
		for (ServerPlayerEntity player : SERVER.getPlayerManager().getPlayerList()) {
			player.sendMessage(Text.literal("[D] " + message), overlay);
		}
	}
}