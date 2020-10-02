package nl.daanh.hiromi.commands.music;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;
import nl.daanh.hiromi.commands.annotations.*;
import nl.daanh.hiromi.commands.context.CommandContext;
import nl.daanh.hiromi.commands.context.CommandInterface;
import nl.daanh.hiromi.music.PlayerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@CommandInvoke("queue")
@CommandInvoke("queued")
@CommandHelp("Shows you a list of the currently queued songs.")
@CommandCategory(CommandCategory.CATEGORY.MUSIC)
@CommandArgument(value = "page number", type = CommandArgument.TYPE.INTEGER)
@SelfPermission(Permission.MESSAGE_WRITE)
@SelfPermission(Permission.MESSAGE_ADD_REACTION)
@SelfPermission(Permission.MESSAGE_MANAGE)
@SelfPermission(Permission.VOICE_CONNECT)
@SelfPermission(Permission.VOICE_SPEAK)
public class QueueCommand implements CommandInterface {
    private final Paginator.Builder pageBuilder;

    public QueueCommand(EventWaiter eventWaiter) {
        this.pageBuilder = new Paginator.Builder().setColumns(1)
                .setItemsPerPage(20)
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .useNumberedItems(true)
                .setFinalAction(m -> {
                    try {
                        m.clearReactions().queue();
                    } catch (PermissionException ex) {
                        m.delete().queue();
                    }
                })
                .setEventWaiter(eventWaiter)
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    public void handle(CommandContext ctx) {
        List<String> args = ctx.getArgs();
        TextChannel channel = ctx.getChannel();
        Guild guild = ctx.getGuild();
        Member member = ctx.getMember();
        Member selfMember = ctx.getSelfMember();
        PlayerManager playerManager = PlayerManager.getInstance();
        BlockingQueue<AudioTrack> queue = playerManager.getQueue(guild);

        if (queue.isEmpty()) {
            channel.sendMessage("It loks like nothing is queued. Try adding some songs to the queue.").queue();
            return;
        }

        List<AudioTrack> tracks = new ArrayList<>(queue);
        int currentPage = 0;
        int pageSize = 20;
        int pageCount = (int) Math.ceil(tracks.size() / (double) pageSize);

        if (!args.isEmpty()) {
            try {
                int specifiedPage = Integer.parseInt(args.get(0)) - 1;
                if (specifiedPage >= 0 && specifiedPage < pageCount) {
                    currentPage = specifiedPage;
                } else {
                    channel.sendMessage("The specified page is out of range.").queue();
                    return;
                }
            } catch (NumberFormatException e) {
                channel.sendMessage("The specified page is not a valid page number").queue();
                return;
            }
        }

        this.pageBuilder.clearItems();

        long totalDuration = 0;

        for (AudioTrack track : tracks) {
            AudioTrackInfo trackInfo = track.getInfo();
            totalDuration += trackInfo.length;
            String trackDuration = String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(trackInfo.length),
                    TimeUnit.MILLISECONDS.toSeconds(trackInfo.length) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(trackInfo.length))
            );

            this.pageBuilder.addItems(String.format("%s - (%s)", trackInfo.title, trackDuration));
        }

        long hours = TimeUnit.MILLISECONDS.toHours(totalDuration);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(totalDuration) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(totalDuration) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);

        String totalDurationString = String.format("%h h, %d min, %d sec", hours, minutes, seconds);

        if (pageCount > 1) {
            this.pageBuilder.setText(String.format("Total queue length: %s.\n" +
                    "Use the buttons down below to switch pages.", totalDurationString));
        } else {
            this.pageBuilder.setText(String.format("Total queue length: %s.", totalDurationString));
        }

        Paginator p = this.pageBuilder
                .setUsers(member.getUser())
                .build();

        p.paginate(channel, currentPage);
    }
}
