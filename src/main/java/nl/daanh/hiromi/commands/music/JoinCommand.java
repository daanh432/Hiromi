package nl.daanh.hiromi.commands.music;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;
import nl.daanh.hiromi.commands.annotations.CommandCategory;
import nl.daanh.hiromi.commands.annotations.CommandHelp;
import nl.daanh.hiromi.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.commands.annotations.SelfPermission;
import nl.daanh.hiromi.commands.context.CommandContext;
import nl.daanh.hiromi.commands.context.CommandInterface;
import nl.daanh.hiromi.music.PlayerManager;

@CommandInvoke("join")
@CommandInvoke("connect")
@CommandHelp("Lets the bot join your voice channel.")
@CommandCategory(CommandCategory.CATEGORY.MUSIC)
@SelfPermission(Permission.MESSAGE_WRITE)
@SelfPermission(Permission.VOICE_CONNECT)
@SelfPermission(Permission.VOICE_SPEAK)
public class JoinCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        AudioManager audioManager = ctx.getAudioManager();
        Member member = ctx.getMember();
        Member selfMember = ctx.getSelfMember();

        if (audioManager.isConnected()) {
            channel.sendMessage("I am already connected to a voice channel.").queue();
            return;
        }

        if (member.getVoiceState() == null || !member.getVoiceState().inVoiceChannel() || member.getVoiceState().getChannel() == null) {
            channel.sendMessage("I couldn't determine in which voice channel you are.").queue();
            return;
        }

        VoiceChannel voiceChannel = member.getVoiceState().getChannel();

        if (!selfMember.hasPermission(voiceChannel, Permission.VOICE_CONNECT) || !selfMember.hasPermission(voiceChannel, Permission.VOICE_SPEAK)) {
            channel.sendMessage("I'm missing the permission to join or speak in that voice channel.").queue();
            return;
        }

        PlayerManager.getInstance().setLastChannel(ctx.getGuild(), ctx.getChannel());
        audioManager.openAudioConnection(voiceChannel);
        channel.sendMessage(String.format("Joining the voice channel `%s`", voiceChannel.getName())).queue();
        audioManager.setSelfDeafened(true);
    }
}
