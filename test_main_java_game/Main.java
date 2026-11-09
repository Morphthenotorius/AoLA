package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.Preferences;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

import java.util.Random;

public class Main extends ApplicationAdapter {

    // ------------------------------------------------------------------
    // Sprite sistemi: hər personajın (Akber, Zombie, Skeleton, Mummy)
    // öz müstəqil frame massivləri və öz animasiyaları var.
    // Heç bir personaj başqasının texture-ından istifadə etmir.
    // ------------------------------------------------------------------

    // İstiqamət kodları (oyunçu və düşmənlər üçün eynidir).
    private static final int DIR_DOWN = 0;
    private static final int DIR_LEFT = 1;
    private static final int DIR_RIGHT = 2;
    private static final int DIR_UP = 3;

    private static final int FRAMES_PER_DIRECTION = 4;
    private static final float FRAME_DURATION = 0.15f;

    // Bir personajın 4 istiqaməti üçün frame massivləri və animasiyaları.
    private static class CharacterSprites {
        final String name;

        Texture[] downFrames;
        Texture[] upFrames;
        Texture[] leftFrames;
        Texture[] rightFrames;

        Animation<Texture> downAnimation;
        Animation<Texture> upAnimation;
        Animation<Texture> leftAnimation;
        Animation<Texture> rightAnimation;

        CharacterSprites(String name) {
            this.name = name;
        }

        void buildAnimations() {
            downAnimation = createLoopAnimation(downFrames);
            upAnimation = createLoopAnimation(upFrames);
            leftAnimation = createLoopAnimation(leftFrames);
            rightAnimation = createLoopAnimation(rightFrames);
        }

        private static Animation<Texture> createLoopAnimation(
            Texture[] frames
        ) {
            Animation<Texture> animation =
                new Animation<>(FRAME_DURATION, frames);
            animation.setPlayMode(Animation.PlayMode.LOOP);
            return animation;
        }

        Animation<Texture> animationFor(int direction) {
            if (direction == DIR_LEFT) return leftAnimation;
            if (direction == DIR_RIGHT) return rightAnimation;
            if (direction == DIR_UP) return upAnimation;
            return downAnimation;
        }

        void dispose() {
            disposeAll(downFrames);
            disposeAll(upFrames);
            disposeAll(leftFrames);
            disposeAll(rightFrames);
        }

        private static void disposeAll(Texture[] textures) {
            if (textures == null) return;
            for (Texture texture : textures) {
                if (texture != null) texture.dispose();
            }
        }
    }

    // Dörd müstəqil personaj.
    private CharacterSprites akberSprites;    // OYUNÇU
    private CharacterSprites zombieSprites;   // DÜŞMƏN
    private CharacterSprites skeletonSprites; // DÜŞMƏN
    private CharacterSprites mummySprites;    // DÜŞMƏN

    private float elapsedTime;
    private boolean showLeaderboard = false;
    private Music backgroundMusic;
    private Sound coinSound;
    private float leaderboardButtonY;
    private float backButtonY;

    private Preferences preferences;
    private int bestScore;
    private SpriteBatch batch;
    private Monster[] monsters;
    private Sound doorSound;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private float walkTimer;
    private boolean playerMoving;
    private int facingDirection = DIR_DOWN;
    private OrthographicCamera camera;
    private final Random random = new Random();
    private MazeMap mazeMap;
    private float tileSize;
    private float mazeOffsetX;
    private float mazeOffsetY;
    private float playerX;
    private float playerY;
    private final float playerSize = 32f;
    private final float playerSpeed = 250f;

    private final float enemySize = 30f;
    private final float enemySpeed = 105f;

    private float coinX;
    private float coinY;
    private final float coinRadius = 12f;

    private int score;
    private int lives;
    private int nextExtraLifeScore;

    private boolean hasKey;
    private boolean doorOpen;
    private boolean gameOver;
    private boolean won;
    private boolean paused;
    private boolean gameInitialized;
    private boolean fullscreen;

    private float invulnerabilityTimer;
    private final float invulnerabilityDuration = 1.4f;

    // Görmə radiusu labirint xanaları ilə ölçülür.
    private float visionRadius = 1.5f;
    private float visionRadiusPixels;

    // TEST MODE: Fog of War kodu saxlanılıb, amma render söndürülüb.
    // Test bitəndə true et ki, düşmənlər yenidən qaranlıqda gizlənsin.
    private static final boolean FOG_OF_WAR_ENABLED = false;

    private float buttonWidth = 220f;
    private float buttonHeight = 55f;
    private float menuScale = 1f;

    private float resumeButtonY;
    private float retryButtonY;
    private float menuCenterX;

    // Hər düşmənin öz mövqeyi, öz sprite-ları və AI yaddaşı var.
    private static class Monster {
        final String name;
        final CharacterSprites sprites; // yalnız bu düşmənin öz sprite-ları
        float x;
        float y;
        float animationTime;
        boolean chasing;
        int lastSeenCol = -1;
        int lastSeenRow = -1;
        float searchTimer;
        int patrolTargetCol = -1;
        int patrolTargetRow = -1;
        int facingDirection = DIR_DOWN;

        Monster(String name, CharacterSprites sprites) {
            this.name = name;
            this.sprites = sprites;
        }

        Animation<Texture> currentAnimation() {
            return sprites.animationFor(facingDirection);
        }
    }

