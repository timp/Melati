package org.melati.admin.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.melati.JettyWebTestCase;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;


/**
 * @author timp
 * @since 2008/01/10
 */
public class DisplayJettyWebTest extends JettyWebTestCase {

  @BeforeClass
  public static void setUp() throws Exception {
    JettyWebTestCase.setUp();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    JettyWebTestCase.tearDown();
  }

  /**
   * Test Display using default template.
   */
  @Test
  public void testDisplay() {
    beginAt("/Display/melatijunit/User/0/");
    assertTextPresent("Melati guest user");
  }

  /**
   * Test Display using default template.
   */
  @Test
  public void testDisplayMethod() {
    beginAt("/Display/melatijunit/User/0/org/melati/admin/Display");
    assertTextPresent("Melati guest user");
  }

  /**
   * Test Display using default template.
   */
  @Test
  public void testDisplayMethodDots() {
    beginAt("/Display/melatijunit/User/0/org.melati.admin.Display");
    assertTextPresent("Melati guest user");
  }

  /**
   * Test Display using default template.
   */
  @Test
  public void testDisplayParameter() {
    beginAt("/Display/melatijunit/User/0/?template=org/melati/admin/Display");
    assertTextPresent("Melati guest user");
  }

  /**
   * Test Display using default template.
   */
  @Test
  public void testDisplayParameterDots() {
    beginAt("/Display/melatijunit/User/0/?template=org.melati.admin.Display");
    assertTextPresent("Melati guest user");
  }

  /**
   * Test Display using default template.
   */
  @Test
  public void testDisplayParameterDotsNoObject() {
    beginAt("/Display/melatijunit/User/?template=org.melati.admin.Display");
    assertTextPresent("null");
  }

  @Test
  public void testProxy() {
    //Not allowed as we have yet to add token to session
    String dbName = "melatijunit";
    // Allowed
    beginAt("/Display/" + dbName + "?template=org.melati.admin.test.ProxyCaller");
    clickLinkWithText("google");

    // Allowed
    beginAt("/Display/" + dbName + "?template=org.melati.admin.test.ProxyCaller");
    clickLinkWithText("check");

  }
}