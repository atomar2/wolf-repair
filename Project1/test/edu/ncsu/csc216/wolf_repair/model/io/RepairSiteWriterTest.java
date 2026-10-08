package edu.ncsu.csc216.wolf_repair.model.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import edu.ncsu.csc216.wolf_repair.model.manager.RepairSite;
import edu.ncsu.csc216.wolf_repair.model.workorder.WorkOrder;

/**
 * Tests file output, omission of empty sites, round-trip loading,
 * and failures when saving repair sites.
 * @author Aanya Tomar
 */
public class RepairSiteWriterTest {

    /** Temporary directory removed by JUnit after testing. */
    @TempDir
    Path temporaryDirectory;

    /** Resets the shared work order counter before each test. */
    @BeforeEach
    public void setUp() {
        WorkOrder.setCounter(1);
    }

    /**
     * Tests exact output records, sorted ids, and loading the saved file.
     * @throws IOException if the saved file cannot be read
     */
    @Test
    public void testWriteRepairSitesToFile() throws IOException {
        RepairSite first = new RepairSite("Bragaw Hall", "Dorm", 15000);
        first.addWorkOrder(new WorkOrder(5, WorkOrder.WORK_NAME,
                "Devon", "HVAC issue", "tech1", null));
        first.addWorkOrder(new WorkOrder(1, WorkOrder.SUBMITTED_NAME,
                "Alice", "Leaking faucet", null, null));

        RepairSite second = new RepairSite("Hunt Library", "Academic", 50000);
        second.addWorkOrder(new WorkOrder(8, WorkOrder.COMPLETED_NAME,
                "Owen", "Bulb replaced", "tech2", "Replaced bulb"));

        List<RepairSite> sites = new ArrayList<RepairSite>();
        sites.add(first);
        sites.add(second);

        Path file = temporaryDirectory.resolve("saved.txt");
        RepairSiteWriter.writeRepairSitesToFile(file.toString(), sites);

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        assertEquals(5, lines.size());
        assertEquals("# Bragaw Hall,Dorm,15000", lines.get(0));
        assertEquals("* 1,Submitted,Alice,Leaking faucet,,", lines.get(1));
        assertEquals("* 5,InProgress,Devon,HVAC issue,tech1,", lines.get(2));
        assertEquals("# Hunt Library,Academic,50000", lines.get(3));
        assertEquals("* 8,Completed,Owen,Bulb replaced,tech2,Replaced bulb",
                lines.get(4));

        ArrayList<RepairSite> loaded =
                RepairSiteReader.readRepairSiteFile(file.toString());
        assertEquals(2, loaded.size());
        assertEquals(first.toString(), loaded.get(0).toString());
        assertEquals(second.toString(), loaded.get(1).toString());
        assertEquals(2, loaded.get(0).getWorkOrders().size());
        assertEquals(first.getWorkOrderById(1).toString(),
                loaded.get(0).getWorkOrderById(1).toString());
        assertEquals(first.getWorkOrderById(5).toString(),
                loaded.get(0).getWorkOrderById(5).toString());
        assertEquals(second.getWorkOrderById(8).toString(),
                loaded.get(1).getWorkOrderById(8).toString());
    }

    /**
     * Tests skipping empty sites before and after a populated site.
     * @throws IOException if the saved file cannot be read
     */
    @Test
    public void testSkipEmptySites() throws IOException {
        List<RepairSite> sites = new ArrayList<RepairSite>();
        sites.add(new RepairSite("Empty First", "Dorm", 0));

        RepairSite populated = new RepairSite("Populated", "Academic", 100);
        populated.addWorkOrder("Alice", "Leak");
        sites.add(populated);
        sites.add(new RepairSite("Empty Last", "Athletic", 200));

        Path file = temporaryDirectory.resolve("skip-empty.txt");
        RepairSiteWriter.writeRepairSitesToFile(file.toString(), sites);

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        assertEquals(2, lines.size());
        assertEquals("# Populated,Academic,100", lines.get(0));
        assertEquals("* 1,Submitted,Alice,Leak,,", lines.get(1));

        ArrayList<RepairSite> loaded =
                RepairSiteReader.readRepairSiteFile(file.toString());
        assertEquals(1, loaded.size());
        assertEquals("Populated", loaded.get(0).getSiteName());
    }

    /**
     * Tests that empty input produces an empty file and replaces old contents.
     * @throws IOException if the test file cannot be written or read
     */
    @Test
    public void testWriteEmptyList() throws IOException {
        Path file = temporaryDirectory.resolve("empty.txt");
        Files.writeString(file, "Old contents", StandardCharsets.UTF_8);

        List<RepairSite> sites = new ArrayList<RepairSite>();
        RepairSiteWriter.writeRepairSitesToFile(file.toString(), sites);
        assertEquals("", Files.readString(file, StandardCharsets.UTF_8));

        sites.add(new RepairSite("Empty Site", "Dorm", 100));
        RepairSiteWriter.writeRepairSitesToFile(file.toString(), sites);
        assertEquals("", Files.readString(file, StandardCharsets.UTF_8));
        assertTrue(RepairSiteReader.readRepairSiteFile(
                file.toString()).isEmpty());
    }

    /** Tests saving to a directory or a path whose parent does not exist. */
    @Test
    public void testWriteInvalidFile() {
        List<RepairSite> sites = new ArrayList<RepairSite>();
        RepairSite site = new RepairSite("Bragaw Hall", "Dorm", 15000);
        site.addWorkOrder("Alice", "Leak");
        sites.add(site);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> RepairSiteWriter.writeRepairSitesToFile(
                        temporaryDirectory.toString(), sites));
        assertEquals("Unable to save file.", exception.getMessage());

        String missingParent = temporaryDirectory.resolve("missing")
                .resolve("output.txt").toString();
        exception = assertThrows(IllegalArgumentException.class,
                () -> RepairSiteWriter.writeRepairSitesToFile(
                        missingParent, sites));
        assertEquals("Unable to save file.", exception.getMessage());
    }

    /** Tests rejecting null file names and null site lists. */
    @Test
    public void testNullArguments() {
        List<RepairSite> sites = new ArrayList<RepairSite>();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> RepairSiteWriter.writeRepairSitesToFile(null, sites));
        assertEquals("Unable to save file.", exception.getMessage());

        String fileName = temporaryDirectory.resolve("null-sites.txt").toString();
        exception = assertThrows(IllegalArgumentException.class,
                () -> RepairSiteWriter.writeRepairSitesToFile(fileName, null));
        assertEquals("Unable to save file.", exception.getMessage());
    }
}