package com.github.philipkoivunen.quality_quests.commands.handlers;

import se.hornta.commando.ValidationResult;
import se.hornta.commando.completers.IArgumentHandler;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.Playlist;
import com.github.philipkoivunen.quality_quests.objects.Playlists;
import org.bukkit.command.CommandSender;
import se.hornta.messenger.MessageManager;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayListHandler implements IArgumentHandler {
    private final Playlists playlists;

    public PlayListHandler(Playlists playlists) {
        this.playlists = playlists;
    }

    @Override
    public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
        return this.playlists.getplaylists()
                .stream()
                .filter(playlist -> playlist.playListName.toLowerCase(Locale.ENGLISH).startsWith(argument.toLowerCase(Locale.ENGLISH)))
                .map(Playlist::getName)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean test(Set<String> items, String argument) {
        return items.contains(argument);
    }

    @Override
    public void whenInvalid(ValidationResult result) {
        MessageManager.sendMessage(result.getCommandSender(), MessageConstants.CREATE_QUEST_FAILED_PARAM);
    }
}
