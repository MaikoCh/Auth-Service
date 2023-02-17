package net.absoft;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

public class AuthenticationServiceTest extends BaseTest
{

  private AuthenticationService authenticationService;
  private int code=202;
  @BeforeSuite
  public void setUp()
  {
    authenticationService = new AuthenticationService();
    System.out.println("setup");
  }

  @AfterSuite
  public void tearDown()
  {
    System.out.println("teardown");
  }

  @DataProvider (name="invalidLoginData", parallel=true)
  public Object[][] generateInvalidLoginData()
  {
    return new Object[][]
            {
                    new Object[]
                            {"user1@test.com", "wrong_password1", new Response(401, "Invalid email or password")},
                    new Object[]
                            {"", "password1", new Response(400, "Email should not be empty string")},
                    new Object[]
                            {"user1", "password1", new Response(400, "Invalid email")},
                    new Object[]
                            {"user1@test.com", "", new Response(400, "Password should not be empty string")}
            };
  }


  @Test (groups = "positive", description ="positive test")
  public void testSuccessfulAuthentication() throws InterruptedException
  {
    SoftAssert sa = new SoftAssert();
    Response response = authenticationService.authenticate("user1@test.com", "password1");
    sa.assertEquals(response.getCode(), code, "Response code should be 200");
    sa.assertTrue(validateToken(response.getMessage()),
        "Token should be the 32 digits string. Got: " + response.getMessage());
    code--;
    System.out.println("Test was executed at " + LocalTime.now());
    sa.assertAll();

  }

  @Test
          (
                  groups = "negative",
                  dataProvider = "invalidLoginData"
          )
  public void testAuthenticationWithWrongPassword(String email, String password, Response expectedResponse)
  {
    Response actualResponse = authenticationService.authenticate(email, password);
    assertEquals(actualResponse, expectedResponse,"Unexpected response");
    System.out.println("Test was executed at " + LocalTime.now());
  }

  /*

  @Test (groups = "negative")
  public void testAuthenticationWithEmptyEmail()
  {
    Response response = new AuthenticationService().authenticate("", "password1");
    SoftAssert sa = new SoftAssert();

    sa.assertEquals(response.getCode(), 400, "Response code should be 400");
    sa.assertEquals(response.getMessage(), "Email should not be empty string", "Response message should be \"Email should not be empty string\"");
    sa.assertAll();
  }

  @Test (groups = "negative")
  public void testAuthenticationWithInvalidEmail()
  {
    Response response = new AuthenticationService().authenticate("user1", "password1");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Invalid email",
        "Response message should be \"Invalid email\"");
  }

  @Test (groups = "negative")
  public void testAuthenticationWithEmptyPassword()
  {
    Response response = new AuthenticationService().authenticate("user1@test", "");
    assertEquals(response.getCode(), 400, "Response code should be 400");
    assertEquals(response.getMessage(), "Password should not be empty string",
        "Response message should be \"Password should not be empty string\"");
  }

   */
  private boolean validateToken(String token)
  {
    final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(token);
    return matcher.matches();
  }
}
