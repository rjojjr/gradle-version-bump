package com.github.rjojjr.gradle.version;

public class Main {

    public static void main(String[] args){
        FileEditor editor = new FileEditor();

        if(args.length == 0 || args[0].equals("beta")){
            System.out.println("Bumping beta version");
            editor.bumpGroovy(null, 3);
        }
        if(args[0].toLowerCase().contains("maj")){
            System.out.println("Bumping major version");
            editor.bumpGroovy(null,0);
        }
        if(args[0].toLowerCase().contains("min")){
            System.out.println("Bumping minor version");
            editor.bumpGroovy(null,1);
        }
        if(args[0].toLowerCase().contains("pa")){
            System.out.println("Bumping patch version");
            editor.bumpGroovy(null,2);
        }
    }

}
