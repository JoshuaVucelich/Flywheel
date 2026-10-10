package dev.engine_room.flywheel.impl.compat;

import java.lang.reflect.Method;

import dev.engine_room.flywheel.impl.FlwImpl;

public final class IrisCompat {
	public static final boolean ACTIVE = CompatMod.IRIS.isLoaded;

	private static final Method IS_SHADER_PACK_IN_USE;
	private static final Method IS_RENDERING_SHADOW_PASS;

	static {
		Method shaderPackInUse = null;
		Method renderingShadowPass = null;

		if (ACTIVE) {
			FlwImpl.LOGGER.debug("Detected Iris");
			try {
				Class<?> irisApi = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
				Object instance = irisApi.getMethod("getInstance").invoke(null);
				shaderPackInUse = instance.getClass().getMethod("isShaderPackInUse");
				renderingShadowPass = instance.getClass().getMethod("isRenderingShadowPass");
			} catch (ReflectiveOperationException e) {
				throw new RuntimeException("Failed to initialize Iris compatibility", e);
			}
		}

		IS_SHADER_PACK_IN_USE = shaderPackInUse;
		IS_RENDERING_SHADOW_PASS = renderingShadowPass;
	}

	private IrisCompat() {
	}

	public static boolean isShaderPackInUse() {
		if (!ACTIVE) {
			return false;
		}

		try {
			Object instance = Class.forName("net.irisshaders.iris.api.v0.IrisApi").getMethod("getInstance").invoke(null);
			return (boolean) IS_SHADER_PACK_IN_USE.invoke(instance);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isRenderingShadowPass() {
		if (!ACTIVE) {
			return false;
		}

		try {
			Object instance = Class.forName("net.irisshaders.iris.api.v0.IrisApi").getMethod("getInstance").invoke(null);
			return (boolean) IS_RENDERING_SHADOW_PASS.invoke(instance);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
