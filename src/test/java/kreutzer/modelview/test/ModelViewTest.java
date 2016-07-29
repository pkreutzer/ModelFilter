package kreutzer.modelview.test;

import kreutzer.modelview.*;
import static kreutzer.modelview.ViewFilter.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Field;

public final class ModelViewTest {

  public static interface PublicString extends View {}
  public static interface PublicInt extends View {}
  public static interface PrivateDouble extends View {}
  public static interface SuperOther extends View {}

  public static interface PublicFloat extends View {}
  public static interface SubOther extends View {}
  
  public static interface AllFieldsSuperClass extends PublicString, PublicInt, PrivateDouble, SuperOther {}
  public static interface AllFieldsSubClass extends AllFieldsSuperClass, PublicFloat, SubOther {}

  public static interface FirstFieldOtherClass extends View {}
  public static interface SecondFieldOtherClass extends View {}
  public static interface AllFieldsOtherClass extends FirstFieldOtherClass, SecondFieldOtherClass {}

  public static interface ContainerList extends View {}
  public static interface ContainerSet extends View {}

  public static interface AllFieldsCollectionContainer extends ContainerList, ContainerSet {};

  public static interface OneDimensionalArrayContainer extends View {};
  public static interface TwoDimensionalArrayContainer extends View {};
  public static interface OneDimensionalPrimitiveArrayContainer extends View {};
  public static interface TwoDimensionalPrimitiveArrayContainer extends View {};

  public static interface Children extends View {}
  public static interface Parent extends View {}

  public static interface NoFieldsDefaultValues extends View {}
  public static interface AllFieldsDefaultValues extends View {}

  public static class SuperClass {

    @InView(PublicString.class)
    public String publicString;

    @InView(PublicInt.class)
    public int publicInt;

    @InView(PrivateDouble.class)
    private double privateDouble;

    @InView(SuperOther.class)
    public OtherClass superOther; 

    public SuperClass() {
      // intentionally left blank
    }

    public SuperClass(final String publicString, final int publicInt,
                      final double privateDouble, final OtherClass superOther) {
      this.publicString = publicString;
      this.publicInt = publicInt;
      this.privateDouble = privateDouble;
      this.superOther = superOther;
    }

    public final double getPrivateDouble() {
      return this.privateDouble;
    }

    public final void setPrivateDouble(final double privateDouble) {
      this.privateDouble = privateDouble;
    }

    protected final boolean doOthersMatch(final OtherClass otherOne, final OtherClass otherTwo) {
      if (otherOne == null && otherTwo == null) {
        return true;
      } else if (otherOne == null) {
        return false;
      }
      return otherOne.equals(otherTwo);
    }

    @Override
    public boolean equals(final Object other) {
      if (!(other instanceof SuperClass)) {
        return false;
      }
      
      final SuperClass otherSuperClass = (SuperClass)other;

      return ModelViewTest.doObjectsMatch(this.publicString, otherSuperClass.publicString)
              && this.publicInt == otherSuperClass.publicInt
              && this.privateDouble == otherSuperClass.privateDouble
              && doOthersMatch(this.superOther, otherSuperClass.superOther);
    }

    @Override
    public String toString() {
      return String.format("SUPER:{%s, %d, %f, %s}",
                          this.publicString, this.publicInt, this.privateDouble, this.superOther);
    }

  }

  public static final class SubClass extends SuperClass {

    @InView(PublicFloat.class)
    public float publicFloat;

    @InView(SubOther.class)
    public OtherClass subOther;

    public SubClass() {
      super();
    }

    public SubClass(final float publicFloat, final OtherClass subOther) {
      super();
      this.publicFloat = publicFloat;
      this.subOther = subOther;
    }

    public SubClass(final String publicString, final int publicInt, final double privateDouble,
                    final OtherClass superOther, final float publicFloat, final OtherClass subOther) {
      super(publicString, publicInt, privateDouble, superOther);
      this.publicFloat = publicFloat;
      this.subOther = subOther;
    }

