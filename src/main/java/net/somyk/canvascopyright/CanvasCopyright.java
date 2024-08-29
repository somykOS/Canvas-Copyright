package net.somyk.canvascopyright;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.somyk.canvascopyright.command.CanvasCommand;
import net.somyk.canvascopyright.util.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanvasCopyright implements ModInitializer {

    public static final String MOD_ID = "canvas-copyright";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModConfig.registerConfigs();
		CommandRegistrationCallback.EVENT.register(CanvasCommand::register);
	}
}