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
	public static final String MOD_ID = "tga";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean IsClientSize = false;
	public static MinecraftServer SERVER;

    @Override
	public void onInitialize() {
		LOGGER.info("Server=>Init...");
		ServerLifecycleEvents.SERVER_STARTED.register(s -> SERVER = s);
		ServerLifecycleEvents.SERVER_STOPPED.register(s -> SERVER = null);
		LOGGER.info("Server=>Items");
		TGAItems.Load(IsClientSize);
		LOGGER.info("Server=>Blocks");
		TGABlocks.Load(IsClientSize);
		LOGGER.info("Server=>Creative list");
		CreativeTab.Load();
		LOGGER.info("Server=>BLocks logics");
		TGATileEnities.Load(IsClientSize);
		TGAScreenHandlers.Load();
		LOGGER.info("Server=>Custom data");
		TGADataCom.Load();
		LOGGER.info("Server=>Init-Ended");
	}

	public static String GetGuiLang(String name) {
		return "gui.tga." + name + ".title";
	}

	public static Identifier GetID(String name) {
		return Identifier.of(TGARef.Mod_ID, name);
	}

	public static void writeInfo(String message, Object... ars) {
		LOGGER.info(ars.length == 0 ? message : String.format(message, ars));
	}

	public static void broadcastDebugMessage(String message) {
		broadcastDebugMessage(message, false);
	}

	public static void broadcastDebugMessage(String message, boolean overlay) {
		if (SERVER == null) return;
		for (ServerPlayerEntity player : SERVER.getPlayerManager().getPlayerList()) {
			player.sendMessage(Text.literal("[Debug] " + message), overlay);
		}
	}
}