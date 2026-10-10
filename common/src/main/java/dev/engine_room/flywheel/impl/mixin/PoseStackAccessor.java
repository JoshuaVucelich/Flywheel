package dev.engine_room.flywheel.impl.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(PoseStack.class)
public interface PoseStackAccessor {
	// 26.1: PoseStack switched from Deque poseStack to List poses + lastIndex
	@Accessor("poses")
	List<PoseStack.Pose> flywheel$getPoses();
}
