package nl.daanh.hiromi.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrackScheduler.class);
    private static final int QUEUE_SIZE = 50;
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private final GuildMusicManager guildMusicManager;

    public TrackScheduler(AudioPlayer player, GuildMusicManager guildMusicManager) {
        this.player = player;
        this.guildMusicManager = guildMusicManager;
        this.queue = new LinkedBlockingQueue<>();
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return this.queue;
    }

    public int getQueueSize() {
        return this.getQueue().size();
    }

    public int getMaxQueueSize() {
        return QUEUE_SIZE;
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track The track to play or add to queue.
     */
    public void queue(AudioTrack track) throws QueueToBigException {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.

        if (this.getQueueSize() >= this.getMaxQueueSize()) {
            throw new QueueToBigException("Queue size limit has been reached / surpassed");
        }

        if (this.player.getPlayingTrack() != null) {
            this.queue.offer(track);
        } else {
            this.player.playTrack(track);
        }
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        AudioTrack track = this.queue.poll();
        while (track == this.player.getPlayingTrack()) {
            track = this.queue.poll();
        }
        this.player.startTrack(this.queue.poll(), false);
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        // A track started playing
        try {
            this.guildMusicManager.getLastChannel().getGuild().getSelfMember().deafen(true).queue();
        } catch (InsufficientPermissionException e) {
            this.guildMusicManager.getLastChannel().sendMessage("Please give me the permissions to server deafen myself.\n" +
                    "This will increase the performance on your and everyone else's audio playback experience.").queue();
        } catch (Exception ignore) {
            // ignore
        }
        this.guildMusicManager.getLastChannel().sendMessage(String.format("Now playing the track: ``%s``", track.getInfo().title)).queue();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            // Start next track
            this.nextTrack();
            this.guildMusicManager.getLastChannel().sendMessage("The track has ended").queue();
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
        this.guildMusicManager.getLastChannel().sendMessage("Something went wrong in audio playback.").queue();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
        this.guildMusicManager.getLastChannel().sendMessage("Looks like the track got stuck. Lets try to go to the next song.").queue();
        this.nextTrack();
    }
}

class QueueToBigException extends Exception {
    public QueueToBigException(String message) {
        super(message);
    }
}
