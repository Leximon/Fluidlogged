package de.leximon.fluidlogged.core;

import de.leximon.fluidlogged.mixin.extensions.ClientLevelExtension;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

/**
 * @see net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler
 */
public class FluidStatePredictionHandler implements AutoCloseable {
	private final Long2ObjectOpenHashMap<ServerVerifiedState> serverVerifiedStates = new Long2ObjectOpenHashMap<>();
	private int currentSequenceNr;
	private boolean isPredicting;

	public void retainKnownServerState(BlockPos blockPos, FluidState fluidState, LocalPlayer localPlayer) {
		this.serverVerifiedStates
			.compute(
				blockPos.asLong(),
				(long_, serverVerifiedState) -> serverVerifiedState != null
						? serverVerifiedState.setSequence(this.currentSequenceNr)
						: new ServerVerifiedState(this.currentSequenceNr, fluidState, localPlayer.position())
			);
	}

	public boolean updateKnownServerState(BlockPos blockPos, FluidState blockState) {
		ServerVerifiedState serverVerifiedState = this.serverVerifiedStates.get(blockPos.asLong());
		if (serverVerifiedState == null) {
			return false;
		} else {
			serverVerifiedState.setFluidState(blockState);
			return true;
		}
	}

	public void endPredictionsUpTo(int i, ClientLevel clientLevel) {
		ObjectIterator<Long2ObjectMap.Entry<ServerVerifiedState>> iterator = this.serverVerifiedStates.long2ObjectEntrySet().iterator();

		while(iterator.hasNext()) {
			Long2ObjectMap.Entry<ServerVerifiedState> entry = iterator.next();
			ServerVerifiedState serverVerifiedState = entry.getValue();

			if (serverVerifiedState.sequence <= i) {
				BlockPos blockPos = BlockPos.of(entry.getLongKey());
				iterator.remove();

				((ClientLevelExtension) clientLevel).syncFluidState(blockPos, serverVerifiedState.fluidState);
			}
		}
	}

	public FluidStatePredictionHandler startPredicting() {
		++this.currentSequenceNr;
		this.isPredicting = true;
		return this;
	}

	public void close() {
		this.isPredicting = false;
	}

	public int currentSequence() {
		return this.currentSequenceNr;
	}

	public boolean isPredicting() {
		return this.isPredicting;
	}

	static class ServerVerifiedState {
		final Vec3 playerPos;
		int sequence;
		FluidState fluidState;

		ServerVerifiedState(int sequence, FluidState fluidState, Vec3 playerPos) {
			this.sequence = sequence;
			this.fluidState = fluidState;
			this.playerPos = playerPos;
		}

		FluidStatePredictionHandler.ServerVerifiedState setSequence(int i) {
			this.sequence = i;
			return this;
		}

		void setFluidState(FluidState fluidState) {
			this.fluidState = fluidState;
		}
	}
}