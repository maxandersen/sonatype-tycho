package org.codehaus.tycho.maven.test;

import java.io.File;
import java.util.List;

import org.apache.maven.Maven;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.testing.SilentLog;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.tycho.BundleResolutionState;
import org.codehaus.tycho.TychoConstants;
import org.codehaus.tycho.testing.AbstractTychoMojoTestCase;
import org.codehaus.tycho.testing.CompoundRuntimeException;
import org.eclipse.osgi.service.resolver.BundleDescription;

public class TychoTest extends AbstractTychoMojoTestCase {

	protected Maven maven;

	protected Logger logger;

	protected void setUp() throws Exception {
		super.setUp();
		maven = lookup(Maven.class);
		logger = new SilentLog();
	}

	public void testModuleOrder() throws Exception {
		File pom = new File(getBasedir("projects/moduleorder"), "pom.xml");

		List<MavenProject> projects = getSortedProjects(pom);
		assertEquals(5, projects.size());

		MavenProject p002 = (MavenProject) projects.get(1);
		MavenProject p001 = (MavenProject) projects.get(2);
		MavenProject p004 = (MavenProject) projects.get(3); // feature
		MavenProject p003 = (MavenProject) projects.get(4); // site

		assertEquals("moduleorder.p001", p001.getArtifactId());
		assertEquals("moduleorder.p002", p002.getArtifactId());
		assertEquals("moduleorder.p003", p003.getArtifactId());
		assertEquals("moduleorder.p004", p004.getArtifactId());
	}

	protected List<MavenProject> getSortedProjects( File pom ) throws Exception
	{
        MavenExecutionRequest request = newMavenExecutionRequest(pom);
        return getSortedProjects( request );
	}

    private List<MavenProject> getSortedProjects( MavenExecutionRequest request )
    {
        request.getProjectBuildingRequest().setProcessPlugins( false ); // really dirty hack
	    MavenExecutionResult result = maven.execute( request );
	    if ( result.hasExceptions() )
	    {
	        throw new CompoundRuntimeException( result.getExceptions() );
	    }
        return result.getTopologicallySortedProjects();
    }

	public void testResolutionError() throws Exception {
		File pom = new File(getBasedir("projects/resolutionerror/p001"), "pom.xml");

		try 
		{
		    getSortedProjects(pom);
		    fail();
		} 
		catch ( Exception e )
		{
//	        List<Exception> exceptions = result.getExceptions();
//	        assertEquals(1, exceptions.size());
	        assertTrue(e.getMessage().contains("Missing Constraint: Import-Package: moduleorder.p002"));
		}

	}

	public void testProjectPriority() throws Exception {
		File platform = new File(getBasedir(), "src/test/resources/projects/projectpriority/platform");
		File pom = new File(getBasedir("projects/projectpriority"), "pom.xml");

		MavenExecutionRequest request = newMavenExecutionRequest(pom);
		request.getUserProperties().put("tycho.targetPlatform", platform.getCanonicalPath());

		List<MavenProject> projects = getSortedProjects( request );

		MavenProject p002 = (MavenProject) projects.get(2);

		List<Dependency> dependencies = p002.getModel().getDependencies();
		Dependency dependency = dependencies.get(0);
		assertEquals("0.0.1", dependency.getVersion());
	}

	public void testFragment() throws Exception {
		File pom = new File(getBasedir("projects/fragment"), "pom.xml");

		List<MavenProject> projects = getSortedProjects(pom);

		MavenProject host = projects.get(1);
		MavenProject dep = projects.get(2);
		MavenProject fragment = projects.get(3);
		MavenProject fragment2 = projects.get(4);
		MavenProject client = projects.get(5);

		assertEquals("host", host.getArtifactId());
		// host does not know anything about fragments
		List<Dependency> hostDependencies = host.getModel().getDependencies();
		assertEquals(0, hostDependencies.size());

		assertEquals("fragment", fragment.getArtifactId());
		List<Dependency> fragmentDependencies = fragment.getModel().getDependencies();
		// host first, then fragment dependency
		assertEquals(2, fragmentDependencies.size());
		assertEquals("host", fragmentDependencies.get(0).getArtifactId()); 
		assertEquals("dep", fragmentDependencies.get(1).getArtifactId());

		assertEquals("fragment2", fragment2.getArtifactId());
		// host only
		List<Dependency> fragment2Dependencies = fragment2.getModel().getDependencies();
		assertEquals(1, fragment2Dependencies.size());
		assertEquals("host", fragment2Dependencies.get(0).getArtifactId());

		assertEquals("client", client.getArtifactId());
		// depends on host and because host has ExtensibleAPI also depends fragment and fragent2
		List<Dependency> clientDependencies = client.getModel().getDependencies();
		assertEquals(3, clientDependencies.size());
		assertEquals("host", clientDependencies.get(0).getArtifactId());
		assertEquals("fragment", clientDependencies.get(1).getArtifactId());
		assertEquals("fragment2", clientDependencies.get(2).getArtifactId());
	}

	public void testPre30() throws Exception {
        File pom = new File(getBasedir("projects/dummy"), "pom.xml");

	    MavenExecutionRequest request = newMavenExecutionRequest(pom);
        request.getUserProperties().put("tycho.targetPlatform", new File("src/test/resources/targetplatforms/pre-3.0").getCanonicalPath());

        MavenProject project = getSortedProjects(request).get(0);

        BundleResolutionState state = (BundleResolutionState) project.getContextValue( TychoConstants.CTX_BUNDLE_RESOLUTION_STATE );

		assertNotNull(state.getBundle("testjar", "1.0.0"));
		assertNotNull(state.getBundle("testdir", "1.0.0"));

		assertTrue(new File(project.getBuild().getDirectory(), "manifests/testdir_1.0.0/META-INF/MANIFEST.MF").canRead());
		assertTrue(new File(project.getBuild().getDirectory(), "manifests/testjar_1.0.0/META-INF/MANIFEST.MF").canRead());
	}

	public void testMNGECLIPSE942() throws Exception {
        File pom = new File(getBasedir("projects/dummy"), "pom.xml");

        MavenExecutionRequest request = newMavenExecutionRequest(pom);
        request.getUserProperties().put("tycho.targetPlatform", new File("src/test/resources/targetplatforms/MNGECLIPSE-942").getCanonicalPath());

        MavenProject project = getSortedProjects(request).get(0);

        BundleResolutionState state = (BundleResolutionState) project.getContextValue( TychoConstants.CTX_BUNDLE_RESOLUTION_STATE );

		List<BundleDescription> bundles = state.getBundles();

		assertEquals(3, bundles.size());
		assertNotNull(state.getBundle( "org.junit4.nl_ru", TychoConstants.HIGHEST_VERSION));
	}
	
}
