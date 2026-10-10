package dev.engine_room.flywheel.backend.gl;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.renderer.texture.AbstractTexture;

public final class GpuTextureCompat {
	private GpuTextureCompat() {
	}

	public static int glId(AbstractTexture texture) {
		return glId(texture.getTexture());
	}

	public static int glId(GpuTextureView view) {
		return glId(view.texture());
	}

	public static int glId(GpuTexture texture) {
		if (texture instanceof GlTexture glTexture) {
			return glTexture.glId();
		}
		throw new IllegalStateException("Expected an OpenGL-backed texture, got " + texture.getClass().getName());
	}

	public static void bind(AbstractTexture texture) {
		bind(texture.getTexture());
	}

	public static void bind(GpuTextureView view) {
		bind(view.texture());
	}

	public static void bind(GpuTexture texture) {
		GlRenderCompat.bindTexture(glId(texture));
	}

	public static void setFilter(AbstractTexture texture, boolean blur, boolean mipmap) {
		int minFilter = mipmap ? (blur ? GL11.GL_LINEAR_MIPMAP_LINEAR : GL11.GL_NEAREST_MIPMAP_LINEAR) : (blur ? GL11.GL_LINEAR : GL11.GL_NEAREST);
		int magFilter = blur ? GL11.GL_LINEAR : GL11.GL_NEAREST;
		int id = glId(texture);
		GlRenderCompat.bindTexture(id);
		GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, minFilter);
		GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, magFilter);
	}
}
