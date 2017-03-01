package org.melati.admin.test;

import junit.framework.AssertionFailedError;
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.html.Cell;
import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.melati.JettyWebTestCase;
import org.melati.util.HTMLUtils;

import java.util.List;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.*;

/**
 * @author timp
 * @since 2008/01/01
 */
public class AdminJettyWebTest extends JettyWebTestCase {
  private String dbName = "admintest";

  /**
   * If you don't know by now.
   */
  public static void main(String[] args) throws Exception {
    startServer(8080);
  }

  /**
   * There appears to be a problem with gargoyle javascript such that
   * we cannot begin at the Main page as this throws NullPointerException,
   * The work around is to begin at the server top and then go to the Main page.
   */
  @BeforeClass
  public static void setUp() throws Exception {
    JettyWebTestCase.setUp();
    setScriptingEnabled(true);
    beginAt("/");
  }

  @AfterClass
  public static void tearDown() throws Exception {
    JettyWebTestCase.tearDown();
  }


  /**
   * Test Page calls
   */
  @Test
  public void testBadUrl() {
    try {
      gotoPage("/Admin/" + dbName + "/Unknown");
    } catch (TestingEngineResponseException e) {
      assertEquals(400, e.getHttpStatusCode());
    }
    assertTextPresent("Melati Error Template");
  }

  @Test
  public void testAdminMain() {
    gotoPage("/Admin/" + dbName + "/Main");
    assertTitleEquals("Melati Database Admin Suite - Admintest database");
  }

  @Test
  public void testAdminTop() {
    gotoPage("/Admin/" + dbName + "/Top");
    assertTextPresent("Melati Database Admin Suite");
    assertTextPresent("Admintest database");
  }

  @Test
  public void testAdminTopWithTable() {
    gotoPage("/Admin/" + dbName + "/user/Top");
    assertTextPresent("Melati Database Admin Suite");
    assertTextPresent("Admintest database");
  }

  @Test
  public void testAdminTopWithTableAndTroid() {
    gotoPage("/Admin/" + dbName + "/user/0/Top");
    assertTextPresent("Melati Database Admin Suite");
    assertTextPresent("Admintest database");
  }

  @Test
  public void testUpload() {
    gotoPage("/Admin/" + dbName + "/user/Upload?field=tim");
    assertTextPresent("File to upload:");
  }

  @Test
  public void testProxy() {
    //Not allowed as we have yet to add token to session
    try {
      String url = "/Admin/" + dbName + "/Proxy?http://google.com/";
      gotoPage(url);
      fail("Should have bombed");
    } catch (TestingEngineResponseException e) {
      // expected
    }

    // Allowed
    beginAt("/Display/" + dbName + "?template=org.melati.admin.test.ProxyCaller");
    clickLinkWithText("google");

    // Allowed
    beginAt("/Display/" + dbName + "?template=org.melati.admin.test.ProxyCaller");
    clickLinkWithText("check");

  }

  /**
   * Test that an AdminSpecialised object has its special templet included in edit.
   */
  @Test
  public void testAdminSpecialised() {
    setScriptingEnabled(true);
    loginAsAdministrator();
    gotoPage("/Admin/admintest/uploadedfile/Main");
    gotoRootWindow();
    gotoFrame("admin_top");
    setWorkingForm("gotoform");
    selectOption("table", "Uploaded File");
    assertFormPresent("gotoform");
    submit();
    gotoRootWindow();
    gotoFrame("admin_bottom");
    gotoFrame("admin_left");
    gotoFrame("admin_navigation");
    clickLink("add");
    gotoRootWindow();
    gotoFrame("admin_bottom");
    gotoFrame("admin_record");

    setTextField("field_filename", "test.txt");
    clickLinkWithText("Upload a new file");


    gotoWindow("filename");
    setTextField("file", "/dist/melati/melati/src/main/java/org/melati/admin/static/file.gif");
    setScriptingEnabled(false);
    submit();
    assertTextPresent("Your file has been saved");
    gotoRootWindow();
  }

