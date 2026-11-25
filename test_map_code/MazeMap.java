package com.mygame;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class MazeMap {

    private static final int WIDTH = 17;
    private static final int HEIGHT = 17;

    private final String[] rows;
    private final int playerStartX;
    private final int playerStartRow;

    public MazeMap() {
        this.rows = generateMaze();

        int startX = -1;
        int startRow = -1;

        for (int row = 0; row < rows.length; row++) {
            for (int col = 0; col < rows[row].length(); col++) {
                if (rows[row].charAt(col) == 'P') {
                    startX = col;
                    startRow = row;
                }
            }
        }

        if (startX == -1) {
            throw new IllegalStateException(
                "Generated maze has no player start"
            );
        }

        this.playerStartX = startX;
        this.playerStartRow = startRow;
    }

    public MazeMap(String[] rows) {
        validateRows(rows);

        this.rows = rows.clone();

        int startX = -1;
        int startRow = -1;

        for (int row = 0; row < this.rows.length; row++) {
            for (int col = 0; col < this.rows[row].length(); col++) {
                if (this.rows[row].charAt(col) == 'P') {
                    startX = col;
                    startRow = row;
                }
            }
        }

        this.playerStartX = startX;
        this.playerStartRow = startRow;
    }

    private String[] generateMaze() {
        Random random = new Random();

        char[][] grid = new char[HEIGHT][WIDTH];

        for (int row = 0; row < HEIGHT; row++) {
            Arrays.fill(grid[row], '#');
        }

        // Labirintin daxilində DFS ilə yollar yarat.
        ArrayDeque<int[]> stack = new ArrayDeque<>();

        grid[1][1] = '.';
        stack.push(new int[]{1, 1});

        int[] dx = {0, 0, -2, 2};
        int[] dy = {-2, 2, 0, 0};

        while (!stack.isEmpty()) {
            int[] current = stack.peek();

            int x = current[0];
            int y = current[1];

            List<int[]> choices = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];

                if (nx > 0 && nx < WIDTH - 1
                    && ny > 0 && ny < HEIGHT - 1
                    && grid[ny][nx] == '#') {

                    choices.add(new int[]{
                        nx,
                        ny,
                        x + dx[i] / 2,
                        y + dy[i] / 2
                    });
                }
            }

            if (choices.isEmpty()) {
                stack.pop();
                continue;
            }

            int[] next = choices.get(
                random.nextInt(choices.size())
            );

            int nx = next[0];
            int ny = next[1];
            int wallX = next[2];
            int wallY = next[3];

            grid[wallY][wallX] = '.';
            grid[ny][nx] = '.';

            stack.push(new int[]{nx, ny});
        }

        // Başlanğıc nöqtəsindən bütün əlçatan xanaları araşdır.
        int total = WIDTH * HEIGHT;

        int[] parent = new int[total];
        int[] distance = new int[total];

        Arrays.fill(parent, -1);
        Arrays.fill(distance, -1);

        Queue<Integer> queue = new ArrayDeque<>();

        int start = WIDTH + 1;

        queue.add(start);
        parent[start] = start;
        distance[start] = 0;

        int[] stepX = {1, -1, 0, 0};
        int[] stepY = {0, 0, 1, -1};

        int farthest = start;

        while (!queue.isEmpty()) {
            int current = queue.remove();

            int x = current % WIDTH;
            int y = current / WIDTH;

            if (distance[current] > distance[farthest]) {
                farthest = current;
            }

            for (int i = 0; i < 4; i++) {
                int nx = x + stepX[i];
                int ny = y + stepY[i];

                if (nx < 1 || nx >= WIDTH - 1
                    || ny < 1 || ny >= HEIGHT - 1) {
                    continue;
                }

                if (grid[ny][nx] == '#') {
                    continue;
                }

                int next = ny * WIDTH + nx;

                if (distance[next] != -1) {
                    continue;
                }

                parent[next] = current;
                distance[next] = distance[current] + 1;

                queue.add(next);
            }
        }

        // Ən uzaq xanaya gedən yolu geriyə doğru tap.
        List<Integer> path = new ArrayList<>();

        int current = farthest;

        while (current != start) {
            path.add(current);
            current = parent[current];

            if (current == -1) {
                throw new IllegalStateException(
                    "Could not construct a path through the maze"
                );
            }
        }

        path.add(start);
        Collections.reverse(path);

        // Açarı çıxışa gedən yolun ortasına yerləşdir.
        int keyIndex = Math.max(1, path.size() / 2);

        if (keyIndex >= path.size() - 1) {
            keyIndex = path.size() - 2;
        }

        int keyPosition = path.get(keyIndex);

        int keyX = keyPosition % WIDTH;
        int keyY = keyPosition / WIDTH;

        int doorX = farthest % WIDTH;
        int doorY = farthest / WIDTH;

        grid[1][1] = 'P';
        grid[keyY][keyX] = 'K';
        grid[doorY][doorX] = 'D';

        // Char massivini String massivinə çevir.
        String[] result = new String[HEIGHT];

        for (int row = 0; row < HEIGHT; row++) {
            result[row] = new String(grid[row]);
        }

        validateRows(result);

        return result;
    }

    private void validateRows(String[] rows) {
        if (rows == null || rows.length == 0) {
            throw new IllegalArgumentException(
                "Maze cannot be empty"
            );
        }

        if (rows[0] == null || rows[0].isEmpty()) {
            throw new IllegalArgumentException(
                "Maze rows cannot be empty"
            );
        }

        int width = rows[0].length();
        int playerCount = 0;
        boolean hasKey = false;
        boolean hasDoor = false;

        for (String row : rows) {
            if (row == null || row.length() != width) {
                throw new IllegalArgumentException(
                    "Maze must be rectangular"
                );
            }

            for (int col = 0; col < row.length(); col++) {
                char tile = row.charAt(col);

                if ("#P.KD".indexOf(tile) == -1) {
                    throw new IllegalArgumentException(
                        "Invalid maze tile: " + tile
                    );
                }

                if (tile == 'P') {
                    playerCount++;
                }

                if (tile == 'K') {
                    hasKey = true;
                }

                if (tile == 'D') {
                    hasDoor = true;
                }
            }
        }

        if (playerCount != 1 || !hasKey || !hasDoor) {
            throw new IllegalArgumentException(
                "Maze must contain exactly one P, at least one K and one D"
            );
        }
    }

    public int getWidth() {
        return rows[0].length();
    }

    public int getHeight() {
        return rows.length;
    }

    public char getTile(int x, int y) {
        if (y < 0 || y >= rows.length
            || x < 0 || x >= rows[y].length()) {
            return '#';
        }

        return rows[y].charAt(x);
    }

    public boolean isWall(int x, int y) {
        return getTile(x, y) == '#';
    }

    public int getPlayerStartX() {
        return playerStartX;
    }

    public int getPlayerStartY() {
        return rows.length - 1 - playerStartRow;
    }

    public String[] getRows() {
        return rows.clone();
    }
}
