TASK MỚI:
* Tranh thủ vì không còn nhiều thời gian nữa
1) Xử lý phần lấy dữ liệu từ MangaDex API (lưu ý là nên cài 1.1.1.1 để sử dụng được), sử dụng Node.js để lưu trữ dữ liệu của user, comment, bài viết, truyện,... (Nguyễn Thắng)
2) Xử lý Google Firebase đăng nhập, đăng ký, xử lý việc nạp xu, thanh toán, mua truyện (từng chap) (Thanh An)
3) Xử lý phần trình bày truyện lên trang web (cả việc lấy trong CSDL SQLite và Node.js), thanh tìm kiếm (Phạm Đức)
4) Tiến hành liên kết sqlite và thực hiện việc lưu truyện, thực hiện chức năng trình bày truyện trên app (là mấy cái trang truyện á) ( Minh Đức)
5) Xử lý phần comment, bình luận, đăng bài cho user, phần nền tối ( Bảo Tâm)
* Cảnh báo có thể bỏ phần nhóm dịch vì mình lấy truyện từ api nên đâu cần nhóm dịch up truyện
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Tiếp theo phần cấu trúc project tạm
1)  api: chứa các interface cho API của Node.js và MangaDex, đồng thời cấu hình chung cho Retrofit
2) local: khởi tạo CSDL (Database) và chứa các câu lệnh SQL (DAO)
3) firebase: Nơi để liên kết với Google Firebase
4) viewmodel: Nơi trung gian logic giữa dữ liệu và API để điều khiển UI
5) models: chứa object
6) views: chứa toàn bộ activities, adapters, fragments
Có thể thay đổi thêm hoặc bớt hay gì tuy, nhưng đừng tạo class bên ngoài loạn lắm
