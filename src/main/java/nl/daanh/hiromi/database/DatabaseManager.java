package nl.daanh.hiromi.database;

import net.dv8tion.jda.api.entities.Guild;
import nl.daanh.hiromi.commands.annotations.CommandCategory;

import java.util.List;

public interface DatabaseManager {
    DatabaseManager instance = new HiromiApiDataSource();

    String getPrefix(Guild guild);

    void setPrefix(Guild guild, String prefix);

    boolean getMusicEnabled(Guild guild);

    void setMusicEnabled(Guild guild, boolean enabled);

    boolean getFunEnabled(Guild guild);

    void setFunEnabled(Guild guild, boolean enabled);

    boolean getModerationEnabled(Guild guild);

    void setModerationEnabled(Guild guild, boolean enabled);

    List<CommandCategory.CATEGORY> getEnabledCategories(Guild guild);
}
