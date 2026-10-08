package edu.ncsu.csc216.wolf_repair.model.manager;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.ncsu.csc216.wolf_repair.model.command.Command;
import edu.ncsu.csc216.wolf_repair.model.command.Command.CommandValue;
import edu.ncsu.csc216.wolf_repair.model.io.RepairSiteReader;
import edu.ncsu.csc216.wolf_repair.model.workorder.WorkOrder;

/**
 * Tests singleton management, repair sites, file operations,
 * work order delegation, and state filtering.
 * @author Aanya Tomar
 */
public class WolfRepairTest {

    /** Temporary directory removed by JUnit after testing. */
    @TempDir
    Path temporaryDirectory;

    /** Manager used by the current test. */
    private WolfRepair manager;

    /** Resets the manager and work order counter before each test. */
    @BeforeEach
    public void setUp() {
        WolfRepair.getInstance().resetManager();
        WorkOrder.setCounter(1);
        manager = WolfRepair.getInstance();
    }

    /** Tests the shared instance and resetting to an empty manager. */
    @Test
    public void testGetInstanceAndResetManager() {
        assertSame(manager, WolfRepair.getInstance());
        manager.addNewRepairSite("Bragaw Hall", "Dorm", 15000);

        manager.resetManager();
        WolfRepair fresh = WolfRepair.getInstance();

        assertNotSame(manager, fresh);
        assertSame(fresh, WolfRepair.getInstance());
        assertEquals(0, fresh.getRepairSiteList().length);
        assertNull(fresh.getActiveRepairSite());
        assertNull(fresh.getActiveRepairSiteName());
    }

    /** Tests site creation, case-insensitive duplicates, and invalid information. */
    @Test
    public void testAddNewRepairSite() {
        manager.addNewRepairSite("Bragaw Hall", "Dorm", 15000);
        RepairSite original = manager.getActiveRepairSite();
        assertEquals("Bragaw Hall", manager.getActiveRepairSiteName());
        assertEquals("Dorm", original.getSiteCategory());
        assertEquals(15000, original.getBudget());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.addNewRepairSite("BRAGAW HALL", "Dorm", 100));
        assertEquals("RepairSite cannot be created.", exception.getMessage());
        assertSame(original, manager.getActiveRepairSite());
        assertEquals(1, manager.getRepairSiteList().length);

        exception = assertThrows(IllegalArgumentException.class,
                () -> manager.addNewRepairSite(null, "Dorm", 100));
        assertEquals("RepairSite cannot be created.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class,
                () -> manager.addNewRepairSite("Invalid", "", 100));
        assertEquals("RepairSite cannot be created.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class,
                () -> manager.addNewRepairSite("Invalid", "Dorm", -1));
        assertEquals("RepairSite cannot be created.", exception.getMessage());

        assertEquals(1, manager.getRepairSiteList().length);
        assertSame(original, manager.getActiveRepairSite());

