package com.app.interstory.util;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Utils {

    //이름 변경
    public static String getRenameFilename(String originalFilename) {
        // 확장자
        String ext = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if(dotIndex > -1)
            ext = originalFilename.substring(dotIndex); // .jpg
        // 형식객체
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS_");
        DecimalFormat df = new DecimalFormat("000");
        return sdf.format(new Date()) + df.format(Math.random() * 1000) + ext;
    }

    public static String getRenameNickname(String nickname) {
        // 형식객체
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS_");
        DecimalFormat df = new DecimalFormat("000");
        return sdf.format(new Date()) + df.format(Math.random() * 1000);
    }

    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp != null) {
            return timestamp.toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
        }
        return null;
    }
}
