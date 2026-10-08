package edu.ncsu.csc216.wolf_repair.model.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import edu.ncsu.csc216.wolf_repair.model.command.Command.CommandValue;
import edu.ncsu.csc216.wolf_repair.model.workorder.WorkOrder;

/**
 * Tests command construction, validation, and access to command information.
 * @author Aanya Tomar
 */
public class CommandTest {

    /** Tests that all nine specified actions exist in the expected order. */
    @Test
    public void testCommandValues() {
        CommandValue[] values = CommandValue.values();
        assertEquals(9, values.length);
        assertEquals(CommandValue.SUBMIT, values[0]);
        assertEquals(CommandValue.APPROVE, values[1]);
        assertEquals(CommandValue.DENY, values[2]);
        assertEquals(CommandValue.DISPATCH, values[3]);
        assertEquals(CommandValue.STATUS, values[4]);
        assertEquals(CommandValue.REASSIGN, values[5]);
        assertEquals(CommandValue.CANCEL, values[6]);
        assertEquals(CommandValue.COMPLETE, values[7]);
        assertEquals(CommandValue.REOPEN, values[8]);
    }

    /** Tests that unrestricted actions preserve null, empty, and text information. */
    @Test
    public void testCommand() {
        CommandValue[] actions = {
            CommandValue.SUBMIT, CommandValue.APPROVE, CommandValue.DENY,
            CommandValue.DISPATCH, CommandValue.REASSIGN,
            CommandValue.COMPLETE, CommandValue.REOPEN
        };

        for (CommandValue action : actions) {
            Command withoutInformation = new Command(action, null);
            assertEquals(action, withoutInformation.getCommand());
            assertNull(withoutInformation.getCommandInformation());

            Command emptyInformation = new Command(action, "");
            assertEquals(action, emptyInformation.getCommand());
            assertEquals("", emptyInformation.getCommandInformation());

            Command withInformation = new Command(action, "Additional information");
            assertEquals(action, withInformation.getCommand());
            assertEquals("Additional information",
                    withInformation.getCommandInformation());
        }
    }

    /** Tests valid status notes and both permitted cancellation reasons. */
    @Test
    public void testRestrictedCommands() {
        Command status = new Command(CommandValue.STATUS, "Parts ordered");
        assertEquals(CommandValue.STATUS, status.getCommand());
        assertEquals("Parts ordered", status.getCommandInformation());

        Command waiting = new Command(CommandValue.CANCEL, WorkOrder.WAITING);
        assertEquals(CommandValue.CANCEL, waiting.getCommand());
        assertEquals(WorkOrder.WAITING, waiting.getCommandInformation());

        Command cancelled = new Command(CommandValue.CANCEL, WorkOrder.CANCELLED);
        assertEquals(CommandValue.CANCEL, cancelled.getCommand());
        assertEquals(WorkOrder.CANCELLED, cancelled.getCommandInformation());
    }

    /** Tests rejecting null actions and invalid STATUS or CANCEL information. */
    @Test
    public void testInvalidCommand() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new Command(null, "Note"));
        assertEquals("Invalid information.", exception.getMessage());

        String[] missingNotes = { null, "" };
        for (String information : missingNotes) {
            exception = assertThrows(IllegalArgumentException.class,
                    () -> new Command(CommandValue.STATUS, information));
            assertEquals("Invalid information.", exception.getMessage());
        }

        String[] invalidReasons = {
            null, "", "Other reason", WorkOrder.DENIED,
            "no technician available", WorkOrder.CANCELLED + " "
        };
        for (String information : invalidReasons) {
            exception = assertThrows(IllegalArgumentException.class,
                    () -> new Command(CommandValue.CANCEL, information));
            assertEquals("Invalid information.", exception.getMessage());
        }
    }

    /** Tests retrieving the action represented by a command. */
    @Test
    public void testGetCommand() {
        Command command = new Command(CommandValue.DISPATCH, "jmartin");
        assertEquals(CommandValue.DISPATCH, command.getCommand());
    }

    /** Tests retrieving information without modifying its contents. */
    @Test
    public void testGetCommandInformation() {
        Command command = new Command(CommandValue.APPROVE, "Inspect HVAC unit");
        assertEquals("Inspect HVAC unit", command.getCommandInformation());

        Command optionalNote = new Command(CommandValue.REOPEN, null);
        assertNull(optionalNote.getCommandInformation());
    }
}