package de.leximon.fluidlogged.mixin.classes.worldwarning;

import com.mojang.serialization.Dynamic;
import de.leximon.fluidlogged.mixin.interfaces.ILevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;

@Mixin(LevelSettings.class)
public abstract class LevelInfoMixin implements ILevelInfo {

    private List<ResourceLocation> fl_fluidList;

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "parse", at = @At("RETURN"))
    private static void injectLoadFluidList(Dynamic<?> dynamic, WorldDataConfiguration dataConfiguration, CallbackInfoReturnable<LevelSettings> cir) {
        ILevelInfo props = (ILevelInfo) (Object) cir.getReturnValue();
        props.fl_setFluidList(dynamic);
    }

    @Override
    public List<ResourceLocation> fl_getFluidList() {
        return this.fl_fluidList;
    }

    @Override
    public void fl_setFluidList(List<ResourceLocation> list) {
        this.fl_fluidList = list;
    }
}
