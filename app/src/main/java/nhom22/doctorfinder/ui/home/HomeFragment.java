package nhom22.doctorfinder.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nhom22.doctorfinder.R;
import nhom22.doctorfinder.data.remote.api.ReportApiService;
import nhom22.doctorfinder.data.remote.client.RetrofitClient;
import nhom22.doctorfinder.data.remote.dto.response.TopDoctor;
import nhom22.doctorfinder.data.remote.dto.response.TopKeyword;
import nhom22.doctorfinder.ui.user.search.SearchBoxActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private HomeViewModel mViewModel;

    // ── AI Suggestion Banner ──────────────────────────────────────────────────
    /** Displays the AI suggestion text inside the banner card */
    private TextView tvAiSuggestion;

    // ── Recommended Doctor Card 1 ─────────────────────────────────────────────
    private TextView tvDoctorName1;
    private TextView tvSpecialty1;
    private TextView tvRating1;
    private TextView tvExperience1;

    // ── Recommended Doctor Card 2 ─────────────────────────────────────────────
    private TextView tvDoctorName2;
    private TextView tvSpecialty2;
    private TextView tvRating2;
    private TextView tvExperience2;

    // ── Retrofit service ──────────────────────────────────────────────────────
    private ReportApiService reportApiService;

    // ─────────────────────────────────────────────────────────────────────────

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Build the Retrofit service once; reuse the shared RetrofitClient singleton
        reportApiService = RetrofitClient.getClient().create(ReportApiService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ── Search bar click → SearchBoxActivity ──────────────────────────────
        View searchBarContainer = view.findViewById(R.id.searchBarContainer);
        if (searchBarContainer != null) {
            searchBarContainer.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), SearchBoxActivity.class);
                startActivity(intent);
            });
        }

        // ── Bind view references ──────────────────────────────────────────────

        // AI suggestion banner — the static TextView at lines 398-405 of fragment_home.xml
        // We traverse: banner CardView → LinearLayout (horizontal) → LinearLayout (vertical)
        // → second child TextView (index 1). Using tag-less approach: find the text directly
        // via the parent view's child traversal is brittle, so we add @id to the TextView.
        // Since we cannot change XML, we locate it by its position in the card hierarchy.
        //
        // Layout path:
        //   CardView (no id)
        //     └─ LinearLayout (horizontal, bg_banner_gradient)
        //         └─ FrameLayout (bulb icon)
        //         └─ LinearLayout (vertical) ← index 1
        //             └─ LinearLayout (horizontal) ← index 0
        //             └─ TextView ← index 1  ★ this is tvAiSuggestion
        //             └─ MaterialButton (btnViewNeurology) ← index 2
        tvAiSuggestion = findAiSuggestionTextView(view);

        // Recommended Doctor Card 1  (include id="recommendedDoctor1")
        View card1 = view.findViewById(R.id.recommendedDoctor1);
        if (card1 != null) {
            tvDoctorName1  = card1.findViewById(R.id.tvDoctorName);
            tvSpecialty1   = card1.findViewById(R.id.tvSpecialty);
            tvRating1      = card1.findViewById(R.id.tvRating);
            tvExperience1  = card1.findViewById(R.id.tvExperience);
        }

        // Recommended Doctor Card 2  (include id="recommendedDoctor2")
