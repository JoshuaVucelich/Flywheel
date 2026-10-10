package dev.engine_room.flywheel.impl.compat;

import java.lang.reflect.Method;

import dev.engine_room.flywheel.impl.FlwImpl;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;

public final class EmbeddiumCompat {
	public static final boolean ACTIVE = CompatMod.EMBEDDIUM.isLoaded;

	private static final Method REMOVE_BLOCK_ENTITIES_IF;

	static {
		Method removeBlockEntitiesIf = null;

		if (ACTIVE) {
			FlwImpl.LOGGER.debug("Detected Embeddium");
			try {
				Class<?> eventClass = Class.forName("org.embeddedt.embeddium.api.ChunkDataBuiltEvent");
				Class<?> dataBuilderClass = Class.forName("org.embeddedt.embeddium.api.ChunkDataBuiltEvent$DataBuilder");
				Object bus = eventClass.getField("BUS").get(null);
				removeBlockEntitiesIf = dataBuilderClass.getMethod("removeBlockEntitiesIf", java.util.function.Predicate.class);

				Method addListener = bus.getClass().getMethod("addListener", java.util.function.Consumer.class);
				Method finalRemoveBlockEntitiesIf = removeBlockEntitiesIf;
				addListener.invoke(bus, (java.util.function.Consumer<Object>) event -> {
					try {
						Object dataBuilder = event.getClass().getMethod("getDataBuilder").invoke(event);
						finalRemoveBlockEntitiesIf.invoke(dataBuilder, (java.util.function.Predicate<Object>) be -> !VisualizationHelper.tryAddBlockEntity((net.minecraft.world.level.block.entity.BlockEntity) be));
					} catch (ReflectiveOperationException e) {
						throw new RuntimeException(e);
					}
				});
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException("Failed to initialize Embeddium compatibility", e);
			}
		}

		REMOVE_BLOCK_ENTITIES_IF = removeBlockEntitiesIf;
	}

	private EmbeddiumCompat() {
	}

	public static void init() {
		// Registration happens in the static initializer when Embeddium is present.
	}
}
