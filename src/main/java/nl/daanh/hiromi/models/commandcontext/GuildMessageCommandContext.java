package nl.daanh.hiromi.models.commandcontext;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import nl.daanh.hiromi.lavaplayer.GuildMusicManager;
import nl.daanh.hiromi.lavaplayer.MusicManager;
import nl.daanh.hiromi.models.configuration.IConfiguration;

import java.util.List;

public class GuildMessageCommandContext implements ICommandContext {
    private final GuildMessageReceivedEvent event;
    private final List<String> args;
    private final IConfiguration configuration;

    public GuildMessageCommandContext(GuildMessageReceivedEvent event, List<String> args, IConfiguration configuration) {
        this.event = event;
        this.args = args;
        this.configuration = configuration;
    }

    public List<String> getArgs() {
        return this.args;
    }

    @Override
    public IConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void replyInstant(String content) {
        this.event.getMessage().reply(content).queue();
    }

    @Override
    public GuildMessageReceivedEvent getEvent() {
        return this.event;
    }

    @Override
    public Guild getGuild() {
        return this.getEvent().getGuild();
    }

    @Override
    public TextChannel getChannel() {
        return this.getEvent().getChannel();
    }

    @Override
    public Message getMessage() {
        return this.getEvent().getMessage();
    }

    @Override
    public Member getMember() {
        return this.getEvent().getMember();
    }

    @Override
    public JDA getJDA() {
        return this.getEvent().getJDA();
    }

    @Override
    public User getSelfUser() {
        return this.getJDA().getSelfUser();
    }

    @Override
    public Member getSelfMember() {
        return this.getGuild().getSelfMember();
    }

    @Override
    public ShardManager getShardManager() {
        return this.getJDA().getShardManager();
    }

    @Override
    public AudioManager getAudioManager() {
        return this.getGuild().getAudioManager();
    }

    @Override
    public GuildMusicManager getGuildMusicManager() {
        return MusicManager.getInstance().getGuildAudioPlayer(this.getGuild());
    }

    @Override
    public MessageAction reply(String content) {
        return this.getChannel().sendMessage(content);
    }

    @Override
    public MessageAction reply(Message message) {
        return this.getChannel().sendMessage(message);
    }
}
