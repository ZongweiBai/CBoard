package org.cboard.services;

import org.junit.jupiter.api.Test;

import static org.springframework.test.util.AssertionErrors.assertTrue;

/**
 * Created by zyong on 2016/9/26.
 */
public class ServiceStatusTest {

    @Test
    public void testServiceStatus() {
        ServiceStatus success = new ServiceStatus(ServiceStatus.Status.Success, "Success");
        assertTrue("Status", success.getStatus().equals("1"));
    }

}
