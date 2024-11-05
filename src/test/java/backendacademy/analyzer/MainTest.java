package backendacademy.analyzer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    public void testCheckDateInput_correctInput() {
        assertTrue(Main.checkDateInput("2023-03-15"));
        assertTrue(Main.checkDateInput("2100-01-01"));
        assertTrue(Main.checkDateInput("1900-01-01"));
    }

    @Test
    public void testCheckDateInput_incorrectInput() {
        assertFalse(Main.checkDateInput("2023-02-30"));
        assertFalse(Main.checkDateInput("2023-13-10"));
        assertFalse(Main.checkDateInput(" 2023-03-15 "));
        assertFalse(Main.checkDateInput("15/03/2023"));
        assertFalse(Main.checkDateInput(""));
        assertFalse(Main.checkDateInput("Hello World"));
    }
}
