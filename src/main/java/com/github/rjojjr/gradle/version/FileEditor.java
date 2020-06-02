package com.github.rjojjr.gradle.version;

import com.kirchnersolutions.utilities.ByteTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FileEditor {

    boolean bumpBetaGroovy(String dir){
        System.out.println("Bumping beta version");
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
        List<String> buffer = bumpBeta(gradle);
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

    List<String> bumpBeta(File gradle) {
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

}
