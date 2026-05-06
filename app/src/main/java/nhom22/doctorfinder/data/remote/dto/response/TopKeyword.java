package nhom22.doctorfinder.data.remote.dto.response;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for a single item from:
 * GET /api/admin/reports/top-search-keywords
 *
 * Example response item:
 * { "rank": 1, "keyword": "đau đầu", "count": 152 }
 */
public class TopKeyword {

    @SerializedName("rank")
    private int rank;

    @SerializedName("keyword")
    private String keyword;

    @SerializedName("count")
    private int count;

    // --- Getters ---

    public int getRank() {
        return rank;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getCount() {
        return count;
    }
}
