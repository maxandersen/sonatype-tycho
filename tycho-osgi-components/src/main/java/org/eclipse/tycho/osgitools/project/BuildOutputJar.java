/*******************************************************************************
 * Copyright (c) 2008, 2011 Sonatype Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.tycho.osgitools.project;

import java.io.File;
import java.util.List;

public class BuildOutputJar {

	private final String name;
	private final List<File> sourceFolders;
	private final File outputDirectory;
	private List<String> extraClasspathEntries;
	
	public BuildOutputJar(String name, File outputDirectory, List<File> sourceFolders, List<String> extraClasspathEntries) {
		this.name = name;
		this.outputDirectory = outputDirectory;
		this.sourceFolders = sourceFolders;
		this.extraClasspathEntries  = extraClasspathEntries;
	}

	public String getName() {
		return name;
	}
	
	public File getOutputDirectory() {
		return outputDirectory;
	}
	
	public List<File> getSourceFolders() {
		return sourceFolders;
	}

	public List<String> getExtraClasspathEntries() {
		return extraClasspathEntries; 
	}
}
