package dev.engine_room.flywheel.backend.mixin;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.platform.Lighting;

import dev.engine_room.flywheel.backend.engine.uniform.LevelUniforms;

@Mixin(value = Lighting.class, remap = false)
abstract class LightingMixin {
	@Inject(method = "updateBuffer", at = @At("HEAD"))
	private void flywheel$onUpdateBuffer(Lighting.Entry entry, Vector3f light0, Vector3f light1, CallbackInfo ci) {
		if (entry == Lighting.Entry.LEVEL) {
			LevelUniforms.LIGHT0_DIRECTION.set(light0);
			LevelUniforms.LIGHT1_DIRECTION.set(light1);
		}
	}
}
