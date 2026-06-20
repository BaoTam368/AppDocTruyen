TASK MỚI: 
* Tranh thủ vì không còn nhiều thời gian nữa 
1) Xử lý phần lấy dữ liệu từ mangadex api(lưu ý là nên cài 1.1.1.1 để sử dụng đc), sử dụng Nodejs để lưu trữ dữ liệu của user, comment, bài viết, truyện,... ( Nguyễn Thắng)
2) Xử lý gg firebase đăng nhập, đăng ký, xử lý việc nạp xu, thanh toán , mua truyện (từng chap) (Thanh An)
3) Xử lý phần trình bày truyện lên trang web (cả việc lấy trong csdl sqlite và nodejs), thanh tìm kiếm (Phạm Đưc)
4) Tiến hành liên kết sqlite và thực hiện việc lưu truyện, thực hiện chức năng trình bày truyện trên app (là mấy cái trang truyện á) ( Minh Đức)
5) Xử lý phần comment, bình luận, đăng bài cho user, phần nền tối ( Bảo Tâm)
* Cảnh báo có thể bỏ phần nhóm dịch vì mình lấy truyện từ api nên đâu cần nhóm dịch up truyện
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Tiếp theo phần cấu trúc project tạm
1)  api: chứa các interface cho api của nodejs và managadex, đông thời cấu hình chung cho retrofit
2) local: khởi tạo csdl(Database) và chưá các câu lệnh SQL(DAO)
3) firebase: Nơi để liên kết với gg firebase 
4) viewmodel: Nơi trung gian logic giữa dữ liệu và api để điều kiển ui
5) models: chưá object
6) views: chứa toàn bộ activities, adapters, fragments
Có thể thay đổi thêm hoặc bớt hay gì tuy, nhưng đừng tạo class bên ngoài loạn lắm
