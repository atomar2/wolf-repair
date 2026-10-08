package edu.ncsu.csc216.wolf_repair.model.manager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.ncsu.csc216.wolf_repair.model.command.Command;
import edu.ncsu.csc216.wolf_repair.model.command.Command.CommandValue;
import edu.ncsu.csc216.wolf_repair.model.workorder.WorkOrder;

/**
 * Tests repair site validation, work order ordering, lookup,
 * deletion, identifiers, and command execution.
 * @author Aanya Tomar
 */
public class RepairSiteTest {

    /** Resets the shared work order counter before each test. */
    @BeforeEach
    public void setUp() {
        WorkOrder.setCounter(1);
    }

    /** Tests valid construction, getters, and an initially empty list. */
    @Test
    public void testRepairSite() {
        RepairSite site = new RepairSite("Bragaw Hall", "Dorm", 15000);
        assertEquals("Bragaw Hall", site.getSiteName());
        assertEquals("Dorm", site.getSiteCategory());
        assertEquals(15000, site.getBudget());
        assertTrue(site.getWorkOrders().isEmpty());

        RepairSite zeroBudget = new RepairSite("Hunt Library", "Academic", 0);
        assertEquals(0, zeroBudget.getBudget());
        assertTrue(zeroBudget.getWorkOrders().isEmpty());
    }

