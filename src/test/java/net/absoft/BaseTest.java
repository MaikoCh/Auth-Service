package net.absoft;

import net.absoft.services.AuthenticationService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;

public class BaseTest
{
    @AfterSuite
    public void BaseTeardown()
    {
        System.out.println("base teardown");
    }
}
