package org.generate.timer;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.TimerTask;
import java.util.stream.Stream;

public class CustomTimerTask{

  // 模拟清理过期文件
  static class CleanUpExpiredFilesTask extends TimerTask {

    private String directoryPath;
    private long expirationTime;

    public CleanUpExpiredFilesTask(String directoryPath, long expirationTime) {
      this.directoryPath = directoryPath;
      this.expirationTime = expirationTime;
    }

    @Override
    public void run() {
      File directory = new File(directoryPath);
      if (!directory.exists() || !directory.isDirectory()) {
        return;
      }

      Optional.ofNullable(directory.listFiles())
        .map(Arrays::stream)
        .orElseGet(Stream::empty)
        .filter(file -> System.currentTimeMillis() - file.lastModified() > expirationTime)
        .forEach(file -> {
          boolean deleted = file.delete();
          System.out.println(deleted ? "Deleted expired file: " + file.getName()
            : "Failed to delete file: " + file.getName());
        });
    }
  }

}
