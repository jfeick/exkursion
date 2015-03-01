package de.uni_weimar.m18.exkursion.data;

public class LevelListJSON {
    public LevelJSON[] getLevels() {
        return levels;
    }

    public LevelJSON[] levels;

    public class LevelJSON {
        public String base_path;
        public String title;
        public String description;
        public int filemtime;
    }
}
