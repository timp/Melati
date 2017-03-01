package org.melati;

import net.sourceforge.jwebunit.junit.JWebUnit;
import net.sourceforge.jwebunit.util.TestingEngineRegistry;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * Much thanks to 
 * http://today.java.net/pub/a/today/2007/04/12/embedded-integration-testing-of-web-applications.html
 * 
 * @author timp
 * @since 2008/01/01
 * 
 */
public class JettyWebTestCase {
  private static Server server;
  private static boolean started = false;
  protected static String contextName = "melatitest";
  protected static String webAppDirName = "src/test/webapp";
  protected static String referenceOutputDir = "src/test/resources";

  protected static int actualPort;  

  @BeforeClass
  public static void setUp() throws Exception {
    JWebUnit.setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_HTMLUNIT);
    // Port 0 means "assign arbitrarily port number"
    actualPort = startServer(0);
    String baseUrl = "http://localhost:" + actualPort + "/" + getContextName() + "/";
    setBaseUrl(baseUrl);
  }

  @AfterClass
  public static void tearDown() throws Exception {
    if (server != null) {
      server.stop();
    }
  }

  public static void main(String[] args) throws Exception {
    actualPort = startServer(8080);
  }

  protected static int startServer(int port) throws Exception {
    Log.setLog(new StdErrLog());

    if (!started || ((NetworkConnector) server.getConnectors()[0]).getLocalPort() < 0) {
      server = new Server(port);
      WebAppContext wac = new WebAppContext();
      wac.setContextPath("/" + getContextName());
      wac.setResourceBase(new File(getWebAppDirName()).getAbsolutePath());
      wac.setDescriptor(getWebAppDirName() + "/WEB-INF/web.xml");
      wac.setLogUrlOnStart(true);

      HandlerList handlers = new HandlerList();
      handlers.setHandlers(new Handler[] { wac, new DefaultHandler()});
      server.setHandler(handlers);
      server.start();
      started = true;
    }

    return ((NetworkConnector)server.getConnectors()[0]).getLocalPort();
  }
  
  protected int getActualPort() { 
    return actualPort;
  }
  
  /**
   * Just to say hello.
   */
  @Test
  public void testIndex() {
    beginAt("/");
    assertTextPresent("Hello World!");
  }
  

  public static String getContextName() {
    return contextName;
  }
  
  /**
   * @return relative path of webapp dir
   */
  public static String getWebAppDirName() {
    return webAppDirName;
  }

  protected static void setContextName(String contextName) {
    JettyWebTestCase.contextName = contextName;
  }

  protected static void setWebAppDirName(String webAppDirName) {
    JettyWebTestCase.webAppDirName = webAppDirName;
  }

  private boolean generateCached() { 
    return false;
  }

  protected void assertPageEqual(String url, String fileName) throws Exception { 
    beginAt(url);
    String generated = getPageSource();
    File referenceFile = new File(referenceOutputDir, fileName);
    if (referenceFile.exists() && ! generateCached()) {
      FileInputStream file = new FileInputStream (referenceFile);
      byte[] b = new byte[file.available()];
      file.read(b);
      file.close ();
      String cached = new String(b);
      assertEquals(cached, generated);
    } else { 
      FileOutputStream file = new FileOutputStream(referenceFile);
      file.write(generated.getBytes());
      file.close();
      fail("Reference output file generated: " + referenceFile.getCanonicalPath() + " modify generateCached and rerun");
    }
  }
  
}