package dev.engine_room.flywheel.backend.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.engine_room.flywheel.backend.engine.uniform.FogUniforms;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;

@Mixin(value = FogRenderer.class, remap = false)
abstract class FogRendererMixin {
	@Inject(method = "updateBuffer(Lnet/minecraft/client/renderer/fog/FogData;)V", at = @At("RETURN"))
	private void flywheel$onUpdateFog(FogData fogData, CallbackInfo ci) {
		FogUniforms.update(fogData);
	}
}
