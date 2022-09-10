package de.leximon.fluidlogged.mixin.classes.worldwarning;

import com.mojang.serialization.Dynamic;
import de.leximon.fluidlogged.mixin.interfaces.ILevelInfo;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.LevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LevelInfo.class)
public abstract class LevelInfoMixin implements ILevelInfo {

    private List<Identifier> fl_fluidList;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "fromDynamic", at = @At("RETURN"))
    private static void injectLoadFluidList(Dynamic<?> dynamic, DataPackSettings dataPackSettings, CallbackInfoReturnable<LevelInfo> cir) {
        ILevelInfo props = (ILevelInfo) (Object) cir.getReturnValue();
        props.fl_setFluidList(dynamic);
    }

    @Override
    public List<Identifier> fl_getFluidList() {
        return this.fl_fluidList;
    }

    @Override
    public void fl_setFluidList(List<Identifier> list) {
        this.fl_fluidList = list;
    }
}
