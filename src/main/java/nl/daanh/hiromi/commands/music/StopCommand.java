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
import nl.daanh.hiromi.music.PlayerManager;

@CommandInvoke("stop")
@CommandInvoke("purge")
@CommandInvoke("clear")
@CommandCategory(CommandCategory.CATEGORY.MUSIC)
@CommandHelp("Stops playback, leaves the voice channel and purgers the queue.")
@SelfPermission(Permission.MESSAGE_WRITE)
@SelfPermission(Permission.VOICE_CONNECT)
@SelfPermission(Permission.VOICE_SPEAK)
public class StopCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        Guild guild = ctx.getGuild();
        Member member = ctx.getMember();
        Member selfMember = ctx.getSelfMember();

        if (selfMember.getVoiceState() == null || !selfMember.getVoiceState().inVoiceChannel()) {
            channel.sendMessage("Well. There's nothing for me to stop or purge I'm afraid.").queue();
            return;
        }

        if (member.getVoiceState() == null || selfMember.getVoiceState().getChannel() != member.getVoiceState().getChannel()) {
            channel.sendMessage("You have to be in the voice channel to add songs.").queue();
            return;
        }

        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.purge(guild);
        guild.getAudioManager().closeAudioConnection();
        channel.sendMessage("Stopping and clearing queue.").queue();
    }
}
