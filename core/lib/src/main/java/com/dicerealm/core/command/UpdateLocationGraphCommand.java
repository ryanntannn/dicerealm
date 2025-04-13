package com.dicerealm.core.command;

import com.dicerealm.core.locations.LocationGraph;

public class UpdateLocationGraphCommand extends Command {

		public LocationGraph graph;

		public UpdateLocationGraphCommand(LocationGraph graph) {
			this.graph = graph; 
			this.type = "UPDATE_LOCATION_GRAPH";
		}
		
		public LocationGraph getGraph() {
			return graph;
		}
}
