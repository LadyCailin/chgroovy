
package com.chgroovy.core;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.extensions.AbstractExtension;

/**
 * CHGroovy extension main class.
 */
public class CHGroovy extends AbstractExtension {

	@Override
	public Version getVersion() {
		return new SimpleVersion(1, 0, 3, "SNAPSHOT");
	}
}