    /** Tests rejecting missing site information and negative budgets. */
    @Test
    public void testInvalidRepairSite() {
        String[] missingValues = { null, "" };
        for (String value : missingValues) {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> new RepairSite(value, "Dorm", 15000));
            assertEquals("RepairSite cannot be created.", exception.getMessage());

            exception = assertThrows(IllegalArgumentException.class,
                    () -> new RepairSite("Bragaw Hall", value, 15000));
            assertEquals("RepairSite cannot be created.", exception.getMessage());
        }

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new RepairSite("Bragaw Hall", "Dorm", -1));
        assertEquals("RepairSite cannot be created.", exception.getMessage());
    }

    /** Tests inserting at the beginning, middle, and end of the list. */
    @Test
    public void testAddWorkOrder() {
        RepairSite site = new RepairSite("Bragaw Hall", "Dorm", 15000);
        WorkOrder five = new WorkOrder(5, WorkOrder.SUBMITTED_NAME,
                "Devon", "HVAC issue", null, null);
        WorkOrder one = new WorkOrder(1, WorkOrder.SUBMITTED_NAME,
                "Alice", "Leaking faucet", null, null);
        WorkOrder three = new WorkOrder(3, WorkOrder.ASSIGNED_NAME,
                "Priya", "No hot water", null, null);

        assertEquals(5, site.addWorkOrder(five));
        assertEquals(1, site.addWorkOrder(one));
        assertEquals(3, site.addWorkOrder(three));

        assertEquals(3, site.getWorkOrders().size());
        assertSame(one, site.getWorkOrders().get(0));
        assertSame(three, site.getWorkOrders().get(1));
        assertSame(five, site.getWorkOrders().get(2));

        int newId = site.addWorkOrder("Jamie", "Broken latch");
        assertEquals(6, newId);
        assertEquals(4, site.getWorkOrders().size());
        assertEquals(6, site.getWorkOrders().get(3).getWorkOrderId());
        assertEquals(WorkOrder.SUBMITTED_NAME,
                site.getWorkOrderById(newId).getState());
        assertEquals("Jamie", site.getWorkOrderById(newId).getRequester());
        assertEquals("Broken latch", site.getWorkOrderById(newId).getDescription());

        WorkOrder duplicate = new WorkOrder(3, WorkOrder.REVIEW_NAME,
                "Other requester", "Other problem", null, null);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> site.addWorkOrder(duplicate));
        assertEquals("WorkOrder cannot be created.", exception.getMessage());
        assertEquals(4, site.getWorkOrders().size());
        assertSame(three, site.getWorkOrders().get(1));
    }

    /** Tests invalid additions leaving the existing list unchanged. */
    @Test
    public void testInvalidAddWorkOrder() {
        RepairSite site = new RepairSite("Bragaw Hall", "Dorm", 15000);
        int id = site.addWorkOrder("Alice", "Leak");
        WorkOrder original = site.getWorkOrderById(id);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> site.addWorkOrder((WorkOrder) null));
        assertEquals("WorkOrder cannot be created.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class,
                () -> site.addWorkOrder("", "Latch"));
        assertEquals("WorkOrder cannot be created.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class,
                () -> site.addWorkOrder("Devon", null));
        assertEquals("WorkOrder cannot be created.", exception.getMessage());

        assertEquals(1, site.getWorkOrders().size());
        assertSame(original, site.getWorkOrders().get(0));
    }

    /** Tests choosing ids from an empty list and a populated list. */
    @Test
    public void testSetWorkOrderId() {
        RepairSite site = new RepairSite("Bragaw Hall", "Dorm", 15000);
        WorkOrder.setCounter(100);
        site.setWorkOrderId();
        assertEquals(1, site.addWorkOrder("Alice", "Leak"));

        WorkOrder ten = new WorkOrder(10, WorkOrder.SUBMITTED_NAME,
                "Devon", "Latch", null, null);
        assertEquals(10, site.addWorkOrder(ten));

        WorkOrder.setCounter(100);
        site.setWorkOrderId();
        assertEquals(11, site.addWorkOrder("Priya", "HVAC"));

        RepairSite other = new RepairSite("Hunt Library", "Academic", 50000);
        other.setWorkOrderId();
        assertEquals(1, other.addWorkOrder("Jamie", "Door lock"));
    }

    /** Tests finding existing ids and returning null for absent ids. */
    @Test
    public void testGetWorkOrderById() {
        RepairSite site = new RepairSite("Bragaw Hall", "Dorm", 15000);
        assertNull(site.getWorkOrderById(1));

        int firstId = site.addWorkOrder("Alice", "Leak");
        int secondId = site.addWorkOrder("Devon", "Latch");
        assertSame(site.getWorkOrders().get(0),
                site.getWorkOrderById(firstId));
        assertSame(site.getWorkOrders().get(1),
                site.getWorkOrderById(secondId));
        assertNull(site.getWorkOrderById(99));
        assertNull(site.getWorkOrderById(-1));
    }

    /** Tests deleting a matching order and ignoring absent ids. */
    @Test
    public void testDeleteWorkOrderById() {
        RepairSite site = new RepairSite("Bragaw Hall", "Dorm", 15000);
        site.deleteWorkOrderById(99);
        assertTrue(site.getWorkOrders().isEmpty());

        int firstId = site.addWorkOrder("Alice", "Leak");
        int secondId = site.addWorkOrder("Devon", "Latch");
        int thirdId = site.addWorkOrder("Priya", "HVAC");

        site.deleteWorkOrderById(secondId);
        assertEquals(2, site.getWorkOrders().size());
        assertNull(site.getWorkOrderById(secondId));
        assertEquals(firstId, site.getWorkOrders().get(0).getWorkOrderId());
        assertEquals(thirdId, site.getWorkOrders().get(1).getWorkOrderId());

        site.deleteWorkOrderById(99);
        assertEquals(2, site.getWorkOrders().size());

        site.deleteWorkOrderById(firstId);
        site.deleteWorkOrderById(thirdId);
        assertTrue(site.getWorkOrders().isEmpty());
    }

    /** Tests command delegation, missing ids, and invalid transitions. */
    @Test
    public void testExecuteCommand() {
        RepairSite site = new RepairSite("Bragaw Hall", "Dorm", 15000);
        int id = site.addWorkOrder("Alice", "Leak");

        site.executeCommand(99, new Command(CommandValue.SUBMIT, null));
        assertEquals(WorkOrder.SUBMITTED_NAME,
                site.getWorkOrderById(id).getState());

        site.executeCommand(id, new Command(CommandValue.SUBMIT, null));
        assertEquals(WorkOrder.REVIEW_NAME, site.getWorkOrderById(id).getState());

        site.executeCommand(id, new Command(CommandValue.APPROVE, "Approved"));
        assertEquals(WorkOrder.ASSIGNED_NAME, site.getWorkOrderById(id).getState());
        assertEquals("Approved", site.getWorkOrderById(id).getNote());

        site.executeCommand(id, new Command(CommandValue.DISPATCH, "tech1"));
        assertEquals(WorkOrder.WORK_NAME, site.getWorkOrderById(id).getState());
        assertEquals("tech1", site.getWorkOrderById(id).getTechnician());

        String before = site.getWorkOrderById(id).toString();
        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> site.executeCommand(id,
                        new Command(CommandValue.SUBMIT, null)));
        assertEquals("Invalid command.", exception.getMessage());
        assertEquals(before, site.getWorkOrderById(id).toString());

        IllegalArgumentException missingNote = assertThrows(
                IllegalArgumentException.class,
                () -> site.executeCommand(id,
                        new Command(CommandValue.COMPLETE, "")));
        assertEquals("A completion note is required.", missingNote.getMessage());
        assertEquals(before, site.getWorkOrderById(id).toString());
    }

    /** Tests formatting a repair site header for file output. */
    @Test
    public void testToString() {
        RepairSite site = new RepairSite("Bragaw Hall", "Dorm", 15000);
        assertEquals("# Bragaw Hall,Dorm,15000", site.toString());

        RepairSite zeroBudget = new RepairSite("Hunt Library", "Academic", 0);
        assertEquals("# Hunt Library,Academic,0", zeroBudget.toString());
    }
}