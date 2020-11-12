package business.validator;

/**
 * Interface used for creating validators
 * @param <T> the object`s class to be validated
 */
public interface Validator<T> {
    /**
     * The method will validate the given parameter
     * @param t the object to be validated
     */
    void validate(T t);
}
