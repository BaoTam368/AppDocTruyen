# Hướng dẫn chạy AppDocTruyen

AppDocTruyen là app Android đọc truyện. Ứng dụng Android gọi backend Node.js qua Retrofit. Backend dùng Express làm lớp trung gian giữa app và MangaDex API, đồng thời dùng SQLite/better-sqlite3 để cache manga, chapter, user, comment và post.

Luồng hiện tại nên hiểu như sau:

```text
Android Home/Search
  -> Retrofit gọi backend local
  -> backend đọc SQLite cache trước
  -> MangaDex chỉ được gọi khi sync thủ công hoặc khi một màn cần fallback rõ ràng
```

Không nên fetch 200+ manga từ MangaDex mỗi lần bật backend hoặc mỗi lần mở Home. Trước khi demo nên preload dữ liệu vào SQLite cache để Home/Search tải nhanh và ít phụ thuộc mạng.

Backend chính để chạy app nằm trong thư mục `backend/`.

## 1. Công nghệ sử dụng

| Thành phần | Công nghệ hiện có |
| --- | --- |
| Mobile app | Android Java/XML |
| IDE | Android Studio |
| Backend | Node.js + Express |
| Android API client | Retrofit 2.9.0 + Gson |
| Image loading | Glide 4.16.0 |
| Backend database/cache | SQLite + better-sqlite3 |
| Android local database | SQLite (`bookshelf.db`) |
| Manga source | MangaDex API |
| Auth | Firebase Auth qua `AuthManager`, có sync user về backend |

Ghi chú: Gradle còn bật Compose và có dependency Compose, nhưng giao diện chính của đồ án hiện dùng Java/XML layout.

## 2. Cấu trúc project

```text
AppDocTruyen/
  app/                  Android app Java/XML
  backend/              Backend chính Node.js + Express
  gradle/               Gradle wrapper/config
  run.md                Hướng dẫn chạy project
  settings.gradle.kts
```

Trong lần kiểm tra hiện tại không thấy root `server.js` hoặc root `package.json`. Backend cần chạy là `backend/server.js`, không chạy nhầm entrypoint ở nơi khác nếu sau này có file test được thêm vào root.

## 3. Yêu cầu trước khi chạy

Cần cài trên máy:

- Android Studio.
- JDK 21, vì `app/build.gradle.kts` đang cấu hình Java 21.
- Node.js và npm.
- Git nếu clone từ repository.
- Mạng truy cập được MangaDex nếu muốn sync dữ liệu mới.

Thông tin Android hiện tại:

- Android Gradle Plugin: theo version catalog của project.
- `compileSdk`: 36.
- `minSdk`: 24.
- `targetSdk`: 36.
- Package/app id: `com.example.appdoctruyen`.

## 4. Clone project

```bash
git clone <URL_REPOSITORY>
cd AppDocTruyen
```

Thay `<URL_REPOSITORY>` bằng link Git thật của nhóm.

## 5. Chạy backend Node.js

Backend chính nằm trong thư mục `backend/`.

```bash
cd backend
npm install
npm start
```

Giải thích:

- `npm install` chỉ cần chạy lần đầu hoặc khi dependency thay đổi.
- `npm start` chạy backend bằng `node server.js`.
- `npm run dev` dùng `nodemon server.js` nếu cần dev backend.
- Backend mặc định chạy port `3000` vì `backend/server.js` dùng `process.env.PORT || 3000`.

Kiểm tra backend:

```text
http://localhost:3000/api/health
```

Nếu cần file môi trường, copy từ file mẫu trong `backend/`:

```cmd
copy .env.example .env
```

Hoặc PowerShell:

```powershell
Copy-Item .env.example .env
```

Các biến hiện có trong `backend/.env.example`:

| Biến | Ý nghĩa |
| --- | --- |
| `PORT` | Port backend, mặc định 3000 |
| `MANGADEX_BASE_URL` | Base URL MangaDex API |
| `MANGADEX_UPLOADS_BASE_URL` | Base URL ảnh cover MangaDex |
| `REQUEST_TIMEOUT_MS` | Timeout khi backend gọi MangaDex |
| `DEFAULT_TRANSLATED_LANGUAGE` | Ngôn ngữ chapter mặc định, hiện là `en` |

## 6. Route backend hiện có

Các route được mount trong `backend/server.js`:

