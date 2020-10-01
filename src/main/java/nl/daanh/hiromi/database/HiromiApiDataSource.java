package nl.daanh.hiromi.database;

import net.dv8tion.jda.api.entities.Guild;
import nl.daanh.hiromi.commands.annotations.CommandCategory;

import java.util.List;

public class HiromiApiDataSource implements DatabaseManager {
    @Override
    public String getPrefix(Guild guild) {
        return null;
    }

    @Override
    public void setPrefix(Guild guild, String prefix) {

    }

    @Override
    public boolean getMusicEnabled(Guild guild) {
        return false;
    }

    @Override
    public void setMusicEnabled(Guild guild, boolean enabled) {

    }

    @Override
    public boolean getFunEnabled(Guild guild) {
        return false;
    }

    @Override
    public void setFunEnabled(Guild guild, boolean enabled) {

    }

    @Override
    public boolean getModerationEnabled(Guild guild) {
        return false;
    }

    @Override
    public void setModerationEnabled(Guild guild, boolean enabled) {

    }

    @Override
    public List<CommandCategory.CATEGORY> getEnabledCategories(Guild guild) {
        return List.of(CommandCategory.CATEGORY.OTHER);
    }
}
