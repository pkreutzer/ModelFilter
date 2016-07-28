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

  private ViewFilter() {
    this.filteredClasses = new HashSet<Class<?>>();
    this.views = new HashSet<Class<? extends View>>();
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

    final Class<?> theClass = objectToFilter.getClass();

    // TODO arrays

    if (objectToFilter instanceof Collection) {
      try {
        final Collection clonedCollection = (Collection) objectToFilter.getClass().getConstructor().newInstance();

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

      // find fields that have a matching annotation
      final List<Field> annotatedFields = findAnnotatedFields(objectToFilter, this.views);

      try {
        // create a clone
        // TODO do not clone object if it is already filtered correctly
        final T clone = (T) objectToFilter.getClass().getConstructor().newInstance();

        // iterate over annotated fields and copy values to clone
        for (final Field field : annotatedFields) {
          field.setAccessible(true);

          // apply the filter to the object the field is referring to
          final Object filteredValue = applyTo(field.get(objectToFilter));

          // set field's value in clone
          field.set(clone, filteredValue);
        }

        return clone;
      } catch (final Throwable throwable) {
        // unable to clone object
        throw new CloningFailedException(throwable, objectToFilter);
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

  public static final <T> List<Field> findAnnotatedFields(final T objectToFilter,
                                                          final Set<Class<? extends View>> views) {
    try {
      final List<Field> annotatedFields = new LinkedList<Field>();

      // iterate over all superclasses
      for (Class objectClass = objectToFilter.getClass(); !objectClass.equals(Object.class); objectClass = objectClass.getSuperclass()) {
        // iterate over all fields
        for (final Field field : objectClass.getDeclaredFields()) {
          // check if field has @InView annotation
          final InView annotation = field.getAnnotation(InView.class);
          if (annotation != null) {
            // found field that is annotated by @InView(...)
            // check if view classes match
            final Class<? extends View> annotationView = annotation.value();
            if (viewsDoMatch(annotationView, views)) {
              annotatedFields.add(field);
            }
          }
        }
      }

      return annotatedFields;
    } catch (final Throwable throwable) {
      // unable to find annotated fields
      throw new CloningFailedException(throwable, objectToFilter);
    }
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
