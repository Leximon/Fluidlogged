package de.leximon.fluidlogged.mixin.classes.worldwarning;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import de.leximon.fluidlogged.mixin.interfaces.ILevelInfo;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.SaveVersionInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LevelProperties.class)
public class LevelPropertiesMixin implements ILevelInfo {

    private List<Identifier> fl_fluidList;

    @Inject(method = "readProperties", at = @At("RETURN"))
    private static void injectLoadFluidList(Dynamic<NbtElement> dynamic, DataFixer dataFixer, int dataVersion, NbtCompound playerData, LevelInfo levelInfo, SaveVersionInfo saveVersionInfo, LevelProperties.SpecialProperty specialProperty, GeneratorOptions generatorOptions, Lifecycle lifecycle, CallbackInfoReturnable<LevelProperties> cir) {
        ILevelInfo props = (ILevelInfo) cir.getReturnValue();
        props.fl_setFluidList(dynamic);
    }

    @Inject(method = "updateProperties", at = @At("TAIL"))
    private void injectSaveFluidList(DynamicRegistryManager registryManager, NbtCompound levelNbt, NbtCompound playerNbt, CallbackInfo ci) {
        NbtList list = new NbtList();
        for (Identifier id : FluidloggedConfig.fluidsLocked)
            list.add(NbtString.of(id.toString()));
        levelNbt.put("fluidlogged", list);
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
