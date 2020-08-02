package in.rcard.kafkaesque;

import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withParametersCount;
import static org.reflections.ReflectionUtils.withReturnTypeAssignableTo;

import in.rcard.kafkaesque.KafkaesqueConsumer.Builder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Stream;
import org.reflections.Reflections;

public class Kafkaesque {

  private Kafkaesque() {
    // Empty body
  }

  public static Kafkaesque newInstance() {
    return new Kafkaesque();
  }
  
  public KafkaesqueConsumer.Builder consume() {
    final Set<Class<? extends Builder>> buildersClass = findClassesImplementingBuilder();
    validateBuilderClasses(buildersClass);
    return buildersClass.stream()
        .flatMap(this::findFactoryMethods)
        .findFirst()
        .map(this::invokeTheFactoryMethod)
        .orElseThrow(() -> new AssertionError("No method found to build a new instance of a Builder"));
  }
  
  private Set<Class<? extends Builder>> findClassesImplementingBuilder() {
    final Reflections reflections = new Reflections("in.rcard.kafkaesque");
    return reflections.getSubTypesOf(Builder.class);
  }
  
  private void validateBuilderClasses(Set<Class<? extends Builder>> buildersClass) {
    verifyIfAnyBuilderClassWasFound(buildersClass);
    verifyIfMoreThanOneBuilderClassWasFound(buildersClass);
  }
  
  private void verifyIfMoreThanOneBuilderClassWasFound(
      Set<Class<? extends Builder>> buildersClass) {
    if (buildersClass.size() > 1) {
      throw new AssertionError(
          String.format(
              "There is more than one implementation of the Kafkaesque consumer %s",
              buildersClass.toString()));
    }
  }
  
  private void verifyIfAnyBuilderClassWasFound(Set<Class<? extends Builder>> buildersClass) {
    if (buildersClass == null || buildersClass.size() == 0) {
      throw new AssertionError("No implementation of a Kafkaesque consumer builder found");
    }
  }
  
  private Builder invokeTheFactoryMethod(Method method) {
    try {
      final Object returnedObject = method.invoke(null);
      return (Builder) returnedObject;
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new AssertionError("No static method found to build a new instance of the builder");
    }
  }
  
  private Stream<Method> findFactoryMethods(Class<? extends Builder> builderClass) {
    //noinspection unchecked
    return getAllMethods(
        builderClass, withReturnTypeAssignableTo(Builder.class), withParametersCount(0))
               .stream();
  }
}
