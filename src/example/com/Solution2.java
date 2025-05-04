package example.com;

import java.util.Scanner;

/**
 * This solution uses integer arithmetic to avoid floating point errors.
 * We use cents, namely integers, for all calculations.
 */
public class Solution2 {

	private static final int[] DENOMINATIONS_IN_CENTS = { 500 * 100, 200 * 100, 100 * 100, 50 * 100, 20 * 100, 10 * 100,
			5 * 100, 2 * 100, 1 * 100, 50, 20, 10, 5, 2, 1 };

	/**
	 * Parses an input string representing an amount of money and converts it to
	 * total cents
	 *
	 * @param inputString - amount of money
	 * @return - total value in cents (then - "16.09" becomes 1609)
	 * @throws - IllegalArgumentException input is invalid
	 */
	private static int parseToCents(String inputString) {
		inputString = inputString.trim().replace(",", ".");
		double amount = Double.parseDouble(inputString);
		return (int) Math.round(amount);
	}

	/**
	 * Calculates the change to be returned to the customer. To avoid floating point
	 * errors we use cents (integers) for all calculations.
	 *
	 * @param inputString     - the amount of change as a string
	 * @param allowSmallCoins - whether 0.01 and 0.02 euro coins are allowed
	 * @return - formatted string describing the change to be returned
	 */
	private static String cashier(String inputString, boolean allowSmallCoins) {
		int totalCents;
		try {
			totalCents = parseToCents(inputString);
		} catch (IllegalArgumentException e) {
			return "Invalid input: " + e.getMessage();
		}

		// Round to nearest 5 cents if 1 and 2 cent coins are not allowed
		if (!allowSmallCoins) {
			totalCents = (int) Math.round(totalCents / 5.0) * 5;
		}

		int[] counts = new int[DENOMINATIONS_IN_CENTS.length];

		for (int i = 0; i < DENOMINATIONS_IN_CENTS.length; i++) {
			int denom = DENOMINATIONS_IN_CENTS[i];

			// Skip 1 and 2 cent coins if not allowed
			if (!allowSmallCoins && (denom == 1 || denom == 2)) {
				continue;
			}

			counts[i] = totalCents / denom;
			totalCents %= denom;
		}

		return outputBuilder(counts);
	}

	/**
	 * Builds an output as the count of each denomination that should be returned as
	 * change to the customer
	 *
	 * @param counts array of counts for each denomination in DENOMINATIONS_IN_CENTS
	 * @return formatted string describing the bills and coins
	 */
	private static String outputBuilder(int[] counts) {
		StringBuilder builder = new StringBuilder();
		builder.append("The cashier has to return to the customer:\n");

		builder.append("BILLS:\n");
		for (int i = 0; i < 7; i++) {
			int denom = DENOMINATIONS_IN_CENTS[i];
			builder.append(denom / 100).append(" Euro bills: ").append(counts[i]).append("\n");
		}

		builder.append("COINS:\n");
		for (int i = 7; i < DENOMINATIONS_IN_CENTS.length; i++) {
			int denom = DENOMINATIONS_IN_CENTS[i];
			if (denom >= 100) {
				builder.append(denom / 100).append(" Euro coins: ").append(counts[i]).append("\n");
			} else {
				builder.append(String.format("0,%02d Euro coins: %d\n", denom, counts[i]));
			}
		}

		return builder.toString();
	}

	/**
	 * Reads a user input from the scanner and ensures it's a valid
	 *
	 * @param scanner the Scanner object for input
	 * @return the parsed double value
	 */
	private static int readValidatedAmount(Scanner scanner) {
		while (true) {
			String input = scanner.nextLine().replace(",", ".").trim();

			if (input.matches("^\\d+(\\.\\d{1,2})?$")) {
				try {
					double amount = Double.parseDouble(input);
					return (int) Math.floor(amount * 100);
				} catch (NumberFormatException ignored) {
				}
			}

			System.out.println(
					"Invalid input. Enter a number with at most two decimal places (for example, 16, 16.09 or 16,09):");
		}
	}

	/**
	 * Asks the user if small coins (0.01 and 0.02 Euro) are allowed and returns the
	 * answer as a boolean.
	 *
	 * @param scanner the Scanner object for input
	 * @return true if small coins are allowed, false otherwise
	 */
	private static boolean isSmallCoinAllowed(Scanner scanner) {
		boolean allowSmallCoins = false;
		while (true) {
			String input = scanner.nextLine().trim().toLowerCase();
			if (input.equals("yes")) {
				allowSmallCoins = true;
				break;
			} else if (input.equals("no")) {
				allowSmallCoins = false;
				break;
			} else {
				System.out.println("Please answer with 'yes' or 'no':");
			}
		}
		return allowSmallCoins;
	}

	/**
	 * Main method, asks the user for the amount due and the amount received from
	 * the customer, then outputs the change information.
	 *
	 * @param args command line arguments (not used)
	 */
	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);
		System.out.println("The customer bought items with a total value of ");
		int amountToPay = readValidatedAmount(scanner);

		int receivedAmount = 0;
		while (true) {
			System.out.println("The customer pays with ");
			receivedAmount = readValidatedAmount(scanner);

			if (receivedAmount < amountToPay) {
				System.out.println("Error: received amount is less than the amount to pay. Please try again.");
			} else {
				break;
			}
		}
		
		int change = receivedAmount - amountToPay;

		System.out.println("Can we use 0.01 Euro coins and 0.02 Euro coins? (yes/no)");

		boolean allowSmallCoins = isSmallCoinAllowed(scanner);
		if (allowSmallCoins) {
			System.out.println("The cashier can use 0.01 Euro coins and 0.02 Euro coins.");
		} else {
			System.out.println("The cashier cannot use 0.01 Euro coins and 0.02 Euro coins.");
		}

		long startTime = System.nanoTime();
		System.out.println(cashier(String.valueOf(change), allowSmallCoins));
		long endTime = System.nanoTime();
		long duration = endTime - startTime; // duration in nanoseconds
		System.out.println("Execution time: " + duration + " nanoseconds");
	}
}
