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
package org.eclipse.tycho.osgitools;

import java.io.File;

import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;
import org.eclipse.tycho.ArtifactDependencyVisitor;
import org.eclipse.tycho.ArtifactDependencyWalker;
import org.eclipse.tycho.ArtifactKey;
import org.eclipse.tycho.ReactorProject;
import org.eclipse.tycho.TargetEnvironment;
import org.eclipse.tycho.TychoProject;
import org.eclipse.tycho.model.Feature;

@Component( role = TychoProject.class, hint = org.eclipse.tycho.ArtifactKey.TYPE_ECLIPSE_FEATURE )
public class EclipseFeatureProject
    extends AbstractArtifactBasedProject
{
    @Override
    protected ArtifactDependencyWalker newDependencyWalker( MavenProject project, TargetEnvironment environment )
    {
        final File location = project.getBasedir();
        final Feature feature = Feature.loadFeature( location );
        return new AbstractArtifactDependencyWalker( getTargetPlatform( project, environment ),
                                                     getEnvironments( project, environment ) )
        {
            public void walk( ArtifactDependencyVisitor visitor )
            {
                traverseFeature( location, feature, visitor );
            }
        };
    }

    public ArtifactKey getArtifactKey( ReactorProject project )
    {
        Feature feature = Feature.loadFeature( project.getBasedir() );
        return new DefaultArtifactKey( org.eclipse.tycho.ArtifactKey.TYPE_ECLIPSE_FEATURE, feature.getId(), feature.getVersion() );
    }
}
