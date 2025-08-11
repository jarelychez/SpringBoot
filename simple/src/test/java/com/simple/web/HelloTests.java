package com.simple.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HelloTests {
    @Test
    public void testHello() {
        var controller = new HelloController();
        String result = controller.hello();
        assertNotNull(result);
        assertTrue(result.contains("ello"));
    }
}
