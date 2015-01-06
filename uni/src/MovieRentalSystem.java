/*
 * class MovieRentalSystem
 * 
 * Implements the data storage and functional requirements
 * for a menu-driven program that manages property sales.
 * 
 * This is the start-up code for Assignment 3 and you should
 * work off this program - the features described in the
 * specification for Stages 2 and 4 should be implemented in 
 * the corresponding helper methods included at the bottom of 
 * this class.
 * 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class MovieRentalSystem
{

	/* 
	 * You can refer to the array and Scanner object here anywhere
	 * within this class, even within helper methods that you are
	 * required to implement the code for each of the features within
	 */

	private static final RentalDVD [] movies = new RentalDVD [100];
	private static int movieCount = 0;

	private static final Scanner sc = new Scanner(System.in);

	public static void main(String[] args)
	{
		String userInput;

		// initialise selection variable to ascii nul to keep compiler happy
		char selection = '\0';  

		//checks if there are records and loads them
		loadRecords();

		// implementation of the program menu
		do
		{
			// print menu to screen
			System.out.println("*** Movie Rental System Menu ***");
			System.out.println();

			System.out.printf("%-25s%s\n", "Add RentalDVD", "A");
			System.out.printf("%-25s%s\n", "Display DVD List", "B");
			System.out.printf("%-25s%s\n", "Borrow DVD", "C");
			System.out.printf("%-25s%s\n", "Return DVD", "D");
			System.out.printf("%-25s%s\n", "Add New Release DVD", "E");
			System.out.printf("%-25s%s\n", "Reserve DVD", "F");
			System.out.printf("%-25s%s\n", "Exit Program", "X");
			System.out.println();

			// prompt user to enter selection
			System.out.print("Enter selection: ");
			userInput = sc.nextLine();

			System.out.println();

			// validate selection input length
			if (userInput.length() != 1)
			{
				System.out.println("Error - invalid selection!");
			}
			else
			{
				// make selection "case insensitive"
				selection = Character.toUpperCase(userInput.charAt(0));

				// process user's selection
				switch (selection)
				{
				case 'A':
					addRentalDVD();
					break;

				case 'B':
					displayDVDList();
					break;

				case 'C':
					borrowDVD();
					break;

				case 'D':
					returnDVD();
					break;

				case 'E':
					addNewReleaseDVD();
					break;

				case 'F':
					reserveDVD();
					break;

				case 'X':
					saveRecords();
					System.out.println("Exiting the program...");
					break;

				default:
					System.out.println("Error - invalid selection!");
				}
			}
			System.out.println();

		} while (selection != 'X');

	}
	//reads dvd data from file, creates dvds and populates the movie array
	private static void loadRecords(){
		try {
			BufferedReader bufr = new BufferedReader(new FileReader("record"));
			String next = bufr.readLine();
			//file is empty
			if(next == null){
				System.out.println("No records found!");
				bufr.close();
				return;
			}
			
			while(next != null){
				System.out.println("loading...");
				if(next.matches("new")){
					//dvd is a new release
					
					String reserverId = bufr.readLine();
					String[] recordData = bufr.readLine().split(":");
					movies[movieCount] = new NewRentalDvd(recordData[0], recordData[1],
							Boolean.valueOf(recordData[3]), recordData[4], reserverId);
					movieCount++;
				}else{
					//ordinary dvd
					
					String[] recordData = next.split(":");
					movies[movieCount] = new RentalDVD(recordData[0], recordData[1],
							Integer.valueOf(recordData[2]), Boolean.valueOf(recordData[3]), recordData[4]);
					movieCount++;
				}
				next = bufr.readLine();
			}
			bufr.close();
			System.out.println("Records found and loaded!");
		} catch (IOException e) {
			System.out.println("No records found!");
		}
	}
	//loops through movie array and saves dvd data to file
	private static void saveRecords(){
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("record")));
			for(RentalDVD dvd: movies){
				if(dvd == null) continue;
				
				if(dvd instanceof NewRentalDvd){
					((NewRentalDvd)dvd).saveRecord(pw);
				}else{
					dvd.saveRecord(pw);
				}
			}
			
			System.out.println("Dvd records have been saved!");
			pw.close();
		} catch (IOException e) {
			System.out.println("Could not write to file.");
			System.out.println("Saving records failed!");
		}
	}

	// helper method which implements the functionality for
	// adding a new RentalDVD

	private static void addRentalDVD()
	{
		System.out.println("Add RentalDVD option selected");
		System.out.println();
		System.out.println("Enter the movie ID: ");
		String movieID = sc.nextLine();
		System.out.println("Enter the movie title: ");
		String title = sc.nextLine();
		System.out.println("Enter the rental fee: ");
		int fee = sc.nextInt();

		movies[movieCount] = new RentalDVD(movieID, title, fee);
		movieCount++;
	}

	private static void displayDVDList()
	{
		// implement your code for Stage 2 Requirement B) here

		System.out.println("Display DVD List option selected");
		System.out.println();
		for(RentalDVD dvd: movies){
			if(dvd == null) continue;
			
			dvd.printDetails();
			System.out.println();
		}
	}

	private static RentalDVD findDvd(){
		System.out.println("Enter the required movie ID: ");
		String movieID = sc.nextLine();

		for(RentalDVD dvd: movies){
			if(dvd == null) continue;
			if(dvd.getMovieID().matches(movieID)){
				return dvd;
			}
		}
		return null;
	}

	private static void borrowDVD()
	{
		// implement your code for Stage 2 Requirement C) here

		System.out.println("Borrow DVD option selected");
		System.out.println();
		RentalDVD dvdOut = findDvd();

		if(dvdOut == null){
			System.out.println("Sorry but the movie ID entered could not be found!");
			System.out.println();
			return;
		}

		System.out.println("Enter the borrowers member ID: ");
		String borrowerID = sc.nextLine();

		try {
			dvdOut.borrowDVD(borrowerID);
			System.out.println("Dvd rental successful!");
			System.out.println();
		} catch (BorrowException e) {
			System.out.println(e.getMessage());
		}
	}


	private static void returnDVD()
	{
		// implement your code for Stage 2 Requirement D) here

		System.out.println("Return DVD option selected");
		System.out.println();
		RentalDVD dvdOut = findDvd();

		if(dvdOut == null){
			System.out.println("Sorry but the movie ID entered could not be found!");
			System.out.println();
			return;
		}
		System.out.println("Enter number of days on loan: ");
		int daysOnLoan = sc.nextInt();
		sc.nextLine();
		double fine = dvdOut.returnDVD(daysOnLoan);
		if(Double.isNaN(fine)){
			System.out.println("Dvd return failed! Dvd is not on loan.");
			System.out.println();
		}else{
			System.out.println("Dvd return successful! Late fees owing: $"+fine);
			System.out.println();
		}

	}

	private static void addNewReleaseDVD()
	{
		// implement your code for Stage 4 Requirement A) here

		System.out.println("Add RentalDVD option selected");
		System.out.println();
		System.out.println("Enter the movie ID: ");
		String movieID = sc.nextLine();
		System.out.println("Enter the movie title: ");
		String title = sc.nextLine();

		NewRentalDvd newDvd = new NewRentalDvd(movieID, title);
		movies[movieCount] = newDvd;
		movieCount++;
	}
	private static void reserveDVD()
	{
		// implement your code for Stage 4 Requirement B) here

		System.out.println("Close Auction option selected");
		System.out.println();
		RentalDVD dvdOut = findDvd();
		if(dvdOut == null || !(dvdOut instanceof NewRentalDvd)){
			System.out.println("Movie ID could not be found or is not a new release!");
			System.out.println();
			return;
		}
		System.out.println("Enter the member ID for reservation: ");
		String memberID = sc.nextLine();
		try {
			((NewRentalDvd)dvdOut).reserveDvd(memberID);
		} catch (BorrowException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("Dvd reservation successful!");
		System.out.println();

	}

}
