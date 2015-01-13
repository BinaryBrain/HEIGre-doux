package menusDownloader;

import java.io.*;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuDownloader {
    private String host;
    private String baseUrl;

    private HttpURLConnection connection;

    public MenuDownloader(final String userName, final String password, String url, final String domain) {
        baseUrl = url;

        Authenticator.setDefault(new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(domain + "\\" + userName, password.toCharArray());
            }
        });
    }

    public void downloadDocx(String fileName, int number) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName, false);
        byte[] buffer = new byte[1024];
        int read;

        openConnection(getMenuUrls()[number * 2]); //WOW so hardcore

        InputStream stream = connection.getInputStream();
        while ((read = stream.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
    }

    private void openConnection(String url) throws IOException {
        URL urlRequest = new URL(url);
        if (host == null) {
            host = urlRequest.getProtocol() + "://" + urlRequest.getHost();
        }

        connection = (HttpURLConnection) urlRequest.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("GET");
    }

    private String[] getMenuUrls() throws IOException {
        openConnection(baseUrl);

        StringBuilder response = new StringBuilder();
        InputStream stream = connection.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        String str;

        while ((str = in.readLine()) != null) {
            response.append(str);
        }
        in.close();

        String html = response.toString();

        String regex = "HREF=\"(/campus/cafeterias.*?\\.docx)\"";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);

        ArrayList<String> matches = new ArrayList<String>();

        while (matcher.find()) {
            matches.add(matcher.group(1));
        }

        String[] urls = new String[matches.size()];

        for (int i = 0; i < matches.size(); i++) {
            urls[i] = host + matches.get(i);
        }

        return urls;
    }
}