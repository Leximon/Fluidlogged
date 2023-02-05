package de.leximon.fluidlogged.mixin.interfaces;

import com.mojang.serialization.Dynamic;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public interface ILevelInfo {

    default void fl_setFluidList(Dynamic<?> dynamic) {
        List<ResourceLocation> ids = dynamic.get("fluidlogged").asList(entry -> ResourceLocation.tryParse(entry.asString("minecraft:empty")));
        fl_setFluidList(ids);
    }

    List<ResourceLocation> fl_getFluidList();

    void fl_setFluidList(List<ResourceLocation> list);

}
