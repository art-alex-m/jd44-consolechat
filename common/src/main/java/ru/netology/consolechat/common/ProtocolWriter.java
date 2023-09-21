package ru.netology.consolechat.common;

import java.io.DataOutputStream;
import java.io.IOException;

public interface ProtocolWriter {
    int write(DataOutputStream out, Message message) throws IOException;
}