    @Override
    public boolean equals(final Object other) {
      if (!(other instanceof SubClass)) {
        return false;
      }
      
      final SubClass otherSubClass = (SubClass)other;

      return super.equals(other)
              && this.publicFloat == otherSubClass.publicFloat
              && super.doOthersMatch(this.subOther, otherSubClass.subOther);
    }

    @Override
    public String toString() {
      return String.format("SUB:{{%s}, %f, %s}", super.toString(), this.publicFloat, this.subOther);
    }

  }

  public static final class OtherClass {

    @InView(FirstFieldOtherClass.class)
    public String firstField;

    @InView(SecondFieldOtherClass.class)
    public String secondField;

    public OtherClass() {
      /* intentionally left blank */
    }

    public OtherClass(final String firstField, final String secondField) {
      this.firstField = firstField;
      this.secondField = secondField;
    }

    @Override
    public boolean equals(final Object other) {
      if (!(other instanceof OtherClass)) {
        return false;
      }

      final OtherClass otherOtherClass = (OtherClass) other;

      return ModelViewTest.doObjectsMatch(this.firstField, otherOtherClass.firstField) &&
             ModelViewTest.doObjectsMatch(this.secondField, otherOtherClass.secondField);
    }

    @Override
    public int hashCode() {
      int hash = 4711;

      if (this.firstField != null) {
        hash += this.firstField.hashCode();
      }

      if (this.secondField != null) {
        hash += this.secondField.hashCode();
      }

      return hash;
    }

    @Override
    public String toString() {
      return String.format("OTHER:{%s, %s}", this.firstField, this.secondField);
    }

  }

  public static final class CollectionContainer {
    @InView(ContainerList.class)
    public List<OtherClass> list;

    @InView(ContainerSet.class)
    public Set<OtherClass> set;

    public CollectionContainer() {
      /* intentionally left blank */
    }

    public CollectionContainer(final List<OtherClass> list, final Set<OtherClass> set) {
      this.list = list;
      this.set = set;
    }

    @Override
    public boolean equals(final Object other) {
      if (!(other instanceof CollectionContainer)) {
        return false;
      }

      final CollectionContainer otherCollectionContainer = (CollectionContainer) other;

      return doObjectsMatch(this.list, otherCollectionContainer.list) &&
             doObjectsMatch(this.set, otherCollectionContainer.set);
    }

    @Override
    public String toString() {
      return String.format("COLLECTION_CONTAINER:{%s, %s}", this.list, this.set);
    }
  }

  public static final class ArrayContainer {
    @InView(OneDimensionalArrayContainer.class)
    public OtherClass[] oneDimensionalArray;

    @InView(TwoDimensionalArrayContainer.class)
    public OtherClass[][] twoDimensionalArray;

    @InView(OneDimensionalPrimitiveArrayContainer.class)
    public int[] oneDimensionalPrimitiveArray;

    @InView(TwoDimensionalPrimitiveArrayContainer.class)
    public int[][] twoDimensionalPrimitiveArray;

    public ArrayContainer() {
      /* intentionally left blank */
    }

    public ArrayContainer(final OtherClass[] oneDimensionalArray,
                          final OtherClass[][] twoDimensionalArray,
                          final int[] oneDimensionalPrimitiveArray,
                          final int[][] twoDimensionalPrimitiveArray) {
      this.oneDimensionalArray = oneDimensionalArray;
      this.twoDimensionalArray = twoDimensionalArray;
      this.oneDimensionalPrimitiveArray = oneDimensionalPrimitiveArray;
      this.twoDimensionalPrimitiveArray = twoDimensionalPrimitiveArray;
    }

    @Override
    public final boolean equals(final Object other) {
      if (!(other instanceof ArrayContainer)) {
        return false;
      }

      final ArrayContainer otherArrayContainer = (ArrayContainer) other;

      return Arrays.equals(this.oneDimensionalArray, otherArrayContainer.oneDimensionalArray) &&
             Arrays.deepEquals(this.twoDimensionalArray, otherArrayContainer.twoDimensionalArray) &&
             Arrays.equals(this.oneDimensionalPrimitiveArray, otherArrayContainer.oneDimensionalPrimitiveArray) &&
             Arrays.deepEquals(this.twoDimensionalPrimitiveArray, otherArrayContainer.twoDimensionalPrimitiveArray);
    }

