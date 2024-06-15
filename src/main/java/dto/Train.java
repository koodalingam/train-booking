package dto;

public class Train {

	private long trainId;
	private int noOfSections;
	private int noOfSeatsPerSection;

	public Train(long trainId, int noOfSections,  int noOfSeatsPerSection  ) {
		this.trainId = trainId;
		this.noOfSections = noOfSections;
		this.noOfSeatsPerSection = noOfSeatsPerSection;
	}

	public long getTrainId() {
		return trainId;
	}
	public void setTrainId(long trainId) {
		this.trainId = trainId;
	}

	public int getNoOfSections() {
		return noOfSections;
	}

	public void setNoOfSection(int noOfSections) {
		this.noOfSections = noOfSections;
	}

	public int getNoOfSeatsPerSection() {
		return noOfSeatsPerSection;
	}

	public void setNoOfSeatsPerSection(int noOfSeatsPerSection) {
		this.noOfSeatsPerSection = noOfSeatsPerSection;
	}

}
