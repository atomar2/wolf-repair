package edu.ncsu.csc216.wolf_repair.model.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import edu.ncsu.csc216.wolf_repair.model.manager.RepairSite;
import edu.ncsu.csc216.wolf_repair.model.workorder.WorkOrder;

/**
 * Tests loading repair sites and work orders, skipping invalid records,
 * and handling files that cannot be loaded.
 * @author Aanya Tomar
 */
public class RepairSiteReaderTest {

    /** Temporary directory removed by JUnit after testing. */
    @TempDir
    Path temporaryDirectory;

    /** Resets the shared work order counter before each test. */
    @BeforeEach
    public void setUp() {
        WorkOrder.setCounter(1);
    }

    /**
     * Tests multiple sites, sorted ids, and all six work order states.
     * @throws IOException if the test input cannot be written
     */
    @Test
    public void testReadValidFile() throws IOException {
        String contents = "# Bragaw Hall,Dorm,15000\n"
                + "* 5,InProgress,Devon,HVAC issue,tech1,\n"
                + "* 1,Submitted,Alice,Leaking faucet,,\n"
                + "* 3,Assigned,Priya,No hot water,,\n"
                + "# Hunt Library,Academic,50000\n"
                + "* 8,Completed,Owen,Replaced bulb,tech2,Replaced bulb\n"
                + "* 2,UnderReview,Marcus,Loose railing,,Review note\n"
                + "* 6,Rejected,Sara,Paint request,,Cosmetic request denied\n";
        Path file = temporaryDirectory.resolve("valid.txt");
        Files.writeString(file, contents, StandardCharsets.UTF_8);

        ArrayList<RepairSite> sites =
                RepairSiteReader.readRepairSiteFile(file.toString());

        assertEquals(2, sites.size());
        RepairSite first = sites.get(0);
        assertEquals("Bragaw Hall", first.getSiteName());
        assertEquals("Dorm", first.getSiteCategory());
        assertEquals(15000, first.getBudget());
        assertEquals(3, first.getWorkOrders().size());
        assertEquals(1, first.getWorkOrders().get(0).getWorkOrderId());
        assertEquals(3, first.getWorkOrders().get(1).getWorkOrderId());
        assertEquals(5, first.getWorkOrders().get(2).getWorkOrderId());
        assertEquals(WorkOrder.SUBMITTED_NAME,
                first.getWorkOrderById(1).getState());
        assertEquals(WorkOrder.ASSIGNED_NAME,
                first.getWorkOrderById(3).getState());
        assertEquals("tech1", first.getWorkOrderById(5).getTechnician());
        assertEquals("", first.getWorkOrderById(5).getNote());

        RepairSite second = sites.get(1);
        assertEquals("Hunt Library", second.getSiteName());
        assertEquals("Academic", second.getSiteCategory());
        assertEquals(50000, second.getBudget());
        assertEquals(3, second.getWorkOrders().size());
        assertEquals(WorkOrder.REVIEW_NAME,
                second.getWorkOrderById(2).getState());
        assertEquals("Review note", second.getWorkOrderById(2).getNote());
        assertEquals(WorkOrder.REJECTED_NAME,
                second.getWorkOrderById(6).getState());
        assertEquals("Cosmetic request denied",
                second.getWorkOrderById(6).getNote());
        assertEquals(WorkOrder.COMPLETED_NAME,
                second.getWorkOrderById(8).getState());
        assertEquals("tech2", second.getWorkOrderById(8).getTechnician());
        assertEquals("Replaced bulb", second.getWorkOrderById(8).getNote());
    }

