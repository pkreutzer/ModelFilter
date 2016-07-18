package kreutzer.modelview;

import java.util.List;
import java.util.LinkedList;

import java.lang.reflect.Field;
import java.lang.annotation.Annotation;

public final class ViewFilter {

  public static final <T> T filter(final T objectToClone, final Class<? extends View> view) {
    // find fields that are annotated accordingly
    final List<Field> annotatedFields = findAnnotatedFields(objectToClone, view);

    try {
      // create a clone
      final T clone = (T) objectToClone.getClass().newInstance();

      // iterate over annotated fields and copy values to clone
      for (final Field field : annotatedFields) {
        field.setAccessible(true);
        field.set(clone, field.get(objectToClone));
      }

      return clone;
    } catch (final Throwable throwable) {
      // unable to clone object
      throw new CloningFailedException(throwable, objectToClone);
    }
  }

  public static final <T> List<Field> findAnnotatedFields(final T objectToClone, final Class<? extends View> view) {
    try {
      final List<Field> annotatedFields = new LinkedList<Field>();

      // iterate over all superclasses
      for (Class objectClass = objectToClone.getClass(); !objectClass.equals(Object.class); objectClass = objectClass.getSuperclass()) {
        // iterate over all fields
        for (final Field field : objectClass.getDeclaredFields()) {
          // check if field has @InView annotation
          final InView annotation = field.getAnnotation(InView.class);
          if (annotation != null) {
            // found field that is annotated by @InView(...)
            // check if view classes match
            final Class<? extends View> annotationView = annotation.value();
            if (annotationView.isAssignableFrom(view)) {
              annotatedFields.add(field);
            }
          }
        }
      }

      return annotatedFields;
    } catch (final Throwable throwable) {
      // unable to find annotated fields
      throw new CloningFailedException(throwable, objectToClone);
    }
  }

}
