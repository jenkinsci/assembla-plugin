package jenkins.plugin.assembla.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class AssemblaTicketsAPI extends AbstractAssemblaAPI {

	public class AssemblaTicket {

		private String reportedBy;
		private String number;
		private String assignedTo;
		private String description;
		private String url;

		public String getReportedBy() {
			return reportedBy;
		}

		public void setReportedBy(String reportedBy) {
			this.reportedBy = reportedBy;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

		public String getAssignedTo() {
			return assignedTo;
		}

		public void setAssignedTo(String assignedTo) {
			this.assignedTo = assignedTo;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		@Override
		public String toString() {

			return "Ticket #" + number + " Reported by:" + reportedBy
					+ " Assigned to: " + assignedTo;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}

	public AssemblaTicketsAPI(AssemblaSite assemblaSite) {

		super(assemblaSite);

	}

	public AssemblaTicket getTicket(String space, int ticketNumber) {

		this.space = space;

		List<AssemblaTicket> result = new LinkedList<AssemblaTicketsAPI.AssemblaTicket>();

		try {

			int response = client.executeHttpGet(space, "tickets/"
					+ ticketNumber);

			if (response == 200) {

				InputStream data = client.getResponseStream();
				AssemblaTicketsXmlDataParser parser = new AssemblaTicketsXmlDataParser(
						data, result);
				parser.start();
			} else {

				return null;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result.get(0);
	}

	public List<AssemblaTicket> getTickets(String space) {

		this.space = space;

		List<AssemblaTicket> result = new LinkedList<AssemblaTicketsAPI.AssemblaTicket>();

		try {

			int response = client.executeHttpGet(space, "tickets");

			if (response == 200) {

				InputStream data = client.getResponseStream();
				AssemblaTicketsXmlDataParser parser = new AssemblaTicketsXmlDataParser(
						data, result);
				parser.start();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public boolean doCommentTicket(String space, int ticketNumber,
			String comment) {

		boolean result = false;

		this.space = space;

		try {
			int response = client.executeHttpPut(space, "tickets/"
					+ ticketNumber, "<ticket><user-comment>"+comment+"</user-comment></ticket>");

			result = response == 200;

		} catch (IOException e) {

			result = false;

			e.printStackTrace();
		}

		return result;

	}

	class AssemblaTicketsXmlDataParser extends XmlDataParser {

		private List<AssemblaTicket> ticketsList;

		private AssemblaTicket currentTicket;

		public AssemblaTicketsXmlDataParser(InputStream instream,
				List<AssemblaTicket> ticketsList) {
			super(instream);

			this.ticketsList = ticketsList;
		}

		@Override
		protected void endElement(String elementName, String elementValue) {

			if ("ticket".equalsIgnoreCase(elementName)) {

				// https://www.assembla.com/spaces/lmit/tickets/56
				currentTicket.setUrl(assemblaSite.getUrl() + "/spaces/" + space
						+ "/tickets/" + currentTicket.getNumber());

				ticketsList.add(currentTicket);

			} else if ("number".equalsIgnoreCase(elementName)) {

				currentTicket.setNumber(elementValue);
			} else if ("reporter".equalsIgnoreCase(elementName)) {

				currentTicket.setReportedBy(elementValue);
			} else if ("assigned-to".equalsIgnoreCase(elementName)) {

				currentTicket.setAssignedTo(elementValue);
			} else if ("description".equalsIgnoreCase(elementName)) {

				currentTicket.setDescription(elementValue);
			}
		}

		@Override
		protected void startElement(String elementName) {

			if ("ticket".equalsIgnoreCase(elementName)) {

				currentTicket = new AssemblaTicket();
			}
		}
	}
}
