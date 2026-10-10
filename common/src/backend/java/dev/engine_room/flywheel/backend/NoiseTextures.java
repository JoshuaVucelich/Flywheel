package dev.engine_room.flywheel.backend;

import java.io.IOException;

import org.jetbrains.annotations.UnknownNullability;
import org.lwjgl.opengl.GL32;

import com.mojang.blaze3d.platform.NativeImage;

import dev.engine_room.flywheel.backend.gl.GlRenderCompat;
import dev.engine_room.flywheel.backend.gl.GlTextureUnit;
import dev.engine_room.flywheel.backend.gl.GpuTextureCompat;
import dev.engine_room.flywheel.lib.util.ResourceUtil;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

public class NoiseTextures {
	public static final Identifier NOISE_TEXTURE = ResourceUtil.rl("textures/flywheel/noise/blue.png");

	@UnknownNullability
	public static DynamicTexture BLUE_NOISE;

	public static void reload(ResourceManager manager) {
		if (BLUE_NOISE != null) {
			BLUE_NOISE.close();
			BLUE_NOISE = null;
		}
		var optional = manager.getResource(NOISE_TEXTURE);

		if (optional.isEmpty()) {
			return;
		}

		try (var is = optional.get()
				.open()) {
			var image = NativeImage.read(NativeImage.Format.LUMINANCE, is);

			BLUE_NOISE = new DynamicTexture(() -> "flywheel_blue_noise", image);

			GlTextureUnit.T0.makeActive();
			GpuTextureCompat.bind(BLUE_NOISE);

			GpuTextureCompat.setFilter(BLUE_NOISE, true, false);
			GlRenderCompat.texParameter(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_WRAP_S, GL32.GL_REPEAT);
			GlRenderCompat.texParameter(GL32.GL_TEXTURE_2D, GL32.GL_TEXTURE_WRAP_T, GL32.GL_REPEAT);

			GlRenderCompat.bindTexture(0);
		} catch (IOException e) {

		}
	}
}