  /**
   * Test that an AdminSpecialised object invokes its own handler.
   */
  @Test
  public void testAdminSpecialisedHandler() {
    setScriptingEnabled(false);
    gotoPage("/Admin/admintest/specialised/Main");
    gotoAddRecord("Specialised");
    setTextField("field_name", "test");
    submit();
    assertTextPresent("Done");
    gotoPage("/Admin/admintest/specialised/0/NotAnAdminMethod");
    assertTextPresent("Hi, I'm Special.");
    // Hmm, is this intended behaviour?
    gotoPage("/Admin/admintest/specialised/0/Edit");
    assertTextPresent("Hi, I'm Special.");
  }

  /**
   * Test that if there is no primary select column on the table
   * no primary criteria are displayed.
   */
  @Test
  public void testNoPrimarySelect() {
    setScriptingEnabled(false);
    gotoPage("/Admin/admintest/specialised/Main");
    gotoAddRecord("Specialised");
    setTextField("field_name", "test");
    submit();
    gotoPage("/Admin/admintest/specialised/0/NotAnAdminMethod");
    assertTextPresent("Hi, I'm Special.");
    // Hmm, is this intended behaviour?
    gotoPage("/Admin/admintest/specialised/0/Edit");
    assertTextPresent("Hi, I'm Special.");

    gotoPage("/Admin/admintest/user/PrimarySelect");
    assertTablePresent("primarySelectTable");
    gotoPage("/Admin/admintest/uploadedfile/PrimarySelect");
    assertTableNotPresent("primarySelectTable");
    gotoPage("/Admin/admintest/user/PrimarySelect?field_name=");
    assertTablePresent("primarySelectTable");

    gotoPage("/Admin/admintest/specialised/0/PrimarySelect");
    assertTableNotPresent("primarySelectTable");

  }


  /**
   * Getting the coverage is proof enough.
   */
  @Test
  public void testDescendingOrder() {
    gotoPage("/Admin/admintest/user/Selection?target=admin_record&returnTarget=admin_record&field_id=0&field_order-1=1&field_order-1-toggle=true");
    gotoPage("/Admin/admintest/user/Selection?target=admin_record&returnTarget=admin_record&field_id=0&field_order-1=1&field_order-1-toggle=true");
    gotoPage("/Admin/admintest/uploadedfile/Selection?target=admin_record&returnTarget=admin_record&field_order-1=70&field_order-1-toggle=true");
    gotoPage("/Admin/admintest/uploadedfile/Selection?target=admin_record&returnTarget=admin_record&field_order-1=70&field_order-1-toggle=true");
  }

  @Test
  public void testSelectionJSON() throws Exception {
    //   assertPageEqual("/Admin/admintest/user/SelectionJSON", "/Admin/admintest/user/SelectionJSON");
    //   assertPageEqual("/Admin/admintest/user/SelectionJSON?field_include-0=name", "/Admin/admintest/user/SelectionJSON_include_name");
    try {
      gotoPage("/Admin/admintest/user/SelectionJSON?field_include-0=11");
    } catch (TestingEngineResponseException e) {
      // expected
    }
  }


  @Test
  public void testAdminBottom() {
    gotoPage("/Admin/" + dbName + "/user/Bottom");
    // Hmmm Should assert something, coverage is the thing
  }

  @Test
  public void testAdminRecord() {
    gotoPage("/Admin/" + dbName + "/user/0/Record");
    // Hmmm Should assert something, coverage is the thing
  }

  @Test
  public void testAdminPrimarySelect() {
    gotoPage("/Admin/" + dbName + "/user/PrimarySelect");
    assertTextPresent("Full name");
  }

