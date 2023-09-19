package ru.netology.consolechat.common;

import java.io.IOException;
import java.io.OutputStream;

public interface ProtocolWriter {
    int write(OutputStream out, Message message) throws IOException;
}
