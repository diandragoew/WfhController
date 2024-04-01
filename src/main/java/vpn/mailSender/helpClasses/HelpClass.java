package vpn.mailSender.helpClasses;

import java.util.Collection;

public class HelpClass {
    public static String makeCollectionWithStringsToStringWithCommas(Collection<String> strings) {
        if(strings.size()==0){
            return "";
        }
        StringBuilder stringBuilderWithCommas = new StringBuilder();
        for (String string : strings) {
            stringBuilderWithCommas.append(string).append(",");
        }
        String stringsWithCommas = stringBuilderWithCommas.toString();
        stringsWithCommas = stringsWithCommas.replaceAll(",$", "");

        return stringsWithCommas;
    }
}
