package de.leximon.fluidlogged.mixin.classes.worldwarning;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import de.leximon.fluidlogged.core.FluidloggedConfig;
import de.leximon.fluidlogged.mixin.interfaces.ILevelInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelVersion;
import net.minecraft.world.level.storage.PrimaryLevelData;

@Mixin(PrimaryLevelData.class)
public class LevelPropertiesMixin implements ILevelInfo {

    private List<ResourceLocation> fl_fluidList;

    @Inject(method = "parse", at = @At("RETURN"))
    private static void injectLoadFluidList(Dynamic<Tag> dynamic, DataFixer dataFixer, int dataVersion, CompoundTag playerData, LevelSettings levelInfo, LevelVersion saveVersionInfo, PrimaryLevelData.SpecialWorldProperty specialProperty, WorldOptions generatorOptions, Lifecycle lifecycle, CallbackInfoReturnable<PrimaryLevelData> cir) {
        ILevelInfo props = (ILevelInfo) cir.getReturnValue();
        props.fl_setFluidList(dynamic);
    }

    @Inject(method = "setTagData", at = @At("TAIL"))
    private void injectSaveFluidList(RegistryAccess registryManager, CompoundTag levelNbt, CompoundTag playerNbt, CallbackInfo ci) {
        ListTag list = new ListTag();
        for (ResourceLocation id : FluidloggedConfig.fluidsLocked)
            list.add(StringTag.valueOf(id.toString()));
        levelNbt.put("fluidlogged", list);
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
