package edu.ncsu.csc216.wolf_repair.model.workorder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.ncsu.csc216.wolf_repair.model.command.Command;
import edu.ncsu.csc216.wolf_repair.model.command.Command.CommandValue;

/**
 * Tests work order construction, validation, identifiers, file formatting,
 * and transitions through all six states.
 * @author Aanya Tomar
 */
public class WorkOrderTest {

    /** Resets the shared id counter before each test. */
    @BeforeEach
    public void setUp() {
        WorkOrder.setCounter(1);
    }

    /** Tests new work order fields and automatic ids. */
    @Test
    public void testNewWorkOrder() {
        WorkOrder order = new WorkOrder("Alice", "Leaking faucet");
        assertEquals(1, order.getWorkOrderId());
        assertEquals(WorkOrder.SUBMITTED_NAME, order.getState());
        assertEquals("Alice", order.getRequester());
        assertEquals("Leaking faucet", order.getDescription());
        assertEquals("", order.getTechnician());
        assertEquals("", order.getNote());

        WorkOrder second = new WorkOrder("Devon", "Broken latch");
        assertEquals(2, second.getWorkOrderId());
    }

    /** Tests loading all six states with their required information. */
    @Test
    public void testLoadedWorkOrder() {
        String[] states = {
            WorkOrder.SUBMITTED_NAME, WorkOrder.REVIEW_NAME,
            WorkOrder.ASSIGNED_NAME, WorkOrder.WORK_NAME,
            WorkOrder.REJECTED_NAME, WorkOrder.COMPLETED_NAME
        };

        for (String state : states) {
            boolean needsTechnician = WorkOrder.WORK_NAME.equals(state)
                    || WorkOrder.COMPLETED_NAME.equals(state);
            WorkOrder order = new WorkOrder(5, state, "Alice", "Leak",
                    needsTechnician ? "tech1" : "", "Recorded note");
            assertEquals(5, order.getWorkOrderId());
            assertEquals(state, order.getState());
            assertEquals("Alice", order.getRequester());
            assertEquals("Leak", order.getDescription());
            assertEquals(needsTechnician ? "tech1" : "", order.getTechnician());
            assertEquals("Recorded note", order.getNote());
        }
    }

