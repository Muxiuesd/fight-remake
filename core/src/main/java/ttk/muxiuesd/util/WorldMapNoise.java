package ttk.muxiuesd.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WorldMapNoise {
    private static final double SQRT_3 = Math.sqrt(3.0);
    private static final double F2 = 0.5 * (SQRT_3 - 1.0);
    private static final double G2 = (3.0 - SQRT_3) / 6.0;

    // Gradients for 2D Simplex
    private static final int[][] GRADIENTS = {
        {1, 1}, {-1, 1}, {1, -1}, {-1, -1},
        {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    private static final Map<String, int[]> gradientCache = new HashMap<>();
    private static final int CACHE_SIZE_LIMIT = 10000; // 限制缓存大小

    private final int seed;

    public WorldMapNoise(int seed) {
        this.seed = seed;
    }

    private int[] generateGradient(int x, int y, int seed) {
        int hash = x * 374761393 + y * 668265263 + seed * 19349663;
        hash = (hash ^ (hash >> 13)) * 1274126177;
        hash = hash ^ (hash >> 16);
        int index = Math.abs(hash % GRADIENTS.length);
        return GRADIENTS[index];
    }

    private int[] getGradient(int x, int y) {
        String key = x + "," + y;
        if (gradientCache.containsKey(key)) {
            return gradientCache.get(key);
        }
        else {
            if (gradientCache.size() > CACHE_SIZE_LIMIT) {
                gradientCache.clear(); // 超过限制时清理缓存
            }
            int[] gradient = generateGradient(x, y, this.seed);
            gradientCache.put(key, gradient);
            return gradient;
        }
    }

    public double noise(double x, double y) {
        double s = (x + y) * F2;
        int i = fastFloor(x + s);
        int j = fastFloor(y + s);

        double t = (i + j) * G2;
        double X0 = i - t;
        double Y0 = j - t;

        double x0 = x - X0;
        double y0 = y - Y0;

        int i1, j1;
        if (x0 > y0) {
            i1 = 1; j1 = 0;
        }
        else {
            i1 = 0; j1 = 1;
        }

        double x1 = x0 - i1 + G2;
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2;
        double y2 = y0 - 1.0 + 2.0 * G2;

        int[] gradient0 = getGradient(i, j);
        int[] gradient1 = getGradient(i + i1, j + j1);
        int[] gradient2 = getGradient(i + 1, j + 1);

        double t0 = 0.5 - x0 * x0 - y0 * y0;
        double n0 = (t0 < 0) ? 0.0 : Math.pow(t0, 4) * dot(gradient0, x0, y0);

        double t1 = 0.5 - x1 * x1 - y1 * y1;
        double n1 = (t1 < 0) ? 0.0 : Math.pow(t1, 4) * dot(gradient1, x1, y1);

        double t2 = 0.5 - x2 * x2 - y2 * y2;
        double n2 = (t2 < 0) ? 0.0 : Math.pow(t2, 4) * dot(gradient2, x2, y2);

        return 70.0 * (n0 + n1 + n2);
    }

    private static int fastFloor(double value) {
        return value > 0 ? (int) value : (int) value - 1;
    }

    private static double dot(int[] gradient, double x, double y) {
        return gradient[0] * x + gradient[1] * y;
    }

    /**
     * 将噪声值映射到新的范围
     *
     * @param value 需要被重新映射的值
     * @param fromLow 原始范围下限
     * @param fromHigh 原始范围上限
     * @param toLow 新范围下限
     * @param toHigh 新范围上限
     * @return 映射后的值
     */
    public static double map(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow) + toLow;
    }

    /**
     * ---------------------------------------------------------------------------------
     * 测试区
     * */
    public static void main(String[] args) {
        new Thread(
            new Runnable(){
                public void run(){
                    int times = 500;
                    long interval = 50;
                    // 随机生成一个起始坐标
                    Random random = new Random();
                    int size = 10000;
                    int ranx = (int)((random.nextDouble()-0.5)*2*size);
                    int rany = (int)((random.nextDouble()-0.5)*2*size);
                    System.out.println(new String(new char[2000]).replace("\0", "\n"));
                    while(times > 0){
                        int startX = ranx+times; // 范围：[-500, 500]
                        int startY = rany; // 范围：[-500, 500]
                        spawnNoiseMap(startX, startY);

                        times--;
                        try{ Thread.sleep(interval);}catch(Exception e){}
                    }
                }
            }
        ).start();
    }

    private static void spawnNoiseMap(int startX, int startY) {
        int width = 80, height = 40;
        StringBuilder charMap = new StringBuilder();
        WorldMapNoise worldMapNoise = new WorldMapNoise(114514);

        // 生成噪声图
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double value = worldMapNoise.getNorNoise(startX+x, startY+y, 0.08);
                String c = noiseToChar(value);
                charMap.append(c);
            }
            charMap.append("\n");
        }
        System.out.println(charMap.toString());
    }

    public double getNorNoise(double x, double y){
        double scl = 0.08;
        return this.getNorNoise(x, y, scl);
    }
    public double getNorNoise(double x, double y, double scl){
        double nx = x * scl;
        double ny = y * scl;
        double value = (SimplexNoise2D.noise(nx, ny) + 1) / 2;
        return value;
    }

    public static String noiseToChar(double value) {
        return value > 0.6 ? "##" : (value > 0.3 ? "++" : "..");
    }

}
