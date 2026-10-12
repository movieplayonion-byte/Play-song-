package com.aistudio.playsong.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends Activity {

    private static final String PREFS_NAME = "PlaySongPrefs";
    private static final String KEY_LINKS = "playlist_links";

    // 5 Default Playlists jo hamesha rahengi
    private final String[] defaultLinks = {
        "https://youtube.com/playlist?list=RDxT9ma1ypEEA&playnext=1",
        "https://youtube.com/playlist?list=RD8tAxzESfBl4&playnext=1",
        "https://youtube.com/playlist?list=RDsLykke8q2ls&playnext=1",
        "https://youtube.com/playlist?list=RDqRlZbWsPnQI&playnext=1",
        "https://youtube.com/playlist?list=RD1kPiZ95_n40&playnext=1"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        // CASE A: User ne YouTube se link SHARE kiya hai
        if (Intent.ACTION_SEND.equals(action) && type != null && "text/plain".equals(type)) {
            String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (sharedText != null && (sharedText.contains("youtube.com") || sharedText.contains("youtu.be"))) {
                saveNewLink(sharedText);
                Toast.makeText(this, "Song Added to Play Song Pool!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Invalid YouTube Link", Toast.LENGTH_SHORT).show();
            }
            finish();
            return;
        }

        // CASE B: App Normal Launch hui (Voice Command ya Tap) -> Random Play & Exit
        playRandomSong();
    }

    private void playRandomSong() {
        List<String> allLinks = getSavedLinks();
        if (allLinks.isEmpty()) {
            allLinks = Arrays.asList(defaultLinks);
        }

        // Pure Random Selection
        int randomIndex = new Random().nextInt(allLinks.size());
        String selectedUrl = allLinks.get(randomIndex);

        // YouTube Direct Launch Intent
        Intent ytIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedUrl));
        ytIntent.setPackage("com.google.android.youtube");
        ytIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(ytIntent);

        // App turant band ho jayegi
        finish();
    }

    private List<String> getSavedLinks() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> savedSet = prefs.getStringSet(KEY_LINKS, null);
        if (savedSet == null || savedSet.isEmpty()) {
            savedSet = new HashSet<>(Arrays.asList(defaultLinks));
            prefs.edit().putStringSet(KEY_LINKS, savedSet).apply();
        }
        return new ArrayList<>(savedSet);
    }

    private void saveNewLink(String newUrl) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> currentLinks = new HashSet<>(getSavedLinks());
        currentLinks.add(newUrl);
        prefs.edit().putStringSet(KEY_LINKS, currentLinks).apply();
    }
}
