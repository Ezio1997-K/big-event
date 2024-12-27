package com.bootzero.big_event;

/**
 * ClassName: ImageMover
 * Package: com.bootzero.big_event
 * Description:
 *
 * @Author Mitsuha
 * @Create 2024/12/27 19:45
 * @Version 1.0
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ImageMover {

    // 定义源目录路径
    private static final String SOURCE_DIR = "D:\\Test\\Test";
    // 定义目标目录路径
    private static final String DEST_DIR = "D:\\Test\\output";

    public static void main(String[] args) {
        // 创建源目录和目标目录的对象
        File sourceDirectory = new File(SOURCE_DIR);
        File destDirectory = new File(DEST_DIR);

        // 检查目标目录是否存在
        if (!destDirectory.exists()) {
            System.err.println("目标文件夹 " + DEST_DIR + " 不存在。");
            return;
        }

        // 遍历源目录及其子目录中的所有文件
        traverseAndMoveFiles(sourceDirectory, destDirectory);
    }

    /**
     * 递归遍历目录及其子目录中的所有文件，并处理图片文件。
     *
     * @param directory   当前遍历的目录
     * @param destDirectory 目标目录
     */
    private static void traverseAndMoveFiles(File directory, File destDirectory) {
        // 获取目录中的所有文件和子目录
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是子目录，则递归调用
                    traverseAndMoveFiles(file, destDirectory);
                } else if (isImageFile(file)) {
                    // 如果是图片文件，则移动文件
                    moveImageFile(file, destDirectory);
                }
            }
        }
    }

    /**
     * 检查文件是否为图片文件。
     *
     * @param file 要检查的文件
     * @return 如果是图片文件返回 true，否则返回 false
     */
    private static boolean isImageFile(File file) {
        // 获取文件名并转换为小写
        String name = file.getName().toLowerCase();
        // 检查文件扩展名是否为图片格式
        return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                name.endsWith(".png") || name.endsWith(".bmp") ||
                name.endsWith(".gif");
    }

    /**
     * 移动图片文件到目标目录，并处理重名情况。
     *
     * @param sourceFile    源文件
     * @param destDirectory 目标目录
     */
    private static void moveImageFile(File sourceFile, File destDirectory) {
        // 获取文件的基本名称（不包括扩展名）
        String baseName = getBaseName(sourceFile);
        // 获取文件的扩展名
        String ext = getFileExtension(sourceFile);
        int counter = 1;

        // 构建目标文件路径
        Path targetPath = Paths.get(destDirectory.getAbsolutePath(), baseName + ext);

        // 检查目标文件路径是否已存在，如果存在则重命名
        while (targetPath.toFile().exists()) {
            targetPath = Paths.get(destDirectory.getAbsolutePath(), baseName + "_" + counter + ext);
            counter++;
        }

        try {
            // 移动文件到目标路径
            Files.move(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("成功: 移动了 \"" + sourceFile.getAbsolutePath() + "\" 到 \"" + targetPath.toString() + "\"");
        } catch (IOException e) {
            // 捕获并处理移动文件时发生的异常
            System.err.println("错误: 移动 \"" + sourceFile.getAbsolutePath() + "\" 到 \"" + targetPath.toString() + "\" 失败。");
            e.printStackTrace();
        }
    }

    /**
     * 获取文件的基本名称（不包括扩展名）。
     *
     * @param file 文件对象
     * @return 文件的基本名称
     */
    private static String getBaseName(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) {
            return name;
        }
        return name.substring(0, dotIndex);
    }

    /**
     * 获取文件的扩展名。
     *
     * @param file 文件对象
     * @return 文件的扩展名
     */
    private static String getFileExtension(File file) {
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return name.substring(dotIndex);
    }
}












