package de.leximon.fluidlogged.mixin.interfaces;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.Identifier;

import java.util.List;

public interface ILevelInfo {

    default void fl_setFluidList(Dynamic<?> dynamic) {
        List<Identifier> ids = dynamic.get("fluidlogged").asList(entry -> Identifier.tryParse(entry.asString("minecraft:empty")));
        fl_setFluidList(ids);
    }

    List<Identifier> fl_getFluidList();

    void fl_setFluidList(List<Identifier> list);

}
