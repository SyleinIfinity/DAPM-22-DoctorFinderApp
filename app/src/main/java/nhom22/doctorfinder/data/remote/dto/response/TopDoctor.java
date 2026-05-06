package nhom22.doctorfinder.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for a single item from:
 * GET /api/admin/reports/top-doctors
 *
 * Example response item:
 * { "rank": 1, "maBacSi": 5, "hoTenDayDu": "PGS.TS Nguyễn Anh", "count": 48 }
 */
public class TopDoctor {

    @SerializedName("rank")
    private int rank;

    @SerializedName("maBacSi")
    private int maBacSi;

    @SerializedName("hoTenDayDu")
    private String hoTenDayDu;

    @SerializedName("count")
    private int count;

    // --- Getters ---

    public int getRank() {
        return rank;
    }

    public int getMaBacSi() {
        return maBacSi;
    }

    public String getHoTenDayDu() {
        return hoTenDayDu;
    }

    public int getCount() {
        return count;
    }
}
