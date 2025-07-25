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
		LOGGER.info("Server=>Items");
		TGAItems.Load();
		LOGGER.info("Server=>Blocks");
		TGABlocks.Load();
		LOGGER.info("Server=>Creative items list");
		CreativeTab.Load();
		LOGGER.info("Server=>BLocks inside logics");
		TGATileEnities.Load();
		LOGGER.info("Server=>Init-Ended");
	}
	public static Identifier GetID(String name) {
		return Identifier.of(TGARef.Mod_ID, name);
	}
}