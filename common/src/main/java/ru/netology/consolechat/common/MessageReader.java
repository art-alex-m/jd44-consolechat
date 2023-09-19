package ru.netology.consolechat.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class MessageReader implements ProtocolReader {

    @Override
    public Message read(InputStream input) throws IOException, ClassNotFoundException {
        Integer size = (Integer) fromByteArray(input.readNBytes(4));
        return (Message) fromByteArray(input.readNBytes(size));
    }

    public Object fromByteArray(byte[] data) throws IOException, ClassNotFoundException {
        try(ByteArrayInputStream bytes = new ByteArrayInputStream(data);
            ObjectInputStream inputStream = new ObjectInputStream(bytes)) {

            return inputStream.readObject();
        }
    }
}
