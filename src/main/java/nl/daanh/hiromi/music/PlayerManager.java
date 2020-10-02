package nl.daanh.hiromi.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new BeamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());
        this.playerManager = playerManager;
    }

    public static synchronized PlayerManager getInstance() {
        if (INSTANCE == null) INSTANCE = new PlayerManager();
        return INSTANCE;
    }

    public BlockingQueue<AudioTrack> getQueue(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        return guildMusicManager.scheduler.getQueue();
    }

    @Nullable
    public AudioTrack getPlayingTrack(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        return guildMusicManager.player.getPlayingTrack();
    }

    @Nullable
    public TextChannel getLastChannel(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        return guildMusicManager.getLastChannel();
    }

    public void setLastChannel(final Guild guild, TextChannel channel) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        guildMusicManager.setLastChannel(channel);
    }

    public boolean isPaused(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        return guildMusicManager.player.isPaused();
    }

    public void setPaused(final Guild guild, final boolean paused) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        guildMusicManager.player.setPaused(paused);
    }

    public void purge(final Guild guild) {
        GuildMusicManager guildMusicManager = this.getGuildAudioPlayer(guild);
        guildMusicManager.scheduler.getQueue().clear();
        guildMusicManager.player.stopTrack();
        guildMusicManager.player.setPaused(false);
    }

    public void skipTrack(final Guild guild) {
        GuildMusicManager musicManager = this.getGuildAudioPlayer(guild);
        musicManager.scheduler.nextTrack();
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(final Guild guild) {
        long guildId = guild.getIdLong();
        GuildMusicManager musicManager = this.musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(this.playerManager);
            this.musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public void loadAndPlay(final TextChannel textChannel, final String trackUrl) {
        GuildMusicManager musicManager = this.getGuildAudioPlayer(textChannel.getGuild());

        this.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                try {
                    musicManager.scheduler.queue(track);
                } catch (QueueToBigException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().remove(0);
                }

                try {
                    musicManager.scheduler.queue(firstTrack);
                } catch (QueueToBigException e) {
                    e.printStackTrace();
                }

                for (AudioTrack audioTrack : playlist.getTracks()) {
                    try {
                        musicManager.scheduler.queue(audioTrack);
                    } catch (QueueToBigException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void noMatches() {
                // Notify the user that we've got nothing
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                // Notify the user that everything exploded
            }
        });
    }
}
