package edu.ncsu.csc216.wolf_repair.model.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import edu.ncsu.csc216.wolf_repair.model.manager.RepairSite;
import edu.ncsu.csc216.wolf_repair.model.workorder.WorkOrder;

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
        if (fileName == null || sites == null) {
            throw new IllegalArgumentException("Unable to save file.");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(fileName), StandardCharsets.UTF_8)) {
            for (RepairSite site : sites) {
                if (!site.getWorkOrders().isEmpty()) {
                    writer.write(site.toString());
                    writer.newLine();

                    for (WorkOrder order : site.getWorkOrders()) {
                        writer.write(order.toString());
                        writer.newLine();
                    }
                }
            }
        } catch (IOException | RuntimeException exception) {
            throw new IllegalArgumentException("Unable to save file.", exception);
        }
    }
}