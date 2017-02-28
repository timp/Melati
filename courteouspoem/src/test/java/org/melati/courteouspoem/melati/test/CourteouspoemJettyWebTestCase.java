/*
 * Copyright (C) 2010 Tim Pizey
 *
 *
 * Contact details for copyright holder:
 *
 *     Tim Pizey <timp At paneris.org>
 *     http://paneris.org/~timp
 */

package org.melati.courteouspoem.melati.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.melati.JettyWebTestCase;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author timp
 * @since  3 Mar 2009
 *
 */



public class CourteouspoemJettyWebTestCase extends JettyWebTestCase {

  public CourteouspoemJettyWebTestCase() {
    webAppDirName = "src/main/webapp";
    contextName = "";
  }

  @BeforeClass
  public static void setUp() throws Exception {
    JettyWebTestCase.setUp();
  }

  @AfterClass
  public static void tearDown() throws Exception {
    JettyWebTestCase.tearDown();
  }

  /**
   * If you don't know by now.
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    contextName = "";
    webAppDirName = "src/main/webapp";
    startServer(8080);
  }

  /**
   * Just to say hello.
   */
  @Test
  public void testIndex() {
    beginAt("/");
    assertTextPresent("Admin");
  }

}
