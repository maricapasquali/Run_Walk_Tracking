package com.run_walk_tracking_gps.utilities;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptographicHashFunctions {
    public static String md5(String password){
        try {
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(password.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            return no.toString(16);

        } catch (NoSuchAlgorithmException e) {// For specifying wrong message digest algorithms
            throw new RuntimeException(e);
        }
    }
}
