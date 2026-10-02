package edu.ncsu.csc216.wolf_repair.model.manager;
import org.junit.jupiter.api.Test;
/**
 * Tests repair site construction, validation, work order ordering,
 * duplicate identifiers, deletion, lookup, and command execution.
 * @author Aanya Tomar
 */
public class RepairSiteTest {
    // TODO Add JUnit tests for site information and work order management.
    /** Tests valid site construction and access to site information. */
    @Test
    public void testRepairSite() {
        // TODO Implement assertions.
    }

    /** Tests rejecting invalid site names, categories, and budgets. */
    @Test
    public void testInvalidRepairSite() {
        // TODO Implement assertions.
    }

    /** Tests both add methods, sorted ordering, and duplicate rejection. */
    @Test
    public void testAddWorkOrder() {
        // TODO Implement assertions.
    }

    /** Tests setting the next id from the site's largest work order id. */
    @Test
    public void testSetWorkOrderId() {
        // TODO Implement assertions.
    }

    /** Tests finding work orders by id, including an absent id. */
    @Test
    public void testGetWorkOrderById() {
        // TODO Implement assertions.
    }

    /** Tests deleting an existing work order and handling an absent id. */
    @Test
    public void testDeleteWorkOrderById() {
        // TODO Implement assertions.
    }

    /** Tests applying a command to a work order in the site. */
    @Test
    public void testExecuteCommand() {
        // TODO Implement assertions.
    }

    /** Tests formatting the site header for file output. */
    @Test
    public void testToString() {
        // TODO Implement assertions.
    }
}