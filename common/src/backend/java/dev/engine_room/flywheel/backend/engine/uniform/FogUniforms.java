package dev.engine_room.flywheel.backend.engine.uniform;

import net.minecraft.client.renderer.fog.FogData;

public final class FogUniforms extends UniformWriter {
	private static final int SIZE = 4 * 7;
	static final UniformBuffer BUFFER = new UniformBuffer(Uniforms.FOG_INDEX, SIZE);

	public static void update(FogData fogData) {
		if (fogData == null) {
			return;
		}

		long ptr = BUFFER.ptr();

		var color = fogData.color;

		ptr = writeFloat(ptr, color.x);
		ptr = writeFloat(ptr, color.y);
		ptr = writeFloat(ptr, color.z);
		ptr = writeFloat(ptr, color.w);
		ptr = writeFloat(ptr, fogData.environmentalStart);
		ptr = writeFloat(ptr, fogData.environmentalEnd);
		// FogShape was removed in 26.1; sphere (0) matches legacy default.
		ptr = writeInt(ptr, 0);

		BUFFER.markDirty();
	}
}
