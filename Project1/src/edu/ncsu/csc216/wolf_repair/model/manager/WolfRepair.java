package edu.ncsu.csc216.wolf_repair.model.manager;

import java.util.List;
import java.util.ArrayList;

import edu.ncsu.csc216.wolf_repair.model.io.RepairSiteReader;
import edu.ncsu.csc216.wolf_repair.model.io.RepairSiteWriter;
import edu.ncsu.csc216.wolf_repair.model.command.Command;
import edu.ncsu.csc216.wolf_repair.model.workorder.WorkOrder;

/**
 * Manages repair sites and delegates work order operations to the active site.
 * Uses the Singleton Pattern to provide one shared manager for the application.
 * @author Aanya Tomar
 */
public class WolfRepair {

    /** Shared instance of the WolfRepair manager. */
    private static WolfRepair singleton;
    /** Repair sites maintained in list order. */
    private List<RepairSite> siteList;
    /** Currently selected repair site, or null if none is active. */
    private RepairSite activeSite;

    /**
     * Constructs a manager with an empty repair site list and no active site.
     */
    private WolfRepair() {
        siteList = new ArrayList<RepairSite>();
    }

    /**
     * Returns the shared manager, creating it on the first call.
     * @return shared WolfRepair manager
     */
    public static synchronized WolfRepair getInstance() {
        if (singleton == null) {
            singleton = new WolfRepair();
        }
        return singleton;
    }

    /**
     * Loads valid repair sites from a file and appends them to the site list.
     * Makes the first loaded site active.
     * @param fileName name of the input file
     * @throws IllegalArgumentException if the file cannot be loaded
     */
    public void loadRepairSitesFromFile(String fileName) {
        ArrayList<RepairSite> loadedSites =
                RepairSiteReader.readRepairSiteFile(fileName);
        siteList.addAll(loadedSites);

        if (!loadedSites.isEmpty()) {
            activeSite = loadedSites.get(0);
            activeSite.setWorkOrderId();
        }
    }

    /**
     * Saves all repair sites and their work orders to a file.
     * Sites with no work orders are omitted.
     * @param fileName name of the output file
     * @throws IllegalArgumentException if no site is active or the file
     *         cannot be saved
     */
    public void saveRepairSitesToFile(String fileName) {
        if (activeSite == null) {
            throw new IllegalArgumentException("Unable to save file.");
        }
        RepairSiteWriter.writeRepairSitesToFile(fileName, siteList);
    }

    /**
     * Creates a repair site, adds it to the list, and makes it active.
     * Site names must be unique without regard to case.
     * @param siteName name of the new site
     * @param siteCategory category of the new site
     * @param budget nonnegative repair budget
     * @throws IllegalArgumentException if site information is invalid or
     *         the name matches an existing site without regard to case
     */
    public void addNewRepairSite(String siteName, String siteCategory,
            int budget) {
        for (RepairSite site : siteList) {
            if (site.getSiteName().equalsIgnoreCase(siteName)) {
                throw new IllegalArgumentException("RepairSite cannot be created.");
            }
        }

        RepairSite site = new RepairSite(siteName, siteCategory, budget);
        siteList.add(site);
        loadRepairSite(siteName);
    }

    /**
     * Selects the named repair site and resets the work order counter
     * according to that site's existing work order ids.
     * @param siteName name of the site to select
     * @throws IllegalArgumentException if no matching site exists
     */
    public void loadRepairSite(String siteName) {
        for (RepairSite site : siteList) {
            if (site.getSiteName().equals(siteName)) {
                activeSite = site;
                activeSite.setWorkOrderId();
                return;
            }
        }
        throw new IllegalArgumentException("RepairSite not available.");
    }

    /**
     * Returns the active repair site.
     * @return active site, or null if none is active
     */
    public RepairSite getActiveRepairSite() {
        return activeSite;
    }

    /**
     * Returns the name of the active repair site.
     * @return active site name, or null if none is active
     */
    public String getActiveRepairSiteName() {
        return activeSite == null ? null : activeSite.getSiteName();
    }

    /**
     * Returns repair site names in list order.
     * @return array of repair site names
     */
    public String[] getRepairSiteList() {
        String[] names = new String[siteList.size()];
        for (int index = 0; index < siteList.size(); index++) {
            names[index] = siteList.get(index).getSiteName();
        }
        return names;
    }

    /**
     * Creates and adds a Submitted work order to the active site.
     * @param requester person filing the request
     * @param description description of the repair problem
     * @return id of the added work order
     * @throws IllegalArgumentException if no site is active or the work
     *         order cannot be created
     */
    public int addWorkOrderToSite(String requester, String description) {
        if (activeSite == null) {
            throw new IllegalArgumentException("No active repair site.");
        }
        return activeSite.addWorkOrder(requester, description);
    }

    /**
     * Applies a command to a work order in the active site.
     * Does nothing if no site is active or no matching work order exists.
     * @param id identifier of the work order
     * @param c command to apply
     * @throws UnsupportedOperationException if the command is invalid
     *         for the work order's current state
     * @throws IllegalArgumentException if required command information
     *         is invalid
     */
    public void executeCommand(int id, Command c) {
        if (activeSite != null) {
            activeSite.executeCommand(id, c);
        }
    }

    /**
     * Removes a work order from the active site.
     * Does nothing if no site is active or no matching work order exists.
     * @param id identifier of the work order to remove
     */
    public void deleteWorkOrderById(int id) {
        if (activeSite != null) {
            activeSite.deleteWorkOrderById(id);
        }
    }

    /**
     * Returns active-site work orders filtered by state.
     * Each row contains id, state, requester, description, and technician.
     * Work orders remain ordered by id.
     * @param stateName state to include, or "All" to include every state
     * @return array of work order information, or null if no site is active
     */
    public String[][] getWorkOrdersAsArray(String stateName) {
        if (activeSite == null) {
            return null;
        }

        List<WorkOrder> matchingOrders = new ArrayList<WorkOrder>();
        for (WorkOrder order : activeSite.getWorkOrders()) {
            if ("All".equals(stateName) || order.getState().equals(stateName)) {
                matchingOrders.add(order);
            }
        }

        String[][] rows = new String[matchingOrders.size()][5];
        for (int index = 0; index < matchingOrders.size(); index++) {
            WorkOrder order = matchingOrders.get(index);
            rows[index][0] = Integer.toString(order.getWorkOrderId());
            rows[index][1] = order.getState();
            rows[index][2] = order.getRequester();
            rows[index][3] = order.getDescription();
            rows[index][4] = order.getTechnician();
        }
        return rows;
    }

    /**
     * Finds a work order in the active site.
     * @param id identifier to find
     * @return matching work order, or null if none exists or no site is active
     */
    public WorkOrder getWorkOrderById(int id) {
        return activeSite == null ? null : activeSite.getWorkOrderById(id);
    }

    /**
     * Clears the shared manager reference so the next call to getInstance()
     * creates a fresh manager. Used to isolate tests.
     */
    protected void resetManager() {
        singleton = null;
    }
}