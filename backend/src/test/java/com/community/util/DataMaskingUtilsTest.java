package com.community.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataMaskingUtilsTest {
    @Test
    void masksPhoneAndIdentityCardAtResponseBoundary() {
        assertEquals("138****1234", DataMaskingUtils.phone("13800131234"));
        assertEquals("110101********1234", DataMaskingUtils.idCard("110101199001011234"));
    }
}
