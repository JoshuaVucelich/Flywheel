package dev.engine_room.flywheel.lib.util;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Historically recycled {@link PoseStack.Pose} objects via the internal Deque.
 *
 * <p>Minecraft 26.1 replaced that Deque with a List + lastIndex stack, so recycling
 * via direct deque mutation is no longer possible. Fall back to vanilla push/pop;
 * callers still get a dedicated stack instance without the old allocation churn API.
 */
public class RecyclingPoseStack extends PoseStack {
	// Intentionally empty — vanilla PoseStack already reuses Pose slots in its List.
}
