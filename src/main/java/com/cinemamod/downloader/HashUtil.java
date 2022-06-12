package com.cinemamod.downloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtil {

    public static String sha1Hash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        try (InputStream input = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int len = input.read(buffer);

            while (len != -1) {
                md.update(buffer, 0, len);
                len = input.read(buffer);
            }

            StringBuilder result = new StringBuilder();
            for (byte b : md.digest())
                result.append(String.format("%02x", b));
            return result.toString();
        }
    }

}
