package nl.daanh.hiromi.commands.context;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.util.List;

public class CommandContext implements CommandContextInterface {
    private final GuildMessageReceivedEvent event;
    private final List<String> args;

    public CommandContext(GuildMessageReceivedEvent event, List<String> args) {
        this.event = event;
        this.args = args;
    }

    public List<String> getArgs() {
        return this.args;
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
}
