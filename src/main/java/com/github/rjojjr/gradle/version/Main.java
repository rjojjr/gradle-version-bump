package com.github.rjojjr.gradle.version;

public class Main {

    public static void main(String[] args){
        FileEditor editor = new FileEditor();

        if(args.length == 0 || args[0].equals("beta")){
            editor.bumpBetaGroovy(null);
        }
    }

}
