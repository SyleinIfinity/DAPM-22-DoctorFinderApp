package nhom22.doctorfinder.data.remote.dto.response;


import java.util.ArrayList;
import java.util.List;

import nhom22.doctorfinder.data.remote.dto.DoctorInfo;
import nhom22.doctorfinder.model.Doctor;

public class DoctorImageResponse {
    public DoctorInfo bacSi;
    public double similarityScore;
    public String matchedImageUrl;

    private void handleData(List<DoctorImageResponse> body) {

        List<Doctor> list = new ArrayList<>();

        for (DoctorImageResponse item : body) {

            DoctorInfo d = item.bacSi;

            Doctor doctor = new Doctor(
                    "0",
                    d.chuyenKhoa != null ? d.chuyenKhoa : "",
                    d.chuyenKhoa,
                    d.trinhDoChuyenMon != null ? d.trinhDoChuyenMon : "",
                    d.tenCoSoYTe,
                    (float) item.similarityScore, // ⭐ dùng similarity làm rating
                    0,
                    false,
                    0,
                    mapDoctorType(d.loaiHinhBacSi)
            );

            list.add(doctor);
        }

        fullList = list;

        applySort(currentSort);
        updateResultCount(list.size());
    }
}
