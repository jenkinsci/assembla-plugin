package jenkins.plugin.assembla;

import hudson.Extension;
import hudson.MarkupText;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.Hudson;
import hudson.scm.ChangeLogAnnotator;
import hudson.scm.ChangeLogSet.Entry;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jenkins.model.Jenkins;
import jenkins.plugin.assembla.api.AssemblaSite;
import jenkins.plugin.assembla.api.AssemblaTicketsAPI;
import jenkins.plugin.assembla.api.AssemblaTicketsAPI.AssemblaTicket;

@Extension
public class AssemblaChangeLogAnnotator extends ChangeLogAnnotator {

    private static final Logger LOGGER = Logger
            .getLogger(AssemblaChangeLogAnnotator.class.getName());

    @Override
    public void annotate(AbstractBuild<?, ?> build, Entry change,
            MarkupText text) {

        AssemblaSite site = AssemblaSite.get(build.getProject());

        if (!site.isPluginEnabled()) {

            return;
        }

        LOGGER.info("Annotating change");

        String commitMessage = change.getMsg();
        Pattern pattern = Pattern.compile(site.getPatternInternal());
        Matcher m = pattern.matcher(commitMessage);

        AssemblaTicketsAPI ticketApi = new AssemblaTicketsAPI(site);

        while (m.find()) {
                String ticketGroupString = m.group();
                try {
                    int ticketNumber = AssemblaPlugin.getTicketNumber(ticketGroupString);
                    LOGGER.info("Annotating ASSEMBLA ticket: '" + ticketNumber
                            + "'");

                    AssemblaTicket ticket = ticketApi.getTicket(
                            site.getSpace(), ticketNumber);

                    if (ticket == null) {
                        continue;
                    }

                    String assemblaLogoUrl = AssemblaPlugin
                            .getResourcePath("assembla_icon.png");
                    text.addMarkup(m.start(), m.end(), String.format(
                            "<a href='%s' tooltip='%s' target='_blank'>%s",
                            ticket.getUrl(),
                            Util.escape(ticket.getDescription()), "<img src='"
                                    + assemblaLogoUrl
                                    + "' style='margin: -2px 3px 0px 0px' />"),
                            "</a>");
                } catch (NumberFormatException e) {
                    LOGGER.warning("Skipping '" + ticketGroupString + "': "
                            + e.getLocalizedMessage());
                }
        }
    }
}
