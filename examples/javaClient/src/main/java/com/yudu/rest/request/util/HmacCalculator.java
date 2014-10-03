package com.yudu.rest.request.util;

import com.yudu.rest.exception.RequestBuilderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author rjm
 */
public class HmacCalculator {

    /**
     * @param stringToSign The string to be signed.
     * @param sharedSecret The shared secret to sign the string with.
     * @return The base64 encoded HMAC SHA256 signature
     */
    public static String calculateHmac(String stringToSign, String sharedSecret) {
        if (StringUtils.isEmpty(sharedSecret))
            throw new RequestBuilderException("Shared secret cannot be blank");
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKey secretKey = new SecretKeySpec(sharedSecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] resultBytes = mac.doFinal(stringToSign.getBytes()); // Calculate the HMAC SHA256
            return Base64.encodeBase64String(resultBytes); // Base 64 encode it.
        } catch (Exception e) {
            throw new RequestBuilderException("Caught Exception while calculating signature.", e);
        }
    }
}
