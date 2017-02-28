/*
 * Copyright (C) 2008 Tim Pizey
 *
 * Part of Melati (http://melati.org), a framework for the rapid
 * development of clean, maintainable web applications.
 *
 * Melati is free software; Permission is granted to copy, distribute
 * and/or modify this software under the terms either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation; either version 2 of the License, or (at your option)
 *    any later version,
 *
 *    or
 *
 * b) any version of the Melati Software License, as published
 *    at http://melati.org
 *
 * You should have received a copy of the GNU General Public License and
 * the Melati Software License along with this program;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA to obtain the
 * GNU General Public License and visit http://melati.org to obtain the
 * Melati Software License.
 *
 * Feel free to contact the Developers of Melati (http://melati.org),
 * if you would like to work out a different arrangement than the options
 * outlined here.  It is our intention to allow Melati to be used by as
 * wide an audience as possible.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Contact details for copyright holder:
 *
 *     Tim Pizey <timp At paneris.org>
 *     http://paneris.org/~timp
 */
package org.melati.test.test;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.melati.JettyWebTestCase;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import static net.sourceforge.jwebunit.junit.JWebUnit.assertTextPresent;
import static net.sourceforge.jwebunit.junit.JWebUnit.clickLinkWithText;
import static net.sourceforge.jwebunit.junit.JWebUnit.setScriptingEnabled;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static net.sourceforge.jwebunit.junit.JWebUnit.*;

/**
 * @author timp
 * @since 7 Mar 2008
 */
public class TemplateServletTestTest extends JettyWebTestCase {

  protected static String servletName;

  @BeforeClass
  public static void setUp() throws Exception {
    JettyWebTestCase.setUp();
    servletName = "/org.melati.test.TemplateServletTest/admintest/";
  }

  @AfterClass
  public static void tearDown() throws Exception {
    JettyWebTestCase.tearDown();
  }

  /**
   * Click Exception link.
   */
  @Test
  public void testException() {
    setScriptingEnabled(false);
    beginAt(servletName);
    try { 
      clickLinkWithText("Exception");
    } catch (FailingHttpStatusCodeException e) { 
      assertEquals(500, e.getStatusCode());
    }
    assertTextPresent("MelatiBugMelatiException");
  }
  /**
   * Click passback link, see exception message.
   * FIXME Webmacro only test
   */
  @Test
  public void testPassbackException() {
    setScriptingEnabled(false);
    beginAt(servletName);
    clickLinkWithText("?passback=true");
    assertTextPresent("[Access denied to Melati guest user]");
  }
  /**
   * Click propagate link, get login screen.
   */
  @Test
  public void testPropagateException() {
    setScriptingEnabled(false);
    beginAt(servletName);
    clickLinkWithText("?propagate=true");
    assertTextPresent("You need to log in");
    assertTextPresent("You need the capability _administer_ ");
    setScriptingEnabled(false);
    beginAt("/org.melati.login.Login/admintest");
    setTextField("field_login", "_administrator_");
    setTextField("field_password", "FIXME");
    checkCheckbox("rememberme");
    submit();
    gotoPage(servletName);
    clickLinkWithText("?propagate=true");
    assertTextPresent("You are logged in as an Administrator");
  }

  /**
   * Fill and click upload.
   */
  @Test
  public void testUpload() {
    setScriptingEnabled(false);
    beginAt("/org.melati.login.Login/admintest");
    setTextField("field_login", "_administrator_");
    setTextField("field_password", "FIXME");
    checkCheckbox("rememberme");
    submit();
    gotoPage(servletName);
    assertTrue("Cannot find file src/main/java/org/melati/admin/static/file.gif",
        new File("src/main/java/org/melati/admin/static/file.gif").exists());
    setTextField("file","src/main/java/org/melati/admin/static/file.gif");
    submit();
    assertWindowPresent("Upload");
    assertTrue("Cannot find file pom.xml", new File("pom.xml").exists());
    setTextField("file","pom.xml");
    submit();
    gotoWindow("Upload");
    assertTextPresent("<groupId>org.melati</groupId>");
    
  }

  /**
   * Click Redirect link.
   */
  @Test
  public void testRedirect() {
    setScriptingEnabled(false);
    beginAt(servletName);
    clickLinkWithText("Redirect");
    assertTextPresent("Melati is a tool");
  }

  /**
   * Click view.
   */
  @Test
  public void testView() {
    setScriptingEnabled(false);
    beginAt(servletName );
    clickLinkWithText("tableinfo/0/View");
    assertTextPresent("logicalDatabase = melatitest, table = tableinfo, troid = 0, method = View");
  }

}
