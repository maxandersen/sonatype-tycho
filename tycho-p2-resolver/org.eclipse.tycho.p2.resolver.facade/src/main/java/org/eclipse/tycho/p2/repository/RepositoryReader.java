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
package org.eclipse.tycho.p2.repository;

import java.io.IOException;
import java.io.InputStream;

public interface RepositoryReader {

    InputStream getContents(GAV gav, String classifier, String extension) throws IOException;

    InputStream getContents(String remoteRelpath) throws IOException;
}
