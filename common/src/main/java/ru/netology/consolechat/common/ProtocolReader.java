package ru.netology.consolechat.common;

import java.io.IOException;
import java.io.InputStream;

public interface ProtocolReader {
    Message read(InputStream input) throws IOException, ClassNotFoundException;
}
