package com.dicerealm.core.inventory;

import java.util.UUID;

public interface Identifiable {
    UUID getId();
		/**
		 * This is used for deserialization
		 */
		String getType();
}
