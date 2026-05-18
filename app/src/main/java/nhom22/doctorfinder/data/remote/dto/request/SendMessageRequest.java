package nhom22.doctorfinder.data.remote.dto.request;

/**
 * Body POST /api/conversations/{id}/messages
 */
public class SendMessageRequest {

    public int maTaiKhoanGui;
    public String noiDungTinNhan;
    public String loaiNoiDung;

    public SendMessageRequest(int maTaiKhoanGui, String noiDungTinNhan, String loaiNoiDung) {
        this.maTaiKhoanGui = maTaiKhoanGui;
        this.noiDungTinNhan = noiDungTinNhan;
        this.loaiNoiDung = loaiNoiDung;
    }
}
