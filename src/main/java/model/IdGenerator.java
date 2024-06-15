package model;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

	private static AtomicLong  id = new AtomicLong(1000000000L);

	public static long getNextId() {
		return id.getAndIncrement();
	}

}