//        View card2 = view.findViewById(R.id.recommendedDoctor2);
//        if (card2 != null) {
//            tvDoctorName2  = card2.findViewById(R.id.tvDoctorName);
//            tvSpecialty2   = card2.findViewById(R.id.tvSpecialty);
//            tvRating2      = card2.findViewById(R.id.tvRating);
//            tvExperience2  = card2.findViewById(R.id.tvExperience);
//        }

        // ── Kick off API calls ────────────────────────────────────────────────
        fetchTopDoctors();
        fetchTopKeywords();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API: Top Doctors → Recommended Doctor cards
    // ─────────────────────────────────────────────────────────────────────────

    private void fetchTopDoctors() {
        reportApiService.getTopDoctors("BOOKING", 2)
                .enqueue(new Callback<List<TopDoctor>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<TopDoctor>> call,
                                           @NonNull Response<List<TopDoctor>> response) {
                        if (!isAdded()) return; // fragment may have been detached

                        if (response.isSuccessful() && response.body() != null) {
                            List<TopDoctor> doctors = response.body();
                            bindTopDoctors(doctors);
                        } else {
                            Log.w(TAG, "getTopDoctors: unsuccessful response code=" + response.code());
                            showToast("Không thể tải bác sĩ đề xuất (lỗi " + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<TopDoctor>> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Log.e(TAG, "getTopDoctors: network failure", t);
                        showToast("Lỗi kết nối khi tải bác sĩ đề xuất");
                    }
                });
    }

    /**
     * Maps a list of {@link TopDoctor} items into the two recommended-doctor card views.
     * Only the first 2 items are used (API already limits to 2, but we guard anyway).
     */
    private void bindTopDoctors(@NonNull List<TopDoctor> doctors) {
        if (doctors.size() >= 1) {
            TopDoctor d1 = doctors.get(0);
            safeSetText(tvDoctorName1, d1.getHoTenDayDu());
            safeSetText(tvSpecialty1,  "Top Doctor");
            safeSetText(tvRating1,     "⭐ " + d1.getCount());
            safeSetText(tvExperience1, "N/A");
        }

        if (doctors.size() >= 2) {
            TopDoctor d2 = doctors.get(1);
            safeSetText(tvDoctorName2, d2.getHoTenDayDu());
            safeSetText(tvSpecialty2,  "Top Doctor");
            safeSetText(tvRating2,     "⭐ " + d2.getCount());
            safeSetText(tvExperience2, "N/A");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API: Top Keywords → AI Suggestion Banner
    // ─────────────────────────────────────────────────────────────────────────

    private void fetchTopKeywords() {
        // Use a rolling 30-day window ending at the current moment
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String to   = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, -30);
        String from = sdf.format(cal.getTime());

        reportApiService.getTopSearchKeywords(from, to, 1)
                .enqueue(new Callback<List<TopKeyword>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<TopKeyword>> call,
                                           @NonNull Response<List<TopKeyword>> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null
                                && !response.body().isEmpty()) {
                            TopKeyword top = response.body().get(0);
                            bindTopKeyword(top);
                        } else {
                            Log.w(TAG, "getTopSearchKeywords: empty or unsuccessful response code="
                                    + response.code());
                            // Leave the default static text in the banner
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<TopKeyword>> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        Log.e(TAG, "getTopSearchKeywords: network failure", t);
                        // Leave the default static text in the banner; no Toast needed for suggestions
                    }
                });
    }

    /**
     * Updates the AI suggestion banner text with the top search keyword.
     *
     * Example output: "Bạn hay tìm 'đau đầu' → Khám phá bác sĩ liên quan"
     */
    private void bindTopKeyword(@NonNull TopKeyword keyword) {
        if (tvAiSuggestion == null) return;
        String suggestion = "Bạn hay tìm '" + keyword.getKeyword()
                + "' → Khám phá bác sĩ liên quan";
        tvAiSuggestion.setText(suggestion);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Finds the AI suggestion body TextView inside the banner card without
     * modifying the XML layout.
     *
     * Layout hierarchy (from fragment_home.xml):
     *   NestedScrollView → LinearLayout (root) → CardView (AI banner, 3rd card-ish)
     *     → LinearLayout (horizontal, bg_banner_gradient)
     *         [0] FrameLayout (bulb icon)
     *         [1] LinearLayout (vertical, weight=1)
     *               [0] LinearLayout (row with "💡 Gợi ý cho bạn")
     *               [1] TextView  ← the suggestion body  ★
     *               [2] MaterialButton (btnViewNeurology)
     *
     * We navigate via {@code btnViewNeurology} which already has an @+id, then
     * walk up to its sibling TextView — this is more robust than child-index math.
     */
    @Nullable
    private TextView findAiSuggestionTextView(@NonNull View root) {
        try {
            // btnViewNeurology is at index 2 inside the inner LinearLayout
            View btn = root.findViewById(R.id.btnViewNeurology);
            if (btn == null) return null;

            ViewGroup innerLinear = (ViewGroup) btn.getParent(); // LinearLayout (vertical)
            // The suggestion TextView is at index 1 (between the title row and the button)
            View candidate = innerLinear.getChildAt(1);
            if (candidate instanceof TextView) {
                return (TextView) candidate;
            }
        } catch (Exception e) {
            Log.e(TAG, "findAiSuggestionTextView: failed to locate view", e);
        }
        return null;
    }

    /** Null-safe setText helper. */
    private void safeSetText(@Nullable TextView tv, @Nullable String text) {
        if (tv != null && text != null) {
            tv.setText(text);
        }
    }

    /** Shows a short Toast if the fragment is still attached. */
    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
