package org.melati.poem.test;

import org.melati.poem.*;

import java.util.Enumeration;

/**
 * @author timp
 * @since 21 Dec 2006
 *
 */
public class NotNullableReferencePoemTypeTest extends SQLPoemTypeSpec<Integer> {

  public NotNullableReferencePoemTypeTest() {
  }

  public NotNullableReferencePoemTypeTest(String name) {
    super(name);
  }

  void setObjectUnderTest() {
    it = new ReferencePoemType(getDb().getCapabilityTable(), false);
  }

  /**
   * Test method for {@link org.melati.poem.SQLType#quotedRaw(java.lang.Object)}.
   */
  public void testQuotedRaw() {
    assertEquals(((SQLPoemType<Integer>)it).sqlDefaultValue(getDb().getDbms()), 
        ((SQLPoemType<Integer>)it).quotedRaw(
            ((SQLPoemType<Integer>)it).rawOfString(
                ((SQLPoemType<Integer>)it).sqlDefaultValue(getDb().getDbms()))));

  }
  
  public void testAssertValidCooked() {
    super.testAssertValidCooked();
    Group g = (Group)getDb().getGroupTable().firstSelection(null);
    try {
      it.assertValidCooked(g);
      fail("Should have blown up");
    } catch (ValidationPoemException e) { 
      e = null;
    }
  }

  public void testPossibleRaws() {
    super.testPossibleRaws();
    Enumeration<Integer> them = it.possibleRaws();
    them = it.possibleRaws();
    int count = 0;
    while(them.hasMoreElements()) {
      them.nextElement();
      count++;
    }
    if (it.getNullable())
      assertEquals(6,count);
    else {
      Enumeration<Integer> them2 = it.possibleRaws();
      while (them2.hasMoreElements()) {
        Integer raw = (Integer)them2.nextElement();
        System.err.println(raw + 
            ((Capability)getDb().getCapabilityTable().getObject(raw.intValue())).getName());
        
      }
     assertEquals(5,count);
    }
    
  }

  public void testRawOfCooked() {
    super.testRawOfCooked();
    assertEquals(new Integer(0), it.rawOfCooked(getDb().administerCapability()));
    
  }

  /**
   * Test method for {@link org.melati.poem.PoemType#toDsdType()}.
   */
  public void testToDsdType() {
    assertEquals("Capability", it.toDsdType()); 

  }

  /**
Test with wrong parameters.   * 
   */
  public void testBadConstructor() {
    try {
      new ReferencePoemType(null, false);
      fail("Should have blown up");
    } catch (NullPointerException e) {
      e = null;
    }
  }

  public void testCanRepresent() {
    DisplayLevelPoemType dlpt = new DisplayLevelPoemType();
    assertNull(it.canRepresent(dlpt));
    assertNull(dlpt.canRepresent(it));

    // Test hack which enables the promotion of an Integer to a ReferenceType
    IntegerPoemType ipt = new IntegerPoemType(false);
    PoemType pt = it.canRepresent(ipt);
    assertNotNull(pt);
  }


}
