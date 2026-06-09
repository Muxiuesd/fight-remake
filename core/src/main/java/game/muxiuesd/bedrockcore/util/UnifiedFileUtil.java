package game.muxiuesd.bedrockcore.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonValue;

/**
 * 统一文件工具
 * <p>
 * 路径开头有：“I:@”表示游戏内部路径的文件， “A:@”表示绝对路径文件， “E:@”表示游戏外部路径文件
 * */
public class UnifiedFileUtil {
    public static final String INTERNAL_MARK = "I:@";
    public static final String ABSOLUTE_MARK = "A:@";
    public static final String EXTERNAL_MARK = "E:@";

    public static final FileUtil INTERNAL_FILE_UTIL = new FileUtil() {
        @Override
        public String getRootPath () {
            return "";
        }

        @Override
        public FileHandle getFileHandle (String path, String name) {
            return this.getFileHandle(path + "/" +name);
        }

        @Override
        public FileHandle getFileHandle (String path) {
            return Gdx.files.internal(path);
        }
    };
    public static final FileUtil ABSOLUTE_FILE_UTIL = new FileUtil() {
        @Override
        public String getRootPath () {
            return "";
        }

        @Override
        public FileHandle getFileHandle (String path, String name) {
            return this.getFileHandle(path + "/" +name);
        }

        @Override
        public FileHandle getFileHandle (String path) {
            return Gdx.files.absolute(path);
        }
    };
    public static final FileUtil EXTERNAL_FILE_UTIL = new FileUtil() {
        @Override
        public String getRootPath () {
            return "";
        }

        @Override
        public FileHandle getFileHandle (String path, String name) {
            return this.getFileHandle(path + "/" + name);
        }

        @Override
        public FileHandle getFileHandle (String path) {
            return Gdx.files.external(path);
        }
    };


    /**
     * 创建文件
     * */
    public static FileHandle createFile (String path, String fileName) {
        FileUtil fileUtil = getFileUtil(path);
        return fileUtil.createFile(removePathStarts(path), fileName);
    }

    /**
     * 创建文件夹
     * @param path 路径
     * @param dirName 文件夹名称
     * */
    public static FileHandle createDir(String path, String dirName) {
        FileUtil fileUtil = getFileUtil(path);
        return fileUtil.createDir(removePathStarts(path), dirName);
    }
    /**
     * 创建文件夹
     * */
    public static FileHandle createDir (String dirPath) {
        FileUtil fileUtil = getFileUtil(dirPath);
        return fileUtil.createDir(dirPath);
    }

    /**
     * 删除文件
     * */
    public static void deleteFile (String path, String fileName) {
        FileUtil fileUtil = getFileUtil(path);
        fileUtil.deleteFile(removePathStarts(path), fileName);
    }

    /**
     * 删除文件夹
     * */
    public static void deleteDir (String path, String dirName) {
        FileUtil fileUtil = getFileUtil(path);
        fileUtil.deleteDir(removePathStarts(path), dirName);
    }

    /**
     * 将文件以字符串形式读取出来
     * */
    public static String readFileAsString (String path, String fileName) {
        return getFile(path, fileName).readString();
    }

    /**
     * 获取这个文件
     * */
    public static FileHandle getFile (String path, String fileName) {
        FileUtil fileUtil = getFileUtil(path);
        return fileUtil.getFile(removePathStarts(path), fileName);
    }

    /**
     * 将读取到的json文件转化为json值
     * @param fileName 默认.json后缀
     * */
    public static JsonValue readJsonFile (String path, String fileName) {
        FileUtil fileUtil = getFileUtil(path);
        return fileUtil.readJsonFile(removePathStarts(path), fileName);
    }

    /**
     * 判断文件是否存在
     * */
    public static boolean fileExists (String path, String fileName) {
        FileUtil fileUtil = getFileUtil(path);
        return fileUtil.fileExists(removePathStarts(path), fileName);
    }

    /**
     * 判断文件夹是否存在（判断某个路径下是否有指定名称的文件夹）
     * */
    public static boolean dirExists (String path, String dirName) {
        return dirExists(path + "/" + dirName);
    }
    /**
     * 判断文件夹是否存在
     * */
    public static boolean dirExists (String path) {
        FileUtil fileUtil = getFileUtil(path);
        return fileUtil.dirExists(removePathStarts(path));
    }

    /**
     * 根据开头来获取对应的文件工具
     * */
    public static FileUtil getFileUtil (String path, String fileName) {
        return getFileUtil(path);
    }
    /**
     * 根据开头来获取对应的文件工具
     * */
    public static FileUtil getFileUtil (String path) {
        if (path.startsWith(INTERNAL_MARK)) {
            return INTERNAL_FILE_UTIL;
        }
        if (path.startsWith(ABSOLUTE_MARK)) {
            return ABSOLUTE_FILE_UTIL;
        }
        if (path.startsWith(EXTERNAL_MARK)) {
            return EXTERNAL_FILE_UTIL;
        }
        //没有对应的开头就默认是游戏文件外部路径
        return EXTERNAL_FILE_UTIL;
    }

    /**
     * 移除开头的路径标记
     * */
    public static String removePathStarts (String path) {
        if (path.startsWith(INTERNAL_MARK)) {
            return path.substring(3);
        }
        if (path.startsWith(ABSOLUTE_MARK)) {
            return path.substring(3);
        }
        if (path.startsWith(EXTERNAL_MARK)) {
            return path.substring(3);
        }
        return path;
    }
}
