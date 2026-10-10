package dev.engine_room.flywheel.backend.engine;

import java.util.Comparator;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;

import dev.engine_room.flywheel.api.material.DepthTest;
import dev.engine_room.flywheel.api.material.Material;
import dev.engine_room.flywheel.api.material.Transparency;
import dev.engine_room.flywheel.api.material.WriteMask;
import dev.engine_room.flywheel.backend.Samplers;
import dev.engine_room.flywheel.backend.gl.GlRenderCompat;
import dev.engine_room.flywheel.backend.gl.GpuTextureCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;

public final class MaterialRenderState {
	public static final Comparator<Material> COMPARATOR = MaterialRenderState::compare;

	private MaterialRenderState() {
	}

	public static void setup(Material material) {
		setupTexture(material);
		setupBackfaceCulling(material.backfaceCulling());
		setupPolygonOffset(material.polygonOffset());
		setupDepthTest(material.depthTest());
		setupTransparency(material.transparency());
		setupWriteMask(material.writeMask());
	}

	public static void setupOit(Material material) {
		setupTexture(material);
		setupBackfaceCulling(material.backfaceCulling());
		setupPolygonOffset(material.polygonOffset());
		setupDepthTest(material.depthTest());

		WriteMask mask = material.writeMask();
		boolean writeColor = mask.color();
		GlRenderCompat.colorMask(writeColor, writeColor, writeColor, writeColor);
	}

	private static void setupTexture(Material material) {
		Samplers.DIFFUSE.makeActive();
		AbstractTexture texture = Minecraft.getInstance()
				.getTextureManager()
				.getTexture(material.texture());
		GpuTextureCompat.setFilter(texture, material.blur(), material.mipmap());
		GpuTextureCompat.bind(texture);
	}

	private static void setupBackfaceCulling(boolean backfaceCulling) {
		if (backfaceCulling) {
			GlRenderCompat.enableCull();
		} else {
			GlRenderCompat.disableCull();
		}
	}

	private static void setupPolygonOffset(boolean polygonOffset) {
		if (polygonOffset) {
			GlRenderCompat.polygonOffset(-1.0F, -10.0F);
			GlRenderCompat.enablePolygonOffset();
		} else {
			GlRenderCompat.polygonOffset(0.0F, 0.0F);
			GlRenderCompat.disablePolygonOffset();
		}
	}

	private static void setupDepthTest(DepthTest depthTest) {
		switch (depthTest) {
		case OFF -> {
			GlRenderCompat.disableDepthTest();
		}
		case NEVER -> {
			GlRenderCompat.enableDepthTest();
			GlRenderCompat.depthFunc(GL11.GL_NEVER);
		}
		case LESS -> {
			GlRenderCompat.enableDepthTest();
			GlRenderCompat.depthFunc(GL11.GL_LESS);
		}
		case EQUAL -> {
			GlRenderCompat.enableDepthTest();
			GlRenderCompat.depthFunc(GL11.GL_EQUAL);
		}
		case LEQUAL -> {
			GlRenderCompat.enableDepthTest();
			GlRenderCompat.depthFunc(GL11.GL_LEQUAL);
		}
		case GREATER -> {
			GlRenderCompat.enableDepthTest();
			GlRenderCompat.depthFunc(GL11.GL_GREATER);
		}
		case NOTEQUAL -> {
			GlRenderCompat.enableDepthTest();
			GlRenderCompat.depthFunc(GL11.GL_NOTEQUAL);
		}
		case GEQUAL -> {
			GlRenderCompat.enableDepthTest();
			GlRenderCompat.depthFunc(GL11.GL_GEQUAL);
		}
		case ALWAYS -> {
			GlRenderCompat.enableDepthTest();
			GlRenderCompat.depthFunc(GL11.GL_ALWAYS);
		}
		}
	}

	private static void setupTransparency(Transparency transparency) {
		switch (transparency) {
		case OPAQUE -> {
			GlRenderCompat.disableBlend();
		}
		case ADDITIVE -> {
			GlRenderCompat.enableBlend();
			GlRenderCompat.blendFunc(SourceFactor.ONE, DestFactor.ONE);
		}
		case LIGHTNING -> {
			GlRenderCompat.enableBlend();
			GlRenderCompat.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		}
		case GLINT -> {
			GlRenderCompat.enableBlend();
			GlRenderCompat.blendFuncSeparate(SourceFactor.SRC_COLOR, DestFactor.ONE, SourceFactor.ZERO, DestFactor.ONE);
		}
		case CRUMBLING -> {
			GlRenderCompat.enableBlend();
			GlRenderCompat.blendFuncSeparate(SourceFactor.DST_COLOR, DestFactor.SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
		}
		case TRANSLUCENT -> {
			GlRenderCompat.enableBlend();
			GlRenderCompat.blendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ONE_MINUS_SRC_ALPHA);
		}
		}
	}

