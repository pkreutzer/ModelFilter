package kreutzer.modelview;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;

import java.lang.reflect.Field;
import java.lang.annotation.Annotation;

public final class ViewFilter {

  // ==================================================================================
  //       F L U E N T   I N T E R F A C E
  // ==================================================================================

  private final Set<Class<?>> filteredClasses;
  private final Set<Class<? extends View>> views;

  private final Map<Object, Object> filteredObjects;

  private ViewFilter() {
    this.filteredClasses = new HashSet<Class<?>>();
    this.views = new HashSet<Class<? extends View>>();
    this.filteredObjects = new HashMap<Object, Object>();
  }

  public static final ViewFilter buildFilter() {
    return new ViewFilter();
  }

  public final ViewFilter forClass(final Class<?> filteredClass) {
    this.filteredClasses.add(filteredClass);
    return this;
  }

  public final ViewFilter forClasses(final Class<?>... filteredClasses) {
    for (final Class<?> filteredClass : filteredClasses) {
      this.filteredClasses.add(filteredClass);
    }

    return this;
  }

  public final ViewFilter useView(final Class<? extends View> view) {
    this.views.add(view);
    return this;
  }

  @SafeVarargs
  public final ViewFilter useViews(final Class<? extends View>... views) {
    for (final Class<? extends View> view : views) {
      this.views.add(view);
    }

    return this;
  }

  @SuppressWarnings("unchecked")
  public final <T> T applyTo(final T objectToFilter) {
    if (objectToFilter == null) {
      return null;
    }

    if (this.filteredObjects.containsKey(objectToFilter)) {
      return (T) this.filteredObjects.get(objectToFilter);
    }

    final Class<?> theClass = objectToFilter.getClass();

    // TODO arrays

    if (objectToFilter instanceof Collection) {
      try {
        final Collection clonedCollection = (Collection) objectToFilter.getClass().getConstructor().newInstance();

        // save cloned object to map so that we can re-use it in case of cycles
        this.filteredObjects.put(objectToFilter, clonedCollection);

        final Collection originalCollection = (Collection) objectToFilter;
        for (final Object element : originalCollection) {
          final Object clonedElement = applyTo(element);
          clonedCollection.add(clonedElement);
        }

        return (T) clonedCollection;
      } catch (final Throwable throwable) {
        // unable to clone object
        throw new CloningFailedException(throwable, objectToFilter);
      }
    } else {
      if (!classShouldBeFiltered(theClass)) {
        return objectToFilter;
      }

      try {
        // create a clone
        // TODO do not clone object if it is already filtered correctly
        final T clone = (T) objectToFilter.getClass().getConstructor().newInstance();

        // save cloned object to map so that we can re-use it in case of cycles
        this.filteredObjects.put(objectToFilter, clone);

        // copy values to clone
        copyAnnotatedFields(objectToFilter, clone);

        return clone;
      } catch (final Throwable throwable) {
        // unable to clone object
        throw new CloningFailedException(throwable, objectToFilter);
      }
    }
  }

  public final <T> void copyAnnotatedFields(final T from, final T to) throws IllegalAccessException {
    for (Class objectClass = from.getClass();
         !objectClass.equals(Object.class);
         objectClass = objectClass.getSuperclass()) {
      // iterate over all fields
      for (final Field field : objectClass.getDeclaredFields()) {
        field.setAccessible(true);

        // check if field has @InView annotation and matches view
        if (isAnnotatedAccordingToViews(field, views)) {
          // apply the filter to the object the field is referring to
          final Object filteredValue = applyTo(field.get(from));

          // set field's value in clone
          field.set(to, filteredValue);
        } else {
          // set to default value
          final Class<?> fieldType = field.getType();
          if (fieldType.equals(boolean.class)) {
            field.setBoolean(to, false);
          } else if (fieldType.isPrimitive()) {
            // this even works for the other primitive types like long or double
            field.setByte(to, (byte)(0));
          } else {
            field.set(to, null);
          }
        }
      }
    }
  }

  private final boolean classShouldBeFiltered(Class<?> theClass) {
    while (!theClass.equals(Object.class)) {
      if (this.filteredClasses.contains(theClass)) {
        return true;
      }
      theClass = theClass.getSuperclass();
    }

    return false;
  }

  public static final boolean viewsDoMatch(final Class<? extends View> annotatedView,
                                           final Set<Class<? extends View>> views) {
    for (final Class<? extends View> filterView : views) {
      if (annotatedView.isAssignableFrom(filterView)) {
        return true;
      }
    }

    return false;
  }

  public static final boolean isAnnotatedAccordingToViews(final Field field,
                                                          final Set<Class<? extends View>> views) {
    final InView annotation = field.getAnnotation(InView.class);

    if (annotation == null) {
      return false;
    }

    // found field that is annotated by @InView(...)
    // check if view classes match
    final Class<? extends View> annotationView = annotation.value();
    return viewsDoMatch(annotationView, views);
  }

  // ==================================================================================
  //       S I M P L E   I N T E R F A C E
  // ==================================================================================

  @SafeVarargs
  public static final <T> T filter(final T objectToFilter, final Class<? extends View>... views) {
    if (objectToFilter == null) {
      return null;
    }

    final ViewFilter viewFilter = buildFilter();
    final Class<?> theClass = objectToFilter.getClass();

    viewFilter.forClass(theClass);
    for (final Class<? extends View> view : views) {
      viewFilter.useView(view);
    }

    return viewFilter.applyTo(objectToFilter);
  }

}
