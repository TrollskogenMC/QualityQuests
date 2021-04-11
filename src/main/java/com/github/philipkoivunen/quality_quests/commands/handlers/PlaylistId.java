package com.github.philipkoivunen.quality_quests.commands.handlers;

import com.github.hornta.commando.ValidationResult;
import com.github.hornta.commando.completers.IArgumentHandler;
import com.github.hornta.messenger.MessageManager;
import com.github.philipkoivunen.quality_quests.QualityQuestsPlugin;
import com.github.philipkoivunen.quality_quests.constants.MessageConstants;
import com.github.philipkoivunen.quality_quests.objects.Playlist;
import com.github.philipkoivunen.quality_quests.objects.Playlists;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class PlaylistId implements IArgumentHandler {
    private QualityQuestsPlugin pluginInstanse;
    private Playlists playlists;

    public PlaylistId() {
        this.pluginInstanse = QualityQuestsPlugin.getInstance();
        this.playlists = pluginInstanse.getPlayLists();
    }

    @Override
    public Set<String> getItems(CommandSender sender, String argument, String[] prevArgs) {
        return this.playlists.getplaylists()
                .stream()
                .filter(playlist -> playlist.id.toString().startsWith(argument.toLowerCase(Locale.ENGLISH)))
                .map(Playlist::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public boolean test(Set<String> items, String argument) {
        return items.contains(argument.toLowerCase());
    }

    @Override
    public void whenInvalid(ValidationResult result) {
        MessageManager.sendMessage(result.getCommandSender(), MessageConstants.CREATE_QUEST_FAILED_PARAM);
    }
}
