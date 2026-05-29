<img width="288" height="485" alt="image" src="https://github.com/user-attachments/assets/e01cad9b-c9f6-4811-99f6-143450ca9ad2" /># AppDocTruyen
## I. Phần layout (lưu ý ảnh có thể mờ tại tui lười zoom cắt ảnh cho rõ nhưng chủ yếu nhìn được rằng cần thiết kế ở đâu rồi vô figma xem thôi)
link figma: https://www.figma.com/design/iQwa0EWvV8KwUv3VbN2U1j/DOANGiaoTiepNguoi-May--Copy-?node-id=0-1&p=f&t=GOrT1cSCXTGfL6br-0
1) activity_login/register

<img width="160" height="338" alt="image" src="https://github.com/user-attachments/assets/d884d130-9888-44d6-85f8-e2396e9da113" />
<img width="157" height="348" alt="image" src="https://github.com/user-attachments/assets/92dc8825-3e67-4b54-afc3-6de1957e7040" />

2)activity_main: Là giao diện khung nền chứa (khung chứa fragment và  bot nav)
ví dụ: là hình này

<img width="234" height="448" alt="image" src="https://github.com/user-attachments/assets/1c48e3e4-1428-4e14-80b8-b18036711a5d" />

3) bot nav : thanh điều hướng phía cuối

<img width="225" height="68" alt="image" src="https://github.com/user-attachments/assets/f110beae-4499-43e7-997f-980261beaab7" />

## Các fragment của nav

1) fragment_bookshelf: chứa tablayout phía trên dành cho tủ sách và khung chứa fragment phía dưới:

<img width="187" height="217" alt="image" src="https://github.com/user-attachments/assets/8bd1c409-d933-4718-be88-4e03375e14c2" />

2) fragment_comic_home: phần trang sách:

<img width="369" height="728" alt="image" src="https://github.com/user-attachments/assets/a38f5b8d-e8ac-42d6-ab08-198ab128dad0" />

3) fragment_word: chứa tablayout phía trên dành cho thế giới và khung chứa fragment phía dưới:

<img width="294" height="260" alt="image" src="https://github.com/user-attachments/assets/9ae7d66e-2523-4b5e-a0ff-620c05b86189" />

4) fragment_group: chứa tablayout phía trên dành cho nhóm dịch và khung chứa fragment phía dưới:

<img width="298" height="614" alt="image" src="https://github.com/user-attachments/assets/cbc0edc0-6b38-4a49-9aa4-b52b617272ce" />

5) fragment_profile: phần profile

<img width="213" height="458" alt="image" src="https://github.com/user-attachments/assets/b0067c77-236c-4111-9c01-02bdb2acf328" />

## Phần tủ sách

1) list_comic: Chứa RecyclerView trống để bỏ danh sách sách vào:

<img width="175" height="337" alt="image" src="https://github.com/user-attachments/assets/388ebdc9-3c3d-43d9-9492-7a40e68da1d4" />

2) item_comic: thiết kế của 1 truyện:

<img width="174" height="288" alt="image" src="https://github.com/user-attachments/assets/d7aa7d3d-9012-47b9-ac25-89b50735c51e" />

3) item_empty: chỉ đơn giản là chữ " Không có truyện để tải "

<img width="379" height="156" alt="image" src="https://github.com/user-attachments/assets/b0ee8c74-c8a0-4b97-991f-fc275e28ac1d" />

## Phần truyện

1) acitivity_search: Trang tìm kiếm
2) item_search_keyword: các mục key word đề xuất ở dưới

<img width="445" height="563" alt="image" src="https://github.com/user-attachments/assets/5868f2d7-62ea-49f3-9ab6-202d37fda705" />

3) activity_notification: Trang thông báo
4) list_notification: danh sách thông báo
5) item_notification: từng mục thông báo

<img width="379" height="308" alt="image" src="https://github.com/user-attachments/assets/ef8e95b7-1210-4818-a489-585f43462ba5" />

6) activity_new_bookshelf: Trang truyện mới cập nhât
7) list_new_comic: danh sách truyện mới (1 cột)
8) item_new_comic: thiết kế 1 truyện mới 

<img width="360" height="696" alt="image" src="https://github.com/user-attachments/assets/8071dc2b-eeab-4576-8ce8-94d9a035b999" />

9) activity_ranking: trang xếp hạng
10) list_ranking: danh sách xếp hạng
11) item_ranking_user: thiết kế 1 user để xếp hạng

<img width="391" height="695" alt="image" src="https://github.com/user-attachments/assets/a8c44e96-9c5b-4b10-b5a5-6bb968a768bf" />

12) activity_filter: Bộ lọc

<img width="404" height="682" alt="image" src="https://github.com/user-attachments/assets/4ae16d30-31dc-4a39-a6eb-297c29b02154" />

## Phần thế giới

1) item_word_feed: thiết kế của 1 bài feed:

<img width="284" height="508" alt="image" src="https://github.com/user-attachments/assets/17951291-b5f2-4f69-9f72-885923d7f5c8" />

2) list_word_comment: Chứa RecyclerView trống để bỏ danh sách sách vào:
3) item_world_comment: thiết kế 1 comment:

<img width="288" height="514" alt="image" src="https://github.com/user-attachments/assets/14902bff-893c-4041-94e7-4aeb39bff01a" />

4) list_world_group: Chứa RecyclerView trống để bỏ danh sách sách vào:
5) item_world_group: thiết kế 1 bài đăng của nhóm

<img width="288" height="485" alt="image" src="https://github.com/user-attachments/assets/44523631-5cc2-401e-969b-1342c6cb12c7" />

## Phần nhóm dịch

1) list_group: Danh sách nhóm dịch (có thể sử dụng lại list truyện nếu đc)
2) item_group: thiết kế của nhóm dịch

<img width="247" height="411" alt="image" src="https://github.com/user-attachments/assets/a297ecab-6c29-4467-b426-064a40aadbf8" />

## Phần người dùng

1) activity_user_detail: chi tiết user :
   
<img width="252" height="544" alt="image" src="https://github.com/user-attachments/assets/3e9e718d-0bc1-4aba-8948-9ccc564b2176" />

2) activity_recharge: nạp xu

<img width="268" height="570" alt="image" src="https://github.com/user-attachments/assets/bd2c3852-7ab8-4f86-87e3-f1fcb7c820ea" />

## Phần chi tiết truyện và đọc truyện (nằm ngoài thanh nav kích hoạt khi chọn 1 bộ truyện)

1) activity_comic_detail: trang chi tiết truyện
2) fragment_comic_info: phần chi tiết truyện
   
<img width="276" height="553" alt="image" src="https://github.com/user-attachments/assets/92ec71bf-a2df-4b4b-b13d-b8ee41dfe46b" />

3) fragment_comic_chapters: phần danh sách chap
4) item_chapter: thiết kế của 1 chap (miễn phí và khóa)
5) list_chaper: danh sách của các chap
   
<img width="289" height="596" alt="image" src="https://github.com/user-attachments/assets/6ed8cc0c-aa57-4593-8f2a-fc2a18c71dc1" />

6) activity_comic_reading: Trang đọc truyện
7) item_comic_page: trang đơn lẻ của 1 chap

<img width="302" height="553" alt="image" src="https://github.com/user-attachments/assets/17ed42e6-1cae-4b52-be3c-64d8464a8bb3" />


