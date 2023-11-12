package bots;

/**
 * This interface contains the methods that are being used by all the bots.
 * The interface is used to generalise the methods for all the bots and make sure that every bot works with the same logic.
 * We use the interface to make a switch case in the Launch class that executes the code for which bot is selected in the menu.
 */
public interface Bot {
    double[] calculateStartVelocities();
    int getTries();
}
