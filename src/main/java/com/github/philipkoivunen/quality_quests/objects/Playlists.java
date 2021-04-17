package com.github.philipkoivunen.quality_quests.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Playlists {
    private List<Playlist> playlists;

    public Playlists() {
        this.playlists = new ArrayList<>();
    }

    public void delete(Playlist playList) {
        List<Playlist> newPlayListList = new ArrayList<>();
        for (Playlist pl : playlists) {
            if(pl.id.equals(playList.id) == false) {
                newPlayListList.add(pl);
            }
        }
        
        this.setList(newPlayListList);
    }

    public void setList(List<Playlist> newPlayListList) {
        this.playlists = newPlayListList;
    }

    public void addPlayList(Playlist playList) {
        if(this.playlists == null || this.playlists.size() < 1) {
            this.playlists.add(playList);
        } else {
            Boolean hasFound = false;

            for(Playlist pl : this.playlists) {
                if (pl.id.equals(playList.id)) {
                    hasFound = true;
                }
            }

            if (!hasFound) {
                this.playlists.add(playList);
            } else {
                this.delete(playList);
                this.playlists.add(playList);
            }
        }
    }

    public Playlist getPlayListByUUID(UUID uuid) {
        Playlist playlist = null;
        for(Playlist p: playlists) {
            if(p.id.equals(uuid)) playlist = p;
        }
        return playlist;
    }

    public List<Playlist> getplaylists() {
        return this.playlists;
    }

    public Playlist getPlayListByName(String playListName) {
        Playlist playlist = null;
        for(Playlist p: playlists) {
            if(p.playListName.equals(playListName)) playlist = p;
        }
        return playlist;
    }

    public List<Playlist> getPlayListsCanBeActivated() {
        List<Playlist> newPlayLists = new ArrayList<>();

        for (Playlist playlist : this.playlists) {
            if(playlist.amountToGenerate > 0 && playlist.activateOnFirstLogin) {
                newPlayLists.add(playlist);
            }
        }
        return newPlayLists;
    }
}
