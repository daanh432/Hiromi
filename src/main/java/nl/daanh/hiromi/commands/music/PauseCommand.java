package nl.daanh.hiromi.commands.music;

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
import nl.daanh.hiromi.listeners.MusicPauseListener;
import nl.daanh.hiromi.music.PlayerManager;

@CommandInvoke("pause")
@CommandInvoke("ps")
@CommandCategory(CommandCategory.CATEGORY.MUSIC)
@CommandHelp("Pauses playback for you")
@SelfPermission(Permission.MESSAGE_WRITE)
@SelfPermission(Permission.VOICE_CONNECT)
@SelfPermission(Permission.VOICE_SPEAK)
public class PauseCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        Guild guild = ctx.getGuild();
        TextChannel channel = ctx.getChannel();
        Member selfMember = ctx.getSelfMember();
        Member member = ctx.getMember();
        PlayerManager playerManager = PlayerManager.getInstance();

        if (selfMember.getVoiceState() == null || !selfMember.getVoiceState().inVoiceChannel()) {
            channel.sendMessage("Well. There's nothing for me to pause I'm afraid.").queue();
            return;
        }

        if (member.getVoiceState() == null || selfMember.getVoiceState().getChannel() != member.getVoiceState().getChannel()) {
            channel.sendMessage("You have to be in the voice channel to add songs.").queue();
            return;
        }

        if (playerManager.isPaused(guild)) {
            channel.sendMessage("Hey! I'm already paused no need to tell me a bedtime story.").queue();
            return;
        }

        channel.sendMessage("*yawns* I'm going to take a quick nap now. I'm pausing playback for you.").queue();
        playerManager.setPaused(guild, true);
        playerManager.setLastChannel(guild, channel);
        MusicPauseListener.scheduleDisconnect(guild);
    }
}
