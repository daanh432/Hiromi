package nl.daanh.hiromi.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.daanh.hiromi.music.PlayerManager;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPauseListener extends ListenerAdapter {
    private static final Timer timer = new Timer();
    private static final HashMap<Long, TimerTask> timerTasks = new HashMap<>();

    /**
     * Checks if music playback should be paused
     *
     * @param channelLeft the channel that was left
     * @param guild       the guild where the event happened
     */
    private void pauseMusicHandler(@Nonnull VoiceChannel channelLeft, @Nonnull Guild guild) {
        if (channelLeft.getMembers().size() == 1) {
            PlayerManager playerManager = PlayerManager.getInstance();
            VoiceChannel musicChannel = guild.getAudioManager().getConnectedChannel();
            if (musicChannel != null && musicChannel.equals(channelLeft)) {
                this.pauseMusic(playerManager, guild);
            }
        }
    }

    public static void scheduleDisconnect(Guild guild) {
        if (MusicPauseListener.timerTasks.containsKey(guild.getIdLong())) return;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (guild.getAudioManager().getConnectedChannel() != null) {
                    PlayerManager playerManager = PlayerManager.getInstance();
                    TextChannel announceChannel = playerManager.getLastChannel(guild);
                    if (announceChannel != null)
                        announceChannel.sendMessage("It looks like I'm no longer needed. Feel free to summon me again! Bye bye :wave:").queue();
                    playerManager.purge(guild);
                    guild.getAudioManager().closeAudioConnection();
                }
            }
        };

        MusicPauseListener.timer.schedule(task, 300000);
        MusicPauseListener.timerTasks.put(guild.getIdLong(), task);
    }

    @Override
    public void onGuildVoiceLeave(@Nonnull GuildVoiceLeaveEvent event) {
        this.pauseMusicHandler(event.getChannelLeft(), event.getGuild());
    }

    @Override
    public void onGuildVoiceMove(@Nonnull GuildVoiceMoveEvent event) {
        this.pauseMusicHandler(event.getChannelLeft(), event.getGuild());
        if (event.getMember().equals(event.getGuild().getSelfMember())) {
            this.pauseMusic(PlayerManager.getInstance(), event.getGuild());
        }
    }

    public static void unScheduleDisconnect(Guild guild) {
        TimerTask task = MusicPauseListener.timerTasks.remove(guild.getIdLong());
        if (task != null) task.cancel();
    }

    /**
     * Pauses music playback
     *
     * @param playerManager a playerManager instance
     * @param guild         the guild where the event happened
     */
    private void pauseMusic(@Nonnull PlayerManager playerManager, @Nonnull Guild guild) {
        TextChannel announceChannel = playerManager.getLastChannel(guild);
        if (announceChannel != null && announceChannel.canTalk() && !playerManager.isPaused(guild)) {
            announceChannel.sendMessage("Pausing music playback because everyone left the channel.").queue();
        }
        MusicPauseListener.scheduleDisconnect(guild);
        playerManager.setPaused(guild, true);
    }
}
