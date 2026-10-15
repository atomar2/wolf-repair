package edu.ncsu.csc216.wolf_repair.model.io;

import java.util.ArrayList;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import edu.ncsu.csc216.wolf_repair.model.manager.RepairSite;
import edu.ncsu.csc216.wolf_repair.model.workorder.WorkOrder;

/**
 * Reads repair sites and their associated work orders from a text file.
 * Invalid work orders are skipped. Sites with invalid headers or no valid
 * work orders are skipped entirely.
 * @author Aanya Tomar
 */
public class RepairSiteReader {
    /**
     * Constructs a repair site reader.
     */
    public RepairSiteReader() {
        // Reading is performed through static methods.
    }
    /**
     * Reads repair sites and work orders from the specified file.
     * Returns an empty list if the file does not begin with a site marker
     * or contains no valid sites.
     * @param fileName name of the file to read
     * @return list of valid repair sites in file order
     * @throws IllegalArgumentException if the file cannot be loaded
     */
	public static ArrayList<RepairSite> readRepairSiteFile(String fileName) {
	    ArrayList<RepairSite> sites = new ArrayList<RepairSite>();
	    String contents;

	    if (fileName == null) {
	        throw new IllegalArgumentException("Unable to load file " + fileName + ".");
	    }

	    try {
	        contents = Files.readString(Paths.get(fileName), StandardCharsets.UTF_8);
	    } catch (IOException | IllegalArgumentException | SecurityException exception) {
	        throw new IllegalArgumentException("Unable to load file " + fileName + ".",
	                exception);
	    }

	    if (contents.isEmpty() || contents.charAt(0) != '#') {
	        return sites;
	    }

	    String[] siteRecords = contents.split("(?m)^#", -1);
	    for (int index = 1; index < siteRecords.length; index++) {
	        try {
	            RepairSite site = processSite(siteRecords[index]);
	            if (site != null) {
	                sites.add(site);
	            }
	        } catch (IllegalArgumentException exception) {
	            // Skip this malformed site and continue with the next site.
	        }
	    }

	    return sites;
	}

    /**
     * Processes a site header and its associated work order records.
     * Invalid work orders are skipped.
     * @param siteText text containing one site and its work orders
     * @return repair site with valid work orders, or null if none are valid
     * @throws IllegalArgumentException if the site header is invalid
     */
	private static RepairSite processSite(String siteText) {
	    String[] lines = siteText.split("\\r?\\n", -1);
	    RepairSite site = processSiteLine(lines[0]);

	    for (int index = 1; index < lines.length; index++) {
	        if (lines[index].startsWith("*")) {
	            try {
	                WorkOrder order = processWorkOrder(lines[index].substring(1));
	                site.addWorkOrder(order);
	            } catch (IllegalArgumentException exception) {
	                // Skip an invalid work order or duplicate id.
	            }
	        }
	    }

	    return site.getWorkOrders().isEmpty() ? null : site;
	}

    /**
     * Creates a repair site from its header line.
     * @param siteLine header containing the name, category, and budget
     * @return repair site represented by the header
     * @throws IllegalArgumentException if the header format or site
     *         information is invalid
     */
	private static RepairSite processSiteLine(String siteLine) {
	    String[] fields = siteLine.trim().split(",", -1);
	    if (fields.length != 3) {
	        throw new IllegalArgumentException("RepairSite cannot be created.");
	    }

	    return new RepairSite(fields[0].trim(), fields[1].trim(),
	            Integer.parseInt(fields[2].trim()));
	}

    /**
     * Creates a work order from its record.
     * @param workOrderLine record containing the id, state, requester,
     *        description, technician, and note
     * @return work order represented by the record
     * @throws IllegalArgumentException if the record format or work order
     *         information is invalid
     */
	private static WorkOrder processWorkOrder(String workOrderLine) {
	    String[] fields = workOrderLine.trim().split(",", -1);
	    if (fields.length != 6) {
	        throw new IllegalArgumentException("WorkOrder cannot be created.");
	    }

	    return new WorkOrder(Integer.parseInt(fields[0].trim()),
	            fields[1].trim(), fields[2].trim(), fields[3].trim(),
	            fields[4].trim(), fields[5].trim());
	}
}