/**
 * package de.leximon.fluidlogged.network;
 *
 * import de.leximon.fluidlogged.Fluidlogged;
 * import it.unimi.dsi.fastutil.shorts.ShortSet;
 * import net.minecraft.core.SectionPos;
 * import net.minecraft.network.FriendlyByteBuf;
 * import net.minecraft.world.level.chunk.LevelChunkSection;
 * import net.minecraft.world.level.material.FluidState;
 *
 * public class ClientboundSectionFluidsUpdatePacket {
 *
 *     private final SectionPos sectionPos;
 *     private final short[] positions;
 *     private final FluidState[] states;
 *
 *     private ClientboundSectionFluidsUpdatePacket(SectionPos sectionPos, short[] positions, FluidState[] states) {
 *         this.sectionPos = sectionPos;
 *         this.positions = positions;
 *         this.states = states;
 *     }
 *
 *     public ClientboundSectionFluidsUpdatePacket(SectionPos sectionPos, ShortSet shortSet, LevelChunkSection levelChunkSection) {
 *         int length = shortSet.size();
 *         this.sectionPos = sectionPos;
 *         this.positions = new short[length];
 *         this.states = new FluidState[length];
 *
 *         int index = 0;
 *         for(short pos : shortSet) {
 *             this.positions[index] = pos;
 *             this.states[index] = levelChunkSection.getFluidState(
 *               SectionPos.sectionRelativeX(pos),
 *               SectionPos.sectionRelativeY(pos),
 *               SectionPos.sectionRelativeZ(pos)
 *             );
 *             index++;
 *         }
 *     }
 *
 *     public void apply(Supplier<NetworkEvent.Context> context) {
 *         context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleSectionFluidsUpdate(this.sectionPos, this.positions, this.states)));
 *         context.get().setPacketHandled(true);
 *     }
 *
 *     public void write(FriendlyByteBuf buf) {
 *         buf.writeLong(this.sectionPos.asLong());
 *         buf.writeVarInt(this.positions.length);
 *
 *         for(int i = 0; i < this.positions.length; i++)
 *             buf.writeVarLong((long) Fluidlogged.getFluidId(this.states[i]) << 12 | (long) this.positions[i]);
 *     }
 *
 *     public static ClientboundSectionFluidsUpdatePacket read(FriendlyByteBuf buf) {
 *         SectionPos sectionPos = SectionPos.of(buf.readLong());
 *         int length = buf.readVarInt();
 *         short[] positions = new short[length];
 *         FluidState[] states = new FluidState[length];
 *
 *         for(int j = 0; j < length; ++j) {
 *             long idAndPos = buf.readVarLong();
 *             positions[j] = (short)((int) (idAndPos & 0xFFF));
 *             states[j] = FluidloggedForge.fluidStateIdMapper.byId((int) (idAndPos >>> 12));
 *         }
 *
 *         return new ClientboundSectionFluidsUpdatePacket(sectionPos, positions, states);
 *     }
 * }
 */
package de.leximon.fluidlogged.network;

import de.leximon.fluidlogged.Fluidlogged;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import java.util.List;

public class ClientboundSectionFluidsUpdatePacket implements CustomPacketPayload {
    
    public static final ResourceLocation ID = Fluidlogged.id("section_fluids_update");
    public static final Type<ClientboundSectionFluidsUpdatePacket> PACKET_TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, ClientboundSectionFluidsUpdatePacket> STREAM_CODEC = CustomPacketPayload.codec(
      ClientboundSectionFluidsUpdatePacket::write,
      ClientboundSectionFluidsUpdatePacket::new
    );
    
    private final SectionPos sectionPos;
    private final short[] positions;
    private final FluidState[] states;
    
    public ClientboundSectionFluidsUpdatePacket(FriendlyByteBuf buf) {
        this.sectionPos = SectionPos.of(buf.readLong());
        int length = buf.readVarInt();
        this.positions = new short[length];
        this.states = new FluidState[length];
        
        for(int j = 0; j < length; ++j) {
            long idAndPos = buf.readVarLong();
            this.positions[j] = (short)((int) (idAndPos & 0xFFF));
            this.states[j] = Fluid.FLUID_STATE_REGISTRY.byId((int) (idAndPos >>> 12));
        }
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
    
    public static void apply(ClientboundSectionFluidsUpdatePacket packet) {
        ClientPacketHandler.handleSectionFluidsUpdate(packet.sectionPos, packet.positions, packet.states);
    }
    
    public void write(FriendlyByteBuf buf) {
        buf.writeLong(this.sectionPos.asLong());
        buf.writeVarInt(this.positions.length);
        
        for(int i = 0; i < this.positions.length; i++) {
            buf.writeVarLong((long) Fluidlogged.getFluidId(this.states[i]) << 12 | (long) this.positions[i]);
        }
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
