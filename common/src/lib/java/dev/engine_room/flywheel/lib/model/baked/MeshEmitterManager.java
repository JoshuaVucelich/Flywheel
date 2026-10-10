package dev.engine_room.flywheel.lib.model.baked;

import java.util.function.BiFunction;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferBuilder;

import dev.engine_room.flywheel.api.material.Material;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.lib.model.SimpleModel;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;

class MeshEmitterManager<T extends MeshEmitter> {
	private static final ChunkSectionLayer[] CHUNK_LAYERS = ChunkSectionLayer.values();

	private final Reference2ReferenceMap<ChunkSectionLayer, T> emitterMap = new Reference2ReferenceArrayMap<>();
	private final ByteBufferBuilderStack byteBufferBuilderStack = new ByteBufferBuilderStack();

	@UnknownNullability
	private BlockMaterialFunction blockMaterialFunction;

	MeshEmitterManager(BiFunction<ByteBufferBuilderStack, ChunkSectionLayer, T> meshEmitterFactory) {
		for (ChunkSectionLayer layer : CHUNK_LAYERS) {
			emitterMap.put(layer, meshEmitterFactory.apply(byteBufferBuilderStack, layer));
		}
	}

	public T getEmitter(ChunkSectionLayer layer) {
		return emitterMap.get(layer);
	}

	public void prepare(BlockMaterialFunction blockMaterialFunction) {
		this.blockMaterialFunction = blockMaterialFunction;
		byteBufferBuilderStack.reset();

		for (MeshEmitter emitter : emitterMap.values()) {
			emitter.prepare(blockMaterialFunction);
		}
	}

	public void prepareForBlock() {
		for (MeshEmitter emitter : emitterMap.values()) {
			emitter.prepareForBlock();
		}
	}

	public SimpleModel end() {
		blockMaterialFunction = null;

		ImmutableList.Builder<Model.ConfiguredMesh> meshes = ImmutableList.builder();

		for (MeshEmitter emitter : emitterMap.values()) {
			emitter.end(meshes);
		}

		return new SimpleModel(meshes.build());
	}

	@Nullable
	public BufferBuilder getBuffer(ChunkSectionLayer layer, boolean shade, boolean ao) {
		Material key = blockMaterialFunction.apply(layer, shade, ao);
		if (key != null) {
			return emitterMap.get(layer).getBuffer(key);
		} else {
			return null;
		}
	}
}
