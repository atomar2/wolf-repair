package edu.ncsu.csc216.wolf_repair.model.manager;

import java.util.List;

import edu.ncsu.csc216.wolf_repair.model.command.Command;
import edu.ncsu.csc216.wolf_repair.model.workorder.WorkOrder;

/**
 * Represents a repair site with a name, category, budget, and work orders.
 * Maintains work orders in ascending order by their identifiers.
 * @author Aanya Tomar
 */
public class RepairSite {

    /** Name of the repair site. */
    private String siteName;
    /** Category of the repair site. */
    private String siteCategory;
    /** Nonnegative repair budget for the site. */
    private int budget;
    /** Work orders maintained in ascending order by id. */
    private List<WorkOrder> workOrders;

    /**
     * Constructs a repair site with an empty work order list.
     * @param siteName name of the site
     * @param siteCategory category of the site
     * @param budget nonnegative repair budget
     * @throws IllegalArgumentException if the name or category is null or
     *         empty, or the budget is negative
     */
    public RepairSite(String siteName, String siteCategory, int budget) {
        // TODO Validate site information and initialize the work order list.
    }

    /**
     * Sets the work order counter to one greater than the largest id
     * currently in this site's list, or 1 if the list is empty.
     */
    public void setWorkOrderId() {
        // TODO Determine the next available work order id.
    }

    /**
     * Sets the site name.
     * @param siteName name of the site
     * @throws IllegalArgumentException if the name is null or empty
     */
    private void setSiteName(String siteName) {
        // TODO Validate and set the site name.
    }

    /**
     * Returns the site name.
     * @return name of the site
     */
    public String getSiteName() {
        return null;
    }

    /**
     * Sets the site category.
     * @param siteCategory category of the site
     * @throws IllegalArgumentException if the category is null or empty
     */
    private void setSiteCategory(String siteCategory) {
        // TODO Validate and set the site category.
    }

    /**
     * Returns the site category.
     * @return category of the site
     */
    public String getSiteCategory() {
        return null;
    }

    /**
     * Sets the repair budget.
     * @param budget nonnegative repair budget
     * @throws IllegalArgumentException if the budget is negative
     */
    private void setBudget(int budget) {
        // TODO Validate and set the budget.
    }

    /**
     * Returns the repair budget.
     * @return budget of the site
     */
    public int getBudget() {
        return 0;
    }

    /**
     * Creates a Submitted work order and adds it in ascending order by id.
     * @param requester person filing the request
     * @param description description of the repair problem
     * @return id of the added work order
     * @throws IllegalArgumentException if the work order information is invalid
     *         or its id duplicates an existing work order
     */
    public int addWorkOrder(String requester, String description) {
        // TODO Create and add a new work order.
        return 0;
    }

    /**
     * Adds an existing work order in ascending order by id.
     * A duplicate id leaves the existing list unchanged.
     * @param workOrder work order to add
     * @return id of the added work order
     * @throws IllegalArgumentException if the id duplicates an existing
     *         work order
     */
    public int addWorkOrder(WorkOrder workOrder) {
        // TODO Insert the work order in sorted order.
        return 0;
    }

    /**
     * Returns the site's work orders in ascending order by id.
     * @return list of work orders
     */
    public List<WorkOrder> getWorkOrders() {
        return null;
    }

    /**
     * Finds a work order with the given id.
     * @param id identifier to find
     * @return matching work order, or null if no match exists
     */
    public WorkOrder getWorkOrderById(int id) {
        return null;
    }

    /**
     * Applies a command to the work order with the given id.
     * Does nothing if no matching work order exists.
     * @param id identifier of the work order
     * @param c command to apply
     * @throws UnsupportedOperationException if the command is invalid for
     *         the work order's current state
     * @throws IllegalArgumentException if required command information is invalid
     */
    public void executeCommand(int id, Command c) {
        // TODO Find the work order and apply the command.
    }

    /**
     * Removes the work order with the given id.
     * Does nothing if no matching work order exists.
     * @param id identifier of the work order to remove
     */
    public void deleteWorkOrderById(int id) {
        // TODO Find and remove the work order.
    }

    /**
     * Returns the site's header in the file format.
     * @return record containing "# " followed by the name, category,
     *         and budget separated by commas
     */
    @Override
    public String toString() {
        return null;
    }
}