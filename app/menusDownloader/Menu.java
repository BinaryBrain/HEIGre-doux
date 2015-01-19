package menusDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Menu {

    private String provenance;
    private List<Aliment> aliments = new ArrayList<>();

    public Menu(List<String> menu) {
        aliments.add(new Aliment(capFirst(menu.remove(0)), Type.Entree));
        aliments.add(new Aliment(capFirst(menu.remove(menu.size() - 1)), Type.Dessert));

        parseProvenance(menu);
        aliments.add(new Aliment(capFirst(menu.remove(0)), Type.Viande));

        parseSauce(menu);
        sanitize(menu);

        for (String m : menu) {
            aliments.add(new Aliment(capFirst(m), Type.Accompagnement));
        }
    }

    /**
    * Helper method. Given a List<String>, finds the provenance (if exists) of the meat.
    * @param lines A List<String> representing the menu.
    */
    private void parseProvenance(List<String> lines) {
        String regex = "\\((.{1,3})\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            matcher = pattern.matcher(line);

            if (matcher.find()) {
                provenance = matcher.group(1);
                line = line.replaceAll(regex, "");
                line = line.replaceAll("\\s\\s+", " ").trim();

                lines.remove(i);
                lines.add(i, line);
            }
        }
    }

    /**
     * Helper method. Can be called to clean up the the remaining data once everything as been parsed.
     * @param lines The remaining lines to be cleaned up.
     */
    private void sanitize(List<String> lines) {
        String line;
        String regex = "\\(.*\\)";

        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).matches(regex)) {
                line = lines.remove(i).replaceAll(regex, "").trim();

                if (!line.isEmpty()) {
                    lines.add(i, line);
                }
            }
        }
    }

    /**
     * Helper method. Attempts to find the sauce of the menu given a List<String>.
     * @param lines A List<String> representing the menu.
     */
    private void parseSauce(List<String> lines) {
        if (lines.get(0).contains("«")) {
            String line = lines.remove(0);
            line = line.substring(line.indexOf("«")).replace("«", "").replace("»", "");
            aliments.add(new Aliment(capFirst(line.trim()), Type.Sauce));
        }
    }

    /**
     * Converts the first character of a String to upper case.
     * @param  str The String to convert
     * @return     A String in lower case with the first character in upper case.
     */
    private String capFirst(String str) {
        str = str.toLowerCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public String toString() {
        return "Menu: { aliments: " + aliments +
               ", provenance: " + provenance + "}";
    }

    public List<Aliment> getAliments() {
        return aliments;
    }

    public String getProvenance() {
        return provenance;
    }
}
