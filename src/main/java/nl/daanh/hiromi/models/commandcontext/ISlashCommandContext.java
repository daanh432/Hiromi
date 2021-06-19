package nl.daanh.hiromi.models.commandcontext;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import net.dv8tion.jda.api.sharding.ShardManager;
import nl.daanh.hiromi.lavaplayer.GuildMusicManager;
import nl.daanh.hiromi.models.configuration.IConfiguration;

import javax.annotation.Nonnull;

public interface ISlashCommandContext extends IBaseCommandContext {
    SlashCommandEvent getEvent();

    ReplyAction reply(@Nonnull String content);

    ReplyAction reply(@Nonnull Message content);
}
