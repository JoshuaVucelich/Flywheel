package dev.engine_room.flywheel.lib.model.baked;

import org.jetbrains.annotations.UnknownNullability;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.resources.model.geometry.BakedQuad;

class BlockModelConsumer implements VertexConsumer {
	@UnknownNullability
	private MeshEmitterManager<MeshEmitter> emitters;
	private boolean defaultAo;

	public void prepare(MeshEmitterManager<MeshEmitter> emitters, boolean defaultAo) {
		this.emitters = emitters;
		this.defaultAo = defaultAo;
	}

	@Override
	public void putBakedQuad(PoseStack.Pose pose, BakedQuad quad, QuadInstance instance) {
		var info = quad.materialInfo();
		boolean ao = info.ambientOcclusion() && defaultAo;
		var buffer = emitters.getBuffer(info.layer(), info.shade(), ao);
		if (buffer != null) {
			buffer.putBakedQuad(pose, quad, instance);
		}
	}

	@Override
	public VertexConsumer addVertex(float x, float y, float z) {
		throw new UnsupportedOperationException("BlockModelConsumer only supports putBakedQuad!");
	}

	@Override
	public VertexConsumer setColor(int red, int green, int blue, int alpha) {
		throw new UnsupportedOperationException("BlockModelConsumer only supports putBakedQuad!");
	}

	@Override
	public VertexConsumer setColor(int color) {
		throw new UnsupportedOperationException("BlockModelConsumer only supports putBakedQuad!");
	}

	@Override
	public VertexConsumer setUv(float u, float v) {
		throw new UnsupportedOperationException("BlockModelConsumer only supports putBakedQuad!");
	}

	@Override
	public VertexConsumer setUv1(int u, int v) {
		throw new UnsupportedOperationException("BlockModelConsumer only supports putBakedQuad!");
	}

	@Override
	public VertexConsumer setUv2(int u, int v) {
		throw new UnsupportedOperationException("BlockModelConsumer only supports putBakedQuad!");
	}

	@Override
	public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
		throw new UnsupportedOperationException("BlockModelConsumer only supports putBakedQuad!");
	}

	@Override
	public VertexConsumer setLineWidth(float width) {
		throw new UnsupportedOperationException("BlockModelConsumer only supports putBakedQuad!");
	}
}