    @Override
    public final String toString() {
      return String.format("ARRAY_CONTAINER:{%s, %s, %s, %s}",
                           Arrays.toString(this.oneDimensionalArray),
                           Arrays.deepToString(this.twoDimensionalArray),
                           Arrays.toString(this.oneDimensionalPrimitiveArray),
                           Arrays.deepToString(this.twoDimensionalPrimitiveArray));
    }
  }

  public static final class CycleClass {
    @InView(Children.class)
    public List<CycleClass> children;

    @InView(Parent.class)
    public CycleClass parent;

    public CycleClass() {
      /* intentionally left blank */
    }

    public CycleClass(final List<CycleClass> children, final CycleClass parent) {
      this.children = children;
      this.parent = parent;
    }

    @Override
    public boolean equals(final Object other) {
      if (!(other instanceof CycleClass)) {
        return false;
      }

      final CycleClass otherCycleClass = (CycleClass) other;

      // do not test parent as this leads to an endless recursion
      return doObjectsMatch(this.children, otherCycleClass.children);
    }

    public final boolean parentsCorrect() {
      if (this.children != null) {
        for (final CycleClass child : this.children) {
          if (child.parent != this) {
            return false;
          }
          if (!child.parentsCorrect()) {
            return false;
          }
        }
      }

      return true;
    }

    @Override
    public String toString() {
      final StringBuilder builder = new StringBuilder();

      builder.append("CYCLE_CLASS:{");

      if (this.children != null) {
        boolean first = true;
        for (final CycleClass child : this.children) {
          if (!first) {
            builder.append(", ");
          }

          first = false;
          builder.append(child);
        }
      } else {
        builder.append("null");
      }

      builder.append("}");

      return builder.toString();
    }
  }

  public static final class DefaultValues {
    @InView(AllFieldsDefaultValues.class)
    public String stringOne;

    @InView(AllFieldsDefaultValues.class)
    public String stringTwo = "DEFAULT_VALUE_TWO";

    @InView(AllFieldsDefaultValues.class)
    public double doubleOne;

    @InView(AllFieldsDefaultValues.class)
    public double doubleTwo = -13.03;

    public DefaultValues() {
      this.stringOne = "DEFAULT_VALUE_ONE";
      this.doubleOne = 13.03;
    }

    public DefaultValues(final String stringOne, final String stringTwo,
                         final double doubleOne, final double doubleTwo) {
      this.stringOne = stringOne;
      this.stringTwo = stringTwo;
      this.doubleOne = doubleOne;
      this.doubleTwo = doubleTwo;
    }

    @Override
    public final boolean equals(final Object other) {
      if (!(other instanceof DefaultValues)) {
        return false;
      }

      final DefaultValues otherDefaultValues = (DefaultValues) other;

      return doObjectsMatch(this.stringOne, otherDefaultValues.stringOne) &&
             doObjectsMatch(this.stringTwo, otherDefaultValues.stringTwo) &&
             this.doubleOne == otherDefaultValues.doubleOne &&
             this.doubleTwo == otherDefaultValues.doubleTwo;
    }

    @Override
    public final String toString() {
      return String.format("DEFAULT_VALUES:{%s, %s, %f, %f}",
                           this.stringOne, this.stringTwo,
                           this.doubleOne, this.doubleTwo);
    }
  }


  // ==================================================================================
  //       H E L P E R   M E T H O D S
  // ==================================================================================


  public static final boolean doObjectsMatch(final Object objectOne, final Object objectTwo) {
    if (objectOne == objectTwo) {
      return true;
    }
    if (objectOne == null) {
      return false;
    }
    return objectOne.equals(objectTwo);
  }

  @SafeVarargs
  public static final <T> Set<T> asSet(final T... elements) {
    final Set<T> set = new HashSet<T>();

    for (final T element : elements) {
      set.add(element);
    }

    return set;
  }


