package ru.netology.consolechat.common;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageWriterTest {

    private MessageWriter sut;

    @BeforeEach
    protected void setUp() {
        try {
            sut = new MessageWriter();
        } catch (IOException e) {
            throw new IllegalCallerException(e);
        }
    }

    @ParameterizedTest
    @MethodSource
    public void toByteArray(Object message, byte[] expected) {
        byte[] result = null;

        try {
            result = sut.toByteArray(message);
        } catch (IOException ignored) {

        }

        assertArrayEquals(expected, result);
    }

    protected Stream<Arguments> toByteArray() {
        return Stream.of(
                new Message("test", "user"),
                244
                )
                .map(m -> Arguments.of(m, SerializationUtils.serialize(m)));
    }

}