package ru.netology.consolechat.common;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MessageReader implements ProtocolReader {
    private final static int INTEGER_LENGTH = 4;

    @Override
    public Message read(DataInputStream input) throws IOException, ClassNotFoundException {
        if (input.available() < INTEGER_LENGTH) {
            return null;
        }
        int size = input.readInt();

        return (Message) fromByteArray(input.readNBytes(size));
    }

    public Object fromByteArray(byte[] data) throws ClassNotFoundException, IOException {
        try (ByteArrayInputStream bytes = new ByteArrayInputStream(data);
             ObjectInputStream inputStream = new ObjectInputStream(bytes)) {
            return inputStream.readObject();
        }
    }
}
