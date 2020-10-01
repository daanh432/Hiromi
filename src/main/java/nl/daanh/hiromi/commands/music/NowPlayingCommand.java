package nl.daanh.hiromi.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromi.commands.annotations.CommandCategory;
import nl.daanh.hiromi.commands.annotations.CommandHelp;
import nl.daanh.hiromi.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.commands.annotations.SelfPermission;
import nl.daanh.hiromi.commands.context.CommandContext;
import nl.daanh.hiromi.commands.context.CommandInterface;
import nl.daanh.hiromi.music.PlayerManager;

import java.util.concurrent.TimeUnit;

@CommandInvoke("nowplaying")
@CommandInvoke("np")
@CommandCategory(CommandCategory.CATEGORY.MUSIC)
@CommandHelp("Shows you the current playing song.")
@SelfPermission(Permission.MESSAGE_WRITE)
@SelfPermission(Permission.VOICE_CONNECT)
@SelfPermission(Permission.VOICE_SPEAK)
public class NowPlayingCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        Member member = ctx.getMember();
        Member selfMember = ctx.getSelfMember();
        Guild guild = ctx.getGuild();
        PlayerManager playerManager = PlayerManager.getInstance();
        AudioTrack playingTrack = playerManager.getPlayingTrack(guild);

        if (playingTrack == null || selfMember.getVoiceState() == null || !selfMember.getVoiceState().inVoiceChannel()) {
            channel.sendMessage("It doens't look like I'm playing any music.").queue();
            return;
        }


        AudioTrackInfo trackInfo = playingTrack.getInfo();
        channel.sendMessage(String.format(
                "**Playing** [%s](%s)\n%s %s - %s",
                trackInfo.title,
                trackInfo.uri,
                playerManager.isPaused(guild) ? "\u23F8" : "\u25B6",
                this.formatTime(playingTrack.getPosition()),
                this.formatTime(playingTrack.getDuration())
        )).queue();
        playerManager.setLastChannel(guild, channel);
    }

    private String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}