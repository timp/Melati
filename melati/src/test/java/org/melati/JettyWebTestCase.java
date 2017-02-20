package org.melati;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import net.sourceforge.jwebunit.junit.WebTestCase;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.StdErrLog;

/**
 *
 * Much thanks to 
 * http://today.java.net/pub/a/today/2007/04/12/embedded-integration-testing-of-web-applications.html
 * 
 * @author timp
 * @since 2008/01/01
 * 
 */
public class JettyWebTestCase extends WebTestCase {

  private static Server server;
  private static boolean started = false;
  protected static String contextName = "melatitest";
  protected static String webAppDirName = "src/test/webapp";
  protected static String referenceOutputDir = "src/test/resources";

  protected static int actualPort;  

  public JettyWebTestCase() {
    super();
  }

  public JettyWebTestCase(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    // Port 0 means "assign arbitrarily port number"
    actualPort = startServer(0);
    getTestContext().setBaseUrl("http://localhost:" + actualPort + "/" );
  }

  protected void tearDown() throws Exception {
    super.tearDown();
  }
  
  public static void main(String[] args) throws Exception {
    actualPort = startServer(8080);
  }

  protected static int startServer(int port) throws Exception {
    Log.setLog(new StdErrLog());

    if (!started) { 
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
      server.dumpStdErr();
      server.join();
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
  public void testIndex() {
    beginAt("/");
    assertTextPresent("Hello World!");
  }
  
  public void beginAt(String url) {
    super.beginAt(contextUrl(url));
  }

  public void gotoPage(String url) {
    System.err.println(contextUrl(url));
    super.gotoPage(contextUrl(url));
  }
  protected String contextUrl(String url) { 
    return "/" + getContextName()  + url;
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
    String generated = getTester().getPageSource();
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