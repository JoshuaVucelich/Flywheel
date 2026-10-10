package dev.engine_room.flywheel.backend.gl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;

/**
 * Compatibility shims for GL state methods removed from {@link com.mojang.blaze3d.systems.RenderSystem} in MC 26.1.
 */
public final class GlRenderCompat {
	private GlRenderCompat() {
	}

	public static void enableBlend() {
		GlStateManager._enableBlend();
	}

	public static void disableBlend() {
		GlStateManager._disableBlend();
	}

	public static void defaultBlendFunc() {
		blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
	}

	public static void blendFunc(SourceFactor src, DestFactor dst) {
		GlStateManager._blendFuncSeparate(toGl(src), toGl(dst), GL11.GL_ONE, GL11.GL_ZERO);
	}

	public static void blendFuncSeparate(SourceFactor srcColor, DestFactor dstColor, SourceFactor srcAlpha, DestFactor dstAlpha) {
		GlStateManager._blendFuncSeparate(toGl(srcColor), toGl(dstColor), toGl(srcAlpha), toGl(dstAlpha));
	}

	public static void blendEquation(int mode) {
		GL14.glBlendEquation(mode);
	}

	public static void enableCull() {
		GlStateManager._enableCull();
	}

	public static void disableCull() {
		GlStateManager._disableCull();
	}

	public static void enableDepthTest() {
		GlStateManager._enableDepthTest();
	}

	public static void disableDepthTest() {
		GlStateManager._disableDepthTest();
	}

	public static void depthFunc(int func) {
		GlStateManager._depthFunc(func);
	}

	public static void depthMask(boolean mask) {
		GlStateManager._depthMask(mask);
	}

	public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		int mask = (red ? 1 : 0) | (green ? 2 : 0) | (blue ? 4 : 0) | (alpha ? 8 : 0);
		GlStateManager._colorMask(mask);
	}

	public static void polygonOffset(float factor, float units) {
		GlStateManager._polygonOffset(factor, units);
	}

	public static void enablePolygonOffset() {
		GlStateManager._enablePolygonOffset();
	}

	public static void disablePolygonOffset() {
		GlStateManager._disablePolygonOffset();
	}

	public static void bindTexture(int id) {
		GlStateManager._bindTexture(id);
	}

	public static void texParameter(int target, int pname, int param) {
		GlStateManager._texParameter(target, pname, param);
	}

	public static void clearColor(float r, float g, float b, float a) {
		GL11.glClearColor(r, g, b, a);
	}

	public static void clear(int mask, boolean ignoredDepthFlag) {
		GlStateManager._clear(mask);
	}

	private static int toGl(SourceFactor factor) {
		return switch (factor) {
			case ZERO -> GL11.GL_ZERO;
			case ONE -> GL11.GL_ONE;
			case SRC_COLOR -> GL11.GL_SRC_COLOR;
			case SRC_ALPHA -> GL11.GL_SRC_ALPHA;
			case DST_COLOR -> GL11.GL_DST_COLOR;
			case DST_ALPHA -> GL11.GL_DST_ALPHA;
			case ONE_MINUS_SRC_COLOR -> GL11.GL_ONE_MINUS_SRC_COLOR;
			case ONE_MINUS_SRC_ALPHA -> GL11.GL_ONE_MINUS_SRC_ALPHA;
			case ONE_MINUS_DST_COLOR -> GL11.GL_ONE_MINUS_DST_COLOR;
			case ONE_MINUS_DST_ALPHA -> GL11.GL_ONE_MINUS_DST_ALPHA;
			case SRC_ALPHA_SATURATE -> GL11.GL_SRC_ALPHA_SATURATE;
			case CONSTANT_COLOR, CONSTANT_ALPHA, ONE_MINUS_CONSTANT_COLOR, ONE_MINUS_CONSTANT_ALPHA -> GL11.GL_ONE;
		};
	}

	private static int toGl(DestFactor factor) {
		return switch (factor) {
			case ZERO -> GL11.GL_ZERO;
			case ONE -> GL11.GL_ONE;
			case SRC_COLOR -> GL11.GL_SRC_COLOR;
			case SRC_ALPHA -> GL11.GL_SRC_ALPHA;
			case DST_COLOR -> GL11.GL_DST_COLOR;
			case DST_ALPHA -> GL11.GL_DST_ALPHA;
			case ONE_MINUS_SRC_COLOR -> GL11.GL_ONE_MINUS_SRC_COLOR;
			case ONE_MINUS_SRC_ALPHA -> GL11.GL_ONE_MINUS_SRC_ALPHA;
			case ONE_MINUS_DST_COLOR -> GL11.GL_ONE_MINUS_DST_COLOR;
			case ONE_MINUS_DST_ALPHA -> GL11.GL_ONE_MINUS_DST_ALPHA;
			case CONSTANT_COLOR, CONSTANT_ALPHA, ONE_MINUS_CONSTANT_COLOR, ONE_MINUS_CONSTANT_ALPHA -> GL11.GL_ZERO;
		};
	}
}
