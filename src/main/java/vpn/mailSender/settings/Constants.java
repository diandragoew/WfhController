package vpn.mailSender.settings;

public class Constants {

	public static final String JIRA_PASSWORD = IniReader.properties.get("Common", "JIRA_PASSWORD");
	public static final String JIRA_USER = IniReader.properties.get("Common", "JIRA_USER");
	public static final String KEY_FOR_JQL_QUERY = IniReader.properties.get("Common", "KEY_FOR_JQL_QUERY");
	public static final String COUNT_OF_TAKE_TASKS = IniReader.properties.get("Common", "COUNT_OF_TAKE_TASKS");

	public static final String CONSTANT_AUTHORIZATION_FOR_CONNECT_WITH_APIS = IniReader.properties
			.get("Common", "CONSTANT_AUTHORIZATION_FOR_CONNECT_WITH_APIS");

	public static final String HRC_URL = IniReader.properties.get("URLs","HRC_LINK");
	public static final String HRC_REQUEST_DEPARTMENTS_WITH_TL = IniReader.properties.get("Common", "HRC_REQUEST_DEPARTMENTS_WITH_TL");
	public static final String HRC_REQUEST_DEPARTMENTS_WITH_DTL = IniReader.properties.get("Common", "HRC_REQUEST_DEPARTMENTS_WITH_DTL");
	public static final String HRC_REQUEST_GET_USER=IniReader.properties.get("Common", "HRC_REQUEST_GET_USER");

	public static final String MMPI_URL = IniReader.properties.get("Common", "MMPI_URL");
	public static final String MMPI_REQUEST_TAKE_PROJECT_RESPONDENTS = IniReader.properties.get("Common", "MMPI_REQUEST_TAKE_PROJECT_RESPONDENTS");

	public static final String DEPARTMENT_VARIANTS_FILE = IniReader.properties.get("Files", "DEPARTMENT_VARIANTS_FILE");

	public static final String VPN_REQUEST = IniReader.properties.get("URLs","VPN_REQUEST");

}
