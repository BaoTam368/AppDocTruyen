# AppDocTruyen

AppDocTruyen là đồ án Android App đọc truyện. Ứng dụng Android viết bằng Java/XML, giao tiếp với Node.js backend qua Retrofit. Backend dùng Express để gọi MangaDex API lấy dữ liệu truyện/chapter/trang đọc, đồng thời dùng SQLite runtime để lưu/cache dữ liệu truyện, user, comment và bài viết.

Các màn chính hiện có trong app gồm Home, Search, Detail, Reader, Bookshelf, World và Profile. Một số phần vẫn là demo hoặc cần kiểm tra thêm theo tình trạng code hiện tại.

## 1. Công nghệ sử dụng

| Thành phần         | Công nghệ                    |
| ------------------ | ---------------------------- |
| Mobile app         | Android Java/XML             |
| IDE                | Android Studio               |
| Backend            | Node.js + Express            |
| API client Android | Retrofit 2.9.0 + Gson        |
| Ảnh                | Glide 4.16.0                 |
| Database Android   | SQLite local                 |
| Database Backend   | SQLite/better-sqlite3        |
| Nguồn truyện       | MangaDex API                 |

Ghi chú: Gradle hiện có bật Compose và một số dependency Compose, nhưng giao diện chính của đồ án đang nằm trong Java/XML layout.

## 2. Cấu trúc project

```text
AppDocTruyen/
  app/                  Android app Java/XML
  backend/              Backend chính Node.js + Express
  gradle/               Gradle wrapper/config
  README.md
  settings.gradle.kts
```

Trong root project hiện cũng có `package.json` và `server.js`, nhưng backend chính dùng để chạy app là thư mục `backend/`.

- Không chạy nhầm root `server.js` vì file này chỉ là Express tối giản, không mount các API `/api/manga`, `/api/local-manga`, `/api/users`, `/api/comments`, `/api/posts`.
- Không xóa root `package.json`/`server.js` nếu chưa hỏi nhóm, vì có thể là file cũ hoặc file test của nhóm.

## 3. Yêu cầu trước khi chạy

Cần cài trên máy:

- Android Studio.
- JDK 21, vì `app/build.gradle.kts` đang cấu hình `sourceCompatibility` và `targetCompatibility` là Java 21.
- Node.js LTS.
- npm.
- Git nếu clone từ GitHub.
- 1.1.1.1/WARP hoặc mạng khác nếu mạng hiện tại không truy cập được MangaDex.

Thông tin project hiện tại:

- Android Gradle Plugin: 9.1.1.
- compileSdk: 36.
- minSdk: 24.
- targetSdk: 36.

## 4. Cách tải project

```bash
git clone https://github.com/BaoTam368/AppDocTruyen.git
cd AppDocTruyen
```

Nếu nhóm dùng repository khác, thay URL bằng link repo thật:

```bash
git clone <URL_REPOSITORY>
cd AppDocTruyen
```

## 5. Chạy backend Node.js

Backend chính nằm trong thư mục `backend/`.

```bash
cd backend
npm install
npm start
```

Trong `backend/package.json` cũng có script dev:

```bash
npm run dev
```

Giải thích:

- `npm install` chỉ cần chạy lần đầu hoặc khi dependency thay đổi.
- `npm start` dùng để chạy backend bằng `node server.js`.
- `npm run dev` dùng `nodemon server.js`, tiện khi sửa backend.
- Backend thường chạy ở port 3000 vì `backend/server.js` dùng `process.env.PORT || 3000`.

Kiểm tra backend bằng trình duyệt hoặc Postman:

```text
http://localhost:3000/api/health
```

Nếu cần file `.env`, tạo từ file mẫu:

```bash
copy .env.example .env
```

Hoặc trên PowerShell:

```powershell
Copy-Item .env.example .env
```

Các biến trong `backend/.env.example`:

| Biến | Ý nghĩa |
| ---- | ------- |
| `PORT` | Port backend, mặc định 3000 |
| `MANGADEX_BASE_URL` | Base URL của MangaDex API |
| `REQUEST_TIMEOUT_MS` | Timeout khi backend gọi MangaDex |

