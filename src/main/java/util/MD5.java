package util;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private static Logger logger = LoggerFactory.getLogger(MD5.class);
    private static PropertiesConfiguration properties = PropertiesReader.getProperties();

    public static String valueOf(String parameter) {
        StringBuffer buffer = new StringBuffer();
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(properties.getString("encoder.algorithm","MD5"));
            messageDigest.update(parameter.getBytes());
            byte[] message = messageDigest.digest();
            for (int i = 0; i < message.length; i++) {
                buffer.append(Integer.toString((message[i] & 0x00ff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            logger.info("valueOf() {}", e.getMessage());
        }
        return buffer.toString();
    }
}