    @Override
    public void create() {
        Gdx.app.log("MazeRunner", "Working dir: "
            + new java.io.File(".").getAbsolutePath());

        batch = new SpriteBatch();
        preferences = Gdx.app.getPreferences("MazeRunnerLeaderboard");
        bestScore = preferences.getInteger("bestScore", 0);
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        camera = new OrthographicCamera();

        mazeMap = new MazeMap();

        // ---- OYUNÇU: yalnız Akber-in öz assetləri ----
        akberSprites = loadAkberSprites();

        // ---- DÜŞMƏNLƏR: hər biri yalnız öz qovluğundan yüklənir ----
        zombieSprites = loadEnemySprites("Zombie", "zombie",
            new Color(0.25f, 0.65f, 0.25f, 1f));
        skeletonSprites = loadEnemySprites("Skeleton", "skeleton",
            new Color(0.85f, 0.85f, 0.80f, 1f));
        mummySprites = loadEnemySprites("Mummy", "mummy",
            new Color(0.75f, 0.65f, 0.40f, 1f));

        // Üç düşmən: Mummy, Skeleton və Zombie (hər biri müstəqil animasiya).
        monsters = new Monster[] {
            new Monster("Mummy", mummySprites),
            new Monster("Skeleton", skeletonSprites),
            new Monster("Zombie", zombieSprites)
        };

        if (Gdx.files.internal("sounds/open_001.ogg").exists()) {
            doorSound = Gdx.audio.newSound(
                Gdx.files.internal("sounds/open_001.ogg")
            );
        }

        if (Gdx.files.internal("sounds/coin.wav").exists()) {
            coinSound = Gdx.audio.newSound(
                Gdx.files.internal("sounds/coin.wav")
            );
        }

        // Musiqi yalnız bir dəfə yaradılır və başladılır.
        String musicPath = "sounds/background.mp3";

        if (!Gdx.files.internal(musicPath).exists()) {
            musicPath =
                "sounds/strawberry_candy-sport-intense-metal-fast-and-strong-622134(1).mp3";
        }

        if (Gdx.files.internal(musicPath).exists()) {
            backgroundMusic = Gdx.audio.newMusic(
                Gdx.files.internal(musicPath)
            );
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.20f);
            backgroundMusic.play();
        }

        int width = Math.max(1, Gdx.graphics.getWidth());
        int height = Math.max(1, Gdx.graphics.getHeight());

        camera.setToOrtho(false, width, height);
        camera.update();

