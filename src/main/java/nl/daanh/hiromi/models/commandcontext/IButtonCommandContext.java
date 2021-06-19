package nl.daanh.hiromi.models.commandcontext;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction;

import javax.annotation.Nonnull;
import java.util.List;

public interface IButtonCommandContext extends IBaseCommandContext {
    ButtonClickEvent getEvent();

    List<String> getArgs();

    WebhookMessageUpdateAction reply(@Nonnull String content);

    WebhookMessageUpdateAction reply(@Nonnull Message content);
}
