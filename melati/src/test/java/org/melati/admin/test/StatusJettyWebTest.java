package org.melati.admin.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.melati.JettyWebTestCase;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.beginAt;
import static net.sourceforge.jwebunit.junit.JWebUnit.setScriptingEnabled;

/**
 * @author timp
 * @since 2008/01/10
 */
public class StatusJettyWebTest extends JettyWebTestCase {

  @BeforeClass
  public static void setUp() throws Exception {
    JettyWebTestCase.setUp();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    JettyWebTestCase.tearDown();
  }

  /**
   * Test Display.
   */
  @Test
  public void testStatus() {
    setScriptingEnabled(false);
    beginAt("/Status/melatijunit");
    assertTextPresent("Database Cache Status");
  }
}
