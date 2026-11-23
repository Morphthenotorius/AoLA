package AoLA.game;

import java.util.*;

public class MazeGenerator {
    /** w and h must be odd. Returns grid[y][x]: 1 = wall, 0 = path. */
    public static int[][] generate(int w, int h, Random rnd) {
        int[][] g = new int[h][w];
        for (int[] row : g) Arrays.fill(row, 1);

        int[][] dirs = {{0, -2}, {0, 2}, {-2, 0}, {2, 0}};
        Deque<int[]> stack = new ArrayDeque<>();
        g[1][1] = 0;
        stack.push(new int[]{1, 1});

        while (!stack.isEmpty()) {
            int[] c = stack.peek();
            List<int[]> options = new ArrayList<>();
            for (int[] d : dirs) {
                int nx = c[0] + d[0], ny = c[1] + d[1];
                if (nx > 0 && ny > 0 && nx < w - 1 && ny < h - 1 && g[ny][nx] == 1) {
                    options.add(d);
                }
            }
            if (options.isEmpty()) {
                stack.pop();
                continue;
            }
            int[] d = options.get(rnd.nextInt(options.size()));
            g[c[1] + d[1] / 2][c[0] + d[0] / 2] = 0;
            g[c[1] + d[1]][c[0] + d[0]] = 0;
            stack.push(new int[]{c[0] + d[0], c[1] + d[1]});
        }
        return g;
    }
}