  // ==================================================================================
  //       T E S T S
  // ==================================================================================


  @Test
  public final void testDoMatchSame() {
    assertTrue("same views do not match",
               viewsDoMatch(PublicString.class, asSet(PublicString.class)));
  }

  @Test
  public final void testDoMatchSameAndOther() {
    assertTrue("same views (and other) do not match",
               viewsDoMatch(PublicString.class, asSet(PublicString.class, PublicFloat.class)));
  }

  @Test
  public final void testDoMatchSameAndOtherDifferentOrder() {
    assertTrue("same views (and other, different order) do not match",
               viewsDoMatch(PublicString.class, asSet(PublicFloat.class, PublicString.class)));
  }

  @Test
  public final void testDoMatchSubclass() {
    assertTrue("subclass view does not match superclass view",
               viewsDoMatch(PublicString.class, asSet(AllFieldsSuperClass.class)));
  }

  @Test
  public final void testDoNotMatchOther() {
    assertFalse("superclass view matches subclass view",
                viewsDoMatch(PublicString.class, asSet(PublicFloat.class)));
  }

  @Test
  public final void testDoNotMatchSuperclass() {
    assertFalse("superclass view matches subclass view",
                viewsDoMatch(AllFieldsSuperClass.class, asSet(PublicString.class)));
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassAll() {
    final SuperClass objectToClone = new SuperClass("string", 13, 3., null);
    final SuperClass clonedObject = filter(objectToClone, AllFieldsSuperClass.class);
    final SuperClass expected = objectToClone;
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlyMultiple() {
    final SuperClass objectToClone = new SuperClass("string", 13, 3., null);
    final SuperClass clonedObject = filter(objectToClone, PublicString.class, PrivateDouble.class);
    final SuperClass expected = new SuperClass("string", 0, 3., null);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlyMultipleAndOther() {
    final SuperClass objectToClone = new SuperClass("string", 13, 3., null);
    final SuperClass clonedObject = filter(objectToClone, PublicString.class, PrivateDouble.class, PublicFloat.class);
    final SuperClass expected = new SuperClass("string", 0, 3., null);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlyNoMatches() {
    final SuperClass objectToClone = new SuperClass("string", 13, 3., null);
    final SuperClass clonedObject = filter(objectToClone, PublicFloat.class);
    final SuperClass expected = new SuperClass(null, 0, 0., null);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassPublicString() {
    final SuperClass objectToClone = new SuperClass("string", 13, 3., null);
    final SuperClass clonedObject = filter(objectToClone, PublicString.class);
    final SuperClass expected = new SuperClass("string", 0, 0., null);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassPublicInt() {
    final SuperClass objectToClone = new SuperClass("string", 13, 3., null);
    final SuperClass clonedObject = filter(objectToClone, PublicInt.class);
    final SuperClass expected = new SuperClass(null, 13, 0., null);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassPrivateDouble() {
    final SuperClass objectToClone = new SuperClass("string", 13, 3., null);
    final SuperClass clonedObject = filter(objectToClone, PrivateDouble.class);
    final SuperClass expected = new SuperClass(null, 0, 3., null);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySubClassAll() {
    final SubClass objectToClone = new SubClass("string", 13, 3., null, 1991.f, null);
    final SubClass clonedObject = filter(objectToClone, AllFieldsSubClass.class);
    final SubClass expected = objectToClone;
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySubClassSuperClassFields() {
    final SubClass objectToClone = new SubClass("string", 13, 3., null, 1991.f, null);
    final SubClass clonedObject = filter(objectToClone, AllFieldsSuperClass.class);
    final SubClass expected = new SubClass("string", 13, 3., null, 0.f, null);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySubClassPublicFloat() {
    final SubClass objectToClone = new SubClass("string", 13, 3., null, 1991.f, null);
    final SubClass clonedObject = filter(objectToClone, PublicFloat.class);
    final SubClass expected = new SubClass(null, 0, 0., null, 1991.f, null);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassAllOtherNotFiltered() {
    final OtherClass otherClass = new OtherClass("first", "second");
    final SuperClass objectToClone = new SuperClass("string", 13, 3., otherClass);
    final SuperClass clonedObject = filter(objectToClone, AllFieldsSuperClass.class);
    final SuperClass expected = objectToClone;
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassAllOtherNotFilteredFluent() {
    final OtherClass otherClass = new OtherClass("first", "second");
    final SuperClass objectToClone = new SuperClass("string", 13, 3., otherClass);

    final SuperClass clonedObject = buildFilter().
                                      forClass(SuperClass.class).
                                      useView(AllFieldsSuperClass.class).
                                      applyTo(objectToClone);

    final SuperClass expected = new SuperClass("string", 13, 3., new OtherClass("first", "second"));

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassAllOtherFilteredEmpty() {
    final OtherClass otherClass = new OtherClass("first", "second");
    final SuperClass objectToClone = new SuperClass("string", 13, 3., otherClass);

    final SuperClass clonedObject = buildFilter().
                                      forClasses(SuperClass.class, OtherClass.class).
                                      useView(AllFieldsSuperClass.class).
                                      applyTo(objectToClone);

    final SuperClass expected = new SuperClass("string", 13, 3., new OtherClass(null, null));

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassAllOtherFilteredFirst() {
    final OtherClass otherClass = new OtherClass("first", "second");
    final SuperClass objectToClone = new SuperClass("string", 13, 3., otherClass);

    final SuperClass clonedObject = buildFilter().
                                      forClass(SuperClass.class).
                                      useView(AllFieldsSuperClass.class).
                                      forClass(OtherClass.class).
                                      useView(FirstFieldOtherClass.class).
                                      applyTo(objectToClone);

    final SuperClass expected = new SuperClass("string", 13, 3., new OtherClass("first", null));

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassAllOtherFilteredSecond() {
    final OtherClass otherClass = new OtherClass("first", "second");
    final SuperClass objectToClone = new SuperClass("string", 13, 3., otherClass);

    final SuperClass clonedObject = buildFilter().
                                      forClass(SuperClass.class).
                                      useView(AllFieldsSuperClass.class).
                                      forClass(OtherClass.class).
                                      useView(SecondFieldOtherClass.class).
                                      applyTo(objectToClone);

    final SuperClass expected = new SuperClass("string", 13, 3., new OtherClass(null, "second"));

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testObjectIsClonedCorrectlySuperClassAllOtherFilteredAll() {
    final OtherClass otherClass = new OtherClass("first", "second");
    final SuperClass objectToClone = new SuperClass("string", 13, 3., otherClass);

    final SuperClass clonedObject = buildFilter().
                                      forClass(SuperClass.class).
                                      useView(AllFieldsSuperClass.class).
                                      forClass(OtherClass.class).
                                      useView(AllFieldsOtherClass.class).
                                      applyTo(objectToClone);

    final SuperClass expected = new SuperClass("string", 13, 3., new OtherClass("first", "second"));

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testListIsClonedCorrectlyElementsNotFiltered() {
    final List<OtherClass> list = new LinkedList<OtherClass>();
    {
      list.add(new OtherClass("first1", "second1"));
      list.add(new OtherClass("first2", "second2"));
    }
    final CollectionContainer objectToClone = new CollectionContainer(list, null);

    final CollectionContainer clonedObject = buildFilter().
                                              forClass(CollectionContainer.class).
                                              useView(AllFieldsCollectionContainer.class).
                                              applyTo(objectToClone);

    final CollectionContainer expected = objectToClone;

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testSetIsClonedCorrectlyElementsNotFiltered() {
    final Set<OtherClass> set = new HashSet<OtherClass>();
    {
      set.add(new OtherClass("first1", "second1"));
      set.add(new OtherClass("first2", "second2"));
    }
    final CollectionContainer objectToClone = new CollectionContainer(null, set);

    final CollectionContainer clonedObject = buildFilter().
                                              forClass(CollectionContainer.class).
                                              useView(AllFieldsCollectionContainer.class).
                                              applyTo(objectToClone);

    final CollectionContainer expected = objectToClone;

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testListIsClonedCorrectlyElementsFilteredAll() {
    final List<OtherClass> list = new LinkedList<OtherClass>();
    {
      list.add(new OtherClass("first1", "second1"));
      list.add(new OtherClass("first2", "second2"));
    }
    final CollectionContainer objectToClone = new CollectionContainer(list, null);

    final CollectionContainer clonedObject = buildFilter().
                                              forClass(CollectionContainer.class).
                                              useView(AllFieldsCollectionContainer.class).
                                              forClass(OtherClass.class).
                                              useView(AllFieldsOtherClass.class).
                                              applyTo(objectToClone);

    final CollectionContainer expected = objectToClone;

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testSetIsClonedCorrectlyElementsFilteredAll() {
    final Set<OtherClass> set = new HashSet<OtherClass>();
    {
      set.add(new OtherClass("first1", "second1"));
      set.add(new OtherClass("first2", "second2"));
    }
    final CollectionContainer objectToClone = new CollectionContainer(null, set);

    final CollectionContainer clonedObject = buildFilter().
                                              forClass(CollectionContainer.class).
                                              useView(AllFieldsCollectionContainer.class).
                                              forClass(OtherClass.class).
                                              useView(AllFieldsOtherClass.class).
                                              applyTo(objectToClone);

    final CollectionContainer expected = objectToClone;

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testListIsClonedCorrectlyElementsFilteredFirstField() {
    final List<OtherClass> list = new LinkedList<OtherClass>();
    {
      list.add(new OtherClass("first1", "second1"));
      list.add(new OtherClass("first2", "second2"));
    }
    final CollectionContainer objectToClone = new CollectionContainer(list, null);

    final CollectionContainer clonedObject = buildFilter().
                                              forClass(CollectionContainer.class).
                                              useView(AllFieldsCollectionContainer.class).
                                              forClass(OtherClass.class).
                                              useView(FirstFieldOtherClass.class).
                                              applyTo(objectToClone);

    final List<OtherClass> filteredList = new LinkedList<OtherClass>();
    {
      filteredList.add(new OtherClass("first1", null));
      filteredList.add(new OtherClass("first2", null));
    }

    final CollectionContainer expected = new CollectionContainer(filteredList, null);

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testOneDimensionalArrayIsClonedCorrectlyElementsFilteredFirstField() {
    final OtherClass[] array = {
                                 new OtherClass("first1", "second1"),
                                 new OtherClass("first2", "second2"),
                               };
    final ArrayContainer objectToClone = new ArrayContainer(array, null, null, null);

    final ArrayContainer clonedObject = buildFilter().
                                          forClass(ArrayContainer.class).
                                          useView(OneDimensionalArrayContainer.class).
                                          forClass(OtherClass.class).
                                          useView(FirstFieldOtherClass.class).
                                          applyTo(objectToClone);

    final OtherClass[] filteredArray = {
                                 new OtherClass("first1", null),
                                 new OtherClass("first2", null),
                               };
    final ArrayContainer expected = new ArrayContainer(filteredArray, null, null, null);

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testTwoDimensionalArrayIsClonedCorrectlyElementsFilteredFirstField() {
    final OtherClass[][] array = {
                                   new OtherClass[] {
                                     new OtherClass("first1", "second1"),
                                     new OtherClass("first2", "second2"),
                                   },
                                   null,
                                   new OtherClass[] {
                                     new OtherClass("first3", "second3"),
                                   },
                                 };
    final ArrayContainer objectToClone = new ArrayContainer(null, array, null, null);

    final ArrayContainer clonedObject = buildFilter().
                                          forClass(ArrayContainer.class).
                                          useView(TwoDimensionalArrayContainer.class).
                                          forClass(OtherClass.class).
                                          useView(FirstFieldOtherClass.class).
                                          applyTo(objectToClone);

    final OtherClass[][] filteredArray = {
                                   new OtherClass[] {
                                     new OtherClass("first1", null),
                                     new OtherClass("first2", null),
                                   },
                                   null,
                                   new OtherClass[] {
                                     new OtherClass("first3", null),
                                   },
                                 };
    final ArrayContainer expected = new ArrayContainer(null, filteredArray, null, null);

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testOneDimensionalPrimitiveArrayIsClonedCorrectly() {
    final int[] array = { 13, 3, 1991 };
    final ArrayContainer objectToClone = new ArrayContainer(null, null, array, null);

    final ArrayContainer clonedObject = buildFilter().
                                          forClass(ArrayContainer.class).
                                          useView(OneDimensionalPrimitiveArrayContainer.class).
                                          applyTo(objectToClone);

    final ArrayContainer expected = new ArrayContainer(null, null, array, null);

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testTwoDimensionalPrimitiveArrayIsClonedCorrectly() {
    final int[][] array = {
                            new int[] { 13, 3 },
                            null,
                            new int[] { 1991 }
                          };
    final ArrayContainer objectToClone = new ArrayContainer(null, null, null, array);

    final ArrayContainer clonedObject = buildFilter().
                                          forClass(ArrayContainer.class).
                                          useView(TwoDimensionalPrimitiveArrayContainer.class).
                                          applyTo(objectToClone);

    final ArrayContainer expected = new ArrayContainer(null, null, null, array);

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testSetIsClonedCorrectlyElementsFilteredFirstField() {
    final Set<OtherClass> set = new HashSet<OtherClass>();
    {
      set.add(new OtherClass("first1", "second1"));
      set.add(new OtherClass("first2", "second2"));
    }
    final CollectionContainer objectToClone = new CollectionContainer(null, set);

    final CollectionContainer clonedObject = buildFilter().
                                              forClass(CollectionContainer.class).
                                              useView(AllFieldsCollectionContainer.class).
                                              forClass(OtherClass.class).
                                              useView(FirstFieldOtherClass.class).
                                              applyTo(objectToClone);

    final Set<OtherClass> filteredSet = new HashSet<OtherClass>();
    {
      filteredSet.add(new OtherClass("first1", null));
      filteredSet.add(new OtherClass("first2", null));
    }

    final CollectionContainer expected = new CollectionContainer(null, filteredSet);

    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

  @Test
  public final void testCyclesInObjectGraph() {
    final CycleClass parent = new CycleClass();

    final CycleClass childOne = new CycleClass(null, parent);
    final CycleClass childTwo = new CycleClass(null, parent);

    parent.children = new ArrayList<CycleClass>();
    parent.children.add(childOne);
    parent.children.add(childTwo);

    assertTrue("References to parents (original) are not correct.", parent.parentsCorrect());

    final CycleClass clonedObject = buildFilter().
                                      forClass(CycleClass.class).
                                      useViews(Children.class, Parent.class).
                                      applyTo(parent);

    final CycleClass expected = parent;

    assertEquals("Clone does not match expected object.", expected, clonedObject);
    assertTrue("References to parents (cloned) are not correct.", clonedObject.parentsCorrect());
  }

  @Test
  public final void testCyclesInObjectGraphNoParents() {
    final CycleClass parent = new CycleClass();

    final CycleClass childOne = new CycleClass(null, parent);
    final CycleClass childTwo = new CycleClass(null, parent);

    parent.children = new ArrayList<CycleClass>();
    parent.children.add(childOne);
    parent.children.add(childTwo);

    assertTrue("References to parents (original) are not correct.", parent.parentsCorrect());

    final CycleClass clonedObject = buildFilter().
                                      forClass(CycleClass.class).
                                      useViews(Children.class).
                                      applyTo(parent);

    final CycleClass expected = parent;

    assertEquals("Clone does not match expected object.", expected, clonedObject);
    // we do not include the parent references -> parent references are not correct in clone
    assertFalse("References to parents (cloned) are not correct.", clonedObject.parentsCorrect());
  }

  @Test
  public final void testDefaultValues() {
    final DefaultValues objectToClone = new DefaultValues();
    final DefaultValues clonedObject = filter(objectToClone, NoFieldsDefaultValues.class);
    final DefaultValues expected = new DefaultValues(null, null, 0., 0.);
    assertEquals("Clone does not match expected object.", expected, clonedObject);
  }

}
