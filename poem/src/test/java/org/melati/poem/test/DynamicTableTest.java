/**
 * 
 */
package org.melati.poem.test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;

import org.melati.poem.ColumnInfo;
import org.melati.poem.ColumnRenamePoemException;
import org.melati.poem.Database;
import org.melati.poem.DisplayLevel;
import org.melati.poem.DuplicateColumnNamePoemException;
import org.melati.poem.DuplicateDeletedColumnPoemException;
import org.melati.poem.DuplicateTroidColumnPoemException;
import org.melati.poem.IntegrityFix;
import org.melati.poem.PoemDatabaseFactory;
import org.melati.poem.PoemThread;
import org.melati.poem.PoemTypeFactory;
import org.melati.poem.Searchability;
import org.melati.poem.StandardIntegrityFix;
import org.melati.poem.TableInfo;
import org.melati.poem.util.EnumUtils;

/**
 * Test the addition, and lter the deletion, of columns to a table.
 * @author timp
 * @since 01-Februray-2007
 */
public class DynamicTableTest extends EverythingTestCase {

  /**
   * Constructor for PoemTest.
   * @param arg0
   */
  public DynamicTableTest(String arg0) {
    super(arg0);
  }

  /**
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
  }

  /**
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    if (!problem) {
      checkDbUnchanged();
    }
  }

  protected void databaseUnchanged() { 
    // It is not good enough to drop the new columns, as the deleted columnInfo's 
    // are still referred to, so drop the whole table
    getDb().sqlUpdate("DROP TABLE " + getDb().getDbms().getQuotedName("dynamic"));
    getDb().sqlUpdate("DROP TABLE " + getDb().getDbms().getQuotedName("tableinfo"));
    getDb().sqlUpdate("DROP TABLE " + getDb().getDbms().getQuotedName("columninfo"));
    PoemThread.commit();
    PoemDatabaseFactory.removeDatabase(databaseName);
  } 
  
  public Database getDb() {
    Database db = super.getDb(); 
    db.setLogSQL(true);
    return db;
  }

  

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitTroid() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo)getDb().getColumnInfoTable().newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testtroidcol");
    columnInfo.setDisplayname("Test Troid Column");
    columnInfo.setDisplayorder(99);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Troid column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.TROID);
    columnInfo.setSize(-1);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.detail);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    try {
      columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
      fail("Should have blown up");
    } catch (DuplicateTroidColumnPoemException e) {
      e = null;
    }
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitDeleted() throws Exception {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testdeletedcol");
    columnInfo.setDisplayname("Test Deleted Column");
    columnInfo.setDisplayorder(99);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Deleted column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.DELETED);
    columnInfo.setSize(-1);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testdeletedcol").selectionWhereEq(Boolean.FALSE)).size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testdeletedcol").selectionWhereEq(Boolean.FALSE)).size());
    assertEquals(Boolean.FALSE, dt.two().getRaw("testdeletedcol"));
    assertEquals(Boolean.FALSE, dt.two().getCooked("testdeletedcol"));
    assertEquals(Boolean.FALSE, dt.getObject(0).getCooked("testdeletedcol"));
    try {
      columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
      fail("Should have blown up");
    } catch (DuplicateColumnNamePoemException e) {
      assertEquals("Can't add duplicate column dynamic.testdeletedcol: "
          + "deleted (BOOLEAN (org.melati.poem.DeletedPoemType)) "
          + "(from the running application) to dynamic "
          + "(from the data structure definition)", e.getMessage());
      e = null;
    }
    try {
      columnInfo.setName("testdeletedcol2");
      fail("Should have blown up");
    } catch (ColumnRenamePoemException e) {
      e = null;
    }
    ColumnInfo columnInfo2 = (ColumnInfo)getDb().getColumnInfoTable()
        .newPersistent();
    columnInfo2.setTableinfo(ti);
    columnInfo2.setName("testdeletedcol2");
    columnInfo2.setDisplayname("Test duplicate Deleted Column");
    columnInfo2.setDisplayorder(99);
    columnInfo2.setSearchability(Searchability.yes);
    columnInfo2.setIndexed(false);
    columnInfo2.setUnique(false);
    columnInfo2.setDescription("A non-nullable extra Deleted column");
    columnInfo2.setUsercreateable(true);
    columnInfo2.setUsereditable(true);
    columnInfo2.setTypefactory(PoemTypeFactory.DELETED);
    columnInfo2.setSize(-1);
    columnInfo2.setWidth(20);
    columnInfo2.setHeight(1);
    columnInfo2.setPrecision(0);
    columnInfo2.setScale(0);
    columnInfo2.setNullable(false);
    columnInfo2.setDisplaylevel(DisplayLevel.summary);
    columnInfo2.makePersistent();
    try {
      columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo2);
      fail("Should have blown up");
    } catch (DuplicateDeletedColumnPoemException e) {
      assertEquals("Can't add testdeletedcol2 to dynamic as a deleted column, "
          + "because it already has one, "
          + "i.e. dynamic.testdeletedcol: deleted "
          + "(BOOLEAN (org.melati.poem.DeletedPoemType)) "
          + "(from the running application)", e.getMessage());
      e = null;
    }
//    String query = "ALTER TABLE " + getDb().getUserTable().quotedName() + 
//    " DROP COLUMN " + getDb().quotedName(columnInfo.getName());
//    getDb().sqlUpdate(query);
//    columnInfo.delete();
//    getDb().getUserTable()
//    columnInfo2.delete();
//    getDb().uncacheContents();
//    getDb().unifyWithDB();
    PoemThread.commit();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitType() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testtypecol");
    columnInfo.setDisplayname("Test Type Column");
    columnInfo.setDisplayorder(99);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Type column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.TYPE);
    columnInfo.setSize(-1);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    Integer t = null;
    Enumeration en = dt.selection();
    Dynamic d = (Dynamic) en.nextElement();
    t = (Integer)d.getRaw("testtypecol");
    int count = 0;
    while (en.hasMoreElements()) {
      d = (Dynamic) en.nextElement();
      if (d.statusExistent()) {
        assertEquals(t, (Integer)d.getRaw("testtypecol"));
        count++;
      }
    }
    assertEquals(1, count);
    
    PoemTypeFactory t2 = null;
    Enumeration en2 = dt.selection();
    t2 = (PoemTypeFactory) ((Dynamic) en2.nextElement()).getCooked("testtypecol");
    while (en2.hasMoreElements()) {
      System.err.println(t2.getName());
      assertEquals(t2.getName(), ((PoemTypeFactory) ((Dynamic) en2.nextElement())
          .getCooked("testtypecol")).getName());
    }

    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testtypecol").selectionWhereEq(new Integer(0))).size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testtypecol").selectionWhereEq(new Integer(0))).size());
    assertEquals(new Integer(0), dt.two().getRaw("testtypecol"));
    assertEquals("user", ((PoemTypeFactory) dt.two().getCooked("testtypecol")).getName());
    assertEquals("user", ((PoemTypeFactory) dt.getObject(0).getCooked(
        "testtypecol")).getName());
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitBoolean() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testbooleancol");
    columnInfo.setDisplayname("Test Boolean Column");
    columnInfo.setDisplayorder(99);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Boolean column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.BOOLEAN);
    columnInfo.setSize(-1);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testbooleancol").selectionWhereEq(Boolean.FALSE)).size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testbooleancol").selectionWhereEq(Boolean.FALSE)).size());
    assertEquals(Boolean.FALSE, dt.two().getRaw("testbooleancol"));
    assertEquals(Boolean.FALSE, dt.two().getCooked(
        "testbooleancol"));
    assertEquals(Boolean.FALSE, dt.getObject(0).getCooked("testbooleancol"));
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitInteger() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testintegercol");
    columnInfo.setDisplayname("Test Integer Column");
    columnInfo.setDisplayorder(199);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Integer column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.INTEGER);
    columnInfo.setSize(8);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testintegercol").selectionWhereEq(new Integer(0))).size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testintegercol").selectionWhereEq(new Integer(0))).size());
    assertEquals(new Integer(0), dt.two()
        .getRaw("testintegercol"));
    assertEquals(new Integer(0), dt.two().getCooked(
        "testintegercol"));
    assertEquals(new Integer(0), dt.getObject(0).getCooked("testintegercol"));
    columnInfo.delete();
    getDb().setLogSQL(false);
  }
  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitNullableInteger() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testnullableintegercol");
    columnInfo.setDisplayname("Test Nullable Integer Column");
    columnInfo.setDisplayorder(199);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A nullable extra Integer column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.INTEGER);
    columnInfo.setSize(8);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(true);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    assertEquals(0, EnumUtils.vectorOf(
        dt.getColumn("testnullableintegercol").selectionWhereEq(new Integer(0))).size());
    PoemThread.commit();
    assertEquals(0, EnumUtils.vectorOf(
        dt.getColumn("testnullableintegercol").selectionWhereEq(new Integer(0))).size());
    assertNull(dt.two().getRaw("testnullableintegercol"));
    assertNull(dt.two().getCooked("testnullableintegercol"));
    assertNull(dt.getObject(0).getCooked("testnullableintegercol"));
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitDouble() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testdoublecol");
    columnInfo.setDisplayname("Test Double Column");
    columnInfo.setDisplayorder(199);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Double column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.DOUBLE);
    columnInfo.setSize(8);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testdoublecol").selectionWhereEq(new Double(0))).size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testdoublecol").selectionWhereEq(new Double(0))).size());
    assertEquals(new Double(0), dt.two().getRaw("testdoublecol"));
    assertEquals(new Double(0), dt.two().getCooked(
        "testdoublecol"));
    assertEquals(new Double(0), dt.getObject(0).getCooked("testdoublecol"));
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitLong() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testlongcol");
    columnInfo.setDisplayname("Test Long Column");
    columnInfo.setDisplayorder(199);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Long column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.LONG);
    columnInfo.setSize(8);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testlongcol").selectionWhereEq(new Long(0))).size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testlongcol").selectionWhereEq(new Long(0))).size());
    assertEquals(new Long(0), dt.two().getRaw("testlongcol"));
    assertEquals(new Long(0), dt.two().getCooked("testlongcol"));
    assertEquals(new Long(0), dt.getObject(0).getCooked("testlongcol"));
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitBigDecimal() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testbigdecimalcol");
    columnInfo.setDisplayname("Test Big Decimal Column");
    columnInfo.setDisplayorder(199);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Big Decimal column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.BIGDECIMAL);
    columnInfo.setSize(8);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testbigdecimalcol").selectionWhereEq(new BigDecimal(0.0)))
        .size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testbigdecimalcol").selectionWhereEq(new BigDecimal(0.0)))
        .size());
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitString() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("teststringcol");
    columnInfo.setDisplayname("Test String Column");
    columnInfo.setDisplayorder(99);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra String column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.STRING);
    columnInfo.setSize(-1);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("teststringcol").selectionWhereEq("default")).size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("teststringcol").selectionWhereEq("default")).size());
    assertEquals("default", dt.two().getRaw("teststringcol"));
    assertEquals("default", dt.two().getCooked("teststringcol"));
    assertEquals("default", dt.getObject(0).getCooked("teststringcol"));
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitPassword() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testpasswordcol");
    columnInfo.setDisplayname("Test Password Column");
    columnInfo.setDisplayorder(99);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Password column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.PASSWORD);
    columnInfo.setSize(-1);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testpasswordcol").selectionWhereEq("default")).size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testpasswordcol").selectionWhereEq("default")).size());
    assertEquals("default", dt.two().getRaw("testpasswordcol"));
    assertEquals("default", dt.two().getCooked("testpasswordcol"));
    assertEquals("default", dt.getObject(0).getCooked("testpasswordcol"));
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitDate() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testdatecol");
    columnInfo.setDisplayname("Test Date Column");
    columnInfo.setDisplayorder(199);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Date column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.DATE);
    columnInfo.setSize(8);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testdatecol").selectionWhereEq(
            new java.sql.Date(new Date().getTime()))).size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testdatecol").selectionWhereEq(
            new java.sql.Date(new Date().getTime()))).size());
    assertEquals(new java.sql.Date(new Date().getTime()).toString(), dt
        .two().getRaw("testdatecol").toString());
    assertEquals(new java.sql.Date(new Date().getTime()).toString(), dt
        .two().getCooked("testdatecol").toString());
    assertEquals(new java.sql.Date(new Date().getTime()).toString(), dt
        .getObject(0).getCooked("testdatecol").toString());
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitTimestamp() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testtimestampcol");
    columnInfo.setDisplayname("Test Timestamp Column");
    columnInfo.setDisplayorder(199);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Timestamp column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.TIMESTAMP);
    columnInfo.setSize(8);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    Timestamp t = null;
    Enumeration en = dt.selection();
    t = (Timestamp) ((Dynamic) en.nextElement()).getRaw("testtimestampcol");
    while (en.hasMoreElements()) {
      assertEquals(t, (Timestamp) ((Dynamic) en.nextElement())
          .getRaw("testtimestampcol"));
    }
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testtimestampcol").selectionWhereEq(t)).size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testtimestampcol").selectionWhereEq(t)).size());
    assertEquals(t, dt.two().getRaw("testtimestampcol"));
    assertEquals(t, dt.two().getCooked("testtimestampcol"));
    assertEquals(t, dt.getObject(0).getCooked("testtimestampcol"));
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void brokentestAddColumnAndCommitBinary() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testbinarycol");
    columnInfo.setDisplayname("Test Binary Column");
    columnInfo.setDisplayorder(199);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Binary column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.BINARY);
    columnInfo.setSize(8);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    byte[] t = null;
    Enumeration en = dt.selection();
    t = (byte[]) ((Dynamic) en.nextElement()).getRaw("testbinarycol");
    while (en.hasMoreElements()) {
      assertEquals(t.length, ((byte[]) ((Dynamic) en.nextElement())
          .getRaw("testbinarycol")).length);
    }
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testbinarycol").selectionWhereEq(t)).size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testbinarycol").selectionWhereEq(t)).size());
    assertEquals(t.length, ((byte[])dt.two().getRaw("testbinarycol")).length);
    assertEquals(t.length, ((byte[])dt.two().getCooked("testbinarycol")).length);
    assertEquals(t.length,
        ((byte[]) dt.getObject(0).getCooked("testbinarycol")).length);
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitDisplaylevel() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testdisplaylevelcol");
    columnInfo.setDisplayname("Test Displaylevel Column");
    columnInfo.setDisplayorder(99);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Displaylevel column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.DISPLAYLEVEL);
    columnInfo.setSize(-1);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    Integer t = null;
    Enumeration en = dt.selection();
    t = (Integer) ((Dynamic) en.nextElement()).getRaw("testdisplaylevelcol");
    while (en.hasMoreElements()) {
      assertEquals(t, (Integer) ((Dynamic) en.nextElement())
          .getRaw("testdisplaylevelcol"));
    }

    DisplayLevel t2 = null;
    Enumeration en2 = dt.selection();
    t2 = (DisplayLevel) ((Dynamic) en2.nextElement())
        .getCooked("testdisplaylevelcol");
    while (en2.hasMoreElements()) {
      System.err.println(t2);
      assertEquals(t2, ((DisplayLevel) ((Dynamic) en2.nextElement())
          .getCooked("testdisplaylevelcol")));
    }

    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testdisplaylevelcol").selectionWhereEq(new Integer(0)))
        .size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testdisplaylevelcol").selectionWhereEq(new Integer(0)))
        .size());
    assertEquals(new Integer(0), dt.two().getRaw(
        "testdisplaylevelcol"));
    assertEquals(DisplayLevel.primary, ((DisplayLevel) dt.two()
        .getCooked("testdisplaylevelcol")));
    assertEquals(DisplayLevel.primary, ((DisplayLevel) dt.getObject(0)
        .getCooked("testdisplaylevelcol")));
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitSearchability() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testsearchabilitycol");
    columnInfo.setDisplayname("Test searchability Column");
    columnInfo.setDisplayorder(99);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Searchability column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.SEARCHABILITY);
    columnInfo.setSize(-1);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    Integer t = null;
    Enumeration en = dt.selection();
    t = (Integer) ((Dynamic) en.nextElement()).getRaw("testsearchabilitycol");
    while (en.hasMoreElements()) {
      assertEquals(t, (Integer) ((Dynamic) en.nextElement())
          .getRaw("testsearchabilitycol"));
    }

    Searchability t2 = null;
    Enumeration en2 = dt.selection();
    t2 = (Searchability) ((Dynamic) en2.nextElement())
        .getCooked("testsearchabilitycol");
    while (en2.hasMoreElements()) {
      System.err.println(t2);
      assertEquals(t2, ((Searchability) ((Dynamic) en2.nextElement())
          .getCooked("testsearchabilitycol")));
    }

    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testsearchabilitycol").selectionWhereEq(new Integer(0)))
        .size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testsearchabilitycol").selectionWhereEq(new Integer(0)))
        .size());
    assertEquals(new Integer(0), dt.two().getRaw(
        "testsearchabilitycol"));
    assertEquals(Searchability.primary, ((Searchability) dt.two()
        .getCooked("testsearchabilitycol")));
    assertEquals(Searchability.primary, ((Searchability) dt.getObject(0)
        .getCooked("testsearchabilitycol")));
    columnInfo.delete();
    getDb().setLogSQL(false);
  }

  /**
   * @see org.melati.poem.Table#addColumnAndCommit(ColumnInfo)
   */
  public void testAddColumnAndCommitIntegrityfix() {
    DynamicTable dt = ((EverythingDatabase)getDb()).getDynamicTable();
    ColumnInfo columnInfo = (ColumnInfo) getDb().getColumnInfoTable()
        .newPersistent();
    TableInfo ti = dt.getTableInfo();
    columnInfo.setTableinfo(ti);
    columnInfo.setName("testintegrityfixcol");
    columnInfo.setDisplayname("Test Integrityfix Column");
    columnInfo.setDisplayorder(99);
    columnInfo.setSearchability(Searchability.yes);
    columnInfo.setIndexed(false);
    columnInfo.setUnique(false);
    columnInfo.setDescription("A non-nullable extra Integrityfix column");
    columnInfo.setUsercreateable(true);
    columnInfo.setUsereditable(true);
    columnInfo.setTypefactory(PoemTypeFactory.INTEGRITYFIX);
    columnInfo.setSize(-1);
    columnInfo.setWidth(20);
    columnInfo.setHeight(1);
    columnInfo.setPrecision(0);
    columnInfo.setScale(0);
    columnInfo.setNullable(false);
    columnInfo.setDisplaylevel(DisplayLevel.record);
    columnInfo.makePersistent();
    getDb().setLogSQL(true);
    columnInfo.getTableinfo().actualTable().addColumnAndCommit(columnInfo);
    Integer t = null;
    Enumeration en = dt.selection();
    t = (Integer) ((Dynamic) en.nextElement()).getRaw("testIntegrityfixcol");
    while (en.hasMoreElements()) {
      assertEquals(t, (Integer) ((Dynamic) en.nextElement())
          .getRaw("testIntegrityfixcol"));
    }

    IntegrityFix t2 = null;
    Enumeration en2 = dt.selection();
    t2 = (IntegrityFix) ((Dynamic) en2.nextElement())
        .getCooked("testIntegrityfixcol");
    while (en2.hasMoreElements()) {
      System.err.println(t2);
      assertEquals(t2, ((IntegrityFix) ((Dynamic) en2.nextElement())
          .getCooked("testIntegrityfixcol")));
    }

    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testIntegrityfixcol").selectionWhereEq(new Integer(2)))
        .size());
    PoemThread.commit();
    assertEquals(2, EnumUtils.vectorOf(
        dt.getColumn("testIntegrityfixcol").selectionWhereEq(new Integer(2)))
        .size());
    assertEquals(new Integer(2), dt.two().getRaw(
        "testIntegrityfixcol"));
    assertEquals(StandardIntegrityFix.prevent, ((IntegrityFix) dt
        .two().getCooked("testIntegrityfixcol")));
    assertEquals(StandardIntegrityFix.prevent, ((IntegrityFix) dt.getObject(0)
        .getCooked("testIntegrityfixcol")));
    columnInfo.delete();
    getDb().setLogSQL(false);
  }


}