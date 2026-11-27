package AoLA.game.graphics;

    import com.badlogic.gdx.Gdx;
    import com.badlogic.gdx.audio.Music;
    import com.badlogic.gdx.audio.Sound;

    public class SoundManager {
        private Music menuMusic;
        private Music gameMusic;
        private Music pauseMusic;
        private Sound keySound;
        private Sound damageSound;
        private Sound victorySound;
        private Sound gameOverSound;

        public SoundManager() {
            menuMusic = Gdx.audio.newMusic(
                Gdx.files.internal("audio/music/menu.mp3")
            );
        }
            public void playMenuMusic() {
                menuMusic.setLooping(true);
                menuMusic.setVolume(0.5f);
                menuMusic.play();
            }
            public void stopMenuMusic() {
                menuMusic.stop();
            }
            public void dispose() {
                menuMusic.dispose();
            }

        }