Các route backend đang được mount trong `backend/server.js`:

- `GET /api/health`
- `/api/manga`
- `/api/chapter`
- `/api/local-manga`
- `/api/users`
- `/api/comments`
- `/api/posts`

## 6. Chạy Android app bằng emulator

1. Mở project bằng Android Studio.
2. Đợi Gradle Sync hoàn tất.
3. Chọn Android emulator.
4. Đảm bảo backend Node.js trong thư mục `backend/` đang chạy.
5. Kiểm tra base URL trong `app/src/main/java/com/example/appdoctruyen/data/api/ApiClient.java`.

Base URL hiện tại:

```text
http://10.0.2.2:3000/api/
```

`10.0.2.2` là địa chỉ đặc biệt để Android emulator gọi backend đang chạy trên máy tính. Sau đó bấm Run trong Android Studio.

## 7. Chạy Android app bằng điện thoại thật

Điện thoại thật không dùng được `10.0.2.2`. Máy tính chạy backend và điện thoại phải cùng Wi-Fi.

Lấy IP LAN của máy tính bằng:

```cmd
ipconfig
```

Ví dụ IP máy tính là:

```text
192.168.1.10
```

Thì base URL trong `ApiClient.java` phải đổi thành:

```text
http://192.168.1.10:3000/api/
```

Lưu ý:

- Cho phép firewall nếu Windows hỏi.
- Backend Node.js phải đang chạy.
- Điện thoại và máy tính phải cùng mạng.
- Sau khi đổi base URL, chạy lại app từ Android Studio.

## 8. Ghi chú về MangaDex API và 1.1.1.1

Backend gọi MangaDex API để lấy danh sách truyện, chi tiết truyện, chapter và ảnh đọc truyện. Một số mạng có thể chặn hoặc làm chậm MangaDex.

Nếu backend báo lỗi không lấy được truyện:

- Bật 1.1.1.1/WARP.
- Đổi mạng.
- Kiểm tra `http://localhost:3000/api/health`.
- Xem log backend trong terminal.

Đây có thể là vấn đề mạng, không phải lúc nào cũng do code.

## 9. Database và dữ liệu local

Backend dùng `better-sqlite3`. Khi chạy, backend có thể tạo SQLite runtime file:

```text
backend/mangas.db
backend/mangas.db-shm
backend/mangas.db-wal
```

Các bảng backend hiện được tạo trong `backend/src/services/databaseService.js`, gồm `mangas`, `chapters`, `users`, `comments`, `posts`.

Android cũng có SQLite local trong `BookshelfDatabaseHelper.java`, database tên `bookshelf.db`. Phần này dùng để lưu/đọc:

- bookmark/tủ sách;
- lịch sử đọc;
- truyện đã tải;
- comment local.

Nếu database Android rỗng, `BookshelfFragment` có fallback dữ liệu mẫu cho phần tủ sách/lịch sử đọc.

Không nên push file runtime lên GitHub:

- `node_modules/`
- `*.db`
- `*.db-shm`
- `*.db-wal`
- `.env`

Chỉ cần push `backend/package.json` và `backend/package-lock.json` để người khác tự chạy `npm install`.

## 10. Các chức năng chính

