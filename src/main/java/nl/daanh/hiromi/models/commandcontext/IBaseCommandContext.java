package nl.daanh.hiromi.models.commandcontext;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.sharding.ShardManager;
import nl.daanh.hiromi.models.configuration.IHiromiConfig;

public interface IBaseCommandContext {
    Guild getGuild();

    IHiromiConfig getConfiguration();

    void replyInstant(String content);

    Member getMember();

    JDA getJDA();

    User getSelfUser();

    Member getSelfMember();

    ShardManager getShardManager();

    AudioManager getAudioManager();

// TODO Guild music manager
//    GuildMusicManager getGuildMusicManager();

    TextChannel getChannel();
}