	private static void setupWriteMask(WriteMask mask) {
		GlRenderCompat.depthMask(mask.depth());
		boolean writeColor = mask.color();
		GlRenderCompat.colorMask(writeColor, writeColor, writeColor, writeColor);
	}

	public static void reset() {
		resetTexture();
		resetBackfaceCulling();
		resetPolygonOffset();
		resetDepthTest();
		resetTransparency();
		resetWriteMask();
	}

	private static void resetTexture() {
		Samplers.DIFFUSE.makeActive();
		GlRenderCompat.bindTexture(0);
	}

	private static void resetBackfaceCulling() {
		GlRenderCompat.enableCull();
	}

	private static void resetPolygonOffset() {
		GlRenderCompat.polygonOffset(0.0F, 0.0F);
		GlRenderCompat.disablePolygonOffset();
	}

	private static void resetDepthTest() {
		GlRenderCompat.disableDepthTest();
		GlRenderCompat.depthFunc(GL11.GL_LEQUAL);
	}

	private static void resetTransparency() {
		GlRenderCompat.disableBlend();
		GlRenderCompat.defaultBlendFunc();
	}

	private static void resetWriteMask() {
		GlRenderCompat.depthMask(true);
		GlRenderCompat.colorMask(true, true, true, true);
	}

	public static boolean materialEquals(Material lhs, Material rhs) {
		if (lhs == rhs) {
			return true;
		}

		// Not here because ubershader: useLight, useOverlay, diffuse, fog shader, ambient occlusion
		// Everything in the comparator should be here.
		// @formatter:off
		return lhs.blur() == rhs.blur()
				&& lhs.mipmap() == rhs.mipmap()
				&& lhs.backfaceCulling() == rhs.backfaceCulling()
				&& lhs.polygonOffset() == rhs.polygonOffset()
				&& lhs.depthTest() == rhs.depthTest()
				&& lhs.transparency() == rhs.transparency()
				&& lhs.writeMask() == rhs.writeMask()
				&& lhs.light().source().equals(rhs.light().source())
				&& lhs.texture().equals(rhs.texture())
				&& lhs.cutout().source().equals(rhs.cutout().source())
				&& lhs.shaders().fragmentSource().equals(rhs.shaders().fragmentSource())
				&& lhs.shaders().vertexSource().equals(rhs.shaders().vertexSource());
		// @formatter:on
	}

	public static boolean materialIsAllNonNull(@Nullable Material material) {
		// We do not trust people to give us valid NotNull objects.
		// @formatter:off
		return material != null &&
				material.shaders() != null &&
				material.shaders().fragmentSource() != null &&
				material.shaders().vertexSource() != null &&
				material.fog() != null &&
				material.fog().source() != null &&
				material.cutout() != null &&
				material.cutout().source() != null &&
				material.light() != null &&
				material.light().source() != null &&
				material.texture() != null &&
				material.depthTest() != null &&
				material.transparency() != null &&
				material.writeMask() != null &&
				material.cardinalLightingMode() != null;
		// @formatter:on
	}

	public static int compare(Material lhs, Material rhs) {
		if (lhs == rhs) {
			return 0;
		}

		int cmp;
		cmp = lhs.transparency()
				.compareTo(rhs.transparency());
		if (cmp != 0) {
			return cmp;
		}
		cmp = lhs.light()
				.source()
				.compareTo(rhs.light()
						.source());
		if (cmp != 0) {
			return cmp;
		}
		cmp = lhs.cutout()
				.source()
				.compareTo(rhs.cutout()
						.source());
		if (cmp != 0) {
			return cmp;
		}
		cmp = lhs.shaders()
				.fragmentSource()
				.compareTo(rhs.shaders()
						.fragmentSource());
		if (cmp != 0) {
			return cmp;
		}
		cmp = lhs.shaders()
				.vertexSource()
				.compareTo(rhs.shaders()
						.vertexSource());
		if (cmp != 0) {
			return cmp;
		}
		cmp = lhs.texture()
				.compareTo(rhs.texture());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Boolean.compare(lhs.blur(), rhs.blur());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Boolean.compare(lhs.mipmap(), rhs.mipmap());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Boolean.compare(lhs.backfaceCulling(), rhs.backfaceCulling());
		if (cmp != 0) {
			return cmp;
		}
		cmp = Boolean.compare(lhs.polygonOffset(), rhs.polygonOffset());
		if (cmp != 0) {
			return cmp;
		}
		cmp = lhs.depthTest()
				.compareTo(rhs.depthTest());
		if (cmp != 0) {
			return cmp;
		}
		cmp = lhs.writeMask()
				.compareTo(rhs.writeMask());
		if (cmp != 0) {
			return cmp;
		}
		return 0;
	}
}
