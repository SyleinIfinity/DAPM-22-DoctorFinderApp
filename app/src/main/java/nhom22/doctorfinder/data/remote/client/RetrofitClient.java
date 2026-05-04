package nhom22.doctorfinder.data.remote.client;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.*;
import java.security.cert.CertificateException;

public class RetrofitClient {

    private static final String BASE_URL = "https://34.126.165.66/finder-doctor/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {

            OkHttpClient client = getUnsafeOkHttpClient();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {}
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[]{}; }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .addInterceptor(chain -> {

                        String username = "sylein"; // nhập cái bạn dùng trong Swagger
                        String password = "123456";

                        String credentials = username + ":" + password;

                        String basicAuth = "Basic " + android.util.Base64.encodeToString(
                                credentials.getBytes(),
                                android.util.Base64.NO_WRAP
                        );

                        okhttp3.Request request = chain.request().newBuilder()
                                .addHeader("Authorization", basicAuth)
                                .build();

                        return chain.proceed(request);
                    })
                    .build();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}