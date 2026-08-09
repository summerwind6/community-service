package com.community.service;

import java.util.List;

public interface TemporaryAttachmentService {
    String create(String storedFilename);

    List<String> consumeAll(String attachmentsJson);
}
