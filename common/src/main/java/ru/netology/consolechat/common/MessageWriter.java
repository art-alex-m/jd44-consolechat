package ru.netology.consolechat.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MessageWriter implements ProtocolWriter {

    @Override
    public int write(DataOutputStream out, Message message) throws IOException {
        byte[] object = toByteArray(message);

        out.writeInt(object.length);
        out.write(object);

        return object.length;
    }

    public byte[] toByteArray(Object obj) throws IOException {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(byteStream)) {
            outputStream.writeObject(obj);

            return byteStream.toByteArray();
        }
    }
}
