package dto;

public class User {
	
	private long userId;
	private String firstName;
	private String lastName;
	private String email;
	
	public User(long userId, String firstName, String lastName, String email  ) {
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;	
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getDisplayName() {
		String name = "";
		if(this.firstName!=null) {
			name = name+ this.firstName;
		}
		if(this.lastName!=null) {
			name = name+ " " + this.lastName;
		}
		return name;
	}
}
