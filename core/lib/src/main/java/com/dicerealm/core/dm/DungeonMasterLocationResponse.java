package com.dicerealm.core.dm;

public class DungeonMasterLocationResponse {
    public Location[] locations;
    public path[] paths;

    public class Location {
        public String displayName;
        public String description;
    }

    public class path {
        public String from;
        public String to;
        public int distance;
    }
}
