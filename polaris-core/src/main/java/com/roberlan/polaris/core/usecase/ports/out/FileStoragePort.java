package com.roberlan.polaris.core.usecase.ports.out;

import java.io.InputStream;

public interface FileStoragePort {
    String upload(String fileName, InputStream content, long size, String contentType);
}