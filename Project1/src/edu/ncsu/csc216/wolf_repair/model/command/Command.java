package edu.ncsu.csc216.wolf_repair.model.command;

/**
 * Represents a user action and its associated information for updating
 * a work order through its finite-state machine.
 * @author Aanya Tomar
 */
public class Command {

	/**
	 * Actions that can be applied to a work order.
	 */
	public enum CommandValue { SUBMIT, APPROVE, DENY, DISPATCH, STATUS, REASSIGN, CANCEL, COMPLETE, REOPEN }
	
	/** Action represented by this command. */
	private CommandValue command;

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
	    // TODO Implement validation and initialize the fields.
	}

	/**
	 * Returns the action represented by this command.
	 * @return the command action
	 */
	public CommandValue getCommand() {
	    return null;
	}

	/**
	 * Returns the information associated with this command.
	 * @return the command information
	 */
	public String getCommandInformation() {
	    return null;
	}
}
