package nl.daanh.hiromi.models.configuration;

import nl.daanh.hiromi.database.IDatabaseManager;

import java.awt.*;

public interface IHiromiConfig {
    static IHiromiConfig getInstance() {
        return BaseHiromiConfig.getInstance();
    }

    Color getEmbedColor();

    String getToken();

    int getTotalShards();

    String getGlobalPrefix();

    String getStatusText();

    boolean getLavalinkEnabled();

    boolean getMusicEnabled();

    IDatabaseManager getDatabaseManager();

    String getGithubLink();

    String getApiToken();
}
