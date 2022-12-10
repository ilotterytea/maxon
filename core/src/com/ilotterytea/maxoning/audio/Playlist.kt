package com.ilotterytea.maxoning.audio

import com.badlogic.gdx.audio.Music
import com.ilotterytea.maxoning.utils.math.Math

/**
 * Playlist.
 */
class Playlist(vararg musics: Music) {
    private val playlist: Array<out Music> = musics
    var playingNow: Music = playlist[0]
    private var index = 0;

    var shuffleMode = false

    /**
     * Play next music.
     */
    fun next() {
        if (playingNow.isPlaying) playingNow.stop()

        if (shuffleMode) {
            index = Math.getRandomNumber(0, playlist.size - 1)
            playingNow = playlist[index]
            playingNow.play()
        } else {
            index++
            if (index > playlist.size - 1) index = 0

            playingNow = playlist[index]
            playingNow.play()
        }
    }
}