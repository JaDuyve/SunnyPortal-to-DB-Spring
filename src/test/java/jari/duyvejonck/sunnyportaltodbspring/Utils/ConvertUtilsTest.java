package jari.duyvejonck.sunnyportaltodbspring.Utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConvertUtilsTest {

    @Test
    public void testKwToW() {
        final double kw = 0.123;
        final int expectedW = 123;

        assertEquals(expectedW, ConvertUtils.kwToW(kw));
    }

}