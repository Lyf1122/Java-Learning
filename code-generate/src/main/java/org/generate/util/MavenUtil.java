package org.generate.util;

import org.apache.maven.cli.MavenCli;

public class MavenUtil {
  private static final MavenCli mavenCli = new MavenCli();

  private static void execute(String[] args) {
    mavenCli.doMain(args, System.getProperty("user.dir"), System.out, System.err);
  }

}
