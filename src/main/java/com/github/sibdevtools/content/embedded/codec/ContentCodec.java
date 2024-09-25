package com.github.sibdevtools.content.embedded.codec;

/**
 * Codec for content value decoding and encoding
 *
 * @author sibmaks
 * @since 0.0.1
 */
public interface ContentCodec {

    /**
     * Encode passed value into a string.
     *
     * @param value content value
     * @param <T>   content's type
     * @return encoded representation of content value
     */
    <T> String encode(T value);

    /**
     * Decode passed value from a string into a passed Java type.
     *
     * @param value encoded content value
     * @param type  content's Java type
     * @param <T>   content's type
     * @return decoded content value
     */
    <T> T decode(String value, Class<T> type);

}
