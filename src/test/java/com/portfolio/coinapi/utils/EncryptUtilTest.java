package com.portfolio.coinapi.utils;

import com.portfolio.coinapi.util.EncryptUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptUtilTest {

    @Test
    void encryptDecrypt_Success() throws Exception {
        String originalData = "testData";
        String encryptedData = EncryptUtil.encrypt(originalData);
        String decryptedData = EncryptUtil.decrypt(encryptedData);

        assertNotNull(encryptedData);
        assertNotEquals(originalData, encryptedData);
        assertEquals(originalData, decryptedData);
    }

    @Test
    void encrypt_ThrowsException() {
        assertThrows(Exception.class, () -> EncryptUtil.encrypt(null));
    }

    @Test
    void decrypt_ThrowsException() {
        assertThrows(Exception.class, () -> EncryptUtil.decrypt(null));
    }
}
