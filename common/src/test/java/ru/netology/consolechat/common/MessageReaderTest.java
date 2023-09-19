package ru.netology.consolechat.common;

import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MessageReaderTest {

    private MessageReader sut;

    @BeforeEach
    protected void setUp() {
        sut = new MessageReader();
    }

    @Test
    public void fromByteArray_Message() {
        Message message = new Message("test", "user");
        byte[] bytes = SerializationUtils.serialize(message);
        Message result = null;

        try {
            result = (Message) sut.fromByteArray(bytes);
        } catch (IOException | ClassNotFoundException ignored) {
        }

        assertNotNull(result);
        assertNotEquals(message, result);
        assertEquals(message.hashCode(), result.hashCode());
    }

    @Test
    public void fromByteArray_Integer() {
        byte[] bytes = SerializationUtils.serialize(244);
        Integer result = null;

        try {
            result = (Integer) sut.fromByteArray(bytes);
        } catch (IOException | ClassNotFoundException ignored) {
        }

        assertEquals(244, result);
    }
}