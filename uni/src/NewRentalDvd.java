import java.io.PrintWriter;

public class NewRentalDvd extends RentalDVD {
	private String reserverId;
	private final String reserveDefault = "No reserves";

	public NewRentalDvd(String movieID, String title) {
		super(movieID, title, 5);
		this.reserverId = reserveDefault;
	}
	public NewRentalDvd(String movieID, String title, boolean onLoan, String borrowerId, String reserverId){
		super(movieID, title, 5, onLoan, borrowerId);
		this.reserverId = reserverId;
	}
	//Sets a reserve on dvd if its not available
	public void reserveDvd(String memberId) throws BorrowException{
		if(!isOnloan()){
			throw new BorrowException("Dvd is available!");
		}else if(!reserverId.matches(reserveDefault)){
			throw new BorrowException("Dvd has already been reserved by someone else");
		}
		reserverId = memberId;
	}
	
	@Override
	public void borrowDVD(String memberId) throws BorrowException{
		if(!reserverId.matches(reserveDefault) && !memberId.matches(reserverId)){
			throw new BorrowException("Dvd has already been reserved by someone else");
		}
		borrowDVD(memberId);
		reserverId = reserveDefault;
	}
	
	@Override
	public double returnDVD(int daysBorrowed){
		// reject return of DVD if it is not currently on loan
		if (!isOnloan())
		{
			return Double.NaN;
		}
		else
		{
			// DVD has been borrowed so reset loan status and borrower ID
			setOnLoan(false);;
			setBorrowerID("not on loan");
			// determine whether the DVD was returned late
			int daysLate = daysBorrowed - 2;

			if (daysLate > 0)
			{
				// DVD was returned late, so return fine
				return daysLate * 5.0;
			}
			else
			{
				// DVD was returned on time - no fine applies
				return 0.0;
			}
		}
	}
	@Override
	public void printDetails(){
		super.printDetails();
		System.out.printf("%15s%s\n", "Reserver ID:", reserverId);
	}
	
	@Override
	//writes dvd data to file
	public void saveRecord(PrintWriter pw){
		   pw.println("new");
		   pw.println(reserverId);
		   super.saveRecord(pw);
	   }
	
}
