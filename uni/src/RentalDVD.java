import java.io.PrintWriter;

/*
 * class RentalDVD
 * 
 * Encapsulates the attribute detail and functionality required to
 * record and manage "normal" DVD's in the movie rental system
 * as described in Stage 1 of the specification.
 * 
 * You should leave this class as-is while implementing the requirements
 * in stages 2-4, but you can/will need to amend this class for stage 5
 * (exception handling) and the bonus marks stage (file handling) to
 * facilitate the addressing of the requirements set out in those stages.
 *  
 */

public class RentalDVD
{
   private String movieID;
   private String title;
   private int rentalFee;
   private boolean onLoan;
   private String borrowerID;
   
   public RentalDVD(String movieID, String title, int rentalFee)
   {
      this.movieID = movieID;
      this.title = title;
      this.rentalFee = rentalFee;
      
      // initialise loan status to false to reflect the DVD being "available" initially
      this.onLoan = false;
      
      // initialise borrower ID to default value
      this.borrowerID = "not on loan";
   }
   
   public RentalDVD(String movieID, String title, int rentalFee, boolean onLoan, String borrowerId){
	   this.movieID = movieID;
	   this.title = title;
	   this.rentalFee = rentalFee;
	   
	   this.onLoan = onLoan;
	   
	   this.borrowerID = borrowerId;
   }

   /*
    * Accessors (getters)
    * 
    * Use these as needed in latter stages of the assignment.
    */
   
   public String getMovieID()
   {
	   return movieID;
   }

   public boolean isOnloan()
   {
	   return onLoan;
   }
   //writes dvd data to file
   public void saveRecord(PrintWriter pw){
	   pw.println(movieID + ":" + title + ":" + rentalFee + ":" + onLoan + ":" + borrowerID);
   }

   /*
    * Mutators (setters)
    * 
    * These are all that is required for stages 2-5 of the assignment, 
    * but you may define additional mutators for the bonus marks stage 
    * (file handling) as needed.
    */
   
   public void setBorrowerID(String memberID)
   {
      this.borrowerID = memberID;
   }

   public void setOnLoan(boolean onLoan)
   {
      this.onLoan = onLoan;
   }
   
   
   /*
    * borrowDVD()
    * 
    * Operation which accepts a the member ID of the borrower as a parameter 
    * and updates the loan status and borrower ID if the DVD is available.
    * 
    * Returns false (indicating that borrowing of the DVD failed) if the
    * DVD is already on loan, otherwise returns true (to indicate that the 
    * DVD has been borrowed successfully).
    */
   public void borrowDVD(String memberID) throws BorrowException
   {
      // reject borrowing of DVD if it is on loan already
      if (onLoan == true)
      {
         throw new BorrowException("Dvd already on Loan");
      }
      else
      {
         // DVD has been borrowed so update loan status and borrower ID
         this.onLoan = true;
         this.borrowerID = memberID;
      }
   }
   
   /*
    * returnDVD()
    * 
    * Operation which accepts the number of days the DVD was on loan
    * as a parameter, resets the loan status and borrower ID to their
    * default values if the DVD was on loan DVD and calculates / returns
    * any fine that may apply for a late returning of the DVD.
    * 
    * Returns DoubleNaN (indicating that returning of the DVD failed) if the
    * DVD is not currently on loan, otherwise returns either 0.0 (indicating
    * that the DVD was returned on time) or a positive double value which
    * represents the fine that applies for returning the DVD late.
    */
   public double returnDVD(int daysBorrowed)
   {
      // reject return of DVD if it is not currently on loan
      if (onLoan == false)
      {
         return Double.NaN;
      }
      else
      {
         // DVD has been borrowed so reset loan status and borrower ID
         this.onLoan = false;
         this.borrowerID = "not on loan";
         
         // determine whether the DVD was returned late
         int daysLate = daysBorrowed - 7;
         
         if (daysLate > 0)
         {
            // DVD was returned late, so return fine
            return daysLate * 2.0;
         }
         else
         {
            // DVD was returned on time - no fine applies
            return 0.0;
         }
      }
   }
   
   /*
    * printDetails()
    * 
    * Prints the instance variable details for this Sale
    * to the screen, as well as the "outcome" of the sale.
    */
   public void printDetails()
   {
      System.out.printf("%15s%s\n", "Movie ID:", movieID);
      System.out.printf("%15s%s\n", "Title:", title);
      System.out.printf("%15s$%d\n", "Rental Fee:", rentalFee);
      System.out.printf("%15s%b\n", "On Loan:", onLoan);
      System.out.printf("%15s%s\n", "Borrower ID:", borrowerID);
   }
   
}
