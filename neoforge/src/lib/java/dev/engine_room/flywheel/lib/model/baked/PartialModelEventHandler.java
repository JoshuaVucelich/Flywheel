package dev.engine_room.flywheel.lib.model.baked;

import java.util.concurrent.ConcurrentMap;

import org.jetbrains.annotations.ApiStatus;

import com.google.common.collect.MapMaker;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

@ApiStatus.Internal
public final class PartialModelEventHandler {
	private static final ConcurrentMap<Identifier, StandaloneModelKey<BlockStateModel>> KEYS = new MapMaker().makeMap();

	private PartialModelEventHandler() {
	}

	public static void onRegisterStandalone(ModelEvent.RegisterStandalone event) {
		for (Identifier modelLocation : PartialModel.ALL.keySet()) {
			StandaloneModelKey<BlockStateModel> key = key(modelLocation);
			event.register(key, SimpleUnbakedStandaloneModel.blockStateModel(modelLocation));
		}
	}

	public static void onBakingCompleted(ModelEvent.BakingCompleted event) {
		PartialModel.populateOnInit = true;
		ModelManager manager = event.getModelManager();

		for (PartialModel partial : PartialModel.ALL.values()) {
			partial.blockStateModel = manager.getStandaloneModel(key(partial.modelLocation()));
		}
	}

	public static StandaloneModelKey<BlockStateModel> key(Identifier modelLocation) {
		return KEYS.computeIfAbsent(modelLocation, id -> new StandaloneModelKey<>(id::toString));
	}
}
