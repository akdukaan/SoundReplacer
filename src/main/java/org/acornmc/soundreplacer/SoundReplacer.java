package org.acornmc.soundreplacer;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Sound;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SoundReplacer extends JavaPlugin {
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        Config.reload(this);
        protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(
                this,
                ListenerPriority.NORMAL,
                PacketType.Play.Server.NAMED_SOUND_EFFECT,
                PacketType.Play.Server.ENTITY_SOUND
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                List<Sound> soundEffects = packet.getSoundEffects().getValues();
                for (int i = 0; i < soundEffects.size(); i++) {
                    Sound sound = soundEffects.get(i);
                    sound = Config.SOUNDS.get(sound);
                    packet.getSoundEffects().write(i, sound);
                }
            }
        });
    }
}
