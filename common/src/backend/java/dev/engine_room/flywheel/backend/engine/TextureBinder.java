package dev.engine_room.flywheel.backend.engine;

import dev.engine_room.flywheel.backend.Samplers;
import dev.engine_room.flywheel.backend.gl.GpuTextureCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class TextureBinder {
	public static void bind(Identifier resourceLocation) {
		GpuTextureCompat.bind(Minecraft.getInstance()
				.getTextureManager()
				.getTexture(resourceLocation));
	}

	public static void bindLightAndOverlay() {
		var gameRenderer = Minecraft.getInstance().gameRenderer;

		Samplers.OVERLAY.makeActive();
		GpuTextureCompat.bind(gameRenderer.overlayTexture().getTextureView());

		Samplers.LIGHT.makeActive();
		GpuTextureCompat.bind(gameRenderer.lightmap());
	}

	public static void resetLightAndOverlay() {
		// Light and overlay bindings are managed by the vanilla render pipeline in 26.1.
	}
}
