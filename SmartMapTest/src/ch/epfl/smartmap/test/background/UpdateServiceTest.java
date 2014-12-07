package ch.epfl.smartmap.test.background;

import org.junit.Test;

import android.content.Intent;
import android.test.ServiceTestCase;
import ch.epfl.smartmap.background.InvitationsService;

public class UpdateServiceTest extends ServiceTestCase<InvitationsService> {
	private Intent testIntent;

	public UpdateServiceTest(Class<InvitationsService> serviceClass) {
		super(serviceClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		testIntent = new Intent(this.getContext(), InvitationsService.class);
	}

	@Test
	public void testOnStartIntentInt() {
		testIntent = new Intent(this.getContext(), InvitationsService.class);
		this.getContext().startService(testIntent);
	}
}