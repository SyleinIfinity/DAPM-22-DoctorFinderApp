package nhom22.doctorfinder.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Quản lý SharedPreferences tập trung cho toàn app.
 * Lưu token đăng nhập và thông tin cơ bản của người dùng.
 */
public class SharedPrefManager {

    private static final String PREF_NAME    = "DoctorFinderPrefs";
    private static final String KEY_TOKEN    = "auth_token";
    private static final String KEY_USER_ID  = "user_id";
    private static final String KEY_ROLE     = "user_role";

    private static SharedPrefManager instance;
    private final SharedPreferences prefs;

    private SharedPrefManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /** Singleton – thread-safe khởi tạo đơn giản */
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    // ─── Token ────────────────────────────────────────────────────────────────

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    /** Trả về chuỗi dạng "Bearer <token>" sẵn dùng cho Retrofit @Header */
    public String getBearerToken() {
        String token = prefs.getString(KEY_TOKEN, "");
        if (token == null || token.isEmpty()) return "";
        return "Bearer " + token;
    }

    public String getRawToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public boolean isLoggedIn() {
        return !getRawToken().isEmpty();
    }

    // ─── User ID ──────────────────────────────────────────────────────────────

    public void saveUserId(int userId) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply();
    }

    /** Trả về maNguoiDung; -1 nếu chưa đăng nhập */
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    // ─── Role ─────────────────────────────────────────────────────────────────

    public void saveRole(String role) {
        prefs.edit().putString(KEY_ROLE, role).apply();
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "");
    }

    // ─── Clear ────────────────────────────────────────────────────────────────

    public void clear() {
        prefs.edit().clear().apply();
    }
}
