package edu.ncsu.csc216.wolf_repair.model.workorder;
import org.junit.jupiter.api.Test;
/**
 * Tests work order construction, validation, identifiers, file formatting,
 * and transitions through all six states.
 * @author Aanya Tomar
 */
public class WorkOrderTest {
    // TODO Add JUnit tests for work order information and state transitions.
    /** Tests creating a Submitted work order with an automatic id. */
    @Test
    public void testNewWorkOrder() {
        // TODO Implement assertions.
    }

    /** Tests constructing a work order from stored information. */
    @Test
    public void testLoadedWorkOrder() {
        // TODO Implement assertions.
    }

    /** Tests rejecting invalid constructor information. */
    @Test
    public void testInvalidWorkOrder() {
        // TODO Implement assertions.
    }

    /** Tests resetting the counter and updating it after loading an id. */
    @Test
    public void testSetCounter() {
        // TODO Implement assertions.
    }

    /** Tests formatting a work order for file output. */
    @Test
    public void testToString() {
        // TODO Implement assertions.
    }

    /** Tests the transition from Submitted to UnderReview. */
    @Test
    public void testSubmittedState() {
        // TODO Implement assertions.
    }

    /** Tests approval and denial from UnderReview. */
    @Test
    public void testReviewState() {
        // TODO Implement assertions.
    }

    /** Tests dispatch and rejection from Assigned. */
    @Test
    public void testAssignedState() {
        // TODO Implement assertions.
    }

    /** Tests reassignment, status updates, cancellation, and completion. */
    @Test
    public void testWorkState() {
        // TODO Implement assertions.
    }

    /** Tests recovery from Rejected according to the rejection reason. */
    @Test
    public void testRejectedState() {
        // TODO Implement assertions.
    }

    /** Tests reopening a completed job while retaining its technician. */
    @Test
    public void testCompletedState() {
        // TODO Implement assertions.
    }

    /** Tests rejecting commands that are inappropriate for each state. */
    @Test
    public void testInvalidCommands() {
        // TODO Implement assertions.
    }
}