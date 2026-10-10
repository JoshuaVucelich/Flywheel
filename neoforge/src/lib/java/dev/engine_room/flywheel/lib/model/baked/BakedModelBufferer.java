package dev.engine_room.flywheel.lib.model.baked;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.engine_room.flywheel.lib.model.SimpleModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockModelLighter;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.block.FluidStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

final class BakedModelBufferer {
	private static final ThreadLocal<ThreadLocalObjects> THREAD_LOCAL_OBJECTS = ThreadLocal.withInitial(ThreadLocalObjects::new);
	private static final BlockModelLighter LIGHTER = new BlockModelLighter();

	private BakedModelBufferer() {
	}

	public static SimpleModel bufferModel(BlockStateModel model, BlockPos pos, BlockAndTintGetter level, BlockState state, @Nullable PoseStack poseStack, BlockMaterialFunction blockMaterialFunction) {
		ThreadLocalObjects objects = THREAD_LOCAL_OBJECTS.get();
		if (poseStack == null) {
			poseStack = objects.identityPoseStack;
		}

		MeshEmitterManager<MeshEmitter> emitters = objects.emitters;
		BlockModelConsumer consumer = objects.consumer;

		emitters.prepare(blockMaterialFunction);

		boolean useAo = Minecraft.getInstance().gameRenderer.getGameRenderState().optionsRenderState.ambientOcclusion;
		boolean defaultAo = state.getLightEmission(level, pos) == 0;
		consumer.prepare(emitters, useAo && defaultAo);

		List<BlockStateModelPart> parts = new ArrayList<>();
		model.collectParts(RandomSource.create(state.getSeed(pos)), parts);

		QuadInstance instance = new QuadInstance();
		int light = LightCoordsUtil.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));
		instance.setOverlayCoords(OverlayTexture.NO_OVERLAY);

		poseStack.pushPose();
		tesselateParts(level, state, pos, poseStack, consumer, parts, useAo, light, instance);
		poseStack.popPose();

		return emitters.end();
	}

	public static SimpleModel bufferBlocks(Iterator<BlockPos> posIterator, BlockAndTintGetter level, @Nullable PoseStack poseStack, boolean renderFluids, BlockMaterialFunction blockMaterialFunction) {
		ThreadLocalObjects objects = THREAD_LOCAL_OBJECTS.get();
		if (poseStack == null) {
			poseStack = objects.identityPoseStack;
		}

		MeshEmitterManager<MeshEmitter> emitters = objects.emitters;
		BlockModelConsumer consumer = objects.consumer;
		TransformingVertexConsumer transformingWrapper = objects.transformingWrapper;

		emitters.prepare(blockMaterialFunction);

		BlockStateModelSet blockStateModelSet = Minecraft.getInstance().getModelManager().getBlockStateModelSet();
		FluidStateModelSet fluidStateModelSet = Minecraft.getInstance().getModelManager().getFluidStateModelSet();
		FluidRenderer fluidRenderer = new FluidRenderer(fluidStateModelSet);

		boolean useAo = Minecraft.getInstance().gameRenderer.getGameRenderState().optionsRenderState.ambientOcclusion;

		while (posIterator.hasNext()) {
			BlockPos pos = posIterator.next();
			BlockState state = level.getBlockState(pos);

			emitters.prepareForBlock();

			if (renderFluids) {
				FluidState fluidState = state.getFluidState();

				if (!fluidState.isEmpty()) {
					poseStack.pushPose();
					poseStack.translate(pos.getX() - (pos.getX() & 0xF), pos.getY() - (pos.getY() & 0xF), pos.getZ() - (pos.getZ() & 0xF));

					PoseStack fluidPoseStack = poseStack;
					fluidRenderer.tesselate(level, pos, layer -> {
						transformingWrapper.prepare(emitters.getBuffer(layer, true, true), fluidPoseStack);
						return transformingWrapper;
					}, state, fluidState);
					poseStack.popPose();
				}
			}

			if (state.getRenderShape() == RenderShape.MODEL) {
				BlockStateModel model = blockStateModelSet.get(state);
				boolean defaultAo = state.getLightEmission(level, pos) == 0;
				consumer.prepare(emitters, useAo && defaultAo);

				List<BlockStateModelPart> parts = new ArrayList<>();
				model.collectParts(RandomSource.create(state.getSeed(pos)), parts);

				QuadInstance instance = new QuadInstance();
				int light = LevelRenderer.getLightCoords(level, pos);
				instance.setOverlayCoords(OverlayTexture.NO_OVERLAY);

				poseStack.pushPose();
				poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
				tesselateParts(level, state, pos, poseStack, consumer, parts, useAo, light, instance);
				poseStack.popPose();
			}
		}

		transformingWrapper.clear();
		return emitters.end();
	}

	private static void tesselateParts(BlockAndTintGetter level, BlockState state, BlockPos pos, PoseStack poseStack, BlockModelConsumer consumer, List<BlockStateModelPart> parts, boolean useAo, int light, QuadInstance instance) {
		for (Direction direction : Direction.values()) {
			for (BlockStateModelPart part : parts) {
				for (BakedQuad quad : part.getQuads(direction)) {
					if (useAo) {
						LIGHTER.prepareQuadAmbientOcclusion(level, state, pos, quad, instance);
					} else {
						LIGHTER.prepareQuadFlat(level, state, pos, light, quad, instance);
					}
					consumer.putBakedQuad(poseStack.last(), quad, instance);
				}
			}
		}

		for (BlockStateModelPart part : parts) {
			for (BakedQuad quad : part.getQuads(null)) {
				if (useAo) {
					LIGHTER.prepareQuadAmbientOcclusion(level, state, pos, quad, instance);
				} else {
					LIGHTER.prepareQuadFlat(level, state, pos, light, quad, instance);
				}
				consumer.putBakedQuad(poseStack.last(), quad, instance);
			}
		}
	}

	private static class ThreadLocalObjects {
		public final PoseStack identityPoseStack = new PoseStack();
		public final MeshEmitterManager<MeshEmitter> emitters = new MeshEmitterManager<>(MeshEmitter::new);
		public final BlockModelConsumer consumer = new BlockModelConsumer();
		public final TransformingVertexConsumer transformingWrapper = new TransformingVertexConsumer();
	}
}
