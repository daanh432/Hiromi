package nl.daanh.hiromi.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.TextChannel;

public class GuildMusicManager {
    public final AudioPlayer player;

    public final TrackScheduler scheduler;

    private TextChannel lastChannel;

    public GuildMusicManager(AudioPlayerManager manager) {
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(this.player, this);
        this.player.addListener(this.scheduler);
    }

    public TextChannel getLastChannel() {
        return this.lastChannel;
    }

    public void setLastChannel(TextChannel lastChannel) {
        this.lastChannel = lastChannel;
    }

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }
}