        updateMazeLayout(width, height);
        resetGame();
    }

    // ------------------------------------------------------------------
    // Sprite yükləmə
    // ------------------------------------------------------------------

    // Akber: assets/akber.asset/akber.<direction>/ (down=sprite_1_*, up=sprite_2_*, right=sprite_3_*, left=sprite_4_*)
    private CharacterSprites loadAkberSprites() {
        Color placeholder = new Color(0.20f, 0.45f, 0.90f, 1f);
        CharacterSprites sprites = new CharacterSprites("Akber");

        sprites.downFrames = loadAkberFrames("down", placeholder);
        sprites.upFrames = loadAkberFrames("up", placeholder);
        sprites.leftFrames = loadAkberFrames("left", placeholder);
        sprites.rightFrames = loadAkberFrames("right", placeholder);
        sprites.buildAnimations();

        return sprites;
    }

    private Texture[] loadAkberFrames(String direction, Color placeholder) {
        // down = sprite_1_*, up = sprite_2_*, right = sprite_3_*, left = sprite_4_*
        int row;
        if (direction.equals("down")) row = 1;
        else if (direction.equals("up")) row = 2;
        else if (direction.equals("right")) row = 3;
        else row = 4;

        return loadFramesFromFolder(
            "akber.asset/akber." + direction,
            "sprite_" + row + "_",
            "Akber",
            placeholder
        );
    }

    // Düşmən: assets/enemy_asset/<folder>_<direction>/<folder>_row1_frame<1..4>.png
    private CharacterSprites loadEnemySprites(
        String displayName,
        String folderName,
        Color placeholder
    ) {
        CharacterSprites sprites = new CharacterSprites(displayName);

        sprites.downFrames =
            loadEnemyFrames(displayName, folderName, "down", placeholder);
        sprites.upFrames =
            loadEnemyFrames(displayName, folderName, "up", placeholder);
        sprites.leftFrames =
            loadEnemyFrames(displayName, folderName, "left", placeholder);
        sprites.rightFrames =
            loadEnemyFrames(displayName, folderName, "right", placeholder);
        sprites.buildAnimations();

        return sprites;
    }

    private Texture[] loadEnemyFrames(
        String displayName,
        String folderName,
        String direction,
        Color placeholder
    ) {
        // Məsələn: enemy_asset/mummy_down/mummy_row1_frame1.png
        return loadFramesFromFolder(
            "enemy_asset/" + folderName + "_" + direction,
            folderName + "_row1_frame",
            displayName,
            placeholder
        );
    }

    // Oyun müxtəlif working directory-lərdən işə düşə bilər (IDE, gradle, jar).
    // Ona görə assets qovluğunu bir neçə yerdə axtarırıq.
    private static final String[] ASSET_ROOTS = {
        "", "assets/", "../assets/", "../../assets/"
    };

    // 1) Dəqiq fayl adları ilə axtarır (prefix + 1..4 + .png).
    // 2) Tapmasa, qovluqdakı .png faylları ada görə sıralayıb ilk 4-nü götürür.
    // Heç nə tapılmasa, personajın öz rəngli placeholder-i istifadə olunur.
    // Başqa personajın texture-ı heç vaxt istifadə olunmur.
    private Texture[] loadFramesFromFolder(
        String folderPath,
        String filePrefix,
        String characterName,
        Color placeholder
    ) {
        FileHandle[] found = null;

        // 1) Dəqiq adlar
        for (String root : ASSET_ROOTS) {
            FileHandle[] candidate = new FileHandle[FRAMES_PER_DIRECTION];
            boolean all = true;

            for (int i = 0; i < FRAMES_PER_DIRECTION; i++) {
                candidate[i] = Gdx.files.internal(
                    root + folderPath + "/" + filePrefix + (i + 1) + ".png"
                );
                if (!candidate[i].exists()) {
                    all = false;
                    break;
                }
            }

            if (all) {
                found = candidate;
                break;
            }
        }

        // 2) Qovluğu oxuyub .png-ləri sırala
        if (found == null) {
            for (String root : ASSET_ROOTS) {
                FileHandle folder = Gdx.files.internal(root + folderPath);
                if (!folder.exists() || !folder.isDirectory()) continue;

                FileHandle[] files = folder.list(".png");
                if (files.length < FRAMES_PER_DIRECTION) continue;

                Arrays.sort(
                    files,
                    (x, y) -> x.name().compareToIgnoreCase(y.name())
                );
                found = Arrays.copyOf(files, FRAMES_PER_DIRECTION);
                break;
            }
        }

        Texture[] frames = new Texture[FRAMES_PER_DIRECTION];

        if (found == null) {
            Gdx.app.error(
                "MazeRunner",
                "NOT FOUND for " + characterName + ": " + folderPath + "/"
                    + filePrefix + "1.png ... " + filePrefix + "4.png"
                    + " | working dir: "
                    + new java.io.File(".").getAbsolutePath()
            );

            for (int i = 0; i < frames.length; i++) {
                frames[i] = createPlaceholderTexture(placeholder);
            }
            return frames;
        }

        Gdx.app.log(
            "MazeRunner",
            characterName + " OK: " + found[0].path()
                + " ... " + found[FRAMES_PER_DIRECTION - 1].name()
        );

        for (int i = 0; i < frames.length; i++) {
            frames[i] = new Texture(found[i]);
        }

        return frames;
    }

    private Texture createPlaceholderTexture(Color color) {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        pixmap.setColor(Color.BLACK);
        pixmap.drawRectangle(0, 0, 32, 32);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    private void updateMazeLayout(int width, int height) {
        if (mazeMap == null || width <= 0 || height <= 0) {
            return;
        }
        backButtonY = Gdx.graphics.getHeight() / 2f - 70f;

        tileSize = Math.max(
            1f,
            Math.min(
                (float) width / mazeMap.getWidth(),
                (float) height / mazeMap.getHeight()
            )
        );

        mazeOffsetX =
            (width - mazeMap.getWidth() * tileSize) / 2f;

        mazeOffsetY =
            (height - mazeMap.getHeight() * tileSize) / 2f;
        backButtonY = Gdx.graphics.getHeight() / 2f - buttonHeight;

        menuCenterX = width / 2f;

        menuScale = Math.max(
            0.45f,
            Math.min(
                1f,
                Math.min(width / 900f, height / 600f)
            )
        );

        buttonWidth = Math.min(220f, width * 0.75f);
        buttonHeight = Math.min(55f, height * 0.14f);

        resumeButtonY = height / 2f + 12f * menuScale;
        retryButtonY = height / 2f - 58f * menuScale;
        backButtonY = height / 2f - 70f * menuScale;
        // drawMenu() ilə eyni dəyər (klikdən əvvəl 0 qalmasın).
        leaderboardButtonY = height / 2f - 85f * menuScale;

        visionRadiusPixels = visionRadius * tileSize;
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        // Kameranı yeni ekran ölçüsünə uyğunlaşdır.
        camera.setToOrtho(false, width, height);
        camera.update();

        // Oyun hələ başlamayıbsa, sadəcə xəritə ölçüsünü hesabla.
        if (!gameInitialized
            || mazeMap == null
            || tileSize <= 0f) {

            updateMazeLayout(width, height);
            return;
        }

        // Köhnə xəritənin ölçülərini saxla.
        float oldTileSize = tileSize;
        float oldMazeOffsetX = mazeOffsetX;
        float oldMazeOffsetY = mazeOffsetY;

        float oldMazeWidth = mazeMap.getWidth() * oldTileSize;
        float oldMazeHeight = mazeMap.getHeight() * oldTileSize;

        if (oldMazeWidth <= 0f || oldMazeHeight <= 0f) {
            updateMazeLayout(width, height);
            return;
        }

        // Oyunçunun xəritəyə nisbətən mövqeyi.
        float playerRelativeX =
            (playerX + playerSize / 2f - oldMazeOffsetX)
                / oldMazeWidth;

        float playerRelativeY =
            (playerY + playerSize / 2f - oldMazeOffsetY)
                / oldMazeHeight;

        // Hər üç düşmənin xəritəyə nisbətən mövqeyini saxla.
        float[] monsterRelativeX = new float[monsters.length];
        float[] monsterRelativeY = new float[monsters.length];

        for (int i = 0; i < monsters.length; i++) {
            monsterRelativeX[i] =
                (monsters[i].x + enemySize / 2f - oldMazeOffsetX)
                    / oldMazeWidth;
            monsterRelativeY[i] =
                (monsters[i].y + enemySize / 2f - oldMazeOffsetY)
                    / oldMazeHeight;
        }

        // Pulun xəritəyə nisbətən mövqeyi.
        float coinRelativeX =
            (coinX - oldMazeOffsetX) / oldMazeWidth;

        float coinRelativeY =
            (coinY - oldMazeOffsetY) / oldMazeHeight;

        // Yeni ekran üçün xəritəni yenidən ölç.
        updateMazeLayout(width, height);

        float newMazeWidth = mazeMap.getWidth() * tileSize;
        float newMazeHeight = mazeMap.getHeight() * tileSize;

        // Oyunçunun mövqeyini yeni ölçülərə uyğunlaşdır.
        float playerCenterX =
            mazeOffsetX + playerRelativeX * newMazeWidth;

        float playerCenterY =
            mazeOffsetY + playerRelativeY * newMazeHeight;

        playerCenterX = clampCenter(
            playerCenterX,
            mazeOffsetX,
            mazeOffsetX + newMazeWidth,
            playerSize / 2f
        );

        playerCenterY = clampCenter(
            playerCenterY,
            mazeOffsetY,
            mazeOffsetY + newMazeHeight,
            playerSize / 2f
        );

        playerX = playerCenterX - playerSize / 2f;
        playerY = playerCenterY - playerSize / 2f;

        // Hər üç düşmənin mövqeyini yeni ölçüyə uyğunlaşdır.
        for (int i = 0; i < monsters.length; i++) {
            float monsterCenterX =
                mazeOffsetX + monsterRelativeX[i] * newMazeWidth;
            float monsterCenterY =
                mazeOffsetY + monsterRelativeY[i] * newMazeHeight;

            monsterCenterX = clampCenter(
                monsterCenterX,
                mazeOffsetX,
                mazeOffsetX + newMazeWidth,
                enemySize / 2f
            );
            monsterCenterY = clampCenter(
                monsterCenterY,
                mazeOffsetY,
                mazeOffsetY + newMazeHeight,
                enemySize / 2f
            );

            monsters[i].x = monsterCenterX - enemySize / 2f;
            monsters[i].y = monsterCenterY - enemySize / 2f;
        }

        // Pulun mövqeyini yeni ölçülərə uyğunlaşdır.
        coinX = mazeOffsetX + coinRelativeX * newMazeWidth;
        coinY = mazeOffsetY + coinRelativeY * newMazeHeight;
    }
    private float clampCenter(
        float center,
        float min,
        float max,
        float halfSize
    ) {
        float minCenter = min + halfSize;
        float maxCenter = max - halfSize;

        if (minCenter > maxCenter) {
            return (min + max) / 2f;
        }

        return Math.max(
            minCenter,
            Math.min(center, maxCenter)
        );
    }
    private void resetGame() {
        mazeMap = new MazeMap();

        updateMazeLayout(
            Math.max(1, Gdx.graphics.getWidth()),
            Math.max(1, Gdx.graphics.getHeight())
        );
        score = 0;
        elapsedTime = 0f;
        lives = 3;
        nextExtraLifeScore = 10;
        showLeaderboard = false;

        hasKey = false;
        doorOpen = false;

        gameOver = false;
        won = false;
        paused = false;

        invulnerabilityTimer = 0f;
        walkTimer = 0f;
        playerMoving = false;
        facingDirection = DIR_DOWN;

        setPlayerToStart();
        placeEnemy();
        moveCoin();

        gameInitialized = true;
    }

    private void setPlayerToStart() {
        int col = mazeMap.getPlayerStartX();

        int row = mazeMap.getHeight() - 1
            - mazeMap.getPlayerStartY();

        playerX = tileCenterX(col) - playerSize / 2f;
        playerY = tileCenterY(row) - playerSize / 2f;
    }

    private float tileCenterX(int col) {
        return mazeOffsetX + (col + 0.5f) * tileSize;
    }

    private float tileCenterY(int row) {
        return mazeOffsetY
            + (mazeMap.getHeight() - row - 0.5f) * tileSize;
    }

    private float tileBottom(int row) {
        return mazeOffsetY
            + (mazeMap.getHeight() - row - 1) * tileSize;
    }

    private int worldCol(float x) {
        return (int) Math.floor((x - mazeOffsetX) / tileSize);
    }

    private int worldRow(float y) {
        return mazeMap.getHeight() - 1
            - (int) Math.floor((y - mazeOffsetY) / tileSize);
    }

    private boolean isInside(int col, int row) {
        return col >= 0
            && col < mazeMap.getWidth()
            && row >= 0
            && row < mazeMap.getHeight();
    }

    private char getTileSafely(int col, int row) {
        if (!isInside(col, row)) {
            return '#';
        }

        return mazeMap.getTile(col, row);
    }

    private boolean isBlocked(int col, int row) {
        char tile = getTileSafely(col, row);

        return tile == '#'
            || (tile == 'D' && !doorOpen);
    }

    private boolean isWalkableForItem(int col, int row) {
        char tile = getTileSafely(col, row);

        return tile != '#'
            && tile != 'D'
            && tile != 'K'
            && tile != 'X';
    }

    private boolean canPlayerMoveTo(float x, float y) {
        float inset = Math.min(3f, playerSize * 0.12f);

        float minX = mazeOffsetX;
        float minY = mazeOffsetY;

        float maxX =
            mazeOffsetX + mazeMap.getWidth() * tileSize;

        float maxY =
            mazeOffsetY + mazeMap.getHeight() * tileSize;

        // Personaj labirintin sərhədlərindən çıxa bilməz.
        if (x + inset < minX
            || y + inset < minY
            || x + playerSize - inset > maxX
            || y + playerSize - inset > maxY) {
            return false;
        }

        // Dörd küncün hamısı yoxlanılır.
        return !isBlocked(
            worldCol(x + inset),
            worldRow(y + inset)
        )
            && !isBlocked(
            worldCol(x + playerSize - inset),
            worldRow(y + inset)
        )
            && !isBlocked(
            worldCol(x + inset),
            worldRow(y + playerSize - inset)
        )
            && !isBlocked(
            worldCol(x + playerSize - inset),
            worldRow(y + playerSize - inset)
        );
    }

    private boolean canEnemyMoveTo(float x, float y) {
        float inset = Math.min(3f, enemySize * 0.12f);

        float minX = mazeOffsetX;
        float minY = mazeOffsetY;

        float maxX =
            mazeOffsetX + mazeMap.getWidth() * tileSize;

        float maxY =
            mazeOffsetY + mazeMap.getHeight() * tileSize;

        if (x + inset < minX
            || y + inset < minY
            || x + enemySize - inset > maxX
            || y + enemySize - inset > maxY) {
            return false;
        }

        return !isBlocked(
            worldCol(x + inset),
            worldRow(y + inset)
        )
            && !isBlocked(
            worldCol(x + enemySize - inset),
            worldRow(y + inset)
        )
            && !isBlocked(
            worldCol(x + inset),
            worldRow(y + enemySize - inset)
        )
            && !isBlocked(
            worldCol(x + enemySize - inset),
            worldRow(y + enemySize - inset)
        );
    }

    private void placeEnemy() {
        int playerCol = worldCol(playerX + playerSize / 2f);
        int playerRow = worldRow(playerY + playerSize / 2f);

        // Düşmənləri oyunçudan uzaq və bir-birindən ayrı yerləşdir.
        for (int i = 0; i < monsters.length; i++) {
            int bestCol = 1;
            int bestRow = 1;
            int bestScore = -1;

            for (int row = 0; row < mazeMap.getHeight(); row++) {
                for (int col = 0; col < mazeMap.getWidth(); col++) {
                    char tile = getTileSafely(col, row);
                    if (tile == '#' || tile == 'D' || tile == 'K'
                        || tile == 'X' || tile == 'P') {
                        continue;
                    }

                    int distanceFromPlayer = Math.abs(col - playerCol)
                        + Math.abs(row - playerRow);
                    int separation = 100;

                    for (int j = 0; j < i; j++) {
                        int otherCol = worldCol(
                            monsters[j].x + enemySize / 2f
                        );
                        int otherRow = worldRow(
                            monsters[j].y + enemySize / 2f
                        );
                        int distance = Math.abs(col - otherCol)
                            + Math.abs(row - otherRow);
                        separation = Math.min(separation, distance);
                    }

                    int candidateScore = distanceFromPlayer + separation * 2;
                    if (candidateScore > bestScore) {
                        bestScore = candidateScore;
                        bestCol = col;
                        bestRow = row;
                    }
                }
            }

            monsters[i].x = tileCenterX(bestCol) - enemySize / 2f;
            monsters[i].y = tileCenterY(bestRow) - enemySize / 2f;
            monsters[i].chasing = false;
            monsters[i].lastSeenCol = -1;
            monsters[i].lastSeenRow = -1;
            monsters[i].searchTimer = 0f;
            monsters[i].patrolTargetCol = -1;
            monsters[i].patrolTargetRow = -1;
            monsters[i].animationTime = 0f;
            monsters[i].facingDirection = DIR_DOWN;
        }
    }

    private void moveCoin() {
        for (int attempt = 0; attempt < 300; attempt++) {
            int col = random.nextInt(mazeMap.getWidth());
            int row = random.nextInt(mazeMap.getHeight());

            if (!isWalkableForItem(col, row)) {
                continue;
            }

            float x = tileCenterX(col);
            float y = tileCenterY(row);

            float dx = x - (playerX + playerSize / 2f);
            float dy = y - (playerY + playerSize / 2f);

            if (dx * dx + dy * dy > tileSize * tileSize) {
                coinX = x;
                coinY = y;
                return;
            }
        }

        coinX = tileCenterX(mazeMap.getWidth() - 2);
        coinY = tileCenterY(1);
    }
    @Override
    public void render() {
        float delta = Math.min(
            Gdx.graphics.getDeltaTime(),
            0.033f
        );

        handleInput();

        // Oyun yalnız aktiv olduqda davam edir.
        if (!paused && !gameOver && !won && !showLeaderboard) {
            elapsedTime += delta;

            updatePlayer(delta);
            checkMazeItems();

            if (!gameOver && !won) {
                updateEnemy(delta);
            }

            checkCoinCollision();
            checkEnemyCollision();
            checkMazeItems();
        }

        // Rekord xalı yadda saxla.
        if (score > bestScore) {
            bestScore = score;
            preferences.putInteger("bestScore", bestScore);
            preferences.flush();
        }

        if (invulnerabilityTimer > 0f) {
            invulnerabilityTimer -= delta;
        }

        Gdx.gl.glClearColor(
            0.025f,
            0.03f,
            0.045f,
            1f
        );

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        drawMaze();
        drawCoin();

        // TEST MODE: Fog of War kodu saxlanılıb, amma render söndürülüb.
        // Üç düşmən və onların hərəkəti tam görünsün deyə FOG_OF_WAR_ENABLED = false.
        // Test bitəndə FOG_OF_WAR_ENABLED = true et.
        if (FOG_OF_WAR_ENABLED) {
            drawFogOfWar();
        }

        drawCharacters();
        drawUI();

        // Hər ekranda yalnız uyğun menyunu göstər.
        if (showLeaderboard) {
            drawLeaderboard();
        } else if (paused || gameOver || won) {
            drawMenu();
        }
    }
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            fullscreen = !fullscreen;

            if (fullscreen) {
                Gdx.graphics.setFullscreenMode(
                    Gdx.graphics.getDisplayMode()
                );
            } else {
                Gdx.graphics.setWindowedMode(1280, 720);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (showLeaderboard) {
                showLeaderboard = false;
            } else if (!gameOver && !won) {
                paused = !paused;
            }
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY =
                Gdx.graphics.getHeight() - Gdx.input.getY();

            if (showLeaderboard) {
                if (insideButton(
                    mouseX,
                    mouseY,
                    backButtonY
                )) {
                    showLeaderboard = false;
                }
            } else if (paused) {
                if (insideButton(
                    mouseX,
                    mouseY,
                    resumeButtonY
                )) {
                    paused = false;
                } else if (insideButton(
                    mouseX,
                    mouseY,
                    retryButtonY
                )) {
                    resetGame();
                } else if (insideButton(
                    mouseX,
                    mouseY,
                    leaderboardButtonY
                )) {
                    showLeaderboard = true;
                }
            } else if (gameOver || won) {
                if (insideButton(
                    mouseX,
                    mouseY,
                    retryButtonY
                )) {
                    resetGame();
                } else if (insideButton(
                    mouseX,
                    mouseY,
                    leaderboardButtonY
                )) {
                    showLeaderboard = true;
                }
            }
        }

        if ((gameOver || won)
            && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            resetGame();
        }
    }
    private boolean insideButton(
        float x,
        float y,
        float centerY
    ) {
        return x >= menuCenterX - buttonWidth / 2f
            && x <= menuCenterX + buttonWidth / 2f
            && y >= centerY - buttonHeight / 2f
            && y <= centerY + buttonHeight / 2f;
    }

    private void updatePlayer(float delta) {
        float dx = 0f;
        float dy = 0f;

        if (Gdx.input.isKeyPressed(Input.Keys.A)
            || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            dx--;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)
            || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            dx++;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)
            || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            dy++;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)
            || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            dy--;
        }

        playerMoving = dx != 0f || dy != 0f;

        if (!playerMoving) {
            walkTimer = 0f;
            return;
        }

        walkTimer += delta;

        if (Math.abs(dx) > Math.abs(dy)) {
            facingDirection = dx < 0f ? DIR_LEFT : DIR_RIGHT;
        } else if (dy != 0f) {
            facingDirection = dy > 0f ? DIR_UP : DIR_DOWN;
        }

        // Diaqonal hərəkətdə sürət artmasın.
        if (dx != 0f && dy != 0f) {
            dx *= 0.70710678f;
            dy *= 0.70710678f;
        }

        float moveX = dx * playerSpeed * delta;
        float moveY = dy * playerSpeed * delta;

        // Böyük hərəkət məsafəsini kiçik addımlara böl.
        float maxStep = Math.max(1f, tileSize / 8f);
        int steps = Math.max(
            1,
            (int) Math.ceil(
                Math.max(Math.abs(moveX), Math.abs(moveY)) / maxStep
            )
        );

        float stepX = moveX / steps;
        float stepY = moveY / steps;

        for (int i = 0; i < steps; i++) {
            // X və Y oxlarını ayrıca yoxla ki,
            // divarın kənarı boyunca sürüşmək mümkün olsun.
            if (stepX != 0f
                && canPlayerMoveTo(playerX + stepX, playerY)) {
                playerX += stepX;
            }

            if (stepY != 0f
                && canPlayerMoveTo(playerX, playerY + stepY)) {
                playerY += stepY;
            }
        }
    }

    private void updateEnemy(float delta) {
        for (Monster monster : monsters) {
            updateMonster(monster, delta);
        }
    }

    private void updateMonster(Monster monster, float delta) {
        int monsterCol = worldCol(monster.x + enemySize / 2f);
        int monsterRow = worldRow(monster.y + enemySize / 2f);
        int playerCol = worldCol(playerX + playerSize / 2f);
        int playerRow = worldRow(playerY + playerSize / 2f);

        // Oyunçunu görürsə, təqib edir; itirərsə son yeri axtarır.
        if (canSeePlayer(monsterCol, monsterRow, playerCol, playerRow)) {
            monster.chasing = true;
            monster.lastSeenCol = playerCol;
            monster.lastSeenRow = playerRow;
            monster.searchTimer = 2.5f;
        } else if (monster.chasing) {
            monster.searchTimer -= delta;
            if (monster.searchTimer <= 0f) {
                monster.chasing = false;
                monster.patrolTargetCol = -1;
                monster.patrolTargetRow = -1;
            }
        }

        int targetCol;
        int targetRow;

        if (monster.chasing) {
            targetCol = playerCol;
            targetRow = playerRow;
        } else if (monster.searchTimer > 0f
            && monster.lastSeenCol >= 0
            && monster.lastSeenRow >= 0) {
            targetCol = monster.lastSeenCol;
            targetRow = monster.lastSeenRow;
        } else {
            if (monster.patrolTargetCol < 0
                || (monsterCol == monster.patrolTargetCol
                && monsterRow == monster.patrolTargetRow)) {
                int[] patrolTarget = choosePatrolTarget(monsterCol, monsterRow);
                if (patrolTarget == null) return;
                monster.patrolTargetCol = patrolTarget[0];
                monster.patrolTargetRow = patrolTarget[1];
            }
            targetCol = monster.patrolTargetCol;
            targetRow = monster.patrolTargetRow;
        }

        if (monsterCol == targetCol && monsterRow == targetRow) {
            if (monster.chasing) {
                moveEnemyToward(
                    monster,
                    playerX + playerSize / 2f,
                    playerY + playerSize / 2f,
                    delta
                );
            }
            return;
        }

        int[] next = findNextPathTile(
            monsterCol, monsterRow, targetCol, targetRow
        );

        if (next == null) {
            monster.patrolTargetCol = -1;
            monster.patrolTargetRow = -1;
            return;
        }

        moveEnemyToward(
            monster,
            tileCenterX(next[0]),
            tileCenterY(next[1]),
            delta
        );
    }

    private boolean canSeePlayer(int startCol, int startRow,
                                 int targetCol, int targetRow) {
        if (!isInside(startCol, startRow)
            || !isInside(targetCol, targetRow)) {
            return false;
        }

        int dxDistance = targetCol - startCol;
        int dyDistance = targetRow - startRow;

        if (dxDistance * dxDistance + dyDistance * dyDistance > 36) {
            return false;
        }

        int x = startCol;
        int y = startRow;
        int dx = Math.abs(targetCol - startCol);
        int dy = Math.abs(targetRow - startRow);
        int sx = startCol < targetCol ? 1 : -1;
        int sy = startRow < targetRow ? 1 : -1;
        int error = dx - dy;

        while (x != targetCol || y != targetRow) {
            int twiceError = 2 * error;

            if (twiceError > -dy) {
                error -= dy;
                x += sx;
            }

            if (twiceError < dx) {
                error += dx;
                y += sy;
            }

            if (x == targetCol && y == targetRow) {
                return true;
            }

            if (!isInside(x, y) || isBlocked(x, y)) {
                return false;
            }
        }

        return true;
    }

    private int[] choosePatrolTarget(int col, int row) {
        int[] dc = {1, -1, 0, 0};
        int[] dr = {0, 0, 1, -1};
        int[][] choices = new int[4][2];
        int count = 0;

        for (int i = 0; i < 4; i++) {
            int nextCol = col + dc[i];
            int nextRow = row + dr[i];

            if (!isInside(nextCol, nextRow)
                || isBlocked(nextCol, nextRow)) {
                continue;
            }

            choices[count][0] = nextCol;
            choices[count][1] = nextRow;
            count++;
        }

        if (count == 0) {
            return null;
        }

        int selected =
            java.util.concurrent.ThreadLocalRandom.current().nextInt(count);

        return new int[] {
            choices[selected][0],
            choices[selected][1]
        };
    }

    private void moveEnemyToward(
        Monster monster,
        float targetX,
        float targetY,
        float delta
    ) {
        float centerX = monster.x + enemySize / 2f;
        float centerY = monster.y + enemySize / 2f;
        float dx = targetX - centerX;
        float dy = targetY - centerY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 0.5f) return;

        if (Math.abs(dx) > Math.abs(dy)) {
            monster.facingDirection = dx < 0f ? DIR_LEFT : DIR_RIGHT;
        } else if (dy != 0f) {
            monster.facingDirection = dy > 0f ? DIR_UP : DIR_DOWN;
        }

        float step = Math.min(enemySpeed * delta, distance);
        float moveX = dx / distance * step;
        float moveY = dy / distance * step;

        if (moveX != 0f && canEnemyMoveTo(monster.x + moveX, monster.y)) {
            monster.x += moveX;
        }
        if (moveY != 0f && canEnemyMoveTo(monster.x, monster.y + moveY)) {
            monster.y += moveY;
        }

        monster.animationTime += delta;
    }

    private int[] findNextPathTile(
        int startCol,
        int startRow,
        int targetCol,
        int targetRow
    ) {
        if (!isInside(startCol, startRow)
            || !isInside(targetCol, targetRow)) {
            return null;
        }

        int width = mazeMap.getWidth();
        int height = mazeMap.getHeight();

        int start = startRow * width + startCol;
        int target = targetRow * width + targetCol;

        int[] parent = new int[width * height];
        Arrays.fill(parent, -1);

        Queue<Integer> queue = new ArrayDeque<>();
        queue.add(start);
        parent[start] = start;

        int[] dc = {1, -1, 0, 0};
        int[] dr = {0, 0, 1, -1};

        while (!queue.isEmpty() && parent[target] == -1) {
            int current = queue.remove();

            int col = current % width;
            int row = current / width;

            for (int i = 0; i < 4; i++) {
                int nextCol = col + dc[i];
                int nextRow = row + dr[i];

                if (!isInside(nextCol, nextRow)
                    || isBlocked(nextCol, nextRow)) {
                    continue;
                }

                int index = nextRow * width + nextCol;

                if (parent[index] != -1) {
                    continue;
                }

                parent[index] = current;
                queue.add(index);
            }
        }

        if (parent[target] == -1) {
            return null;
        }

        int step = target;

        while (parent[step] != start && step != start) {
            step = parent[step];
        }

        if (step == start) {
            return null;
        }

        return new int[]{
            step % width,
            step / width
        };
    }
    private void checkMazeItems() {
        int col = worldCol(playerX + playerSize / 2f);
        int row = worldRow(playerY + playerSize / 2f);

        char tile = getTileSafely(col, row);

        if (tile == 'K' && !hasKey) {
            hasKey = true;
            doorOpen = true;
            if (doorSound != null) {
                doorSound.play(0.7f);
            }

            Gdx.app.log("MazeRunner", "ACAR GOTURULDU! QAPI ACILDI!");
        }

        if (tile == 'D' && hasKey && doorOpen) {
            won = true;
            Gdx.app.log("MazeRunner", "OYUNU QAZANDIN!");
        }
    }
    private void checkCoinCollision() {
        float dx = coinX - (playerX + playerSize / 2f);
        float dy = coinY - (playerY + playerSize / 2f);

        float radius = coinRadius + playerSize * 0.42f;

        if (dx * dx + dy * dy < radius * radius) {
            score++;
            if (coinSound != null) {
                coinSound.play(0.7f);
            }

            if (score >= nextExtraLifeScore) {
                lives++;
                nextExtraLifeScore += 10;
            }

            moveCoin();
        }
    }

    private void checkEnemyCollision() {
        if (invulnerabilityTimer > 0f) return;

        for (Monster monster : monsters) {
            boolean overlap =
                playerX < monster.x + enemySize
                    && playerX + playerSize > monster.x
                    && playerY < monster.y + enemySize
                    && playerY + playerSize > monster.y;

            if (overlap) {
                lives--;
                invulnerabilityTimer = invulnerabilityDuration;
                if (lives <= 0) {
                    gameOver = true;
                } else {
                    setPlayerToStart();
                }
                return;
            }
        }
    }

    private void drawMaze() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int row = 0; row < mazeMap.getHeight(); row++) {
            for (int col = 0; col < mazeMap.getWidth(); col++) {

                float x = mazeOffsetX + col * tileSize;
                float y = tileBottom(row);

                char tile = getTileSafely(col, row);

                shapeRenderer.setColor(
                    new Color(0.14f, 0.16f, 0.19f, 1f)
                );

                shapeRenderer.rect(
                    x, y, tileSize, tileSize
                );

                if (tile == '#') {
                    shapeRenderer.setColor(
                        new Color(0.27f, 0.31f, 0.37f, 1f)
                    );

                    shapeRenderer.rect(
                        x + 1f,
                        y + 1f,
                        tileSize - 2f,
                        tileSize - 2f
                    );

                } else if (tile == 'K' && !hasKey) {
                    shapeRenderer.setColor(Color.GOLD);

                    shapeRenderer.circle(
                        x + tileSize / 2f,
                        y + tileSize / 2f,
                        tileSize * 0.16f
                    );

                } else if (tile == 'D') {
                    if (doorOpen) {
                        shapeRenderer.setColor(
                            new Color(0.35f, 0.55f, 0.30f, 1f)
                        );
                    } else {
                        shapeRenderer.setColor(
                            new Color(0.48f, 0.25f, 0.10f, 1f)
                        );
                    }

                    shapeRenderer.rect(
                        x + tileSize * 0.16f,
                        y + tileSize * 0.06f,
                        tileSize * 0.68f,
                        tileSize * 0.88f
                    );

                    shapeRenderer.setColor(
                        new Color(0.78f, 0.58f, 0.25f, 1f)
                    );

                    shapeRenderer.circle(
                        x + tileSize * 0.69f,
                        y + tileSize * 0.5f,
                        tileSize * 0.045f
                    );
                }
            }
        }

        shapeRenderer.end();
    }

    private void drawCoin() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.circle(coinX, coinY, coinRadius);

        shapeRenderer.setColor(
            new Color(1f, 0.78f, 0.05f, 1f)
        );

        shapeRenderer.circle(
            coinX,
            coinY,
            coinRadius * 0.55f
        );

        shapeRenderer.end();
    }

    // Fog of War implementasiyası saxlanılıb; render FOG_OF_WAR_ENABLED ilə idarə olunur.
    private void drawFogOfWar() {
        float playerCenterX = playerX + playerSize / 2f;
        float playerCenterY = playerY + playerSize / 2f;

        visionRadiusPixels = visionRadius * tileSize;

        // Qaranlıqdan kənarda qalan hissələri örtürük.
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int row = 0; row < mazeMap.getHeight(); row++) {
            for (int col = 0; col < mazeMap.getWidth(); col++) {

                float centerX = tileCenterX(col);
                float centerY = tileCenterY(row);

                float dx = centerX - playerCenterX;
                float dy = centerY - playerCenterY;

                float distance =
                    (float) Math.sqrt(dx * dx + dy * dy);

                if (distance > visionRadiusPixels) {
                    float darkness = Math.min(
                        0.98f,
                        0.72f
                            + (distance - visionRadiusPixels)
                            / Math.max(tileSize * 5f, 1f)
                    );

                    shapeRenderer.setColor(
                        0.005f,
                        0.008f,
                        0.018f,
                        darkness
                    );

                    shapeRenderer.rect(
                        mazeOffsetX + col * tileSize,
                        tileBottom(row),
                        tileSize,
                        tileSize
                    );
                }
            }
        }

        shapeRenderer.end();
    }

    private void drawCharacters() {
        batch.begin();

        // ---- OYUNÇU (Akber): yalnız akberSprites ----
        if (invulnerabilityTimer <= 0f
            || ((int) (invulnerabilityTimer * 12f) % 2 == 0)) {
            Texture currentHero;

            if (!playerMoving) {
                currentHero = akberSprites.rightFrames[0];
            } else {
                currentHero = akberSprites
                    .animationFor(facingDirection)
                    .getKeyFrame(walkTimer);
            }

            batch.draw(currentHero, playerX, playerY, playerSize, playerSize);
        }

        // ---- DÜŞMƏNLƏR: hər biri öz sprite-ları ilə (Mummy, Skeleton, Zombie) ----
        // Test üçün hər üç düşmən fog radiusundan asılı olmayaraq görünür.
        for (Monster monster : monsters) {
            Texture frame = monster.currentAnimation().getKeyFrame(
                monster.animationTime
            );
            batch.draw(
                frame,
                monster.x,
                monster.y,
                enemySize,
                enemySize
            );
        }

        batch.end();
    }

    private String formatTime(float seconds) {
        int totalSeconds = (int) seconds;
        int minutes = totalSeconds / 60;
        int remainingSeconds = totalSeconds % 60;

        return String.format(
            java.util.Locale.ROOT,
            "%02d:%02d",
            minutes,
            remainingSeconds
        );
    }
    private void drawUI() {
        batch.begin();

        font.setColor(Color.WHITE);
        font.getData().setScale(1f);

        float screenHeight = Gdx.graphics.getHeight();

        font.draw(
            batch,
            "Score: " + score,
            18f,
            screenHeight - 22f
        );

        font.draw(
            batch,
            "Best Score: " + bestScore,
            18f,
            screenHeight - 46f
        );

        font.draw(
            batch,
            "Lives: " + lives,
            18f,
            screenHeight - 70f
        );

        font.draw(
            batch,
            "Key: " + (hasKey ? "Yes" : "No"),
            18f,
            screenHeight - 94f
        );

        font.draw(
            batch,
            "Time: " + formatTime(elapsedTime),
            18f,
            screenHeight - 118f
        );

        font.draw(
            batch,
            "WASD / Arrows: Move | ESC: Pause | F11: Fullscreen",
            18f,
            24f
        );

        batch.end();
    }
    private void drawLeaderboard() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float centerX = screenWidth / 2f;
        float centerY = screenHeight / 2f;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0f, 0f, 0f, 0.9f);
        shapeRenderer.rect(0f, 0f, screenWidth, screenHeight);

        shapeRenderer.setColor(
            new Color(0.15f, 0.30f, 0.60f, 1f)
        );

        shapeRenderer.rect(
            centerX - buttonWidth / 2f,
            backButtonY - buttonHeight / 2f,
            buttonWidth,
            buttonHeight
        );

        shapeRenderer.end();

        batch.begin();

        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);

        String title = "LEADERBOARD";
        GlyphLayout titleLayout = new GlyphLayout(font, title);

        font.draw(
            batch,
            title,
            centerX - titleLayout.width / 2f,
            centerY + 100f
        );

        font.getData().setScale(1.1f);

        String record = "Best Score: " + bestScore;
        GlyphLayout recordLayout = new GlyphLayout(font, record);

        font.draw(
            batch,
            record,
            centerX - recordLayout.width / 2f,
            centerY + 25f
        );

        font.getData().setScale(1f);

        String back = "Back";
        GlyphLayout backLayout = new GlyphLayout(font, back);

        font.draw(
            batch,
            back,
            centerX - backLayout.width / 2f,
            backButtonY + backLayout.height / 2f
        );

        batch.end();
    }
    private void drawMenu() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float centerX = screenWidth / 2f;
        float centerY = screenHeight / 2f;

        menuCenterX = centerX;

        resumeButtonY = centerY + 55f * menuScale;
        retryButtonY = centerY - 15f * menuScale;
        leaderboardButtonY = centerY - 85f * menuScale;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0f, 0f, 0f, 0.82f);
        shapeRenderer.rect(0f, 0f, screenWidth, screenHeight);

        // Resume button
        if (paused) {
            shapeRenderer.setColor(
                new Color(0.16f, 0.48f, 0.24f, 1f)
            );

            shapeRenderer.rect(
                centerX - buttonWidth / 2f,
                resumeButtonY - buttonHeight / 2f,
                buttonWidth,
                buttonHeight
            );
        }

        // Retry button
        shapeRenderer.setColor(
            new Color(0.62f, 0.20f, 0.17f, 1f)
        );

        shapeRenderer.rect(
            centerX - buttonWidth / 2f,
            retryButtonY - buttonHeight / 2f,
            buttonWidth,
            buttonHeight
        );

        // Leaderboard button
        shapeRenderer.setColor(
            new Color(0.15f, 0.30f, 0.60f, 1f)
        );

        shapeRenderer.rect(
            centerX - buttonWidth / 2f,
            leaderboardButtonY - buttonHeight / 2f,
            buttonWidth,
            buttonHeight
        );

        shapeRenderer.end();

        String title;

        if (won) {
            title = "YOU WIN!";
        } else if (gameOver) {
            title = "GAME OVER";
        } else {
            title = "PAUSED";
        }

        batch.begin();

        font.setColor(Color.WHITE);

        float titleScale = Math.max(
            0.55f,
            Math.min(2f, menuScale * 1.8f)
        );

        font.getData().setScale(titleScale);

        GlyphLayout titleLayout = new GlyphLayout(font, title);

        if (titleLayout.width > screenWidth * 0.9f) {
            titleScale *=
                (screenWidth * 0.9f) / titleLayout.width;

            font.getData().setScale(titleScale);
            titleLayout.setText(font, title);
        }

        font.draw(
            batch,
            title,
            centerX - titleLayout.width / 2f,
            centerY + 150f * menuScale
        );

        font.getData().setScale(
            Math.max(0.5f, menuScale)
        );

        // Resume text
        if (paused) {
            String resume = "Resume";
            GlyphLayout resumeLayout = new GlyphLayout(font, resume);

            font.draw(
                batch,
                resume,
                centerX - resumeLayout.width / 2f,
                resumeButtonY + resumeLayout.height / 2f
            );
        }

        // Retry text
        String retry = "Restart";
        GlyphLayout retryLayout = new GlyphLayout(font, retry);

        font.draw(
            batch,
            retry,
            centerX - retryLayout.width / 2f,
            retryButtonY + retryLayout.height / 2f
        );

        // Leaderboard text
        String leaderboard = "Leaderboard";
        GlyphLayout leaderboardLayout =
            new GlyphLayout(font, leaderboard);

        font.draw(
            batch,
            leaderboard,
            centerX - leaderboardLayout.width / 2f,
            leaderboardButtonY + leaderboardLayout.height / 2f
        );

        font.getData().setScale(1f);

        batch.end();
    }
    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }

        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }

        if (font != null) {
            font.dispose();
        }

        // Hər personajın texture-ları ayrıca dispose olunur.
        if (akberSprites != null) akberSprites.dispose();
        if (zombieSprites != null) zombieSprites.dispose();
        if (skeletonSprites != null) skeletonSprites.dispose();
        if (mummySprites != null) mummySprites.dispose();

        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }

        if (doorSound != null) {
            doorSound.dispose();
        }

        if (coinSound != null) {
            coinSound.dispose();
        }
    }
}
