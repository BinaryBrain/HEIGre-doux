package menusDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Menu {
    private String entree;
    private String viande;
    private String provenance;
    private String sauce;
    private String dessert;
    private List<String> accompagnement;

    public Menu(List<String> menu) {
        entree = menu.remove(0);
        dessert = menu.remove(menu.size() - 1);
        parseProvenance(menu);
        viande = menu.remove(0);
        parseSauce(menu);
        sanityze(menu);
        accompagnement = menu;
    }

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

    private void sanityze(List<String> lines) {
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

    private void parseSauce(List<String> lines) {
        if (lines.get(0).contains("«")) {
            String line = lines.remove(0);
            line = line.substring(line.indexOf("«")).replace("«", "").replace("»", "");
            sauce = line.trim();
        }
    }

    public void print() {
        System.out.println("entree: " + entree);
        System.out.println("viande: " + viande);
        System.out.println("provenance: " + provenance);
        System.out.println("sauce: " + sauce);
        System.out.println("dessert: " + dessert);
        System.out.print("accompagnement: ");
        for (String s : accompagnement) {
            System.out.println(s);
        }
    }

    public String getEntree() {
        return entree;
    }

    public String getViande() {
        return viande;
    }

    public String getProvenance() {
        return provenance;
    }

    public String getSauce() {
        return sauce;
    }

    public String getDessert() {
        return dessert;
    }

    public List<String> getAccompagnement() {
        return new ArrayList<String>(accompagnement);
    }
}
