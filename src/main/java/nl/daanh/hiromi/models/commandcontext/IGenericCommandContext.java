package nl.daanh.hiromi.models.commandcontext;

import net.dv8tion.jda.api.entities.Message;

public interface IGenericCommandContext extends IBaseCommandContext {
    void reply(String content);

    void reply(Message content);
}
