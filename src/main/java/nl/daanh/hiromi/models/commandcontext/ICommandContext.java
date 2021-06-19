package nl.daanh.hiromi.models.commandcontext;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.List;

public interface ICommandContext extends IBaseCommandContext {
    GuildMessageReceivedEvent getEvent();

    List<String> getArgs();

    Message getMessage();

    MessageAction reply(String content);

    MessageAction reply(Message content);
}
