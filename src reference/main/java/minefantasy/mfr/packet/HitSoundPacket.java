package minefantasy.mfr.packet;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import minefantasy.mfr.config.ConfigClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class HitSoundPacket extends PacketMF {
    public static final String packetName = "MF2_Hitsound";
    private String sound;
    private int entId;

    public HitSoundPacket(String sound, Entity hit) {
        this.sound = sound;
        this.entId = hit.getEntityId();
    }

    public HitSoundPacket() {
    }

    @Override
    public void process(ByteBuf packet, EntityPlayer player) {
        entId = packet.readInt();
        sound = ByteBufUtils.readUTF8String(packet);
        Entity entity = player.world.getEntityByID(entId);
        if (entity != null) {
            if (ConfigClient.playHitsound) {
                entity.world.playSound(entity.posX, entity.posY, entity.posZ, sound, 1.0F, 1.0F, false);
            }
        }
    }

    @Override
    public String getChannel() {
        return packetName;
    }

    @Override
    public void write(ByteBuf packet) {
        packet.writeInt(entId);
        ByteBufUtils.writeUTF8String(packet, sound);
    }
}
