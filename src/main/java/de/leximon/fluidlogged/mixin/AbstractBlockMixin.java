package de.leximon.fluidlogged.mixin;

import de.leximon.fluidlogged.FluidloggedMod;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
//
//    @Inject(method = "getFluidState", at = @At("RETURN"), cancellable = true)
//    private void injectFluidState(BlockState state, CallbackInfoReturnable<FluidState> cir) {
//        if(state.contains(FluidloggedMod.PROPERTY_FLUID)) {
//            Fluid f = FluidloggedMod.getFluid(state);
//            if(f == null)
//                return;
//            cir.setReturnValue(f.getDefaultState());
//        }
//    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
    private void injectFlowingFluid(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        Fluid f = FluidloggedMod.getFluid(state);
        if (f == null || Fluids.EMPTY.equals(f))
            return;
        world.createAndScheduleFluidTick(pos, f, f.getTickRate(world));
    }


}
