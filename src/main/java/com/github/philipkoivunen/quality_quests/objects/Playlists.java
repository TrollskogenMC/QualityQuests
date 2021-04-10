package com.github.philipkoivunen.quality_quests.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Playlists {
    private List<Playlist> playlists;

    public Playlists() {
        this.playlists = new ArrayList<Playlist>();
    }

    public void delete(Playlist playList) {
        List<Playlist> newPlayListList = null;
        for (Playlist pl : playlists) {
            if(!pl.id.equals(playList.id)) {
                newPlayListList.add(pl);
            }
        }
        
        this.setList(newPlayListList);
    }

    public void setList(List<Playlist> newPlayListList) {
        this.playlists = newPlayListList;
    }

    public void addPlayList(Playlist playList) {
        if(playlists.size() < 1) {
            playlists.add(playList);
        } else {
            Boolean hasFound = false;

            for(Playlist pl : playlists) {
                if (pl.id.equals(playList.id)) {
                    hasFound = true;
                }
            }

            if (!hasFound) {
                playlists.add(playList);
            } else {
                delete(playList);
                playlists.add(playList);
            }
        }
    }

    public void removePlayList(Playlist playList) {

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
}
