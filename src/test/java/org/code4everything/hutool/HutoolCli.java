package org.code4everything.hutool;

/**
 * @author pantao
 * @since 2020/10/30
 */
public class HutoolCli {

    public static void main(String[] args) {
        Hutool.workDir = "./hutool";
        Hutool.main(args);
    }
}