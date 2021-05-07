import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class SheetsIntegration {

    public static Sheets sheetsService;
    public static String APPLICATION_NAME = "Google Sheets Example";
    public static String SPREDSHEET_ID = "1X-L99KlZeOlX89j_n4LohpUBZYE9E15Mb40Tv9UF6I0";

    public static void setSheetsService(Sheets sheetsService) {
        SheetsIntegration.sheetsService = sheetsService;
    }

    private static Credential authorize() throws IOException, GeneralSecurityException {
        System.out.println("Начало авторизации");

//        https://stackoverflow.com/questions/32476746/com-google-api-client-googleapis-json-googlejsonresponseexception-403-forbidden
//        final List<String> SCOPES =
//                Arrays.asList(SheetsScopes.SPREADSHEETS,SheetsScopes.DRIVE);
//        System.out.println(SCOPES);
//
//         final java.io.File DATA_STORE_DIR = new java.io.File(
//                System.getProperty("user.home"), ".credentials/2/sheets.googleapis.com-java-quickstart.json");
//        System.out.println(DATA_STORE_DIR);


        InputStream in = SheetsIntegration.class.getResourceAsStream("/credentials.json");
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(), new InputStreamReader(in)
        );
        System.out.println("Получение данных");
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver())
                .authorize("user");
        System.out.println("Конец авторизации");
        return credential;
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