  @Test
  public void testAdminSelection() {
    gotoPage("/Admin/" + dbName + "/user/Selection?target=&returnTarget=");
    assertTextPresent("Full name");
    assertTextPresent("Melati guest user");
    clickLinkWithText("Full name");
    Table s = getTable("selectionTable");
    List<?> rows = s.getRows();
    for (int i = 0; i < rows.size(); i++) {
      List<?> cells = ((Row) rows.get(i)).getCells();
      for (int j = 0; j < cells.size(); j++) {
        String value = ((Cell) cells.get(j)).getValue();
        if (i == 2 && j == 2)
          assertEquals("_guest_", value.trim());
        if (i == 3 && j == 2)
          assertEquals("_administrator_", value.trim());
      }
    }
    clickLinkWithText("Full name");
    s = getTable("selectionTable");
    rows = s.getRows();
    for (int i = 0; i < rows.size(); i++) {
      List<?> cells = ((Row) rows.get(i)).getCells();
      for (int j = 0; j < cells.size(); j++) {
        String value = ((Cell) cells.get(j)).getValue();
        if (i == 2 && j == 2)
          assertEquals("_administrator_", value.trim());
        if (i == 3 && j == 2)
          assertEquals("_guest_", value.trim());
      }
    }
    clickLinkWithText("Full name");
    s = getTable("selectionTable");
    rows = s.getRows();
    for (int i = 0; i < rows.size(); i++) {
      List<?> cells = ((Row) rows.get(i)).getCells();
      for (int j = 0; j < cells.size(); j++) {
        String value = ((Cell) cells.get(j)).getValue();
        if (i == 2 && j == 2)
          assertEquals("_guest_", value.trim());
        if (i == 3 && j == 2)
          assertEquals("_administrator_", value.trim());
      }
    }
    clickLinkWithText("Full name");
    s = getTable("selectionTable");
    rows = s.getRows();
    for (int i = 0; i < rows.size(); i++) {
      List<?> cells = ((Row) rows.get(i)).getCells();
      for (int j = 0; j < cells.size(); j++) {
        String value = ((Cell) cells.get(j)).getValue();
        if (i == 2 && j == 2)
          assertEquals("_administrator_", value.trim());
        if (i == 3 && j == 2)
          assertEquals("_guest_", value.trim());
      }
    }
  }

  /**
   * Can we get to page three and back.
   */
  @Test
  public void testSelectionPaging() {
    gotoPage("/Admin/" + dbName + "/columnInfo/Selection?target=&returnTarget=");
    clickLinkWithText(">");
    clickLinkWithText(">");
    clickLinkWithText(">");
    assertTextPresent("Records 61 to 80 of 90");
    clickLinkWithText("<");
    clickLinkWithText("<");
    clickLinkWithText("<");
    assertTextPresent("Records 1 to 20 of 90");
  }

  @Test
  public void testAdminEditHeader() {
    gotoPage("/Admin/" + dbName + "/parent/0/EditHeader");
    clickLink("admin_edit_Parent_0");
    gotoWindow("admin_edit_Parent_0");
    assertElementPresent("selection");
  }

  @Test
  public void testAdminEdit() {
    gotoPage("/Admin/" + dbName + "/user/0/Edit");
    assertTextPresent("Full name");
    assertTextPresent("_guest_");
    clickLinkWithText("More ...");
    assertLinkNotPresentWithText("More ...");
  }

  @Test
  public void testAdminEditFieldNoJS() {
    gotoPage("/Admin/admintest/markup/Main");
    gotoAddRecord("Markup");
    assertEquals("&Aacute;", HTMLUtils.entityFor("\u00C1".charAt(0), false, null, false));
    //char it = 193;
    //System.err.println("\u00C1".charAt(0));
    //System.err.println(new Integer("\u00C1".charAt(0)));
    //System.err.println(new Integer("?".charAt(0)));
    //System.err.println("Acirc=" +new Integer("Â".charAt(0)));
    //System.err.println("");

    setTextField("field_text", "\u00C1");
    setScriptingEnabled(false);
    submit("action", "Create");
    assertTextPresent("Done");
    gotoPage("/Admin/admintest/markup/0/Edit");
    assertEquals("\u00C1", getFormFieldValue("field_text"));
    assertTextPresent("\u00C1");
  }

  // Seems to work in real life
  //@Test
  public void brokenestAdminEditField() {
    gotoPage("/Admin/admintest/markup/Main");
    gotoAddRecord("Markup");
    assertEquals("&Aacute;", HTMLUtils.entityFor("\u00C1".charAt(0), false, null, false));
    //char it = 193;
    //System.err.println("\u00C1".charAt(0));
    //System.err.println(new Integer("\u00C1".charAt(0)));
    //System.err.println(new Integer("?".charAt(0)));
    //System.err.println("Acirc=" +new Integer("Â".charAt(0)));
    //System.err.println("");

    setTextField("field_text", "\u00C1");
    submit();
    //assertNotNull("Main body element should have id 'edit'", getElementById("edit"));
    submit("action");
    gotoPage("/Admin/admintest/markup/Selection");
    assertEquals("\u00C1", getFormFieldValue("field_text"));
    assertTextPresent("\u00C1");
  }