| Route | Trạng thái theo code hiện tại |
| --- | --- |
| `GET /api/health` | Có |
| `GET /api/manga` | Có, list/search đọc SQLite cache local-first |
| `GET /api/manga/:mangaId` | Có, đọc cache trước rồi fallback MangaDex nếu thiếu |
| `GET /api/manga/:mangaId/chapters` | Có, đọc chapter cache trước rồi fallback MangaDex nếu thiếu |
| `GET /api/manga/:mangaId/covers` | Có, gọi MangaDex covers |
| `GET /api/manga/chapter/:chapterId/pages` | Có |
| `GET /api/chapter/:chapterId/pages` | Có alias |
| `GET /api/local-manga` | Có, đọc SQLite cache |
| `GET /api/local-manga/search` | Có, search SQLite cache |
| `POST /api/local-manga/sync-popular` | Có, sync thủ công popular manga |
| `POST /api/local-manga/sync/popular` | Có route cũ, vẫn được giữ |
| `GET /api/groups` | Có |
| `GET /api/groups/search` | Có |
| `GET /api/groups/:groupId` | Có |
| `GET/POST/PUT/DELETE /api/comments` | Có |
| `GET/POST/PUT/DELETE /api/posts` | Có, thêm `POST /api/posts/:postId/like` |
| `POST /api/users` | Có |
| `POST /api/users/sync` | Có |
| `GET /api/users/:userId` | Có |
| `PUT /api/users/:userId` | Có |
| `DELETE /api/users` | Chưa thấy route delete user |

Backend `createPost`, `createComment` và like post hiện validate `userId`; thiếu user trả lỗi `400` với message `User is required.`.

## 7. Đồng bộ dữ liệu MangaDex vào SQLite cache

Không nên fetch 200+ manga mỗi lần bật backend hoặc mỗi lần mở app. Backend đã có SQLite cache và các route Home/Search nên đọc cache trước.

Trước khi demo, nên sync một lần để preload dữ liệu:

```bash
cd backend
node scripts/syncPopularManga.js --limit 200 --page-size 50
```

Nếu đang đứng ở root project, có thể chạy:

```bash
node backend/scripts/syncPopularManga.js --limit 200 --page-size 50
```

Script này dùng service backend, fetch MangaDex theo từng page, page size mặc định 50, có delay/retry giới hạn khi gặp rate limit. Script ghi dữ liệu vào SQLite runtime `backend/mangas.db`.

Nếu muốn gọi qua HTTP endpoint, backend phải đang chạy:

```text
POST http://localhost:3000/api/local-manga/sync-popular?total=200&limit=50&pages=4
```

Flow lần đầu:

1. Chạy `npm install` trong `backend/` nếu chưa có dependency.
2. Chạy script sync hoặc gọi endpoint sync để preload MangaDex vào SQLite.
3. Chạy backend bằng `npm start`.
4. Android Home/Search đọc dữ liệu từ SQLite cache.

Flow các lần sau:

1. Chạy backend bằng `npm start`.
2. Android đọc cache ngay.
3. Không cần sync lại MangaDex nếu chưa muốn refresh dữ liệu.

Nếu sync báo lỗi MangaDex/network, thử đổi mạng hoặc bật 1.1.1.1/WARP. Không nên phụ thuộc sync live ngay trong lúc demo.

## 8. Chạy Android app bằng emulator

Base URL emulator hiện tại trong `ApiClient.java`:

```text
http://10.0.2.2:3000/api/
```

Các bước chạy:

1. Mở project bằng Android Studio.
2. Đợi Gradle Sync hoàn tất.
3. Chạy backend trong thư mục `backend/`.
4. Kiểm tra `http://localhost:3000/api/health` trả về OK.
5. Nếu muốn Home có nhiều truyện, chạy sync/preload MangaDex vào SQLite trước demo.
6. Run app trên Android emulator.

`10.0.2.2` là địa chỉ đặc biệt để Android emulator gọi `localhost` của máy tính. Không dùng `localhost` trong app Android emulator.

## 9. Chạy Android app bằng điện thoại thật

Điện thoại thật không dùng được `10.0.2.2`. Điện thoại và máy tính phải cùng Wi-Fi.

Lấy IP LAN máy tính bằng:

```cmd
ipconfig
```

Ví dụ IP máy tính là:

```text
192.168.1.10
```

Khi đó base URL cần dùng dạng:

```text
http://192.168.1.10:3000/api/
```

Lưu ý:

