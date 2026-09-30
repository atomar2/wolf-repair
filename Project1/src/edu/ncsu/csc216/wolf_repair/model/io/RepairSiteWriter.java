package edu.ncsu.csc216.wolf_repair.model.io;

import java.util.List;

import edu.ncsu.csc216.wolf_repair.model.manager.RepairSite;

/**
 * Writes repair sites and their work orders to a text file that can be
 * loaded back into WolfRepair. Sites with no work orders are omitted.
 * @author Aanya Tomar
 */
public class RepairSiteWriter {

    /**
     * Writes the provided repair sites and their work orders to a file.
     * Preserves site list order and each site's work order order.
     * Sites with no work orders are skipped.
     * @param fileName name of the output file
     * @param sites repair sites to write
     * @throws IllegalArgumentException if the file cannot be saved
     */
    public static void writeRepairSitesToFile(String fileName,
            List<RepairSite> sites) {
        // TODO Write site headers and their work order records.
    }
}