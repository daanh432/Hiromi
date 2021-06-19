package nl.daanh.hiromi.database;

import net.dv8tion.jda.api.entities.Guild;
import nl.daanh.hiromi.models.commands.annotations.CommandCategory;

import java.util.List;

public interface IDatabaseManager {
    String getPrefix(Guild guild);

    void setPrefix(Guild guild, String prefix);

    List<CommandCategory.CATEGORY> getEnabledCategories(Guild guild);

    boolean getCategoryEnabled(Guild guild, CommandCategory.CATEGORY category);

    void setCategoryEnabled(Guild guild, CommandCategory.CATEGORY category, boolean enabled);
}
