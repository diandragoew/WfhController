package vpn.mailSender.helpClasses;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class HTMLTableBuilder {
  private static final Logger logger = LogManager.getLogger(HTMLTableBuilder.class);

  private final int columns;
  private final StringBuilder table = new StringBuilder();
  private static String HTML_START = "<html>";
  private static String HTML_END = "</html>";
  private static String TABLE_START_BORDER = "<table border=\"1\">";
  private static String TABLE_START = "<table>";
  private static String TABLE_END = "</table>";
  private static String HEADER_START = "<th>";
  private static String HEADER_END = "</th>";
  private static String ROW_START = "<tr>";
  private static String ROW_END = "</tr>";
  private static String COLUMN_START = "<td>";
  private static String COLUMN_END = "</td>";


  public HTMLTableBuilder(String header, boolean border, int rows, int columns)
  {
    this.columns = columns;
    table.append(HTML_START);
    if (header != null) {
      table.append(header);
    }
    table.append(border ? TABLE_START_BORDER : TABLE_START);
    table.append(TABLE_END);
    table.append(HTML_END);
  }

  public void addTableHeader(String... values)
  {
    if (values.length != columns) {
      logger.error("Error column length");
    }
    else {
      int lastIndex = table.lastIndexOf(TABLE_END);
      if (lastIndex > 0) {
        createRow(lastIndex, HEADER_START, HEADER_END, values);
      }
    }
  }

  private void createRow(int lastIndex, String headerStart, String headerEnd, String[] values) {
    StringBuilder sb = new StringBuilder();
    sb.append(ROW_START);
    for (String value : values) {
      sb.append(headerStart);
      sb.append(value);
      sb.append(headerEnd);
    }
    sb.append(ROW_END);
    table.insert(lastIndex, sb);
  }

  public void addRowValues(String... values)
  {
    if (values.length != columns) {
      logger.error("Error column length");
    }
    else {
      int lastIndex = table.lastIndexOf(ROW_END);
      if (lastIndex > 0) {
        int index = lastIndex + ROW_END.length();
        createRow(index, COLUMN_START, COLUMN_END, values);
      }
    }
  }

  public String build()
  {
    return table.toString();
  }
}