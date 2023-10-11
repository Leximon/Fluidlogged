package de.leximon.fluidlogged.network.forge;

import de.leximon.fluidlogged.Fluidlogged;
import de.leximon.fluidlogged.FluidloggedForge;
import de.leximon.fluidlogged.network.ClientPacketHandler;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundSectionFluidsUpdatePacket {

    private final SectionPos sectionPos;
    private final short[] positions;
    private final FluidState[] states;

    private ClientboundSectionFluidsUpdatePacket(SectionPos sectionPos, short[] positions, FluidState[] states) {
        this.sectionPos = sectionPos;
        this.positions = positions;
        this.states = states;
    }

    public ClientboundSectionFluidsUpdatePacket(SectionPos sectionPos, ShortSet shortSet, LevelChunkSection levelChunkSection) {
        int length = shortSet.size();
        this.sectionPos = sectionPos;
        this.positions = new short[length];
        this.states = new FluidState[length];

        int index = 0;
        for(short pos : shortSet) {
            this.positions[index] = pos;
            this.states[index] = levelChunkSection.getFluidState(
                    SectionPos.sectionRelativeX(pos),
                    SectionPos.sectionRelativeY(pos),
                    SectionPos.sectionRelativeZ(pos)
            );
            index++;
        }
    }

    public void apply(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSectionFluidsUpdate(this.sectionPos, this.positions, this.states)));
        context.get().setPacketHandled(true);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeLong(this.sectionPos.asLong());
        buf.writeVarInt(this.positions.length);

        for(int i = 0; i < this.positions.length; i++)
            buf.writeVarLong((long) Fluidlogged.getFluidId(this.states[i]) << 12 | (long) this.positions[i]);
    }

    public static ClientboundSectionFluidsUpdatePacket read(FriendlyByteBuf buf) {
        SectionPos sectionPos = SectionPos.of(buf.readLong());
        int length = buf.readVarInt();
        short[] positions = new short[length];
        FluidState[] states = new FluidState[length];

        for(int j = 0; j < length; ++j) {
            long idAndPos = buf.readVarLong();
            positions[j] = (short)((int) (idAndPos & 0xFFF));
            states[j] = FluidloggedForge.fluidStateIdMapper.byId((int) (idAndPos >>> 12));
        }

        return new ClientboundSectionFluidsUpdatePacket(sectionPos, positions, states);
    }
}
