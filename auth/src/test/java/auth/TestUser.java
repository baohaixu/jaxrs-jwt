package auth;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.baohaixu.model.User;

public class TestUser {
	
	@Test
	public void createUser() {
		User user = new User();
		user.setPassword("yajing");
		boolean stimmt = user.verifyPassword("yajing");

		assertTrue(stimmt);		
	}
}
