package jenkins.plugin.assembla;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static int getTicketNumber(String group) {
        Matcher numberMatcher = Pattern.compile("[0-9]+").matcher(group);
        if (!numberMatcher.find()) {
            throw new NumberFormatException(
                    "Cannot find any valid ticket number in sequence '" + group
                            + "'");
        }
        return Integer.parseInt(numberMatcher.group());
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
