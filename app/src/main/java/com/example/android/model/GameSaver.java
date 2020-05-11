package com.example.android.model;

import android.content.Context;
import android.util.Log;

import com.example.android.Board;
import com.example.android.activity.PlayActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GameSaver implements Serializable {

    private List<GameStates> gameStates;
    public static final long serialVersionUID = 42L;
    private static final String savedGamesFilename = "savedGames.dat";
    private static final String currentGameFilename = "currentGame.dat";
    private static File savedGamesDir;
    private static File currentGameDir;
    private GameStates currentGame;


    /**
     * Get ALL the saved games that are saved to file.
     * @return List of game states found on the file.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public List<GameStates> getAllGameStates(Context context) throws IOException{
        this.gameStates = readSavedGames(context);
        return gameStates;
    }

    /**
     * Add a new saved game to the file and write the file so that it's there forever.
     * @param savedGame GameStates of the saved game.
     * @throws IOException
     */
    public void addNewSave(GameStates savedGame, Context context){
        try {
            this.gameStates = getAllGameStates(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.gameStates.add(savedGame);
        writeSavedGames(this, context);
    }

    /**
     * Sort the list of gamesStates by name, if any.
     */
    public List<GameStates> sortByName() throws NullPointerException{
        if (gameStates == null){
            throw new NullPointerException();
        }
        if (gameStates.isEmpty()){
            throw new NullPointerException();
        }
        this.gameStates = gameStates.stream()
                .sorted(Comparator.comparing(gameStates1 -> gameStates1.getName()))
                .collect(Collectors.toList());
        return this.gameStates;
    }

    /**
     * Sort the list og gameStates by date, if any.
     */
    public List<GameStates> sortByDate() throws NullPointerException{
        if (gameStates == null){
            throw new NullPointerException();
        }
        if (gameStates.isEmpty()){
            throw new NullPointerException();
        }
        gameStates.sort(Comparator.comparing(o -> o.getSaveDate()));
        return gameStates;
    }


    /**
     * Delete a gameState at the specified index.
     * @param index Index of the gameState to delete.
     * @throws NullPointerException When there are no gameStates
     * @return The gameStates list, with the designated element deleted.
     */
    public List<GameStates> delete(int index, Context context) throws NullPointerException{
        if (gameStates == null){
            throw new NullPointerException();
        }
        if (gameStates.isEmpty()){
            throw new NullPointerException();
        }
        gameStates.remove(index);
        writeSavedGames(this, context);
        return gameStates;

    }

    /**
     * Used when a game is in play in hopes of decreasing memory usage. Serializes the
     * given Gamestates into a file for use at the end of the game.
     * The given gamestates are assumed to not already be in the file and are safe
     * to be destroyed when they are added to the file.
     * @param states States to save.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void storeCurrentGameStates(GameStates states, Context context) throws IOException, ClassNotFoundException {
        GameStates alreadyStored = loadCurrentGameStates(context);
        alreadyStored.addAllStates(states.getStates());
        currentGame = alreadyStored;
        writeCurrentGame(currentGame, context);
    }

    //TODO: Decide if this is needed.
    public void storeCurrentGameStates(GameStates.State state, Context context) throws IOException, ClassNotFoundException {
        GameStates alreadyStored = loadCurrentGameStates(context);
        alreadyStored.addState(state);
        currentGame = alreadyStored;
        writeCurrentGame(currentGame, context);
    }

    //TODO: Decide if this is needed.
    public GameStates loadCurrentGameStates(Context context) throws IOException, ClassNotFoundException {
        GameStates alreadyStored = readCurrentGames(context);
        return alreadyStored;
    }

    private static void writeCurrentGame(GameStates states, Context context) throws IOException {
        OutputStream outputStream = context.openFileOutput(currentGameFilename, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(outputStream);
        oos.writeObject(states);
    }

    private GameStates readCurrentGames(Context context) throws IOException {
        FileInputStream fis = context.openFileInput(currentGameFilename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        GameStates states = null;
        try {
            states = (GameStates)ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            // File not found, create a new one!
            File file = context.getDir(currentGameFilename, Context.MODE_PRIVATE);
            e.printStackTrace();
        }
        return states;
    }

    /**
     * Write the saved games to a file.
     * @param gameSaver This GameSaver
     * @param context Context from the app.
     */
    private void writeSavedGames(GameSaver gameSaver, Context context) {
        OutputStream outputStream = null;
        try {
            outputStream = context.openFileOutput(savedGamesFilename, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(outputStream);
            oos.writeObject(gameSaver);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Read the saved games from the file. If the file is not found, then it is created.
     * @param context Context from the app.
     * @return The list of gameStates found on the file, or a new/empty list of gameStates.
     */
    private List<GameStates> readSavedGames(Context context){
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = context.openFileInput(savedGamesFilename);
            ois = new ObjectInputStream(fis);
        } catch (IOException e) {
            // File isn't found. Create a new one!
            File file = context.getDir(savedGamesFilename, Context.MODE_PRIVATE);
            this.gameStates = new ArrayList<GameStates>();
            return this.gameStates;
        }
        GameSaver gameSaver = null;
        try {
            gameSaver = (GameSaver)ois.readObject();
            this.gameStates = gameSaver.gameStates;
        } catch (ClassNotFoundException | IOException e) {
            // File isn't found. Create a new one!
            File file = context.getDir(savedGamesFilename, Context.MODE_PRIVATE);
            this.gameStates = new ArrayList<GameStates>();
            e.printStackTrace();
        }
        return this.gameStates;
    }

//    /**
//     * Find a file with the given file name.
//     * @param fileName File name.
//     * @return File, if found.
//     */
//    public static File findPath(String fileName){
//        File file = new File("..");
//        //System.out.println("Trying to find:" + fileName);
//        return find(fileName, file);
//    }
//
//    /**
//     * Utility to recurse through directories to find the file.
//     * @param fileName File name of the file.
//     * @param currentDirectory Current directory the search is in.
//     * @return File found, if any.
//     */
//    private static File find(String fileName, File currentDirectory){
//        if (currentDirectory.isDirectory()){
//            File[] list = currentDirectory.listFiles();
//            if (list == null){
//                //return null;
//            }
//            for (File f : list){
//                File found = find(fileName, f);
//                if (found != null){
//                    return found;
//                }
//            }
//        }
//        else{
//            if (currentDirectory.getName().equalsIgnoreCase(fileName)){
//                return currentDirectory;
//            }
//        }
//        return null;
//    }

}

