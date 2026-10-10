package dev.engine_room.flywheel.impl.compat;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.jetbrains.annotations.Nullable;

import dev.engine_room.flywheel.api.visualization.BlockEntityVisualizer;
import dev.engine_room.flywheel.impl.FlwImpl;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class SodiumCompat {
	public static final boolean ACTIVE = CompatMod.SODIUM.isLoaded;

	private static final Class<?> PREDICATE_CLASS;
	private static final Method ADD_RENDER_PREDICATE;
	private static final Method REMOVE_RENDER_PREDICATE;
	private static final Method RENDER_HANDLER_INSTANCE;

	static {
		Class<?> predicateClass = null;
		Method addRenderPredicate = null;
		Method removeRenderPredicate = null;
		Method renderHandlerInstance = null;

		if (ACTIVE) {
			FlwImpl.LOGGER.debug("Detected Sodium");
			try {
				Class<?> handlerClass = Class.forName("net.caffeinemc.mods.sodium.api.blockentity.BlockEntityRenderHandler");
				predicateClass = Class.forName("net.caffeinemc.mods.sodium.api.blockentity.BlockEntityRenderPredicate");
				renderHandlerInstance = handlerClass.getMethod("instance");
				addRenderPredicate = handlerClass.getMethod("addRenderPredicate", BlockEntityType.class, predicateClass);
				removeRenderPredicate = handlerClass.getMethod("removeRenderPredicate", BlockEntityType.class, predicateClass);
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException("Failed to initialize Sodium compatibility", e);
			}
		}

		PREDICATE_CLASS = predicateClass;
		ADD_RENDER_PREDICATE = addRenderPredicate;
		REMOVE_RENDER_PREDICATE = removeRenderPredicate;
		RENDER_HANDLER_INSTANCE = renderHandlerInstance;
	}

	private SodiumCompat() {
	}

	@Nullable
	public static <T extends BlockEntity> Object onSetBlockEntityVisualizer(BlockEntityType<T> type, @Nullable BlockEntityVisualizer<? super T> oldVisualizer, @Nullable BlockEntityVisualizer<? super T> newVisualizer, @Nullable Object predicate) {
		if (!ACTIVE) {
			return null;
		}

		if (oldVisualizer == null && newVisualizer != null) {
			if (predicate != null) {
				throw new IllegalArgumentException("Sodium predicate must be null when old visualizer is null");
			}

			return addPredicate(type);
		} else if (oldVisualizer != null && newVisualizer == null) {
			if (predicate == null) {
				throw new IllegalArgumentException("Sodium predicate must not be null when old visualizer is not null");
			}

			removePredicate(type, predicate);
			return null;
		}

		return predicate;
	}

	private static <T extends BlockEntity> Object addPredicate(BlockEntityType<T> type) {
		Object predicate = createPredicate();

		try {
			Object handler = RENDER_HANDLER_INSTANCE.invoke(null);
			ADD_RENDER_PREDICATE.invoke(handler, type, predicate);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}

		return predicate;
	}

	private static <T extends BlockEntity> void removePredicate(BlockEntityType<T> type, Object predicate) {
		try {
			Object handler = RENDER_HANDLER_INSTANCE.invoke(null);
			REMOVE_RENDER_PREDICATE.invoke(handler, type, predicate);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private static Object createPredicate() {
		InvocationHandler handler = (proxy, method, args) -> !VisualizationHelper.tryAddBlockEntity((BlockEntity) args[2]);
		return Proxy.newProxyInstance(PREDICATE_CLASS.getClassLoader(), new Class<?>[] {PREDICATE_CLASS}, handler);
	}
}