- Backend phải đang chạy.
- Windows Firewall phải cho phép Node.js nhận kết nối.
- Nếu đổi IP/base URL thì cần rebuild/rerun app.
- `ApiClient.java` dùng Retrofit base URL cho phần lớn API.
- `AuthManager.java` hiện vẫn hard-code URL `http://10.0.2.2:3000/api/users...` cho sync/update user bằng Volley. Nếu chạy điện thoại thật, cần đổi cả các URL trong `AuthManager.java` hoặc refactor sau để dùng chung base URL.

## 10. Database và cache

Backend dùng `better-sqlite3`, database path hiện nằm ở:

```text
backend/mangas.db
```

Khi chạy, SQLite có thể tạo thêm WAL files:

```text
backend/mangas.db-shm
backend/mangas.db-wal
```

Database backend hiện tạo các bảng chính:

- `mangas`
- `chapters`
- `users`
- `comments`
- `posts`
- `post_likes`

Các file DB này là runtime local cache, không push lên GitHub. Nếu muốn có dữ liệu demo, chạy script/endpoint sync để tạo cache local trên máy.

Android cũng dùng SQLite local qua `BookshelfDatabaseHelper.java`, database tên:

```text
bookshelf.db
```

SQLite Android dùng cho:

- Following/bookmark.
- Recently read/reading history.
- Saved manga trong Bookshelf.
- Local comments.

Phần Saved hiện lưu metadata/chapter vào local DB và Firebase helper, chưa tải toàn bộ ảnh chapter về storage để đọc offline thật. Vì vậy tài liệu và UI nên gọi là `Save/Saved`, không gọi là offline download.

## 11. Chức năng chính

| Chức năng | Trạng thái | Ghi chú |
| --- | --- | --- |
| Home/truyện | Đã có | `ComicHomeFragment` gọi `getLocalMangaList`, có loading/empty/error/retry; refresh chỉ reload cache local, không sync 200 manga. |
| Search | Đã có | `SearchActivity` debounce 500ms, query ngắn không gọi API, search trong SQLite cache qua `/api/local-manga/search`, có empty/error/retry. |
| Chi tiết truyện | Đã có | `ComicDetailActivity`/`ComicInfoFragment` gọi backend detail; cover dùng Glide placeholder/error. |
| Chapter list | Đã có | Lấy chapter qua backend, backend có cache/fallback MangaDex khi thiếu. |
| Reader | Đã có | Lấy page chapter qua backend, có loading/empty/error; không tự tạo mock page, nhưng Glide có placeholder/error image khi URL lỗi. |
| Save/Bookshelf | Đã có | Guest thấy login-required state; user login có following, history, saved manga trong SQLite/Firebase helper. Chưa phải offline download thật. |
| Groups/Translation Teams | Đã có | Bottom nav mở `BookshelfGroupFragment`, backend có `/api/groups`, `/api/groups/search`, `/api/groups/:groupId`; stats thiếu thì UI hiển thị `N/A`/message không có thống kê. |
| Comment/Post | Đã có/đang hoàn thiện | Backend có CRUD comments/posts; Android có luồng post/comment. Guest cần bị chặn ở UI trước khi tạo dữ liệu. |
| Profile/Login/Register | Đã có | `AuthManager` dùng Firebase Auth, có Google/Facebook credential methods và sync user về backend. Profile guest có login prompt. |
| Backend cache/sync | Đã có | SQLite local-first, script `syncPopularManga.js`, endpoint sync thủ công. |
| MangaDex API | Đã có | Backend gọi MangaDex bằng axios; mạng yếu/MangaDex lỗi có thể làm sync hoặc fallback detail/chapter fail. |

## 12. Phân công nhóm tham khảo

Nếu nhóm vẫn dùng `Task.md` cho báo cáo, có thể giữ phân công ở mức tham khảo:

- Nguyễn Thắng: MangaDex API, Node.js backend, lưu trữ dữ liệu user/comment/post/manga, Bookshelf và Groups nếu nhóm giữ.
- Thanh An: Firebase đăng nhập/đăng ký, nạp xu/thanh toán/mua chapter nếu demo có dùng.
- Phạm Đức: trình bày truyện, lấy dữ liệu từ SQLite/Node.js, search.
- Minh Đức: SQLite, lưu truyện, trình bày trang truyện.
- Bảo Tâm: comment, bình luận, đăng bài user, dark mode nếu còn trong scope.

Phân công này chỉ là tham khảo báo cáo; khi sửa lỗi demo nên ưu tiên app chạy ổn và giữ đúng kiến trúc hiện tại.