  private String getFormFieldValue(String fieldName) {
    try {
      return getTester().getElementAttributeByXPath(
          "//input[@name='" + fieldName + "']", "value");
    } catch (AssertionFailedError e) {
      System.out.println("Form element not present:" + fieldName);
      System.out.println(getTester().getPageSource());
      throw e;
    }

  }

  /**
   * @return value of named field
   */
  public String getFormTextareaValue(String fieldName) {
    return getTester().getElementTextByXPath(
        "//textarea[@name='" + fieldName + "']");
  }

  /**
   * Test that login is required.
   */
  @Test
  public void testAdminEditAdministrator() {
    gotoPage("Logout/" + dbName);
    gotoPage("/Admin/" + dbName + "/user/1/Edit");
    assertTextPresent("You need to log in.");
    setTextField("field_login", "_administrator55_");
    setTextField("field_password", "FIXME");
    checkCheckbox("rememberme");
    submit("action");
    setTextField("field_login", "_administrator_");
    setTextField("field_password", "FIXME_not");
    checkCheckbox("rememberme");
    submit("action");
    setTextField("field_login", "_administrator_");
    setTextField("field_password", "FIXME");
    checkCheckbox("rememberme");
    submit("action");
    assertTextPresent("Full name");
    assertTextPresent("_administrator_");

    // Note that logging out has no effect if rememberme was chosen
    gotoPage("/Logout/" + dbName + "");
    gotoPage("/Admin/" + dbName + "/user/1/Edit");
    setTextField("field_login", "_administrator_");
    setTextField("field_password", "FIXME");
    assertTextFieldEquals("field_email", "");
    setTextField("field_email", "test@test.com");
    setScriptingEnabled(false);
    submit("action", "Update");
    clickLink("continue");
    assertTextFieldEquals("field_email", "test@test.com");
    submit("action", "Update");
    clickLink("continue");
    assertNotNull("Main body element should have id 'edit'", getElementById("edit"));
  }

  /**
   * Test that we are challenged to get into the everything db.
   */
  @Test
  public void testEverythingIsProtected() {
    gotoPage("/Logout/everything");
    gotoPage("/Admin/everything/Main");
    System.out.println(getPageSource());
    assertTextPresent("You need to log in");
  }

  @Test
  public void testAdminTree() {
    gotoPage("/Admin/" + dbName + "/user/0/Record");
    gotoFrame("admin_edit_header");
    assertTextPresent("_guest_");
    assertTextPresent("[ Group membership ]");
    clickLink("recordTree");
    gotoRootWindow();
    gotoFrame("admin_edit_User_0");
    assertTitleEquals("Melati guest user tree");
    clickLinkWithText("Melati guest user");
  }

  @Test
  public void testAdminTreeNoScript() {
    gotoPage("/Admin/" + dbName + "/user/0/Tree");
    assertTitleEquals("Melati guest user tree");
  }

  @Test
  public void testAdminTableTree() {
    setScriptingEnabled(true);
    gotoPage("/Admin/" + dbName + "/user/Table");
    gotoFrame("admin_navigation");
    String page = getPageSource();
    System.err.println(page);
    clickLink("tableTree");
    gotoRootWindow();
    gotoFrame("admin_selection");
    assertTitleEquals("User table tree");
    assertLinkPresentWithText("Melati guest user");
    assertLinkPresentWithText("Melati database administrator");
    setScriptingEnabled(false);
  }

  @Test
  public void testAdminSelectionWindow() {
    gotoPage("/Admin/" + dbName + "/user/SelectionWindow?returnfield=field_user");
    assertTitleEquals("Melati Database Admin Suite - Select a User");
  }

  @Test
  public void testAdminSelectionWindowPrimarySelect() {
    gotoPage("/Admin/" + dbName + "/user/SelectionWindowPrimarySelect?returnfield=field_user");
    assertTextPresent("Full name");
  }

  @Test
  public void testAdminSelectionWindowSelection() {
    gotoPage("/Admin/" + dbName + "/user/SelectionWindowSelection?returnfield=field_user");
    assertTextPresent("Records 1 to 2 of 2");
  }

  @Test
  public void testAdminSelectionCSV() {
    gotoPage("/Admin/" + dbName + "/columnInfo/Selection");
    assertTextPresent("Records 1 to 20 of 90");
    clickLink("csv");
    gotoWindow("_columnInfocsv");
    assertTextPresent("\"0\",\"0\",\"id\",\"10\",\"false\",");
  }

