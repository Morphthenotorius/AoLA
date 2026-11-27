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
                Gdx.files.internal("audio/music/menu.ogg")
            );
            gameMusic = Gdx.audio.newMusic(
                Gdx.files.internal("audio/music/gameplay.ogg")
            );
            pauseMusic = Gdx.audio.newMusic(
                Gdx.files.internal("audio/music/pause.ogg")
            );
            keySound = Gdx.audio.newSound(
                Gdx.files.internal("audio/sfx/key.wav")
            );
            damageSound = Gdx.audio.newSound(
                Gdx.files.internal("audio/sfx/damage.wav")
            );
            victorySound = Gdx.audio.newSound(
                Gdx.files.internal("audio/sfx/victory.wav")
            );
            gameOverSound = Gdx.audio.newSound(
                Gdx.files.internal("audio/sfx/gameover.wav")
            );

        }
        public void playGameMusic() {
            menuMusic.stop();
            gameMusic.setLooping(true);
            gameMusic.setVolume(0.5f);
            gameMusic.play();
        }
        public void stopGameMusic() {
            gameMusic.stop();
        }
        public void playKeySound() {
            keySound.play(0.5f);
        }
        public void playDamageSound() {
            damageSound.play(0.5f);
        }
        public void playVictorySound() {
            victorySound.play(0.5f);
        }
        public void playGameOverSound() {
            gameOverSound.play(0.5f);
        }
        public void playPauseMusic() {
            gameMusic.pause();
            pauseMusic.setLooping(true);
            pauseMusic.setVolume(0.5f);
            pauseMusic.play();
        }
        public void resumeGameMusic() {
            pauseMusic.stop();
            gameMusic.play();
        }
        public void playMenuMusic() {
            gameMusic.stop();
            pauseMusic.stop();
            menuMusic.setLooping(true);
            menuMusic.setVolume(0.5f);
            menuMusic.play();
        }
            public void stopMenuMusic() {
                menuMusic.stop();
            }
        public void dispose() {
            menuMusic.dispose();
            gameMusic.dispose();
            pauseMusic.dispose();
            keySound.dispose();
            damageSound.dispose();
            victorySound.dispose();
            gameOverSound.dispose();
        }

        }


