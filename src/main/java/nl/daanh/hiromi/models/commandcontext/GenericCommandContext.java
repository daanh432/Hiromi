package nl.daanh.hiromi.models.commandcontext;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.sharding.ShardManager;
import nl.daanh.hiromi.models.configuration.IHiromiConfig;
import org.jetbrains.annotations.NotNull;

public class GenericCommandContext implements IGenericCommandContext {
    private final Guild guild;
    private final TextChannel textChannel;
    private final Member member;
    private final Event event;
    private final IHiromiConfig configuration;

    public GenericCommandContext(GuildMessageReceivedEvent event, IHiromiConfig configuration) {
        this.guild = event.getGuild();
        this.textChannel = event.getChannel();
        this.member = event.getMember();
        this.event = event;
        this.configuration = configuration;
    }


    public GenericCommandContext(SlashCommandEvent event, IHiromiConfig configuration) {
        this.guild = event.getGuild();
        this.textChannel = event.getTextChannel();
        this.member = event.getMember();
        this.event = event;
        this.configuration = configuration;
    }

    @Override
    public IHiromiConfig getConfiguration() {
        return this.configuration;
    }

    @Override
    public void reply(@NotNull String content) {
        if (this.event instanceof SlashCommandEvent) {
            SlashCommandEvent e = (SlashCommandEvent) this.event;
            e.reply(content).setEphemeral(true).queue();
            return;
        }

        this.getChannel().sendMessage(content).queue();
    }

    @Override
    public void reply(@NotNull Message content) {
        if (this.event instanceof SlashCommandEvent) {
            SlashCommandEvent e = (SlashCommandEvent) this.event;
            e.reply(content).queue();
            return;
        }

        this.getChannel().sendMessage(content).queue();
    }

    @Override
    public void replyInstant(String content) {
        if (this.event instanceof SlashCommandEvent) {
            SlashCommandEvent e = (SlashCommandEvent) this.event;
            e.reply(content).setEphemeral(true).queue();
            return;
        }

        this.getChannel().sendMessage(content).queue();
    }

    @Override
    public Guild getGuild() {
        return this.guild;
    }

    @Override
    public Member getMember() {
        return this.member;
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
        return this.textChannel;
    }
}
