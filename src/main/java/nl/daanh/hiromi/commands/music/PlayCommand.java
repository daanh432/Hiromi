package nl.daanh.hiromi.commands.music;


import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.daanh.hiromi.commands.annotations.CommandCategory;
import nl.daanh.hiromi.commands.annotations.CommandHelp;
import nl.daanh.hiromi.commands.annotations.CommandInvoke;
import nl.daanh.hiromi.commands.annotations.SelfPermission;
import nl.daanh.hiromi.commands.context.CommandContext;
import nl.daanh.hiromi.commands.context.CommandInterface;
import nl.daanh.hiromi.music.PlayerManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@CommandInvoke("play")
@CommandInvoke("p")
@CommandInvoke("add")
@CommandHelp("Plays a song from a youtube video or playlist.")
@CommandCategory(CommandCategory.CATEGORY.MUSIC)
@SelfPermission(Permission.MESSAGE_WRITE)
@SelfPermission(Permission.VOICE_CONNECT)
@SelfPermission(Permission.VOICE_SPEAK)
public class PlayCommand implements CommandInterface {
    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getChannel();
        List<String> args = ctx.getArgs();
        Member member = ctx.getMember();
        Member selfMember = ctx.getSelfMember();
        String input = String.join(" ", args);

        if (selfMember.getVoiceState() == null || !selfMember.getVoiceState().inVoiceChannel()) {
            channel.sendMessage("Well. I'm going to have to be in a voice channel to player audio").queue();
            return;
        }

        if (member.getVoiceState() == null || selfMember.getVoiceState().getChannel() != member.getVoiceState().getChannel()) {
            channel.sendMessage("You have to be in the voice channel to add songs.").queue();
            return;
        }

        if (args.isEmpty()) {
            channel.sendMessage("Please specify a url or song title you want me to play.").queue();
            return;
        }

        if (!isUrl(input)) {
            channel.sendMessage("Please specify a valid url").queue();
        }

        PlayerManager manager = PlayerManager.getInstance();
        manager.setLastChannel(ctx.getGuild(), ctx.getChannel());
        manager.loadAndPlay(channel, input);

    }

    private boolean isUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (MalformedURLException exception) {
            return false;
        }
    }
}
