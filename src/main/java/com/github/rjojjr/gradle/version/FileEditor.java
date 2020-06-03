package com.github.rjojjr.gradle.version;

import com.kirchnersolutions.utilities.ByteTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FileEditor {

    static final int MAJOR = 0, MINOR = 1, PATCH = 2, BETA = 3;

    boolean bumpGroovy(String dir, int type){
        File gradle;
        if(dir == null){
            gradle = new File("build.gradle");
        } else {
            gradle = new File(dir + "/build.gradle");
        }
        if(!gradle.exists()){
            System.err.println("build.gradle file not found");
            return false;
        }
        List<String> buffer;
        if(type != BETA){
            buffer = bump(gradle, type);
        } else {
            buffer = bumpBeta(gradle);
        }
        if(buffer == null){
            return false;
        }
        try{
            String raw = buffer.stream()
                    .collect(Collectors.joining("\n"));
            ByteTools.writeBytesToFile(gradle, raw.getBytes("UTF-8"));
            System.out.println("Wrote new version to build.gradle");
            return true;
        }catch (Exception e){
            System.err.println("Failed to write to build.gradle");
            e.printStackTrace();
            return false;
        }
    }

    private List<String> bumpBeta(File gradle) {
        List<String> buffer = new ArrayList<>();
        String version = "";
        int buildNum = 0;
        try{
            Scanner in = new Scanner(gradle);
            while(in.hasNextLine()){
                String line = in.nextLine();
                if(line.contains("def build")){
                    int build = Integer.parseInt(line.split(" = ")[1]);
                    buildNum = build + 1;
                    buffer.add("def build = " + buildNum);
                } else if (line.contains("def versionString")){
                    version = line.split(" = ")[1].split("'")[1];
                    buffer.add("def versionString = '" + version + "'");
                } else if(line.contains("version '")){
                    buffer.add("version '" + version + "." + buildNum + "'");
                }else{
                    buffer.add(line);
                }
            }
            return buffer;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private List<String> bump(File gradle, int type) {
        List<String> buffer = new ArrayList<>();
        String version = "";
        int buildNum = 0;
        try{
            Scanner in = new Scanner(gradle);
            while(in.hasNextLine()){
                String line = in.nextLine();
                if(line.contains("def build")){
                    int build = Integer.parseInt(line.split(" = ")[1]);
                    buildNum = build + 1;
                    buffer.add("def build = " + buildNum);
                } else if (line.contains("def versionString")){
                    version = extractAndBump(line, type);
                    buffer.add("def versionString = '" + version + "'");
                } else if(line.contains("version '")){
                    buffer.add("version '" + version + "'");
                }else{
                    buffer.add(line);
                }
            }
            return buffer;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static String extractAndBump(String line, int type){
        String extractedVersion = line.split(" = ")[1].split("'")[1];
        String oldMinor = extractedVersion.split(".")[type];
        String newMinor = bumpAndMaintainPadding(oldMinor);
        String[] parts = extractedVersion.split(oldMinor);
        switch (type) {
            case MAJOR:
                return newMinor + "." + parts[1] + "." + parts[2];
            case MINOR:
                return parts[0] + "." + newMinor + "." + parts[2];
            case PATCH:
                return parts[0] + "." + parts[1] + "." + newMinor;
            default:
                throw new IllegalArgumentException("Invalid type");

        }
    }

    private static String bumpAndMaintainPadding(String num){
        String padding = "";
        char[] chars = num.toCharArray();
        for(char chr : chars){
            if(chr == '0'){
                padding = padding + "0";
            } else {
                break;
            }
        }
        String numString = num.replaceFirst(padding, "");
        int number = Integer.parseInt(numString);
        number++;
        return padding + number;
    }

}