    /**
     * Tests invalid work orders, duplicates, malformed headers, and empty sites.
     * @throws IOException if the test input cannot be written
     */
    @Test
    public void testReadInvalidRecords() throws IOException {
        String contents = "# Valid Site,Dorm,100\n"
                + "* 1,Submitted,Alice,First request,,\n"
                + "* 1,UnderReview,Other,Duplicate request,,\n"
                + "* 2,Submitted,Alice,Too few fields,\n"
                + "* 3,Submitted,Alice,Extra field,,,extra\n"
                + "* bad,Submitted,Alice,Bad id,,\n"
                + "* 0,Submitted,Alice,Zero id,,\n"
                + "* 4,Unknown,Alice,Bad state,,\n"
                + "* 5,Submitted,,Missing requester,,\n"
                + "* 6,Submitted,Alice,,,\n"
                + "* 7,Submitted,Alice,Unexpected technician,tech1,\n"
                + "* 8,InProgress,Alice,Missing technician,,\n"
                + "* 9,Rejected,Alice,Missing rejection note,,\n"
                + "* 10,Completed,Alice,Missing completion note,tech1,\n"
                + "This is not a work order\n"
                + "* 11,UnderReview,Jamie,Valid review,,\n"
                + "# Missing budget,Dorm\n"
                + "* 12,Submitted,Alice,Skip malformed site,,\n"
                + "# Extra header,Dorm,100,extra\n"
                + "* 13,Submitted,Alice,Skip malformed site,,\n"
                + "# Negative budget,Dorm,-1\n"
                + "* 14,Submitted,Alice,Skip negative budget,,\n"
                + "# Non numeric budget,Dorm,money\n"
                + "* 15,Submitted,Alice,Skip bad budget,,\n"
                + "# ,Dorm,100\n"
                + "* 16,Submitted,Alice,Skip missing name,,\n"
                + "# Missing category,,100\n"
                + "* 17,Submitted,Alice,Skip missing category,,\n"
                + "# Empty Site,Dorm,100\n"
                + "# Invalid Orders Only,Dorm,100\n"
                + "* 18,Unknown,Alice,Bad state,,\n"
                + "# Final Site,Academic,0\n"
                + "* 19,Assigned,Priya,Valid final request,,\n";
        Path file = temporaryDirectory.resolve("invalid-records.txt");
        Files.writeString(file, contents, StandardCharsets.UTF_8);

        ArrayList<RepairSite> sites =
                RepairSiteReader.readRepairSiteFile(file.toString());

        assertEquals(2, sites.size());
        RepairSite first = sites.get(0);
        assertEquals("Valid Site", first.getSiteName());
        assertEquals(2, first.getWorkOrders().size());
        assertEquals("Alice", first.getWorkOrderById(1).getRequester());
        assertEquals("First request", first.getWorkOrderById(1).getDescription());
        assertEquals(WorkOrder.SUBMITTED_NAME,
                first.getWorkOrderById(1).getState());
        assertEquals(WorkOrder.REVIEW_NAME,
                first.getWorkOrderById(11).getState());
        assertNull(first.getWorkOrderById(8));

        RepairSite last = sites.get(1);
        assertEquals("Final Site", last.getSiteName());
        assertEquals(0, last.getBudget());
        assertEquals(1, last.getWorkOrders().size());
        assertEquals(19, last.getWorkOrders().get(0).getWorkOrderId());
    }

    /**
     * Tests empty files and files that do not begin with a site marker.
     * @throws IOException if the test inputs cannot be written
     */
    @Test
    public void testInvalidFileStart() throws IOException {
        Path empty = temporaryDirectory.resolve("empty.txt");
        Files.writeString(empty, "", StandardCharsets.UTF_8);
        assertTrue(RepairSiteReader.readRepairSiteFile(
                empty.toString()).isEmpty());

        Path invalid = temporaryDirectory.resolve("invalid-start.txt");
        Files.writeString(invalid,
                "* 1,Submitted,Alice,Leak,,\n"
                + "# Site,Dorm,100\n"
                + "* 2,Submitted,Devon,Latch,,\n",
                StandardCharsets.UTF_8);
        assertTrue(RepairSiteReader.readRepairSiteFile(
                invalid.toString()).isEmpty());
    }

    /**
     * Tests Windows line endings and a final record without a newline.
     * @throws IOException if the test input cannot be written
     */
    @Test
    public void testWindowsLineEndings() throws IOException {
        Path file = temporaryDirectory.resolve("windows.txt");
        Files.writeString(file,
                "# Site,Dorm,100\r\n"
                + "* 1,Submitted,Alice,Leak,,\r\n"
                + "# Second,Academic,200\r\n"
                + "* 2,Assigned,Devon,Latch,,",
                StandardCharsets.UTF_8);

        ArrayList<RepairSite> sites =
                RepairSiteReader.readRepairSiteFile(file.toString());

        assertEquals(2, sites.size());
        assertEquals("Site", sites.get(0).getSiteName());
        assertEquals("Leak", sites.get(0).getWorkOrderById(1).getDescription());
        assertEquals("Second", sites.get(1).getSiteName());
        assertEquals(WorkOrder.ASSIGNED_NAME,
                sites.get(1).getWorkOrderById(2).getState());
    }

    /** Tests the exception message for a missing input file. */
    @Test
    public void testReadMissingFile() {
        String fileName = temporaryDirectory.resolve("missing.txt").toString();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> RepairSiteReader.readRepairSiteFile(fileName));
        assertEquals("Unable to load file " + fileName + ".",
                exception.getMessage());
    }
}