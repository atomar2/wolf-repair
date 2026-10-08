package edu.ncsu.csc216.wolf_repair.model.command;
import edu.ncsu.csc216.wolf_repair.model.workorder.WorkOrder;

/**
 * Represents a user action and its associated information for updating
 * a work order through its finite-state machine.
 * @author Aanya Tomar
 */
public class Command {

    /**
     * Actions that can be applied to a work order.
     */
    public enum CommandValue {
        /** Submits a request for review. */
        SUBMIT,
        /** Approves a request for technician assignment. */
        APPROVE,
        /** Denies a request with a rejection note. */
        DENY,
        /** Dispatches a technician or records a rejection reason. */
        DISPATCH,
        /** Records a status update on work in progress. */
        STATUS,
        /** Returns a job to Assigned for technician reassignment. */
        REASSIGN,
        /** Cancels a job with a permitted rejection reason. */
        CANCEL,
        /** Marks a job completed with a completion note. */
        COMPLETE,
        /** Reopens a rejected or completed work order. */
        REOPEN
    }
	
    /** Action represented by this command. */
    private CommandValue commandValue;
    
	/** Information associated with the action. */
	private String commandInformation;
	
	/**
	 * Constructs a command with an action and associated information.
	 * @param command action to perform
	 * @param commandInformation information associated with the action
	 * @throws IllegalArgumentException if command is null, STATUS information
	 *         is null or empty, or CANCEL information is not a permitted reason
	 */
	public Command(CommandValue command, String commandInformation) {
        if (command == null) {
            throw new IllegalArgumentException("Invalid information.");
        }

        if (command == CommandValue.STATUS
                && (commandInformation == null || commandInformation.isEmpty())) {
            throw new IllegalArgumentException("Invalid information.");
        }

        if (command == CommandValue.CANCEL
                && !WorkOrder.WAITING.equals(commandInformation)
                && !WorkOrder.CANCELLED.equals(commandInformation)) {
            throw new IllegalArgumentException("Invalid information.");
        }

        this.commandValue = command;
        this.commandInformation = commandInformation;
	}

	/**
	 * Returns the action represented by this command.
	 * @return the command action
	 */
    public CommandValue getCommand() {
        return commandValue;
    }

	/**
	 * Returns the information associated with this command.
	 * @return the command information
	 */
    public String getCommandInformation() {
        return commandInformation;
    }
}
