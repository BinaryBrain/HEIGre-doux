import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MenuParser {

    private MenuParser() {
    }

    public static List<List<Menu>> parseMenusDocx(String fileName) throws IOException, InvalidFormatException {
        FileInputStream fis = new FileInputStream(fileName);
        XWPFDocument document = new XWPFDocument(OPCPackage.open(fis));
        List<XWPFTable> tables = document.getTables();

        List<List<List<String>>> menusCells = new ArrayList<List<List<String>>>();
        menusCells.add(new ArrayList<List<String>>());
        menusCells.add(new ArrayList<List<String>>());

        for (XWPFTable table : tables) {
            List<XWPFTableRow> rows = table.getRows();

            for (XWPFTableRow row : rows) {
                for (int i = 0; i < menusCells.size(); i++) {
                    List<String> lines = new ArrayList<String>();

                    for (XWPFParagraph paragraph : row.getCell(i + 1).getParagraphs()) {
                        String text;

                        if (!(text = paragraph.getText()).matches("\\s*")) {
                            lines.add(text.replaceAll("\u00A0+", " "));
                        }
                    }

                    menusCells.get(i).add(lines);
                }
            }
        }

        List<List<Menu>> menus = new ArrayList<List<Menu>>();
        menus.add(new ArrayList<Menu>());
        menus.add(new ArrayList<Menu>());

        for (int i = 0; i < menus.size(); i++) {
            for (List<String> cell : menusCells.get(i)) {
                menus.get(i).add(new Menu(cell));
            }
        }

        return menus;
    }
}
