package de.leximon.fluidlogged.mixin.classes.storing_and_access;

import de.leximon.fluidlogged.mixin.extensions.LevelChunkSectionExtension;
import de.leximon.fluidlogged.platform.services.Services;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunkSection.class)
public class LevelChunkSectionMixin implements LevelChunkSectionExtension {

    @Unique private Short2ObjectMap<FluidState> fluidStates;

    @Override
    public Short2ObjectMap<FluidState> createAndSetFluidStatesMap() {
        Short2ObjectOpenHashMap<FluidState> fluidStates = new Short2ObjectOpenHashMap<>();
        fluidStates.defaultReturnValue(Fluids.EMPTY.defaultFluidState());
        this.fluidStates = fluidStates;
        return fluidStates;
    }

    @Override
    public Short2ObjectMap<FluidState> getFluidStates() {
        return this.fluidStates;
    }


    @Override
    public FluidState setFluidState(int x, int y, int z, FluidState fluidState) {
        if (fluidState.isEmpty())
            return this.fluidStates.remove((short) (x << 8 | y << 4 | z));

        return this.fluidStates.put((short) (x << 8 | y << 4 | z), fluidState);
    }

    @Override
    public FluidState getFluidStateExact(int x, int y, int z) {
        return this.fluidStates.get((short) (x << 8 | y << 4 | z));
    }

    @Inject(method = "<init>(Lnet/minecraft/core/Registry;)V", at = @At("RETURN"))
    private void injectInit(Registry<Biome> registry, CallbackInfo ci) {
        createAndSetFluidStatesMap();
    }

    @Inject(method = "getFluidState", at = @At("RETURN"), cancellable = true)
    private void injectGetFluidState(int x, int y, int z, CallbackInfoReturnable<FluidState> cir) {
        FluidState fluidState = cir.getReturnValue();
        if (!fluidState.isEmpty())
            return;

        fluidState = this.fluidStates.get((short) (x << 8 | y << 4 | z));
        if (fluidState == null)
            return;

        cir.setReturnValue(fluidState);
    }

    @Inject(method = "hasOnlyAir", at = @At("RETURN"), cancellable = true)
    private void injectHasOnlyAir(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue())
            return;
        cir.setReturnValue(fluidStates.isEmpty());
    }

    @Inject(method = "getSerializedSize", at = @At("RETURN"), cancellable = true)
    private void injectGetSerializedSize(CallbackInfoReturnable<Integer> cir) {
        int fluidStatesSize = Short.BYTES + this.fluidStates.size() * (Short.BYTES + Integer.BYTES);
        cir.setReturnValue(cir.getReturnValue() + fluidStatesSize);
    }

    @Inject(method = "write", at = @At("TAIL"))
    private void injectWrite(FriendlyByteBuf buf, CallbackInfo ci) {
        buf.writeShort(this.fluidStates.size());

        for (Short2ObjectMap.Entry<FluidState> entry : this.fluidStates.short2ObjectEntrySet()) {
            buf.writeShort(entry.getShortKey());
            buf.writeInt(Services.PLATFORM.getFluidStateIdMapper().getId(entry.getValue()));
        }
    }

    @Inject(method = "read", at = @At("TAIL"))
    private void injectRead(FriendlyByteBuf buf, CallbackInfo ci) {
        this.fluidStates.clear();

        short size = buf.readShort();

        for (short i = 0; i < size; i++) {
            short pos = buf.readShort();
            FluidState fluidState = Services.PLATFORM.getFluidStateIdMapper().byId(buf.readInt());

            this.fluidStates.put(pos, fluidState);
        }
    }
}
