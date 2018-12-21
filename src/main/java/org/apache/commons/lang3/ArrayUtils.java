package org.apache.commons.lang3;

/**
 * Created by shitalm on 22/05/15.
 */
public class ArrayUtils {
    /**
     * An empty immutable {@code Object} array.
     */
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    /**
     * An empty immutable {@code Class} array.
     */
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];
    /**
     * An empty immutable {@code String} array.
     */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];
    /**
     * An empty immutable {@code long} array.
     */
    public static final long[] EMPTY_LONG_ARRAY = new long[0];
    /**
     * An empty immutable {@code Long} array.
     */
    public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];
    /**
     * An empty immutable {@code int} array.
     */
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    /**
     * An empty immutable {@code Integer} array.
     */
    public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
    /**
     * An empty immutable {@code short} array.
     */
    public static final short[] EMPTY_SHORT_ARRAY = new short[0];
    /**
     * An empty immutable {@code Short} array.
     */
    public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];
    /**
     * An empty immutable {@code byte} array.
     */
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    /**
     * An empty immutable {@code Byte} array.
     */
    public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
    /**
     * An empty immutable {@code double} array.
     */
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    /**
     * An empty immutable {@code Double} array.
     */
    public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
    /**
     * An empty immutable {@code float} array.
     */
    public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
    /**
     * An empty immutable {@code Float} array.
     */
    public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
    /**
     * An empty immutable {@code boolean} array.
     */
    public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    /**
     * An empty immutable {@code Boolean} array.
     */
    public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
    /**
     * An empty immutable {@code char} array.
     */
    public static final char[] EMPTY_CHAR_ARRAY = new char[0];
    /**
     * An empty immutable {@code Character} array.
     */
    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];

    // ----------------------------------------------------------------------
    /**
     * <p>Checks if an array of Objects is empty or {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is empty or {@code null}
     * @since 2.1
     */
    public static boolean isEmpty(final Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive longs is empty or {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is empty or {@code null}
     * @since 2.1
     */
    public static boolean isEmpty(final long[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive ints is empty or {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is empty or {@code null}
     * @since 2.1
     */
    public static boolean isEmpty(final int[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive shorts is empty or {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is empty or {@code null}
     * @since 2.1
     */
    public static boolean isEmpty(final short[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive chars is empty or {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is empty or {@code null}
     * @since 2.1
     */
    public static boolean isEmpty(final char[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive bytes is empty or {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is empty or {@code null}
     * @since 2.1
     */
    public static boolean isEmpty(final byte[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive doubles is empty or {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is empty or {@code null}
     * @since 2.1
     */
    public static boolean isEmpty(final double[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive floats is empty or {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is empty or {@code null}
     * @since 2.1
     */
    public static boolean isEmpty(final float[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>Checks if an array of primitive booleans is empty or {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is empty or {@code null}
     * @since 2.1
     */
    public static boolean isEmpty(final boolean[] array) {
        return array == null || array.length == 0;
    }

    // ----------------------------------------------------------------------
    /**
     * <p>Checks if an array of Objects is not empty or not {@code null}.</p>
     *
     * @param <T> the component type of the array
     * @param array  the array to test
     * @return {@code true} if the array is not empty or not {@code null}
     * @since 2.5
     */
    public static <T> boolean isNotEmpty(final T[] array) {
        return (array != null && array.length != 0);
    }

    /**
     * <p>Checks if an array of primitive longs is not empty or not {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is not empty or not {@code null}
     * @since 2.5
     */
    public static boolean isNotEmpty(final long[] array) {
        return (array != null && array.length != 0);
    }

    /**
     * <p>Checks if an array of primitive ints is not empty or not {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is not empty or not {@code null}
     * @since 2.5
     */
    public static boolean isNotEmpty(final int[] array) {
        return (array != null && array.length != 0);
    }

    /**
     * <p>Checks if an array of primitive shorts is not empty or not {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is not empty or not {@code null}
     * @since 2.5
     */
    public static boolean isNotEmpty(final short[] array) {
        return (array != null && array.length != 0);
    }

    /**
     * <p>Checks if an array of primitive chars is not empty or not {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is not empty or not {@code null}
     * @since 2.5
     */
    public static boolean isNotEmpty(final char[] array) {
        return (array != null && array.length != 0);
    }

    /**
     * <p>Checks if an array of primitive bytes is not empty or not {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is not empty or not {@code null}
     * @since 2.5
     */
    public static boolean isNotEmpty(final byte[] array) {
        return (array != null && array.length != 0);
    }

    /**
     * <p>Checks if an array of primitive doubles is not empty or not {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is not empty or not {@code null}
     * @since 2.5
     */
    public static boolean isNotEmpty(final double[] array) {
        return (array != null && array.length != 0);
    }

    /**
     * <p>Checks if an array of primitive floats is not empty or not {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is not empty or not {@code null}
     * @since 2.5
     */
    public static boolean isNotEmpty(final float[] array) {
        return (array != null && array.length != 0);
    }

    /**
     * <p>Checks if an array of primitive booleans is not empty or not {@code null}.</p>
     *
     * @param array  the array to test
     * @return {@code true} if the array is not empty or not {@code null}
     * @since 2.5
     */
    public static boolean isNotEmpty(final boolean[] array) {
        return (array != null && array.length != 0);
    }

    /**
     * <p>Clones an array returning a typecast result and handling
     * {@code null}.</p>
     *
     * <p>This method returns {@code null} for a {@code null} input array.</p>
     *
     * @param array  the array to clone, may be {@code null}
     * @return the cloned array, {@code null} if {@code null} input
     */
    public static char[] clone(final char[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }

}
