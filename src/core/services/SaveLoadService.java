package core.services;

import core.domain.Season;
import interfaces.ISport;
import sports.football.FootballSport;
import sports.volleyball.VolleyballSport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveLoadService {
    private static final String SAVE_DIR = "saves/";
    private static final String SAVE_PREFIX = "slot_";
    private static final String SAVE_EXT = ".ser";

    public SaveLoadService() {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
        } catch (IOException exception) {
            System.err.println("Could not create saves dir: " + exception.getMessage());
        }
    }

    public void save(Season season, int slot) throws IOException {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new FileOutputStream(SAVE_DIR + SAVE_PREFIX + slot + SAVE_EXT))) {
            objectOutputStream.writeObject(season);
        }
    }

    public Season load(int slot) throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream(SAVE_DIR + SAVE_PREFIX + slot + SAVE_EXT))) {
            return (Season) objectInputStream.readObject();
        }
    }

    public boolean slotExists(int slot) {
        return new File(SAVE_DIR + SAVE_PREFIX + slot + SAVE_EXT).exists();
    }

    public ISport resolveSport(String sportName) {
        if (sportName == null) {
            throw new IllegalArgumentException("sportName cannot be null");
        }

        switch (sportName.toLowerCase()) {
            case "football":
                return new FootballSport();
            case "volleyball":
                return new VolleyballSport();
            default:
                throw new IllegalArgumentException("Unknown sport: " + sportName);
        }
    }
}
