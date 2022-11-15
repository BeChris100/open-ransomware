package com.bechris100.open_ransomware.mounts;

import com.bechris100.open_ransomware.utils.FileUtil;
import com.bechris100.open_ransomware.utils.RuntimeEnvironment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Mounts {

    public static List<MountInfo> getMountedPartitions() {
        if (RuntimeEnvironment.isWindows()) {
            File[] drives = File.listRoots();
            if (drives == null)
                return new ArrayList<>();

            List<MountInfo> mounts = new ArrayList<>();
            for (File drive : drives)
                mounts.add(new MountInfo(null, drive.getPath(), drive.getTotalSpace(), drive.getFreeSpace(), drive.getUsableSpace()));

            return mounts;
        }

        try {
            String contents = new String(FileUtil.read("/proc/mounts"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

}
