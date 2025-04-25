package example.com;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * This is a straightforward solution to the problem of calculating the change
 * to be given by a cashier.
 */
public class Solution1 {

	private static int[] bills = { 500, 200, 100, 50, 20, 10, 5 };
	private static double[] coins = { 2.0, 1.0, 0.50, 0.20, 0.10, 0.05, 0.02, 0.01 };

	/**
	 * Forms a text string with information about the number of bills and coins to
	 * be given to the client.
	 *
	 * @param banknotesForChange list of bills used for change
	 * @param coinsForChange     list of coins used for change
	 * @return a string listing the quantity of each bill and coin
	 */
	static String outputBuilder(List<Integer> banknotesForChange, List<Double> coinsForChange) {

		StringBuilder builder = new StringBuilder();
		builder.append("The cashier has to return to the customer: \n");
		builder.append("BILLS: \n");
		for (int i : bills) {
			builder.append(i).append(" Euro bills:");
			builder.append(Collections.frequency(banknotesForChange, i)).append("\n");
		}
		builder.append("COINS: \n");
		for (double i : coins) {
			builder.append(i).append(" Euro coins:");
			builder.append(Collections.frequency(coinsForChange, i)).append("\n");
		}

		return builder.toString();

	}

	/**
	 * Calculates the change to be given by the cashier using the available
	 * denominations of bills and coins.
	 *
	 * @param amount          the amount of change to be given out
	 * @param allowSmallCoins whether 0.01 and 0.02 coins are allowed
	 * @return string with information about bills and coins that make up the
	 *         change.
	 */
	private static String cashier(double amount, boolean allowSmallCoins) {
		List<Integer> banknotesForChange = new ArrayList<>();
		List<Double> coinsForChange = new ArrayList<>();

		BigDecimal remaining = BigDecimal.valueOf(amount);
		if (!allowSmallCoins) {
			remaining = remaining.divide(BigDecimal.valueOf(0.05), 0, RoundingMode.HALF_UP)
					.multiply(BigDecimal.valueOf(0.05));
		}
		remaining = remaining.setScale(2, RoundingMode.HALF_UP);

		for (int note : bills) {
			BigDecimal noteValue = BigDecimal.valueOf(note);
			while (remaining.compareTo(noteValue) >= 0) {
				banknotesForChange.add(note);
				remaining = remaining.subtract(noteValue);
			}
		}

		for (double coin : coins) {
			if (!allowSmallCoins && (coin == 0.01 || coin == 0.02)) {
				continue;
			}
			BigDecimal coinValue = BigDecimal.valueOf(coin).setScale(2, RoundingMode.HALF_UP);
			while (remaining.compareTo(coinValue) >= 0) {
				coinsForChange.add(coin);
				remaining = remaining.subtract(coinValue);
				remaining = remaining.setScale(2, RoundingMode.HALF_UP);
			}
		}

		return outputBuilder(banknotesForChange, coinsForChange);
	}

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
	 * Reads a user input from the scanner and ensures it's a valid monetary value
	 * with at most two digits after the decimal separator (dot or comma).
	 *
	 * @param scanner the Scanner object for input
	 * @return the parsed double value
	 */
	private static double readValidatedAmount(Scanner scanner) {
		String input;
		while (true) {
			input = scanner.nextLine().replace(",", ".").trim();

			// Matches number with up to 2 decimal digits ( for example, 12, 12.1, 12.11)
			if (input.matches("^\\d+(\\.\\d{1,2})?$")) {
				try {
					return Double.parseDouble(input);
				} catch (NumberFormatException e) {

				}
			}

			System.out.println(
					"Invalid input. Enter a number with at most two decimal places (for example, 1.5 or 1,50):");
		}
	}

	/**
	 * Main method, asks the user for the amount due and the amount received from
	 * the customer, then outputs the change information.
	 *
	 * @param args command line arguments (not used)
	 */
	public static void main(String[] args) {
		double amountToPay = 0.0;
		double receivedAmount = 0.0;

		Scanner scanner = new Scanner(System.in);
		System.out.println("The customer bought items with a total value of ");
		amountToPay = readValidatedAmount(scanner);

		System.out.println("The customer pays with ");
		receivedAmount = readValidatedAmount(scanner);

		double result = receivedAmount - amountToPay;
		result = Math.floor(result * 100) / 100;

		System.out.println("Can we use 0.01 Euro coins and 0.02 Euro coins? (yes/no)");

		boolean allowSmallCoins = isSmallCoinAllowed(scanner);
		if (allowSmallCoins) {
			System.out.println("The cashier can use 0.01 Euro coins and 0.02 Euro coins.");
		} else {
			System.out.println("The cashier cannot use 0.01 Euro coins and 0.02 Euro coins.");
		}

		System.out.println(cashier(result, allowSmallCoins));
	}
}
