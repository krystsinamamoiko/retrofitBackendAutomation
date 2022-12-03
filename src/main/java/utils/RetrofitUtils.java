package utils;

import lombok.experimental.UtilityClass;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

@UtilityClass
public class RetrofitUtils {

    Properties prop = new Properties();
    private static InputStream configFile;
    HttpLoggingInterceptor httpLogging = new HttpLoggingInterceptor();
    LoggingInterceptor logging = new LoggingInterceptor();
    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    static {
        try {
            configFile = new FileInputStream("src/main/resources/my.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String getBaseUrl() throws IOException {
        prop.load(configFile);
        return prop.getProperty("url");
    }

    public Retrofit getRetrofit() throws IOException {
        httpLogging.setLevel(BODY);
        httpClient.addInterceptor(logging);
        return new Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .client(httpClient.build())
            .build();
    }
}
