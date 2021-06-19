package nl.daanh.hiromi.models.commandcontext;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

import javax.annotation.Nonnull;

public interface ISlashCommandContext extends IBaseCommandContext {
    SlashCommandEvent getEvent();

    ReplyAction reply(@Nonnull String content);

    ReplyAction reply(@Nonnull Message content);
}
