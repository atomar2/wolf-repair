package edu.ncsu.csc216.wolf_repair.model.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests command construction, validation, and access to command information.
 * @author Aanya Tomar
 */
public class CommandTest {

    /**
     * Tests that the command enumeration contains the nine specified actions.
     */
    @Test
    public void testCommandValues() {
        assertEquals(9, Command.CommandValue.values().length);
    }
}