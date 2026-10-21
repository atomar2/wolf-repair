# WolfRepair
### Repair Work Order Management System

WolfRepair is a Java desktop application for managing repair requests across multiple repair sites. It tracks each work order from submission through review, technician dispatch, and completion, with support for rejection, reassignment, and reopening.

Developed for **CSC 216: Software Development Fundamentals at North Carolina State University**, the project demonstrates object-oriented design, finite-state machines, file processing, and unit testing.

## Features

- **Repair site management:** Create and switch between sites with a name, category, and repair budget.
- **Work order tracking:** Create, view, and delete requests containing a requester, problem description, and unique identifier within a site.
- **Six-state workflow:** Enforce valid actions as requests move through review, assignment, repair, and resolution.
- **Technician dispatch:** Record technician assignments, reassign work, and handle technician unavailability.
- **Status updates:** Record repair notes, rejection reasons, and completion details.
- **File persistence:** Load repair sites and work orders from text files and save them for later use.
- **Desktop interface:** Interact with the application through a Java Swing GUI.

## Technologies

| Technology | Purpose |
| --- | --- |
| Java 21 | Application and business logic |
| Java Swing | Desktop user interface |
| JUnit 5 | Unit testing |
| Eclipse | Project configuration and development environment |
| Javadoc | API documentation |

## Design

The application separates the user interface from the model and file-processing logic.

- **State pattern:** `WorkOrder` delegates behavior to six state implementations. Each state accepts only the commands valid for that stage of the repair process.
- **Singleton pattern:** `WolfRepair` provides a shared manager for repair sites and the currently active site.
- **Command representation:** `Command` packages an action and its associated information for work-order updates.
- **File I/O:** `RepairSiteReader` and `RepairSiteWriter` handle loading and saving site data.

### Work Order States

| State | Role |
| --- | --- |
| Submitted | Newly created request awaiting submission for review |
| UnderReview | Request awaiting approval or denial |
| Assigned | Approved request awaiting technician dispatch |
| InProgress | Repair being handled by a technician |
| Rejected | Request denied, cancelled, or waiting for an available technician |
| Completed | Finished repair with a completion note |

The workflow also supports reassignment and reopening. A completed order can return to `InProgress`; a rejected order can be reconsidered or dispatched depending on its rejection reason.

## Getting Started

### Requirements

- JDK 21, matching the included Eclipse configuration
- Eclipse with Java support for the IDE instructions below
- JUnit 5 to run the tests

### Clone the Repository

```bash
git clone https://github.com/atomar2/wolf-repair.git
cd wolf-repair
```

### Run in Eclipse

1. Import the `Project1` folder as an existing Eclipse project.
2. Ensure the project uses JDK 21. The included classpath also references the JUnit 5 library.
3. Open `WolfRepairGUI.java` in `edu.ncsu.csc216.wolf_repair.view.ui`.
4. Run the class as a **Java Application**.

### Run from Terminal

From the repository root, compile the application and launch the GUI:

```bash
cd Project1
javac -d out -sourcepath src src/edu/ncsu/csc216/wolf_repair/view/ui/WolfRepairGUI.java
java -cp out edu.ncsu.csc216.wolf_repair.view.ui.WolfRepairGUI
```

These commands compile the application only; they do not run the JUnit tests. A graphical desktop environment is required to display the Swing interface.

### Try the Application

1. Use **File > Add Repair Site** to create a site.
2. Add a work order with a requester and problem description.
3. Submit the request for review, approve it, and dispatch a technician.
4. Record status updates and complete the repair with a note.
5. Use **File > Save Repair Sites** to save your work, then **File > Load Repair Sites File** to reload it.

Saving omits sites that have no work orders.

## Testing

JUnit 5 tests are included for commands, work orders, repair sites, the application manager, and file reading and writing. They exercise state transitions, input validation, duplicate identifiers, and file persistence.

To run them in Eclipse, select the `test` source folder and choose **Run As > JUnit Test**.

- **Test fixtures:** `Project1/test-files/`
- **System test plan:** `Project1/project_docs/STP_P1P2.pdf`

## Repository Layout

| Path | Contents |
| --- | --- |
| `Project1/src/edu/ncsu/csc216/wolf_repair/model/` | Commands, file I/O, site management, and work-order logic |
| `Project1/src/edu/ncsu/csc216/wolf_repair/view/ui/` | Swing user interface |
| `Project1/test/` | JUnit tests |
| `Project1/test-files/` | Test input and output files |
| `Project1/project_docs/` | System test plan |
| `Project1/doc/` | Generated Javadoc documentation |

## Author and Acknowledgments

**Aanya Tomar** — model implementation, file-processing logic, and unit tests.

This project was completed as NC State CSC 216 coursework. The course-provided Swing interface, `WolfRepairGUI.java`, credits **Sarah Heckman** and **Chandrika Satyavolu**. Original source attributions are retained.