| Chức năng | Trạng thái hiện tại | Ghi chú |
| --------- | ------------------- | ------- |
| Home/truyện | Đã có | `ComicHomeFragment` gọi backend local manga và có nút đồng bộ MangaDex. Cần backend chạy. |
| Tìm kiếm | Đang hoàn thiện | `SearchActivity` tìm trong local manga qua backend; phần filter có UI nhưng `applySearch()` hiện chưa xử lý. |
| Chi tiết truyện | Đã có | `ComicDetailActivity`, `ComicInfoFragment`, `ComicChaptersFragment` lấy chi tiết/chapter qua backend. |
| Đọc truyện | Đã có | `ComicReadingActivity` lấy ảnh chapter qua backend; có fallback ảnh mẫu khi thiếu dữ liệu/lỗi. |
| Tủ sách | Đã có | Dùng SQLite Android `bookshelf.db`, có dữ liệu mẫu nếu DB rỗng. |
| Nhóm dịch | Cần kiểm tra thêm | Bottom nav còn item Nhóm dịch nhưng `MainActivity` hiện không mở fragment; backend hiện chưa mount `/api/groups`. |
| World/comment/post | Demo/đang hoàn thiện | UI post/comment có dữ liệu mẫu; backend có API `/api/comments` và `/api/posts`. |
| Profile/login/register | Demo/đang hoàn thiện | Màn login/register/profile có UI; `AuthManager` hiện trống, chưa thấy dependency Firebase trong Gradle. |
| Node.js backend | Đã có | Backend chính ở `backend/`, dùng Express, SQLite và route `/api/health`. |
| MangaDex API | Đã có | Backend gọi MangaDex bằng axios; cần kiểm tra mạng/1.1.1.1 nếu lỗi. |

Trạng thái trên dựa theo đọc source hiện tại, chưa phải kết quả chạy build hay chạy app.

## 11. Phân công nhóm

Theo `Task.md`:

- Nguyễn Thắng: xử lý MangaDex API, Node.js để lưu trữ dữ liệu user/comment/bài viết/truyện, Tủ sách và phần Nhóm dịch nếu nhóm quyết định giữ.
- Thanh An: Firebase đăng nhập/đăng ký, nạp xu, thanh toán, mua truyện từng chapter.
- Phạm Đức: trình bày truyện, lấy dữ liệu từ SQLite/Node.js, thanh tìm kiếm.
- Minh Đức: liên kết SQLite, lưu truyện, trình bày trang truyện trên app.
- Bảo Tâm: comment, bình luận, đăng bài cho user, phần nền tối/dark mode.

## 12. Lưu ý về Nhóm dịch

```text
Phần Nhóm dịch đang được nhóm cân nhắc giữ hoặc bỏ vì dữ liệu truyện lấy trực tiếp từ MangaDex API. Nếu giữ, cần đảm bảo tab Nhóm dịch có Fragment điều khiển và backend/API tương ứng. Nếu bỏ, cần dọn tab/navigation có chủ đích.
```

Ghi chú theo code hiện tại: `bottom_nav_menu.xml` vẫn có `nav_translation_team`, nhưng `MainActivity` đang để comment `Feature removed - translation teams not needed for API-based manga app`. Backend hiện chưa thấy route `/api/groups` được mount trong `backend/server.js`, dù Android interface có khai báo `GET groups`.

## 13. Lỗi thường gặp khi chạy

### Backend không chạy

- Kiểm tra đã `cd backend` chưa.
- Chạy `npm install`.
- Kiểm tra Node.js và npm:

```bash
node -v
npm -v
```

### Android không gọi được backend

- Emulator dùng `10.0.2.2`, không dùng `localhost`.
- Điện thoại thật dùng IP LAN của máy tính.
- Backend phải đang chạy.
- Kiểm tra firewall Windows.
- Kiểm tra base URL trong `ApiClient.java`.

### Không lấy được truyện MangaDex

- Bật 1.1.1.1/WARP.
- Đổi mạng.
- Kiểm tra endpoint `/api/health`.
- Kiểm tra log backend.

### Lỗi Gradle Sync

- Mở project bằng Android Studio.
- Kiểm tra JDK 21.
- Không xóa Gradle wrapper.
- Kiểm tra `local.properties` có SDK path đúng trên máy cá nhân.

### Không nên push file nào

```gitignore
node_modules/
*.db
*.db-shm
*.db-wal
.gradle/
build/
app/build/
local.properties
.env
```

## 14. Quy trình chạy nhanh

Chạy backend:

```bash
cd backend
npm install
npm start
```

Sau đó:

- Mở Android Studio.
- Đợi Gradle Sync.
- Run app trên emulator.
- Đảm bảo base URL là:

```text
http://10.0.2.2:3000/api/
```