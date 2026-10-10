package dev.engine_room.flywheel.backend.gl;

import java.util.Set;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32;

import com.mojang.blaze3d.GraphicsWorkarounds;
import com.mojang.blaze3d.opengl.DirectStateAccess;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;

public final class RenderTargetCompat {
	private static DirectStateAccess directStateAccess;

	private RenderTargetCompat() {
	}

	public static int getDepthTextureId(RenderTarget target) {
		return GpuTextureCompat.glId(target.getDepthTexture());
	}

	public static void bindWrite(RenderTarget target) {
		GlTexture color = (GlTexture) target.getColorTexture();
		int fbo = color.getFbo(directStateAccess(), target.getDepthTexture());
		GlStateManager._glBindFramebuffer(GL32.GL_FRAMEBUFFER, fbo);
	}

	private static DirectStateAccess directStateAccess() {
		if (directStateAccess == null) {
			directStateAccess = DirectStateAccess.create(GL.getCapabilities(), Set.of(), GraphicsWorkarounds.get(RenderSystem.getDevice()));
		}
		return directStateAccess;
	}
}
