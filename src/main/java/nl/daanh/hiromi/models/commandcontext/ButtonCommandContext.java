package nl.daanh.hiromi.models.commandcontext;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageUpdateAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import nl.daanh.hiromi.models.configuration.IHiromiConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ButtonCommandContext implements IButtonCommandContext {
    private final ButtonClickEvent event;
    private final IHiromiConfig configuration;
    private final List<String> args;

    public ButtonCommandContext(ButtonClickEvent event, String[] data, IHiromiConfig configuration) {
        this.event = event;
        this.configuration = configuration;
        this.args = Arrays.asList(data).subList(3, data.length);
    }

    @Override
    public List<String> getArgs() {
        return this.args;
    }

    @Override
    public void replyInstant(String content) {
        this.event.getHook().editOriginal(content)
                .setActionRows()
                .queue();
    }

    @Override
    public IHiromiConfig getConfiguration() {
        return this.configuration;
    }

    @Override
    public ButtonClickEvent getEvent() {
        return this.event;
    }

    @Override
    public WebhookMessageUpdateAction<Message> reply(@NotNull String content) {
        return this.event.getHook().editOriginal(content);
    }

    @Override
    public WebhookMessageUpdateAction<Message> reply(@NotNull Message content) {
        return this.event.getHook().editOriginal(content);
    }

    @Override
    public Guild getGuild() {
        return this.event.getGuild();
    }

    @Override
    public Member getMember() {
        return this.event.getMember();
    }

    @Override
    public JDA getJDA() {
        return this.event.getJDA();
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

// TODO Music manager
//    @Override
//    public GuildMusicManager getGuildMusicManager() {
//        return MusicManager.getInstance().getGuildAudioPlayer(this.getGuild());
//    }

    @Override
    public TextChannel getChannel() {
        return this.getEvent().getTextChannel();
    }
}
