package tga;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TotalGreedyAgent implements ModInitializer {
	public static final String MOD_ID = "tga";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitialize() {
		LOGGER.info("Server=>Init...");
		TGABlocks.register();
		LOGGER.info("Server=>Init-Ended");
	}
	public static Identifier GetID(String name) {
		return Identifier.of(TGARef.Mod_ID, name);
	}
}