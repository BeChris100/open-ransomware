package com.bechris100.open_ransomware.mounts;

import org.jetbrains.annotations.Nullable;

public record MountInfo(@Nullable String blockDevice, String mountPoint, long totalSize, long freeSize, long usableSpace) {
}