        manager.addNewRepairSite("Hunt Library", "Academic", 50000);
        assertEquals("Hunt Library", manager.getActiveRepairSiteName());
        assertArrayEquals(new String[] { "Bragaw Hall", "Hunt Library" },
                manager.getRepairSiteList());
    }

    /** Tests site selection and resetting ids for the selected site. */
    @Test
    public void testLoadRepairSite() {
        manager.addNewRepairSite("Bragaw Hall", "Dorm", 15000);
        assertEquals(1, manager.addWorkOrderToSite("Alice", "Leak"));
        assertEquals(2, manager.addWorkOrderToSite("Devon", "Latch"));
        RepairSite first = manager.getActiveRepairSite();

        manager.addNewRepairSite("Hunt Library", "Academic", 50000);
        assertEquals(1, manager.addWorkOrderToSite("Priya", "Door lock"));

        manager.loadRepairSite("Bragaw Hall");
        assertSame(first, manager.getActiveRepairSite());
        assertEquals(3, manager.addWorkOrderToSite("Jamie", "HVAC"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.loadRepairSite("Missing Site"));
        assertEquals("RepairSite not available.", exception.getMessage());
        assertSame(first, manager.getActiveRepairSite());

        exception = assertThrows(IllegalArgumentException.class,
                () -> manager.loadRepairSite(null));
        assertEquals("RepairSite not available.", exception.getMessage());
        assertSame(first, manager.getActiveRepairSite());
    }

    /** Tests site getters when the manager is empty and populated. */
    @Test
    public void testRepairSiteInformation() {
        assertNull(manager.getActiveRepairSite());
        assertNull(manager.getActiveRepairSiteName());
        assertArrayEquals(new String[0], manager.getRepairSiteList());

        manager.addNewRepairSite("Bragaw Hall", "Dorm", 15000);
        assertEquals("Bragaw Hall", manager.getActiveRepairSiteName());
        assertEquals("Bragaw Hall", manager.getActiveRepairSite().getSiteName());
        assertArrayEquals(new String[] { "Bragaw Hall" },
                manager.getRepairSiteList());
        assertTrue(manager.getActiveRepairSite().getWorkOrders().isEmpty());
    }

    /**
     * Tests appending loaded sites and selecting the first loaded site.
     * @throws IOException if the input file cannot be written
     */
    @Test
    public void testLoadRepairSitesFromFile() throws IOException {
        manager.addNewRepairSite("Existing Site", "Dorm", 100);
        manager.addWorkOrderToSite("Alice", "Existing request");

        Path file = temporaryDirectory.resolve("load.txt");
        Files.writeString(file,
                "# First Loaded,Academic,200\n"
                + "* 5,Submitted,Devon,Leak,,\n"
                + "# Second Loaded,Athletic,300\n"
                + "* 20,Assigned,Priya,Latch,,\n",
                StandardCharsets.UTF_8);

        manager.loadRepairSitesFromFile(file.toString());

        assertArrayEquals(new String[] {
            "Existing Site", "First Loaded", "Second Loaded"
        }, manager.getRepairSiteList());
        assertEquals("First Loaded", manager.getActiveRepairSiteName());
        assertEquals("Devon", manager.getWorkOrderById(5).getRequester());
        assertEquals(6, manager.addWorkOrderToSite("Jamie", "HVAC"));

        manager.loadRepairSite("Existing Site");
        assertEquals("Existing request", manager.getWorkOrderById(1).getDescription());
    }

    /**
     * Tests empty loads and missing files preserving existing manager state.
     * @throws IOException if the empty file cannot be written
     */
    @Test
    public void testEmptyAndMissingLoads() throws IOException {
        Path empty = temporaryDirectory.resolve("empty.txt");
        Files.writeString(empty, "", StandardCharsets.UTF_8);

        manager.loadRepairSitesFromFile(empty.toString());
        assertEquals(0, manager.getRepairSiteList().length);
        assertNull(manager.getActiveRepairSite());

        manager.addNewRepairSite("Existing Site", "Dorm", 100);
        RepairSite original = manager.getActiveRepairSite();

        manager.loadRepairSitesFromFile(empty.toString());
        assertEquals(1, manager.getRepairSiteList().length);
        assertSame(original, manager.getActiveRepairSite());

        String missing = temporaryDirectory.resolve("missing.txt").toString();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.loadRepairSitesFromFile(missing));
        assertEquals("Unable to load file " + missing + ".", exception.getMessage());
        assertSame(original, manager.getActiveRepairSite());
        assertEquals(1, manager.getRepairSiteList().length);
    }

    /** Tests saving every populated site and rejecting unavailable output. */
    @Test
    public void testSaveRepairSitesToFile() {
        String fileName = temporaryDirectory.resolve("saved.txt").toString();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.saveRepairSitesToFile(fileName));
        assertEquals("Unable to save file.", exception.getMessage());

        manager.addNewRepairSite("Bragaw Hall", "Dorm", 15000);
        manager.addWorkOrderToSite("Alice", "Leak");
        manager.addNewRepairSite("Hunt Library", "Academic", 50000);
        manager.addWorkOrderToSite("Devon", "Latch");
        manager.addNewRepairSite("Empty Site", "Dorm", 0);

        manager.saveRepairSitesToFile(fileName);
        ArrayList<RepairSite> loaded =
                RepairSiteReader.readRepairSiteFile(fileName);

        assertEquals(2, loaded.size());
        assertEquals("Bragaw Hall", loaded.get(0).getSiteName());
        assertEquals("Leak", loaded.get(0).getWorkOrderById(1).getDescription());
        assertEquals("Hunt Library", loaded.get(1).getSiteName());
        assertEquals("Latch", loaded.get(1).getWorkOrderById(1).getDescription());
        assertEquals("Empty Site", manager.getActiveRepairSiteName());

        exception = assertThrows(IllegalArgumentException.class,
                () -> manager.saveRepairSitesToFile(
                        temporaryDirectory.toString()));
        assertEquals("Unable to save file.", exception.getMessage());
    }

    /** Tests adding and finding work orders through the active site. */
    @Test
    public void testAddAndGetWorkOrder() {
        assertNull(manager.getWorkOrderById(1));
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> manager.addWorkOrderToSite("Alice", "Leak"));
        assertEquals("No active repair site.", exception.getMessage());

        manager.addNewRepairSite("Bragaw Hall", "Dorm", 15000);
        int id = manager.addWorkOrderToSite("Alice", "Leak");

        assertEquals(1, id);
        assertEquals("Alice", manager.getWorkOrderById(id).getRequester());
        assertEquals("Leak", manager.getWorkOrderById(id).getDescription());
        assertEquals(WorkOrder.SUBMITTED_NAME,
                manager.getWorkOrderById(id).getState());
        assertSame(manager.getActiveRepairSite().getWorkOrderById(id),
                manager.getWorkOrderById(id));
        assertNull(manager.getWorkOrderById(99));

        exception = assertThrows(IllegalArgumentException.class,
                () -> manager.addWorkOrderToSite("", "Latch"));
        assertEquals("WorkOrder cannot be created.", exception.getMessage());
        assertEquals(1, manager.getActiveRepairSite().getWorkOrders().size());
    }

    /** Tests command delegation and no-op behavior without a matching order. */
    @Test
    public void testExecuteCommand() {
        manager.executeCommand(1, new Command(CommandValue.SUBMIT, null));
        assertNull(manager.getActiveRepairSite());

        manager.addNewRepairSite("Bragaw Hall", "Dorm", 15000);
        int id = manager.addWorkOrderToSite("Alice", "Leak");

        manager.executeCommand(99, new Command(CommandValue.SUBMIT, null));
        assertEquals(WorkOrder.SUBMITTED_NAME,
                manager.getWorkOrderById(id).getState());

        manager.executeCommand(id, new Command(CommandValue.SUBMIT, null));
        assertEquals(WorkOrder.REVIEW_NAME, manager.getWorkOrderById(id).getState());

        UnsupportedOperationException exception = assertThrows(
                UnsupportedOperationException.class,
                () -> manager.executeCommand(id,
                        new Command(CommandValue.COMPLETE, "Fixed")));
        assertEquals("Invalid command.", exception.getMessage());
        assertEquals(WorkOrder.REVIEW_NAME, manager.getWorkOrderById(id).getState());

        manager.executeCommand(id, new Command(CommandValue.APPROVE, null));
        manager.executeCommand(id, new Command(CommandValue.DISPATCH, "tech1"));
        assertEquals(WorkOrder.WORK_NAME, manager.getWorkOrderById(id).getState());
        assertEquals("tech1", manager.getWorkOrderById(id).getTechnician());
    }

    /** Tests deletion through the active site and no-op cases. */
    @Test
    public void testDeleteWorkOrderById() {
        manager.deleteWorkOrderById(1);
        assertNull(manager.getActiveRepairSite());

        manager.addNewRepairSite("Bragaw Hall", "Dorm", 15000);
        int first = manager.addWorkOrderToSite("Alice", "Leak");
        int second = manager.addWorkOrderToSite("Devon", "Latch");

        manager.deleteWorkOrderById(99);
        assertEquals(2, manager.getActiveRepairSite().getWorkOrders().size());

        manager.deleteWorkOrderById(first);
        assertNull(manager.getWorkOrderById(first));
        assertEquals("Devon", manager.getWorkOrderById(second).getRequester());
        assertEquals(1, manager.getActiveRepairSite().getWorkOrders().size());

        manager.deleteWorkOrderById(second);
        assertEquals(0, manager.getWorkOrdersAsArray("All").length);
    }

    /** Tests array columns, sorted ids, and state filtering. */
    @Test
    public void testGetWorkOrdersAsArray() {
        assertNull(manager.getWorkOrdersAsArray("All"));

        manager.addNewRepairSite("Bragaw Hall", "Dorm", 15000);
        assertEquals(0, manager.getWorkOrdersAsArray("All").length);

        RepairSite site = manager.getActiveRepairSite();
        site.addWorkOrder(new WorkOrder(5, WorkOrder.WORK_NAME,
                "Devon", "HVAC", "tech1", null));
        site.addWorkOrder(new WorkOrder(1, WorkOrder.SUBMITTED_NAME,
                "Alice", "Leak", null, null));
        site.addWorkOrder(new WorkOrder(3, WorkOrder.SUBMITTED_NAME,
                "Priya", "Latch", null, null));

        String[][] all = manager.getWorkOrdersAsArray("All");
        assertEquals(3, all.length);
        assertArrayEquals(new String[] {
            "1", "Submitted", "Alice", "Leak", ""
        }, all[0]);
        assertArrayEquals(new String[] {
            "3", "Submitted", "Priya", "Latch", ""
        }, all[1]);
        assertArrayEquals(new String[] {
            "5", "InProgress", "Devon", "HVAC", "tech1"
        }, all[2]);

        String[][] submitted =
                manager.getWorkOrdersAsArray(WorkOrder.SUBMITTED_NAME);
        assertEquals(2, submitted.length);
        assertArrayEquals(all[0], submitted[0]);
        assertArrayEquals(all[1], submitted[1]);

        String[][] working = manager.getWorkOrdersAsArray(WorkOrder.WORK_NAME);
        assertEquals(1, working.length);
        assertArrayEquals(all[2], working[0]);

        assertEquals(0, manager.getWorkOrdersAsArray(
                WorkOrder.COMPLETED_NAME).length);
        assertEquals(0, manager.getWorkOrdersAsArray("Unknown").length);
    }
}
