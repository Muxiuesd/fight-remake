package ttk.muxiuesd.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 版本号大小比较
 * */
public class SemanticVersion implements Comparable<SemanticVersion> {
    private final int major;
    private final int minor;
    private final int patch;
    private final String[] preRelease;

    public SemanticVersion(String version) {
        // 使用正则表达式拆分版本号
        Pattern pattern = Pattern.compile(
            "^(?<major>\\d+)" +
                "(\\.(?<minor>\\d+))?" +
                "(\\.(?<patch>\\d+))?" +
                "(-(?<pre>[0-9A-Za-z-.]+))?" +
                "(\\+(?<meta>[0-9A-Za-z-.]+))?$"
        );

        Matcher matcher = pattern.matcher(version);
        if (!matcher.find()) {
            throw new IllegalArgumentException("无效的版本号格式: " + version);
        }

        this.major = parseInt(matcher.group("major"));
        this.minor = parseInt(matcher.group("minor"));
        this.patch = parseInt(matcher.group("patch"));
        this.preRelease = splitPreRelease(matcher.group("pre"));
    }

    private int parseInt(String value) {
        return (value == null) ? 0 : Integer.parseInt(value);
    }

    private String[] splitPreRelease(String pre) {
        return (pre == null) ? new String[0] : pre.split("\\.");
    }

    @Override
    public int compareTo(SemanticVersion other) {
        // 比较主版本号
        int cmp = Integer.compare(this.major, other.major);
        if (cmp != 0) return cmp;

        // 比较次版本号
        cmp = Integer.compare(this.minor, other.minor);
        if (cmp != 0) return cmp;

        // 比较修订号
        cmp = Integer.compare(this.patch, other.patch);
        if (cmp != 0) return cmp;

        // 比较预发布标签
        return comparePreRelease(this.preRelease, other.preRelease);
    }

    private int comparePreRelease(String[] a, String[] b) {
        // 正式版本 > 预发布版本
        if (a.length == 0 && b.length > 0) return 1;
        if (b.length == 0 && a.length > 0) return -1;
        if (a.length == 0 && b.length == 0) return 0;

        // 逐个比较预发布标识符
        int max = Math.min(a.length, b.length);
        for (int i = 0; i < max; i++) {
            int cmp = compareIdentifier(a[i], b[i]);
            if (cmp != 0) return cmp;
        }

        return Integer.compare(a.length, b.length);
    }

    private int compareIdentifier(String a, String b) {
        boolean aNumeric = a.matches("\\d+");
        boolean bNumeric = b.matches("\\d+");

        if (aNumeric && bNumeric) {
            return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
        } else if (aNumeric || bNumeric) {
            return aNumeric ? -1 : 1;  // 数字标识符 < 非数字
        } else {
            return a.compareTo(b);     // 字母顺序比较
        }
    }
}
