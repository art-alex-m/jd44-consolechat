package ru.netology.consolechat.common;

import java.io.DataInputStream;
import java.io.IOException;

public interface ProtocolReader {
    Message read(DataInputStream input) throws IOException, ClassNotFoundException;
}
