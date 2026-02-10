package ttk.muxiuesd.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * 文件工具
 * <p>
 * 规定：path都是基于游戏文件的路径（基准路径）起始的路径
 * */
public class FileUtil {
    public static String TAG = FileUtil.class.getName();

    /**
     * 创建文件
     * */
    public static FileHandle createFile(String path, String fileName) {
        if (!fileName.contains(".")) {
            Log.error(TAG, "不太合适的文件名称：" + fileName + " ！！！");
        }
        try {
            FileHandle file = getFileHandle(path, fileName);
            if (!file.exists()) {
                // 创建空文件
                file.write(false);
            }
            return file;
        } catch (GdxRuntimeException e) {
            Log.error(TAG, "创建文件失败：" + path + "/" + fileName, e);
            return null;
        }
    }

    /**
     * 创建文件夹
     * @param path 路径
     * @param dirName 文件夹名称
     * */
    public static FileHandle createDir(String path, String dirName) {
        if (path.endsWith("/")) return createDir(path + dirName);
        return createDir(path + "/" + dirName);
    }
    /**
     * 创建文件夹
     * */
    public static FileHandle createDir (String dirPath) {
        try {
            FileHandle dir = getFileHandle(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return dir;
        } catch (GdxRuntimeException e) {
            Log.error(TAG, "创建文件夹失败: " + dirPath, e);
            return null;
        }
    }

    /**
     * 删除文件
     * */
    public static void deleteFile(String path, String fileName) {
        try {
            FileHandle file = getFileHandle(path, fileName);
            if (file.exists() && !file.isDirectory()) {
                file.delete();
            }
        } catch (GdxRuntimeException e) {
            Log.error(TAG, "删除文件失败: " + path + "/" + fileName, e);
        }
    }

    /**
     * 删除文件夹
     * */
    public static void deleteDir(String path, String dirName) {
        try {
            FileHandle dir = getFileHandle(path, dirName);
            if (!dir.isDirectory()) {
                Log.error(TAG, "路径：" + path + " 下不存在文件夹：" + dirName + " ！！！");
            }
            if (dir.exists()) {
                dir.delete();
            }
        } catch (GdxRuntimeException e) {
            Log.error(TAG, "删除文件夹失败: " + path + "/" + dirName, e);
        }
    }

    /**
     * 复制文件
     * */
    public static void copyFile(String srcPath, String destPath, String fileName) {
        //TODO
    }

    public static FileHandle getFile(FileHandle fileHandle, String fileName) {
        FileHandle file = getFileHandle(fileHandle, fileName);
        if (!file.exists() || file.isDirectory()) {
            Log.error(TAG, "不存在文件：" + fileHandle);
        }
        return file;
    }
    /**
     * 获取这个文件
     * */
    public static FileHandle getFile(String path, String fileName) {
        FileHandle file = getFileHandle(path, fileName);
        if (!file.exists() || file.isDirectory()) {
            Log.error(TAG, "不存在文件：" + path + "/" + fileName);
        }
        return file;
    }

    /**
     * 将文件以字符串形式读取出来
     * */
    public static String readFileAsString(String path, String fileName) {
        return getFile(path, fileName).readString();
    }

    public static JsonValue readJsonFile (FileHandle fileHandle, String fileName) {
        if (fileName.endsWith(".json")) {
            String string = getFile(fileHandle, fileName).readString();
            return new JsonReader().parse(string);
        }
        String string = getFile(fileHandle, fileName + ".json").readString();
        return new JsonReader().parse(string);
    }
    /**
     * 将读取到的json文件转化为json值
     * @param fileName 默认.json后缀
     * */
    public static JsonValue readJsonFile (String path, String fileName) {
        if (fileName.endsWith(".json")) {
            String string = getFile(path, fileName).readString();
            return new JsonReader().parse(string);
        }
        String string = getFile(path, fileName + ".json").readString();
        return new JsonReader().parse(string);
    }

    /**
     * 根据某个文件夹的文件处理来判断此文件夹下是否有某个文件
     * */
    public static boolean fileExists(FileHandle fileHandle, String fileName) {
        FileHandle file = getFileHandle(fileHandle, fileName);
        return file.exists() && !file.isDirectory();
    }
    /**
     * 判断文件是否存在
     * */
    public static boolean fileExists(String path, String fileName) {
        FileHandle file = getFileHandle(path, fileName);
        return file.exists() && !file.isDirectory();
    }

    /**
     * 判断文件夹是否存在（判断某个路径下是否有指定名称的文件夹）
     * */
    public static boolean dirExists(String path, String dirName) {
        FileHandle dir = getFileHandle(path, dirName);
        return dir.exists() && dir.isDirectory();
    }
    /**
     * 判断文件夹是否存在
     * */
    public static boolean dirExists(String path) {
        FileHandle dir = getFileHandle(path);
        return dir.exists() && dir.isDirectory();
    }

    /**
     * 获取文件读写的基准路径
     * */
    public static String getRootPath () {
        return Gdx.files.getLocalStoragePath();
    }

    /**
     * 获取文件处理，目前默认以游戏文件所在文件夹为基准
     * @param path 基准路径下的路径
     * @param name 文件或文件夹的名称
     * */
    public static FileHandle getFileHandle (String path, String name) {
        return Gdx.files.absolute(getRootPath() + "/" + path + "/" + name);
    }

    /**
     * 获取文件处理，目前默认以游戏文件所在文件夹为基准
     * @param path 基准路径下的路径
     * */
    public static FileHandle getFileHandle (String path) {
        return Gdx.files.absolute(getRootPath() + "/" + path);
    }

    /**
     * 获取文件处理，目前默认以游戏文件所在文件夹为基准
     * @param fileHandle 获取文件处理下的某一个文件的文件处理（此文件处理是某个文件夹）
     * */
    public static FileHandle getFileHandle (FileHandle fileHandle, String name) {
        return fileHandle.child(name);
    }
}
