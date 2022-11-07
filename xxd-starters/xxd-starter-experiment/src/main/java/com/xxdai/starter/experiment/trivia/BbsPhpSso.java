package com.xxdai.starter.experiment.trivia;

/**
 * 测试论坛和官网的同步问题
 *
 * Created by fangdajiang on 2018/10/29.
 */
public class BbsPhpSso {
    private static java.util.regex.Pattern PATTERN = java.util.regex.Pattern.compile("src=\"(.*?)\" reload");

    public static void main(String[] args) {
        String loginDiscuz = "<script type=\"text/javascript\" src=\"http://bbs.xxd.com/api/uc.php?time=1540454431&code=7c43ZFMPu%2BFAnMWJzbsxSUhFo88APQ3fU84yQUWyh1%2Bfd6tzutPaP8aejsaw270w1fmFjWQ6z%2F6QoDmBbIJX4l2pzSn6b67mqmonUucumwufNb0706z7tG70hnEKLu31TDs0rlpJC8M2VR3lKoXa6bsKSQRRqk6IabLnPjACGaLje%2B3IYA\" reload=\"1\"></script>";
        System.out.println("s1:" + loginDiscuz);
        java.util.regex.Matcher m = PATTERN.matcher(loginDiscuz);
        while (m.find()) {
            loginDiscuz = m.group(1);
            break;
        }
        System.out.println("s2:" + loginDiscuz);
        loginDiscuz = "<script type=\"text/javascript\">$.get('" + loginDiscuz + "');</script>";
        System.out.println("s3:" + loginDiscuz);
    }
}