    /** Tests invalid ids, states, requesters, and descriptions. */
    @Test
    public void testInvalidWorkOrder() {
        int[] invalidIds = { 0, -1 };
        for (int id : invalidIds) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new WorkOrder(id, WorkOrder.SUBMITTED_NAME,
                            "Alice", "Leak", null, null));
            assertEquals("WorkOrder cannot be created.", exception.getMessage());
        }

        String[] invalidStates = { null, "", "Unknown", "submitted" };
        for (String state : invalidStates) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new WorkOrder(1, state, "Alice", "Leak", null, null));
            assertEquals("WorkOrder cannot be created.", exception.getMessage());
        }

        String[] missingValues = { null, "" };
        for (String value : missingValues) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new WorkOrder(value, "Leak"));
            assertEquals("WorkOrder cannot be created.", exception.getMessage());

            exception = assertThrows(IllegalArgumentException.class,
                    () -> new WorkOrder("Alice", value));
            assertEquals("WorkOrder cannot be created.", exception.getMessage());
        }

        assertEquals(1, new WorkOrder("Alice", "Leak").getWorkOrderId());
    }

    /** Tests technician presence rules in every state. */
    @Test
    public void testInvalidTechnician() {
        String[] withoutTechnician = {
            WorkOrder.SUBMITTED_NAME, WorkOrder.REVIEW_NAME,
            WorkOrder.ASSIGNED_NAME, WorkOrder.REJECTED_NAME
        };
        for (String state : withoutTechnician) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new WorkOrder(1, state, "Alice", "Leak",
                            "tech1", "Note"));
            assertEquals("WorkOrder cannot be created.", exception.getMessage());
        }

        String[] withTechnician = {
            WorkOrder.WORK_NAME, WorkOrder.COMPLETED_NAME
        };
        String[] missingValues = { null, "" };
        for (String state : withTechnician) {
            for (String value : missingValues) {
                IllegalArgumentException exception = assertThrows(
                        IllegalArgumentException.class,
                        () -> new WorkOrder(1, state, "Alice", "Leak",
                                value, "Note"));
                assertEquals("WorkOrder cannot be created.", exception.getMessage());
            }
        }
    }

    /** Tests required rejection and completion notes when loading records. */
    @Test
    public void testRequiredLoadedNotes() {
        String[] missingValues = { null, "" };
        for (String value : missingValues) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new WorkOrder(1, WorkOrder.REJECTED_NAME,
                            "Alice", "Leak", null, value));
            assertEquals("A rejection note is required.", exception.getMessage());

            exception = assertThrows(IllegalArgumentException.class,
                    () -> new WorkOrder(1, WorkOrder.COMPLETED_NAME,
                            "Alice", "Leak", "tech1", value));
            assertEquals("A completion note is required.", exception.getMessage());
        }
    }

    /** Tests resetting the counter and loading ids above and below it. */
    @Test
    public void testSetCounter() {
        WorkOrder.setCounter(10);
        assertEquals(10, new WorkOrder("Alice", "Leak").getWorkOrderId());

        WorkOrder lower = new WorkOrder(3, WorkOrder.SUBMITTED_NAME,
                "Alice", "Leak", null, null);
        assertEquals(3, lower.getWorkOrderId());
        assertEquals(11, new WorkOrder("Alice", "Leak").getWorkOrderId());

        WorkOrder higher = new WorkOrder(20, WorkOrder.SUBMITTED_NAME,
                "Alice", "Leak", null, null);
        assertEquals(20, higher.getWorkOrderId());
        assertEquals(21, new WorkOrder("Alice", "Leak").getWorkOrderId());
    }

    /** Tests file formatting with absent and present optional fields. */
    @Test
    public void testToString() {
        WorkOrder order = new WorkOrder("Alice", "Leak");
        assertEquals("* 1,Submitted,Alice,Leak,,", order.toString());

        WorkOrder completed = new WorkOrder(8, WorkOrder.COMPLETED_NAME,
                "Devon", "Latch", "tech1", "Replaced latch");
        assertEquals("* 8,Completed,Devon,Latch,tech1,Replaced latch",
                completed.toString());
    }

    /** Tests submitting a new request for review. */
    @Test
    public void testSubmittedState() {
        WorkOrder order = new WorkOrder("Alice", "Leak");
        order.update(new Command(CommandValue.SUBMIT, null));
        assertEquals(WorkOrder.REVIEW_NAME, order.getState());
        assertEquals("", order.getTechnician());
        assertEquals("", order.getNote());
    }

    /** Tests approval, denial, and missing rejection notes. */
    @Test
    public void testReviewState() {
        WorkOrder approved = new WorkOrder(1, WorkOrder.REVIEW_NAME,
                "Alice", "Leak", null, null);
        approved.update(new Command(CommandValue.APPROVE, "Valid request"));
        assertEquals(WorkOrder.ASSIGNED_NAME, approved.getState());
        assertEquals("Valid request", approved.getNote());

        WorkOrder denied = new WorkOrder(2, WorkOrder.REVIEW_NAME,
                "Devon", "Latch", null, null);
        String[] missingValues = { null, "" };
        for (String value : missingValues) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> denied.update(new Command(CommandValue.DENY, value)));
            assertEquals("A rejection note is required.", exception.getMessage());
            assertEquals(WorkOrder.REVIEW_NAME, denied.getState());
            assertEquals("", denied.getNote());
        }

        denied.update(new Command(CommandValue.DENY, WorkOrder.DENIED));
        assertEquals(WorkOrder.REJECTED_NAME, denied.getState());
        assertEquals(WorkOrder.DENIED, denied.getNote());
        assertEquals("", denied.getTechnician());
    }

    /** Tests technician dispatch and each form of unavailable dispatch. */
    @Test
    public void testAssignedState() {
        WorkOrder dispatched = new WorkOrder(1, WorkOrder.ASSIGNED_NAME,
                "Alice", "Leak", null, null);
        dispatched.update(new Command(CommandValue.DISPATCH, "tech1"));
        assertEquals(WorkOrder.WORK_NAME, dispatched.getState());
        assertEquals("tech1", dispatched.getTechnician());

        String[] reasons = { null, "", WorkOrder.WAITING, WorkOrder.CANCELLED };
        for (String reason : reasons) {
            WorkOrder rejected = new WorkOrder(2, WorkOrder.ASSIGNED_NAME,
                    "Devon", "Latch", null, null);
            rejected.update(new Command(CommandValue.DISPATCH, reason));
            assertEquals(WorkOrder.REJECTED_NAME, rejected.getState());
            assertEquals("", rejected.getTechnician());
            assertEquals(WorkOrder.CANCELLED.equals(reason)
                    ? WorkOrder.CANCELLED : WorkOrder.WAITING, rejected.getNote());
        }
    }

    /** Tests status updates, reassignment, cancellation, and completion. */
    @Test
    public void testWorkState() {
        WorkOrder order = new WorkOrder(1, WorkOrder.WORK_NAME,
                "Alice", "Leak", "tech1", null);
        order.update(new Command(CommandValue.STATUS, "Parts ordered"));
        assertEquals(WorkOrder.WORK_NAME, order.getState());
        assertEquals("Parts ordered", order.getNote());
        assertEquals("tech1", order.getTechnician());

        order.update(new Command(CommandValue.REASSIGN, "Ignored information"));
        assertEquals(WorkOrder.ASSIGNED_NAME, order.getState());
        assertEquals("", order.getTechnician());

        order.update(new Command(CommandValue.DISPATCH, "tech2"));
        assertEquals(WorkOrder.WORK_NAME, order.getState());
        assertEquals("tech2", order.getTechnician());

        String[] missingValues = { null, "" };
        for (String value : missingValues) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> order.update(new Command(CommandValue.COMPLETE, value)));
            assertEquals("A completion note is required.", exception.getMessage());
            assertEquals(WorkOrder.WORK_NAME, order.getState());
            assertEquals("tech2", order.getTechnician());
        }

        order.update(new Command(CommandValue.COMPLETE, "Leak fixed"));
        assertEquals(WorkOrder.COMPLETED_NAME, order.getState());
        assertEquals("Leak fixed", order.getNote());
        assertEquals("tech2", order.getTechnician());

        WorkOrder cancelled = new WorkOrder(2, WorkOrder.WORK_NAME,
                "Devon", "Latch", "tech1", null);
        cancelled.update(new Command(CommandValue.CANCEL, WorkOrder.CANCELLED));
        assertEquals(WorkOrder.REJECTED_NAME, cancelled.getState());
        assertEquals(WorkOrder.CANCELLED, cancelled.getNote());
        assertEquals("", cancelled.getTechnician());
    }

    /** Tests dispatching waiting orders and reopening other rejected orders. */
    @Test
    public void testRejectedState() {
        WorkOrder waiting = new WorkOrder(1, WorkOrder.REJECTED_NAME,
                "Alice", "Leak", null, WorkOrder.WAITING);
        String[] invalidTechnicians = {
            null, "", WorkOrder.WAITING, WorkOrder.CANCELLED
        };

        for (String technician : invalidTechnicians) {
            UnsupportedOperationException exception = assertThrows(
                    UnsupportedOperationException.class,
                    () -> waiting.update(new Command(
                            CommandValue.DISPATCH, technician)));
            assertEquals("Invalid command.", exception.getMessage());
            assertEquals(WorkOrder.REJECTED_NAME, waiting.getState());
            assertEquals(WorkOrder.WAITING, waiting.getNote());
            assertEquals("", waiting.getTechnician());
        }

        waiting.update(new Command(CommandValue.DISPATCH, "tech1"));
        assertEquals(WorkOrder.WORK_NAME, waiting.getState());
        assertEquals("tech1", waiting.getTechnician());

        WorkOrder denied = new WorkOrder(2, WorkOrder.REJECTED_NAME,
                "Devon", "Latch", null, WorkOrder.DENIED);
        denied.update(new Command(CommandValue.REOPEN, "Please reconsider"));
        assertEquals(WorkOrder.REVIEW_NAME, denied.getState());
        assertEquals("Please reconsider", denied.getNote());
        assertEquals("", denied.getTechnician());
        assertEquals(WorkOrder.WAITING, waiting.getNote());
    }

    /** Tests reopening a completed job and retaining its technician. */
    @Test
    public void testCompletedState() {
        WorkOrder order = new WorkOrder(1, WorkOrder.COMPLETED_NAME,
                "Alice", "Leak", "tech1", "Fixed");
        order.update(new Command(CommandValue.REOPEN, "Leak returned"));
        assertEquals(WorkOrder.WORK_NAME, order.getState());
        assertEquals("tech1", order.getTechnician());
        assertEquals("Leak returned", order.getNote());

        order.update(new Command(CommandValue.COMPLETE, "Fixed again"));
        order.update(new Command(CommandValue.REOPEN, null));
        assertEquals(WorkOrder.WORK_NAME, order.getState());
        assertEquals("tech1", order.getTechnician());
        assertEquals("", order.getNote());
    }

    /** Tests every command that is unsupported in each state. */
    @Test
    public void testInvalidCommands() {
        String[] states = {
            WorkOrder.SUBMITTED_NAME, WorkOrder.REVIEW_NAME,
            WorkOrder.ASSIGNED_NAME, WorkOrder.WORK_NAME,
            WorkOrder.REJECTED_NAME, WorkOrder.REJECTED_NAME,
            WorkOrder.COMPLETED_NAME
        };

        for (int index = 0; index < states.length; index++) {
            String state = states[index];
            boolean needsTechnician = WorkOrder.WORK_NAME.equals(state)
                    || WorkOrder.COMPLETED_NAME.equals(state);
            String note = index == 4 ? WorkOrder.WAITING : WorkOrder.DENIED;
            WorkOrder order = new WorkOrder(1, state, "Alice", "Leak",
                    needsTechnician ? "tech1" : null, note);

            for (CommandValue action : CommandValue.values()) {
                boolean supported;
                if (WorkOrder.SUBMITTED_NAME.equals(state)) {
                    supported = action == CommandValue.SUBMIT;
                } else if (WorkOrder.REVIEW_NAME.equals(state)) {
                    supported = action == CommandValue.APPROVE
                            || action == CommandValue.DENY;
                } else if (WorkOrder.ASSIGNED_NAME.equals(state)) {
                    supported = action == CommandValue.DISPATCH;
                } else if (WorkOrder.WORK_NAME.equals(state)) {
                    supported = action == CommandValue.REASSIGN
                            || action == CommandValue.STATUS
                            || action == CommandValue.CANCEL
                            || action == CommandValue.COMPLETE;
                } else if (WorkOrder.REJECTED_NAME.equals(state)) {
                    supported = index == 4 ? action == CommandValue.DISPATCH
                            : action == CommandValue.REOPEN;
                } else {
                    supported = action == CommandValue.REOPEN;
                }

                if (!supported) {
                    String information = action == CommandValue.CANCEL
                            ? WorkOrder.CANCELLED : "Note";
                    Command command = new Command(action, information);
                    String before = order.toString();
                    UnsupportedOperationException exception = assertThrows(
                            UnsupportedOperationException.class,
                            () -> order.update(command));
                    assertEquals("Invalid command.", exception.getMessage());
                    assertEquals(before, order.toString());
                }
            }
        }

        WorkOrder order = new WorkOrder("Alice", "Leak");
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class, () -> order.update(null));
        assertEquals("Invalid command.", exception.getMessage());
        assertEquals(WorkOrder.SUBMITTED_NAME, order.getState());
    }
}