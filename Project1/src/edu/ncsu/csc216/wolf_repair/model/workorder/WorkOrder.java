package edu.ncsu.csc216.wolf_repair.model.workorder;

import edu.ncsu.csc216.wolf_repair.model.command.Command;
import edu.ncsu.csc216.wolf_repair.model.command.Command.CommandValue;
/**
 * Represents a repair request and manages its lifecycle using the State Pattern.
 * Stores the request information, assigned technician, note, and current state.
 * @author Aanya Tomar
 */
public class WorkOrder {

    /** Reason for denying an invalid, duplicate, or non-actionable request. */
    public static final String DENIED = "Invalid/Duplicate/Not Actionable";
    /** Rejection reason indicating that no technician is available. */
    public static final String WAITING = "No technician available";
    /** Rejection reason indicating cancellation by the requester. */
    public static final String CANCELLED = "Cancelled by requester";

    /** Name of the Submitted state. */
    public static final String SUBMITTED_NAME = "Submitted";
    /** Name of the UnderReview state. */
    public static final String REVIEW_NAME = "UnderReview";
    /** Name of the Assigned state. */
    public static final String ASSIGNED_NAME = "Assigned";
    /** Name of the InProgress state. */
    public static final String WORK_NAME = "InProgress";
    /** Name of the Rejected state. */
    public static final String REJECTED_NAME = "Rejected";
    /** Name of the Completed state. */
    public static final String COMPLETED_NAME = "Completed";

    /** Identifier of this work order. */
    private int workOrderId;
    /** Person who filed the request. */
    private String requester;
    /** Description of the repair problem. */
    private String description;
    /** Assigned technician, or null when none is recorded. */
    private String technician;
    /** Note associated with the work order, or null when none is recorded. */
    private String note;
    /** Identifier to assign to the next newly created work order. */
    private static int counter = 1;

    /** Current state of this work order. */
    private WorkOrderState currentState;
    /** Submitted state instance. */
    private final WorkOrderState submittedState = new SubmittedState();
    /** UnderReview state instance. */
    private final WorkOrderState reviewState = new ReviewState();
    /** Assigned state instance. */
    private final WorkOrderState assignedState = new AssignedState();
    /** InProgress state instance. */
    private final WorkOrderState workState = new WorkState();
    /** Rejected state instance. */
    private final WorkOrderState rejectedState = new RejectedState();
    /** Completed state instance. */
    private final WorkOrderState completedState = new CompletedState();

    /**
     * Constructs a Submitted work order with an automatically assigned id.
     * The counter is incremented after assigning the id.
     * @param requester person filing the request
     * @param description description of the problem
     * @throws IllegalArgumentException if requester or description is null
     *         or empty
     */
    public WorkOrder(String requester, String description) {
        this(counter, SUBMITTED_NAME, requester, description, null, null);
    }

    /**
     * Constructs a work order from stored information.
     * Updates the counter if needed to avoid assigning an existing id.
     * @param id positive work order identifier
     * @param state name of the work order state
     * @param requester person filing the request
     * @param description description of the problem
     * @param technician assigned technician, required only for InProgress
     *        and Completed states
     * @param note associated note, required for Rejected and Completed states
     * @throws IllegalArgumentException if any information is invalid for
     *         the specified state
     */
    public WorkOrder(int id, String state, String requester,
            String description, String technician, String note) {
        setId(id);
        setState(state);
        setRequester(requester);
        setDescription(description);
        setTechnician(technician);
        setNote(note);

        if (id >= counter) {
            counter = id + 1;
        }
    }

