package nl.daanh.hiromi.commands.context;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

public interface CommandContextInterface {
    GuildMessageReceivedEvent getEvent();

    Guild getGuild();

    TextChannel getChannel();

    Message getMessage();

    Member getMember();

    JDA getJDA();

    User getSelfUser();

    Member getSelfMember();

    ShardManager getShardManager();
}