  @Test
  public void testAdminPopup() {
    gotoPage("/Admin/" + dbName + "/user/PopUp");
    assertTitleEquals("Search User Table");
  }

  @Test
  public void testAdminDSD() {
    gotoPage("/Admin/" + dbName + "/DSD");
    assertTextPresent("Generated for ");
    assertTextPresent("package org.melati.admin.test;");
  }

  /**
   * Move to login
   */
  @Test
  public void testLoginWithContinuation() {
    gotoPage("/Logout/" + dbName);
    gotoPage("/Login/" + dbName + "?continuationURL=/" + getContextName() + "/index.html");
    setTextField("field_login", "_administrator_");
    setTextField("field_password", "FIXME");
    checkCheckbox("rememberme");
    submit("action");
    assertTextPresent("Hello World!");
  }

  /**
   * Test setting the defaults, with javascript enabled
   */
  @Test
  public void testSetupStory() {
    setScriptingEnabled(true);
    loginAsAdministrator();
    gotoFrame("admin_top");
    clickLinkWithText("Setup");
    gotoRootWindow();
    gotoFrame("admin_bottom");
    gotoFrame("admin_record");
    gotoFrame("admin_edit_setting");
    assertNotNull(getElementById("blank"));
  }

  /**
   * Test setting the defaults, with javascript disabled
   */
  @Test
  public void testSetupStoryNoJS() {
    loginAsAdministrator();
    gotoFrame("admin_top");
    setScriptingEnabled(false);
    clickLinkWithText("Setup");
    gotoRootWindow();
    gotoFrame("admin_bottom");
    assertTextPresent("Done");
  }


  /**
   * Search for a set of records.
   */
  @Test
  public void testSearchAndGoto() {
//    setScriptingEnabled(true);
    gotoPage("/Admin/" + dbName + "/Main");
    gotoRootWindow();
    gotoFrame("admin_top");
    setWorkingForm("gotoform");
    selectOption("table", "Column");
    assertFormPresent("gotoform");
    submit();
    gotoRootWindow();
    gotoFrame("admin_bottom");
    gotoFrame("admin_left");
    gotoFrame("admin_navigation");
    clickLink("search");
    gotoWindow("admin_search");
    setTextField("field_displayname", "Id");
    selectOptionByValue("field_order-1", "42"); //Id
    selectOptionByValue("field_order-2", "43"); //Owning table
    submit();
    closeWindow();

//    String page = getPageSource();
//    System.err.println(page);

    gotoFrame("admin_bottom");
    gotoFrame("admin_left");
    gotoFrame("admin_selection");
    assertTextPresent("Records 1 to 15 of 15");
  }

