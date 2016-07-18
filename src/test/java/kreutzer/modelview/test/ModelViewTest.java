package kreutzer.modelview.test;

import kreutzer.modelview.*;
import static kreutzer.modelview.ViewFilter.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;
import java.lang.reflect.Field;

public final class ModelViewTest {

  public static interface PublicString extends View {}
  public static interface PublicInt extends View {}
  public static interface PrivateDouble extends View {}

  public static interface PublicFloat extends View {}

  public static interface AllFieldsSuperClass extends PublicString, PublicInt, PrivateDouble {}
  public static interface AllFieldsSubClass extends AllFieldsSuperClass, PublicFloat {}

  public static class SuperClass {
    @InView(PublicString.class)
    public String publicString;

    @InView(PublicInt.class)
    public int publicInt;

    @InView(PrivateDouble.class)
    private double privateDouble;

    public SuperClass() {
      // intentionally left blank
    }

    public SuperClass(final String publicString, final int publicInt, final double privateDouble) {
      this.publicString = publicString;
      this.publicInt = publicInt;
      this.privateDouble = privateDouble;
    }

    public final double getPrivateDouble() {
      return this.privateDouble;
    }

    public final void setPrivateDouble(final double privateDouble) {
      this.privateDouble = privateDouble;
    }

    protected static final boolean doStringsMatch(final String stringOne, final String stringTwo) {
      if (stringOne == stringTwo) {
        return true;
      }
      if (stringOne == null) {
        return false;
      }
      return stringOne.equals(stringTwo);
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof SuperClass)) {
        return false;
      }
      
      final SuperClass otherSuperClass = (SuperClass)other;

      return doStringsMatch(this.publicString, otherSuperClass.publicString)
              && this.publicInt == otherSuperClass.publicInt
              && this.privateDouble == otherSuperClass.privateDouble;
    }

    @Override
    public String toString() {
      return String.format("{%s, %d, %f}", this.publicString, this.publicInt, this.privateDouble);
    }
  }

  public static final class SubClass extends SuperClass {
    @InView(PublicFloat.class)
    public float publicFloat;

    public SubClass() {
      super();
    }

    public SubClass(final float publicFloat) {
      super();
      this.publicFloat = publicFloat;
    }

    public SubClass(final String publicString, final int publicInt, final double privateDouble,
                    final float publicFloat) {
      super(publicString, publicInt, privateDouble);
      this.publicFloat = publicFloat;
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof SubClass)) {
        return false;
      }
      
      final SubClass otherSubClass = (SubClass)other;

      return super.equals(other)
              && this.publicFloat == otherSubClass.publicFloat;
    }

    @Override
    public String toString() {
      return String.format("{%s, {%f}}", super.toString(), this.publicFloat);
    }
  }

  @Test
  public final void testAnnotatedFieldsAreDetectedCorrectlySuperClassAll() {
    final SuperClass objectToClone = new SuperClass();
    final List<Field> annotatedFields = findAnnotatedFields(objectToClone, AllFieldsSuperClass.class);
    assertEquals("Number of detected fields does not match.", 3, annotatedFields.size());
  }

  @Test
  public final void testAnnotatedFieldsAreDetectedCorrectlySuperClassPublicString() {
    final SuperClass objectToClone = new SuperClass();
    final List<Field> annotatedFields = findAnnotatedFields(objectToClone, PublicString.class);
    assertEquals("Number of detected fields does not match.", 1, annotatedFields.size());
  }

  @Test
  public final void testAnnotatedFieldsAreDetectedCorrectlySuperClassPublicInt() {
    final SuperClass objectToClone = new SuperClass();
    final List<Field> annotatedFields = findAnnotatedFields(objectToClone, PublicInt.class);
    assertEquals("Number of detected fields does not match.", 1, annotatedFields.size());
  }

  @Test
  public final void testAnnotatedFieldsAreDetectedCorrectlySuperClassPrivateDouble() {
    final SuperClass objectToClone = new SuperClass();
    final List<Field> annotatedFields = findAnnotatedFields(objectToClone, PrivateDouble.class);
    assertEquals("Number of detected fields does not match.", 1, annotatedFields.size());
  }

  @Test
  public final void testAnnotatedFieldsAreDetectedCorrectlySubClassAll() {
    final SubClass objectToClone = new SubClass();
    final List<Field> annotatedFields = findAnnotatedFields(objectToClone, AllFieldsSubClass.class);
    assertEquals("Number of detected fields does not match.", 4, annotatedFields.size());
  }

  @Test
  public final void testAnnotatedFieldsAreDetectedCorrectlySubClassSuperClassFields() {
    final SubClass objectToClone = new SubClass();
    final List<Field> annotatedFields = findAnnotatedFields(objectToClone, AllFieldsSuperClass.class);
    assertEquals("Number of detected fields does not match.", 3, annotatedFields.size());
  }

  @Test
  public final void testAnnotatedFieldsAreDetectedCorrectlySubClassPublicFloat() {
    final SubClass objectToClone = new SubClass();
    final List<Field> annotatedFields = findAnnotatedFields(objectToClone, PublicFloat.class);
    assertEquals("Number of detected fields does not match.", 1, annotatedFields.size());
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassAll() {
    final SuperClass objectToClone = new SuperClass("string", 13, 3.);
    final SuperClass clonedObject = filter(objectToClone, AllFieldsSuperClass.class);
    final SuperClass expected = objectToClone;
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassPublicString() {
    final SuperClass objectToClone = new SuperClass("string", 13, 3.);
    final SuperClass clonedObject = filter(objectToClone, PublicString.class);
    final SuperClass expected = new SuperClass("string", 0, 0.);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassPublicInt() {
    final SuperClass objectToClone = new SuperClass("string", 13, 3.);
    final SuperClass clonedObject = filter(objectToClone, PublicInt.class);
    final SuperClass expected = new SuperClass(null, 13, 0.);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassPrivateDouble() {
    final SuperClass objectToClone = new SuperClass("string", 13, 3.);
    final SuperClass clonedObject = filter(objectToClone, PrivateDouble.class);
    final SuperClass expected = new SuperClass(null, 0, 3.);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySubClassAll() {
    final SubClass objectToClone = new SubClass("string", 13, 3., 1991.f);
    final SubClass clonedObject = filter(objectToClone, AllFieldsSubClass.class);
    final SubClass expected = objectToClone;
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySubClassSuperClassFields() {
    final SubClass objectToClone = new SubClass("string", 13, 3., 1991.f);
    final SubClass clonedObject = filter(objectToClone, AllFieldsSuperClass.class);
    final SubClass expected = new SubClass("string", 13, 3., 0.f);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySubClassPublicFloat() {
    final SubClass objectToClone = new SubClass("string", 13, 3., 1991.f);
    final SubClass clonedObject = filter(objectToClone, PublicFloat.class);
    final SubClass expected = new SubClass(null, 0, 0., 1991.f);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }
  
}
