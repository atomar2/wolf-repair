package edu.ncsu.csc216.wolf_repair.model.command;
import org.junit.jupiter.api.Test;
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
    
    /** Tests constructing commands with valid actions and information. */
    @Test
    public void testCommand() {
        // TODO Implement assertions.
    }

    /** Tests rejecting null actions and invalid STATUS or CANCEL information. */
    @Test
    public void testInvalidCommand() {
        // TODO Implement assertions.
    }

    /** Tests retrieving the action represented by a command. */
    @Test
    public void testGetCommand() {
        // TODO Implement assertions.
    }

    /** Tests retrieving the information associated with a command. */
    @Test
    public void testGetCommandInformation() {
        // TODO Implement assertions.
    }
}