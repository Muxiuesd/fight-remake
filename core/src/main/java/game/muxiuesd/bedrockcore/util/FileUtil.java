package game.muxiuesd.bedrockcore.util;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.nio.charset.StandardCharsets;

/**
 * 抽象的文件工具类
 * <p>
 * 获取文件的基准路径需要自己实现，实现后获取的每一个文件的路径都以基准路径为起始点来获取。
 * */
public abstract class FileUtil {
    public  String TAG = UnifiedFileUtil.class.getName();

    /**
     * 获取文件读写的基准路径
     * */
    public abstract String getRootPath ();

    /**
     * 获取文件处理（内部路径、外部路径还是绝对路径需自己实现）
     * @param path 基准路径下的路径
     * @param name 文件或文件夹的名称
     * */
    public abstract FileHandle getFileHandle (String path, String name);

    /**
     * 获取文件处理 （内部路径、外部路径还是绝对路径需自己实现）
     * @param path 基准路径下的路径
     * */
    public abstract FileHandle getFileHandle (String path);



    /**
     * 获取文件处理
     * @param fileHandle 获取文件处理下的某一个文件的文件处理（此文件处理是某个文件夹）
     * */
    public FileHandle getFileHandle (FileHandle fileHandle, String name) {
        return fileHandle.child(name);
    }

    /**
     * 创建文件
     * */
    public FileHandle createFile (String path, String fileName) {
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
    public FileHandle createDir (String path, String dirName) {
        if (path.endsWith("/")) return createDir(path + dirName);
        return createDir(path + "/" + dirName);
    }
    /**
     * 创建文件夹
     * */
    public FileHandle createDir (String dirPath) {
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
    public void deleteFile (String path, String fileName) {
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
    public void deleteDir (String path, String dirName) {
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
    public void copyFile (String srcPath, String destPath, String fileName) {
        //TODO
    }

    public FileHandle getFile (FileHandle fileHandle, String fileName) {
        FileHandle file = getFileHandle(fileHandle, fileName);
        if (!file.exists() || file.isDirectory()) {
            Log.error(TAG, "不存在文件：" + fileHandle);
        }
        return file;
    }
    /**
     * 获取这个文件
     * */
    public FileHandle getFile (String path, String fileName) {
        FileHandle file = getFileHandle(path, fileName);
        if (!file.exists() || file.isDirectory()) {
            Log.error(TAG, "不存在文件：" + path + "/" + fileName);
        }
        return file;
    }

    /**
     * 以 UTF-8 读取整个文本文件
     * <p>
     * 不管文件本身是什么编码，一律按 UTF-8 解码（无法解码的字节会被替换成 U+FFFD）。
     * 开头的 UTF-8 BOM 会被跳过，避免解析 json 时多出一个不可见字符。
     * <p>
     * 全项目读取文本都走这个方法，不要直接调用 {@link FileHandle#readString()}
     * */
    public static String readText (FileHandle file) {
        String text = new String(file.readBytes(), StandardCharsets.UTF_8);
        return !text.isEmpty() && text.charAt(0) == 0xFEFF ? text.substring(1) : text;
    }

    /**
     * 将文件以字符串形式读取出来
     * */
    public String readFileAsString (String path, String fileName) {
        return readText(getFile(path, fileName));
    }

    public JsonValue readJsonFile (FileHandle fileHandle, String fileName) {
        if (fileName.endsWith(".json")) {
            String string = readText(getFile(fileHandle, fileName));
            return new JsonReader().parse(string);
        }
        String string = readText(getFile(fileHandle, fileName + ".json"));
        return new JsonReader().parse(string);
    }
    /**
     * 将读取到的json文件转化为json值
     * @param fileName 默认.json后缀
     * */
    public JsonValue readJsonFile (String path, String fileName) {
        if (fileName.endsWith(".json")) {
            String string = readText(getFile(path, fileName));
            return new JsonReader().parse(string);
        }
        String string = readText(getFile(path, fileName + ".json"));
        return new JsonReader().parse(string);
    }

    /**
     * 根据某个文件夹的文件处理来判断此文件夹下是否有某个文件
     * */
    public boolean fileExists (FileHandle fileHandle, String fileName) {
        FileHandle file = getFileHandle(fileHandle, fileName);
        return file.exists() && !file.isDirectory();
    }
    /**
     * 判断文件是否存在
     * */
    public boolean fileExists (String path, String fileName) {
        FileHandle file = getFileHandle(path, fileName);
        return file.exists() && !file.isDirectory();
    }

    /**
     * 判断文件夹是否存在（判断某个路径下是否有指定名称的文件夹）
     * */
    public boolean dirExists (String path, String dirName) {
        FileHandle dir = getFileHandle(path, dirName);
        return dir.exists() && dir.isDirectory();
    }
    /**
     * 判断文件夹是否存在
     * */
    public boolean dirExists (String path) {
        FileHandle dir = getFileHandle(path);
        return dir.exists() && dir.isDirectory();
    }
}