    /**
     * Sets the work order identifier.
     * @param id positive identifier
     * @throws IllegalArgumentException if id is not positive
     */
    private void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("WorkOrder cannot be created.");
        }
        workOrderId = id;
    }

    /**
     * Returns the work order identifier.
     * @return the work order id
     */
    public int getWorkOrderId() {
        return workOrderId;
    }

    /**
     * Sets the current state from its name.
     * @param stateValue name of the state
     * @throws IllegalArgumentException if the state name is unrecognized
     */
    private void setState(String stateValue) {
        if (SUBMITTED_NAME.equals(stateValue)) {
            currentState = submittedState;
        } else if (REVIEW_NAME.equals(stateValue)) {
            currentState = reviewState;
        } else if (ASSIGNED_NAME.equals(stateValue)) {
            currentState = assignedState;
        } else if (WORK_NAME.equals(stateValue)) {
            currentState = workState;
        } else if (REJECTED_NAME.equals(stateValue)) {
            currentState = rejectedState;
        } else if (COMPLETED_NAME.equals(stateValue)) {
            currentState = completedState;
        } else {
            throw new IllegalArgumentException("WorkOrder cannot be created.");
        }
    }

    /**
     * Returns the name of the current state.
     * @return the state name
     */
    public String getState() {
        return currentState.getStateName();
    }

    /**
     * Sets the requester.
     * @param requester person filing the request
     * @throws IllegalArgumentException if requester is null or empty
     */
    private void setRequester(String requester) {
        if (requester == null || requester.isEmpty()) {
            throw new IllegalArgumentException("WorkOrder cannot be created.");
        }
        this.requester = requester;
    }

    /**
     * Returns the requester.
     * @return the person who filed the request
     */
    public String getRequester() {
        return requester;
    }

    /**
     * Sets the description of the problem.
     * @param description problem description
     * @throws IllegalArgumentException if description is null or empty
     */
    private void setDescription(String description) {
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("WorkOrder cannot be created.");
        }
        this.description = description;
    }

    /**
     * Returns the description of the problem.
     * @return the problem description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the technician according to the current state's requirements.
     * @param technician technician identifier or no recorded technician
     * @throws IllegalArgumentException if technician presence does not
     *         match the current state
     */
    private void setTechnician(String technician) {
        boolean present = technician != null && !technician.isEmpty();
        boolean required = currentState == workState || currentState == completedState;

        if (present != required) {
            throw new IllegalArgumentException("WorkOrder cannot be created.");
        }
        this.technician = technician;
    }

    /**
     * Returns the recorded technician.
     * @return technician identifier, or an empty string if none is recorded
     */
    public String getTechnician() {
    	return technician == null ? "" : technician;
    }

    /**
     * Sets the note according to the current state's requirements.
     * @param note note to record
     * @throws IllegalArgumentException if a required rejection or completion
     *         note is null or empty
     */
    private void setNote(String note) {
        if (note == null || note.isEmpty()) {
            if (currentState == rejectedState) {
                throw new IllegalArgumentException("A rejection note is required.");
            }
            if (currentState == completedState) {
                throw new IllegalArgumentException("A completion note is required.");
            }
        }
        this.note = note;
    }

    /**
     * Returns the recorded note.
     * @return the note, or an empty string if none is recorded
     */
    public String getNote() {
        return note == null ? "" : note;
    }

    /**
     * Sets the identifier counter used when creating new work orders.
     * @param newCount next identifier to assign
     */
    public static void setCounter(int newCount) {
        counter = newCount;
    }

    /**
     * Returns this work order in the comma-separated file format.
     * Includes the id, state, requester, description, technician, and note.
     * @return formatted work order record beginning with "* "
     */
    @Override
    public String toString() {
        return "* " + workOrderId + "," + getState() + "," + requester
                + "," + description + "," + getTechnician() + "," + getNote();
    }

    /**
     * Applies a command through the current state's transition behavior.
     * @param c command to apply
     * @throws UnsupportedOperationException if the command is not valid
     *         for the current state
     * @throws IllegalArgumentException if the transition requires a note
     *         that is null or empty
     */
    public void update(Command c) {
        if (c == null) {
            throw new UnsupportedOperationException("Invalid command.");
        }
        currentState.updateState(c);
    }

    /**
     * Interface for states in the WorkOrder State Pattern. All concrete
     * WorkOrder states must implement the WorkOrderState interface.
     * The WorkOrderState interface should be a private interface of the
     * WorkOrder class.
     * @author Sarah Heckman
     * @author Chandrika Satyavolu
     */
    private interface WorkOrderState {

        /**
         * Update the WorkOrder from the given Command. An
         * UnsupportedOperationException is thrown if the Command is not a valid
         * action for the given state.
         * @param command Command describing the action that will update the
         * WorkOrder's state
         * @throws UnsupportedOperationException if the Command is not valid for
         * this state
         */
        void updateState(Command command);

        /**
         * Returns the name of this state as a String.
         * @return the name of this state
         */
        String getStateName();

    }

    /**
     * Represents a newly submitted request awaiting submission for review.
     */
    private class SubmittedState implements WorkOrderState {
        /** Constructs the Submitted state. */
        private SubmittedState() {
            // Data is maintained by the enclosing work order.
        }
        /**
         * Processes SUBMIT to move the work order to UnderReview.
         * @param command action to apply
         * @throws UnsupportedOperationException if the action is not SUBMIT
         */
    	@Override
    	public void updateState(Command command) {
    	    if (command.getCommand() != CommandValue.SUBMIT) {
    	        throw new UnsupportedOperationException("Invalid command.");
    	    }
    	    currentState = reviewState;
    	}

        /**
         * Returns the Submitted state name.
         * @return the state name
         */
    	@Override
    	public String getStateName() {
    	    return SUBMITTED_NAME;
    	}
    }

    /**
     * Represents a request being reviewed for approval or denial.
     */
    private class ReviewState implements WorkOrderState {
        /** Constructs the UnderReview state. */
        private ReviewState() {
            // Data is maintained by the enclosing work order.
        }
        /**
         * Processes APPROVE to Assigned or DENY to Rejected.
         * @param command action to apply
         * @throws UnsupportedOperationException if the action is not supported
         * @throws IllegalArgumentException if DENY has no required note
         */
    	@Override
    	public void updateState(Command command) {
    	    if (command.getCommand() == CommandValue.APPROVE) {
    	        note = command.getCommandInformation();
    	        currentState = assignedState;
    	    } else if (command.getCommand() == CommandValue.DENY) {
    	        String information = command.getCommandInformation();
    	        if (information == null || information.isEmpty()) {
    	            throw new IllegalArgumentException("A rejection note is required.");
    	        }
    	        note = information;
    	        technician = null;
    	        currentState = rejectedState;
    	    } else {
    	        throw new UnsupportedOperationException("Invalid command.");
    	    }
    	}

        /**
         * Returns the UnderReview state name.
         * @return the state name
         */
    	@Override
    	public String getStateName() {
    	    return REVIEW_NAME;
    	}
    }

    /**
     * Represents an approved request awaiting technician dispatch.
     */
    private class AssignedState implements WorkOrderState {
        /** Constructs the Assigned state. */
        private AssignedState() {
            // Data is maintained by the enclosing work order.
        }
        /**
         * Processes DISPATCH to InProgress or Rejected based on its information.
         * @param command action to apply
         * @throws UnsupportedOperationException if the action is not supported
         */
    	@Override
    	public void updateState(Command command) {
    	    if (command.getCommand() != CommandValue.DISPATCH) {
    	        throw new UnsupportedOperationException("Invalid command.");
    	    }

    	    String information = command.getCommandInformation();
    	    if (information == null || information.isEmpty()
    	            || WAITING.equals(information) || CANCELLED.equals(information)) {
    	        note = CANCELLED.equals(information) ? CANCELLED : WAITING;
    	        technician = null;
    	        currentState = rejectedState;
    	    } else {
    	    	technician = information;
    	    	currentState = workState;
    	    }
    	}

        /**
         * Returns the Assigned state name.
         * @return the state name
         */
    	@Override
    	public String getStateName() {
    	    return ASSIGNED_NAME;
    	}
    }

    /**
     * Represents a repair being performed by a technician.
     */
    private class WorkState implements WorkOrderState {
        /** Constructs the InProgress state. */
        private WorkState() {
            // Data is maintained by the enclosing work order.
        }
        /**
         * Processes reassignment, status updates, cancellation, or completion.
         * @param command action to apply
         * @throws UnsupportedOperationException if the action is not supported
         * @throws IllegalArgumentException if a required note is missing
         */
    	@Override
    	public void updateState(Command command) {
    	    if (command.getCommand() == CommandValue.REASSIGN) {
    	        technician = null;
    	        currentState = assignedState;
    	    } else if (command.getCommand() == CommandValue.STATUS) {
    	        note = command.getCommandInformation();
    	    } else if (command.getCommand() == CommandValue.CANCEL) {
    	        note = command.getCommandInformation();
    	        technician = null;
    	        currentState = rejectedState;
    	    } else if (command.getCommand() == CommandValue.COMPLETE) {
    	        String information = command.getCommandInformation();
    	        if (information == null || information.isEmpty()) {
    	            throw new IllegalArgumentException("A completion note is required.");
    	        }
    	        note = information;
    	        currentState = completedState;
    	    } else {
    	        throw new UnsupportedOperationException("Invalid command.");
    	    }
    	}

        /**
         * Returns the InProgress state name.
         * @return the state name
         */
    	@Override
    	public String getStateName() {
    	    return WORK_NAME;
    	}
    }

    /**
     * Represents a rejected request that may be dispatched or reconsidered,
     * depending on its recorded rejection reason.
     */
    private class RejectedState implements WorkOrderState {
        /** Constructs the Rejected state. */
        private RejectedState() {
            // Data is maintained by the enclosing work order.
        }
        /**
         * Dispatches a waiting request or reopens another rejected request.
         * @param command action to apply
         * @throws UnsupportedOperationException if the action or dispatch
         *         information is not valid for the rejection reason
         */
    	@Override
    	public void updateState(Command command) {
    	    String information = command.getCommandInformation();

    	    if (WAITING.equals(note)) {
    	        if (command.getCommand() != CommandValue.DISPATCH
    	                || information == null || information.isEmpty()
    	                || WAITING.equals(information) || CANCELLED.equals(information)) {
    	            throw new UnsupportedOperationException("Invalid command.");
    	        }
    	        technician = information;
    	        currentState = workState;
    	    } else {
    	        if (command.getCommand() != CommandValue.REOPEN) {
    	            throw new UnsupportedOperationException("Invalid command.");
    	        }
    	        note = information;
    	        currentState = reviewState;
    	    }
    	}

        /**
         * Returns the Rejected state name.
         * @return the state name
         */
    	@Override
    	public String getStateName() {
    	    return REJECTED_NAME;
    	}
    }

    /**
     * Represents a finished repair that can be reopened if the issue recurs.
     */
    private class CompletedState implements WorkOrderState {
        /** Constructs the Completed state. */
        private CompletedState() {
            // Data is maintained by the enclosing work order.
        }
        /**
         * Processes REOPEN to InProgress while retaining the technician.
         * @param command action to apply
         * @throws UnsupportedOperationException if the action is not REOPEN
         */
    	@Override
    	public void updateState(Command command) {
    	    if (command.getCommand() != CommandValue.REOPEN) {
    	        throw new UnsupportedOperationException("Invalid command.");
    	    }
    	    note = command.getCommandInformation();
    	    currentState = workState;
    	}

        /**
         * Returns the Completed state name.
         * @return the state name
         */
    	@Override
    	public String getStateName() {
    	    return COMPLETED_NAME;
    	}
    }
}