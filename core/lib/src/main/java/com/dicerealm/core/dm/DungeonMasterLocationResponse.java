package com.dicerealm.core.dm;
public class DungeonMasterLocationResponse {
    public Location[] locations;
    public PathList[] paths;

    public class Location {
        public String displayName;
        public String description;
    }

    public class PathList {
        public String from;
        public String to;
        public int distance;
    }
}