  /**
   * User story.
   */
  @Test
  public void testCreateTableStory() {
    setScriptingEnabled(false);
    gotoPage("/Logout/" + dbName);
    loginAsAdministrator();
    gotoAddRecord("Table");
    setTextField("field_name", "test");
    setTextField("field_displayname", "Test");
    setTextField("field_description", "A Test table");
    setTextField("field_displayorder", "0");
    selectOptionByValue("field_category", "3"); //Normal
    submit();
    //gotoFrame("admin_bottom");
    assertTextPresent("Done");
    String tableTroid = getElementAttributeByXPath(
        "//input[@name='" + "troid" + "']", "value");
    gotoPage("/Admin/" + dbName + "/tableinfo/" + tableTroid + "/Main");
    gotoFrame("admin_bottom");
    gotoFrame("admin_record");
    gotoFrame("admin_edit_header");
    clickLinkWithText("Column");
    gotoRootWindow();
    gotoFrame("admin_bottom");
    gotoFrame("admin_record");
    gotoFrame("admin_edit_tableInfo_" + tableTroid);
    clickLink("create_columnInfo");
    setTextField("field_name", "test");
    setTextField("field_description", "A Test column");
    setTextField("field_displayorder", "0");
    checkCheckbox("field_usercreateable");
    checkCheckbox("field_indexed");
    // We want to duplicate
    //checkCheckbox("field_unique");
    setTextField("field_displayname", "Test");
    checkCheckbox("field_nullable");
    setTextField("field_size", "20");
    setTextField("field_width", "20");
    setTextField("field_height", "1");
    setTextField("field_precision", "1");
    setTextField("field_scale", "1");
    submit();
    assertTextPresent("Done");
    String columnTroid = getElementAttributeByXPath(
        "//input[@name='" + "troid" + "']", "value");
    gotoAddRecord("Test");
    setTextField("field_test", "test");
    submit();
    assertTextPresent("Done");
    String recordTroid = getElementAttributeByXPath(
        "//input[@name='" + "troid" + "']", "value");

    clickLink("continue");
    gotoPage("/Admin/" + dbName + "/test/" + recordTroid + "/Main");
    gotoFrame("admin_bottom");
    gotoFrame("admin_record");
    gotoFrame("admin_edit_test_" + recordTroid);
    submit("action", "Duplicate");
    assertTextPresent("Done");
    //String href = getElementAttributByXPath(
    //    "//a[@id='" + "continue" + "']", "href");
    //System.err.println("Continue:" + href);
    clickLink("continue");

    // Records will be sorted by id
    deleteRecord("test", "test", new Integer(recordTroid));
    deleteRecord("test", "test", new Integer(recordTroid) + 1);

    gotoPage("/Admin/" + dbName + "/columnInfo/" + columnTroid + "/Main");
    gotoFrame("admin_bottom");
    gotoFrame("admin_record");
    gotoFrame("admin_edit_columnInfo_" + columnTroid);
    submit("action", "Delete");
    assertTextPresent("Done");

    int c = new Integer(columnTroid) - 1;
    columnTroid = Integer.toString(c);
    gotoPage("/Admin/" + dbName + "/columninfo/" + columnTroid + "/Main");
    gotoFrame("admin_bottom");
    gotoFrame("admin_record");
    gotoFrame("admin_edit_columnInfo_" + columnTroid);
    submit("action", "Delete");
    assertTextPresent("Done");

    gotoPage("/Admin/" + dbName + "/tableinfo/" + tableTroid + "/Main");
    gotoFrame("admin_bottom");
    gotoFrame("admin_record");
    gotoFrame("admin_edit_tableInfo_" + tableTroid);
    setScriptingEnabled(false);
    submit("action", "Delete");


    assertTextPresent("Done");
    assertTitleEquals("Updated a Table Record");

    setScriptingEnabled(true);
    clickLink("continue");
    gotoRootWindow();

    assertTitleEquals("Melati Database Admin Suite - Admintest database");
  }

  private void gotoAddRecord(String table) {
    gotoRootWindow();
    gotoFrame("admin_top");
    selectOption("table", table);
    assertFormPresent("gotoform");
    setWorkingForm("gotoform");
    submit();
    gotoRootWindow();
    gotoFrame("admin_bottom");
    //System.err.println(getTester().getDialog().getPageURL());

    gotoFrame("admin_left");
    gotoFrame("admin_navigation");
    clickLink("add");
    gotoRootWindow();
    gotoFrame("admin_bottom");
    gotoFrame("admin_record");
  }

  /**
   * Start and end at top window.
   */
  private void deleteRecord(String tableName, String uniqueKeyValue, int troid) {
    gotoPage("/Admin/" + dbName + "/" + tableName + "/Main");
    gotoFrame("admin_bottom");
    gotoFrame("admin_left");
    gotoFrame("admin_selection");
    clickLinkWithText(uniqueKeyValue);
    gotoRootWindow();
    gotoFrame("admin_bottom");
    gotoFrame("admin_record");
    gotoFrame("admin_edit_" + tableName + "_" + troid);
    clickButton("delete");
    gotoPage("/Admin/" + dbName + "/Main");
  }

  /**
   * Returns us to top frame.
   */
  private void loginAsAdministrator() {
    gotoPage("/Logout/" + dbName);
    gotoPage("/Admin/" + dbName + "/Top");
    clickButton("login");
    setTextField("field_login", "_administrator_");
    setTextField("field_password", "FIXME");
    // checkCheckbox("rememberme"); // We need to forget to stop tests interacting
    submit("action");
  }

  @Test
  public void testCopy() throws Exception {
    // cannot copy onto itself
    try {
      gotoPage("/Copy/everything");
    } catch (TestingEngineResponseException e) {
      // expected
    }
    // cannot copy onto itself
    try {
      gotoPage("/Copy/everything/everything");
    } catch (TestingEngineResponseException e) {
      // expected
    }

    gotoPage("/Copy/everything/everything2");

  }
}