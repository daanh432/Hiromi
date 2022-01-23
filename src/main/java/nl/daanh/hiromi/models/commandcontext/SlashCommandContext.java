package nl.daanh.hiromi.models.commandcontext;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import nl.daanh.hiromi.models.configuration.IHiromiConfig;
import org.jetbrains.annotations.NotNull;

public class SlashCommandContext implements ISlashCommandContext {
    private final SlashCommandEvent event;
    private final IHiromiConfig configuration;

    public SlashCommandContext(SlashCommandEvent event, IHiromiConfig configuration) {
        this.event = event;
        this.configuration = configuration;
    }

    @Override
    public IHiromiConfig getConfiguration() {
        return this.configuration;
    }

    @Override
    public SlashCommandEvent getEvent() {
        return this.event;
    }

    @Override
    public ReplyAction reply(@NotNull String content) {
        return this.event.reply(content);
    }

    @Override
    public ReplyAction reply(@NotNull Message content) {
        return this.event.reply(content);
    }

    @Override
    public void replyInstant(String content) {
        this.event.reply(content).setEphemeral(true).queue();
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

// TODO Guild music manager
//    @Override
//    public GuildMusicManager getGuildMusicManager() {
//        return MusicManager.getInstance().getGuildAudioPlayer(this.getGuild());
//    }

    @Override
    public TextChannel getChannel() {
        return this.getEvent().getTextChannel();
    }
}
