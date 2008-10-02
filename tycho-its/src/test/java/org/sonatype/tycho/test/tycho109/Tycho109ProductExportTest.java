package org.sonatype.tycho.test.tycho109;

import java.io.File;
import java.io.StringWriter;

import junit.framework.Assert;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.cli.CommandLineUtils;
import org.apache.maven.it.util.cli.Commandline;
import org.apache.maven.it.util.cli.StreamConsumer;
import org.apache.maven.it.util.cli.WriterStreamConsumer;
import org.junit.Test;
import org.sonatype.tycho.test.AbstractTychoIntegrationTest;

public class Tycho109ProductExportTest extends AbstractTychoIntegrationTest {

	private static final String WINDOWS_OS = "windows";

	private static final String MAC_OS = "mac os x";

	private static final String MAC_OS_DARWIN = "darwin";

	private static final String LINUX_OS = "linux";

	@Test
	public void exportPluginProduct() throws Exception {
		Verifier verifier = getVerifier("/tycho109/plugin-rcp/MyFirstRCP");
		verifier.executeGoal("package");
		verifier.verifyErrorFreeLog();

		File basedir = new File(verifier.getBasedir());
		File output = new File(basedir, "target/product");

		Assert.assertTrue("Exported product folder not found", output
				.isDirectory());

		File launcher = getLauncher(output, "MyFirstRCPProduct");
		Assert.assertTrue("Launcher not found\n" + launcher, launcher.isFile());
		Assert.assertTrue("config.ini not found", new File(output,
				"configuration/config.ini").isFile());

		File plugins = new File(output, "plugins");
		Assert.assertTrue("Plugins not found", plugins.isDirectory());
		Assert.assertEquals("No found the expected plugins number", 26, plugins
				.list().length);

		// launch to be sure
		Commandline cmd = new Commandline();
		cmd.setExecutable(launcher.getAbsolutePath());

		StringWriter logWriter = new StringWriter();
		StreamConsumer out = new WriterStreamConsumer(logWriter);
		StreamConsumer err = new WriterStreamConsumer(logWriter);
		int returnCode = CommandLineUtils.executeCommandLine(cmd, out, err);
		Assert.assertEquals("Didn't get a controled exit\n" + logWriter, 101,
				returnCode);
	}

	@Test
	public void exportFeatureProduct() throws Exception {
		Verifier verifier = getVerifier("/tycho109/feature-rcp");
		verifier.executeGoal("package");
		verifier.verifyErrorFreeLog();

		File basedir = new File(verifier.getBasedir());
		File output = new File(basedir, "target/product");

		Assert.assertTrue("Exported product folder not found\n" + output.getAbsolutePath(), output
				.isDirectory());
		File launcher = getLauncher(output, null);
		Assert.assertTrue("Launcher not found\n" + launcher, launcher.isFile());
		Assert.assertTrue("config.ini not found", new File(output,
				"configuration/config.ini").isFile());

		File plugins = new File(output, "plugins");
		Assert.assertTrue("Plugins folder not found", plugins.isDirectory());
		Assert.assertEquals("No found the expected plugins number", 324,
				plugins.list().length);

		File features = new File(output, "features");
		Assert.assertTrue("Features folder not found", features.isDirectory());
		Assert.assertEquals("No found the expected features number", 18,
				features.list().length);

		// launch to be sure
		Commandline cmd = new Commandline();
		cmd.setExecutable(launcher.getAbsolutePath());

		StringWriter logWriter = new StringWriter();
		StreamConsumer out = new WriterStreamConsumer(logWriter);
		StreamConsumer err = new WriterStreamConsumer(logWriter);
		int returnCode = CommandLineUtils.executeCommandLine(cmd, out, err);
		Assert.assertEquals("Didn't get a controled exit\n" + logWriter, 101,
				returnCode);
	}

	private File getLauncher(File output, String expectedName) {
		if (expectedName == null) {
			expectedName = "launcher";
		}
		String os = System.getProperty("os.name").toLowerCase();
		if (os.startsWith(WINDOWS_OS)) {
			return new File(output, expectedName + ".exe");
		} else if (os.startsWith(LINUX_OS)) {
			return new File(output, expectedName);
		} else if (os.startsWith(MAC_OS) || os.startsWith(MAC_OS_DARWIN)) {
			return new File(output, "Eclipse.app/Contents/MacOS/"
					+ expectedName);
		} else {
			Assert.fail("Unable to determine launcher to current OS: " + os);
			return null;
		}
	}

}