## 13. Lỗi thường gặp

### Backend không chạy

- Kiểm tra đã `cd backend` chưa.
- Chạy `npm install` nếu chưa có dependency.
- Chạy `npm start`.
- Kiểm tra `http://localhost:3000/api/health`.
- Nếu port 3000 bận, đổi `PORT` trong `backend/.env` hoặc tắt process đang chiếm port.

### Home không có truyện

- Backend có thể chưa có cache manga.
- Chạy script/endpoint sync MangaDex trước.
- Kiểm tra `backend/mangas.db` đã được tạo chưa.
- Kiểm tra log backend có dòng `Cached manga count`.
- Test nhanh: `http://localhost:3000/api/local-manga?limit=30&offset=0`.

### MangaDex chậm/lỗi

- Bật 1.1.1.1/WARP hoặc đổi mạng.
- Không nên phụ thuộc MangaDex khi demo.
- Nên preload data vào SQLite trước.
- Nếu sync fail, app vẫn có thể đọc cache đã có.

### Android không gọi được backend

- Emulator dùng `10.0.2.2`, không dùng `localhost`.
- Điện thoại thật dùng IP LAN máy tính.
- Backend đang chạy.
- Firewall cho phép Node.js.
- Kiểm tra base URL trong `ApiClient.java` và các URL hard-code còn lại trong `AuthManager.java` nếu chạy điện thoại thật.

### Search không ra kết quả

- Kiểm tra đã sync manga vào SQLite chưa.
- Query dưới 2 ký tự có thể không gọi API.
- Test backend: `http://localhost:3000/api/local-manga/search?title=one&limit=30&offset=0`.
- Nếu cache rỗng, search sẽ trả empty state thay vì gọi MangaDex liên tục.

### Reader không có trang

- ChapterId có thể thiếu hoặc MangaDex chưa có page cho chapter đó.
- Kiểm tra endpoint `GET /api/chapter/:chapterId/pages`.
- Nếu backend/MangaDex lỗi, Reader hiển thị `Unable to load pages. Please try again.`.

### Gradle Sync lỗi

- Mở đúng root project bằng Android Studio.
- Kiểm tra JDK 21.
- Không xóa Gradle wrapper.
- Kiểm tra `local.properties` có SDK path đúng trên máy cá nhân.

## 14. Không nên push lên GitHub

Không push các file runtime/cấu hình cá nhân sau:

```gitignore
node_modules/
backend/node_modules/
*.db
*.db-shm
*.db-wal
backend/*.db
backend/*.db-shm
backend/*.db-wal
.gradle/
build/
app/build/
local.properties
.env
backend/.env
```

Chỉ push `package.json`/`package-lock.json`, không push `node_modules`. Không push database runtime. Không push file `.env` chứa cấu hình cá nhân.

## 15. Quy trình chạy nhanh

### Lần đầu clone hoặc máy chưa có dependency

```bash
cd backend
npm install
```

Nếu cần dữ liệu demo 200+ truyện, preload cache trước hoặc chạy ở terminal riêng:

```bash
node scripts/syncPopularManga.js --limit 200 --page-size 50
```

Sau đó chạy backend:

```bash
npm start
```

Kiểm tra backend:

```text
http://localhost:3000/api/health
```

Mở Android Studio, kiểm tra `ApiClient.java` đang dùng:

```text
http://10.0.2.2:3000/api/
```

Run app trên emulator.

### Nếu muốn sync bằng endpoint thay vì script

Terminal 1:

```bash
cd backend
npm start
```

Terminal 2 hoặc Postman:

```text
POST http://localhost:3000/api/local-manga/sync-popular?total=200&limit=50&pages=4
```

## 16. Cần kiểm tra thêm

- Sync 200 manga phụ thuộc mạng tới MangaDex; nếu mạng hiện tại chặn MangaDex, cần WARP/đổi mạng trước khi preload.
- Saved manga hiện chưa phải offline download ảnh thật. Nếu muốn offline reader, cần bổ sung cơ chế tải ảnh chapter về local storage và đọc từ local path.
- Groups lấy dữ liệu từ MangaDex group API, nhưng MangaDex không cung cấp đủ member/manga/follower stats cho mọi group; UI hiện hiển thị `N/A` khi thiếu thống kê.
- Khi chạy trên điện thoại thật, cần kiểm tra lại các URL hard-code trong `AuthManager.java`, không chỉ `ApiClient.java`.
