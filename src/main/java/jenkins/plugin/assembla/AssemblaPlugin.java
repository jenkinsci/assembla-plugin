package jenkins.plugin.assembla;

import hudson.Functions;
import hudson.Plugin;

public class AssemblaPlugin extends Plugin {
    public static final String NAME = "assembla";

	private transient AssemblaSCMListener scmListener;
	
	@Override
	public void start() throws Exception {

		scmListener = new AssemblaSCMListener();
		scmListener.register();

		super.start();
	}
	
	@Override
	public void stop() throws Exception {
	
		scmListener.unregister();
		
		super.stop();
	}

    public static String getResourcePath(String resourceFileName) {
        if (resourceFileName.startsWith("/")) {
            resourceFileName = resourceFileName.substring(1);
        }
        String assemblaLogoUrl = String.format("%s/plugin/" + NAME + "/%s",
                Functions.getResourcePath(), resourceFileName);
        return assemblaLogoUrl;
    }
}
