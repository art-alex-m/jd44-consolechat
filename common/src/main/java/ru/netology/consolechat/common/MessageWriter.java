package ru.netology.consolechat.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class MessageWriter implements ProtocolWriter {

    private final ObjectOutputStream outputStream;
    private final ByteArrayOutputStream byteStream;

    public MessageWriter() throws IOException {
        byteStream = new ByteArrayOutputStream();
        outputStream = new ObjectOutputStream(byteStream);
    }

    @Override
    public int write(OutputStream out, Message message) throws IOException {
        byte[] object = toByteArray(message);
        byte[] size = toByteArray(object.length);

        out.write(size);
        out.write(object);
        out.flush();

        return object.length;
    }

    public byte[] toByteArray(Object obj) throws IOException {
        outputStream.writeObject(obj);
        byte[] byteArray = byteStream.toByteArray();
        outputStream.reset();

        return byteArray;
    }
}
