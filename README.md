# 소개

<aside>

미국 여행 시 필요한 앱입니다. 미국 여행에서 도움을 받을 수 있는 전화번호들, 각 주의 여행 경보 단계와 대표 여행지, 미국 달러와 단위(무게, 길이, 온도) 변환을 제공합니다.

</aside>

## 사용 기술

<aside>

**Front-end** : Kotlin

**IDE** : Android Studio

**API** : 한국수출입은행 환율 API

**협업 툴** : Github

</aside>

# Tab 1 : Helplines

<p align="center">
  <img src="https://github.com/user-attachments/assets/d58562b0-b92f-4c8a-8cd9-6577c0f4cb2c" width="30%">
  <img src="https://github.com/user-attachments/assets/f3b80de6-324e-4330-b068-317fa8a5d3b3" width="30%">
  <img src="https://github.com/user-attachments/assets/45a4c0d3-54b8-4cb9-972d-1a1361f53b9f" width="30%">
</p>



기능

- 미국에서 위급한 상황을 마주했을 때 쓸 수 있는 가장 대표적인 긴급전화 911부터, 여러 상황에서 도움을 받을 수 있는 곳의 전화번호와, 도움 받을 수 있는 상황을 보여줍니다.
- 연락처를 누르면 전화 연결할 수 있는 팝업이 나오고, 확인을 누르면 전화 앱으로 넘어갑니다.

구현 방법

- JSON 파일에 연락처 이름, 전화번호, 도움 받을 수 있는 상황을 저장하여 `RecyclerView`를 이용해 보여주었습니다.
- `Intent`로 연락처 리스트 중 하나를 클릭하면 전화 앱으로 연결되도록 하였습니다.

# Tab 2 : Attractions

<p align="center">
  <img src="https://github.com/user-attachments/assets/e47a28f6-dd98-4fd7-a3c4-4501c924d5ad" width="30%">
  <img src="https://github.com/user-attachments/assets/e8d49f32-69df-4de1-827e-b8d0445e5fbf" width="30%">
  <img src="https://github.com/user-attachments/assets/0e2de521-c746-48d8-ae29-6c34b6b56bda" width="30%">
</p>


- 각 주마다 사진, 이름, 여행 경보를 제공했습니다. 각 주의 항목은 `RecylcerView` 를 이용해 표현했습니다. 각 주의 테두리 색깔은 여행경보의 색깔로 했으며 여행경보가 없다면 회색을 사용했습니다.
    - 파랑 : 여행유의
    - 황색 : 여행자제
    - 적색 : 출국권고
    - 흑색 : 여행금지

<p align="center">
  <img src="https://github.com/user-attachments/assets/e9b6d989-a6b5-4fbe-9ef1-942c21dfff45" width="30%">
  <img src="https://github.com/user-attachments/assets/07489e14-eaca-4c2a-908d-0c2d07bcab2b" width="30%">
  <img src="https://github.com/user-attachments/assets/fa7981d1-704e-458c-8049-5ecb036d778d" width="30%">
</p>


- 각 주를 클릭하면 그 주의 관광지들을 볼 수 있는 페이지가 나옵니다. 추가 버튼이 있습니다. 각 관광지 항목도 `RecyclerView` 를 이용해 구현했습니다.
    - 핸드폰 뒤로가기 버튼을 누르면 `OnBackPressedDispatcher`을 이용하여 항상 전 단계로 돌아갑니다. 맨 처음 주들만 있는 항목에서는 앱이 꺼집니다.
    - 추가버튼을 누르면 갤러리에서 사진을 업로드할 수 있습니다. 사진(필수), 설명(필수), 주소(선택)을 입력하면 관광지 페이지에 업로드 할 수 있습니다.
 
 
 <p align="center">
  <img src="https://github.com/user-attachments/assets/c6531b95-4c66-44bb-956a-46523683dce9" width="30%">
  <img src="https://github.com/user-attachments/assets/b6f0e73f-37e8-4486-99e1-24b47fcdb63f" width="30%">
  <img src="https://github.com/user-attachments/assets/9f158897-d164-4392-aeeb-da39249ee068" width="30%">
</p>

- 관광지 페이지에는 다음과 같은 기능 또한 있습니다.
    - 관광지 페이지의 항목을 누르면, 지도 검색을 하는지 묻는 창이 나옵니다. 취소를 누르면 돌아가고, 확인을 누르면 `Intent` 를 이용하여 구글 맵에 입력했던 주소로 검색이 됩니다.
    - 관광지 페이지의 항목을 꾹 누르면, 체크박스로 선택을 할 수 있습니다. 또한, 추가 버튼이 없어지고 삭제 버튼과 공유버튼이 나옵니다. 체크박스로 선택한 것 모두를 삭제/공유 할 수 있습니다.
    - 삭제와 추가는 앱을 껐다 켜도 유지됩니다. 이는 처음의  `Json` 파일을 핸드폰 내부에 저장 시켜서, 그 파일에서 데이터를 사용하기 때문입니다.

# Tab 3 : Utilities

<p align="center">
  <img src="https://github.com/user-attachments/assets/5d7e2dcb-4949-4a01-8cd9-293b0bac3051" width="45%">
  <img src="https://github.com/user-attachments/assets/15acfe7d-2b13-425f-9637-8295e3112500" width="45%">
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/6b470add-424c-4040-b70f-b2671260bfb4" width="45%">
  <img src="https://github.com/user-attachments/assets/96a20315-2880-4093-9ec2-9cbbe8757b3f" width="45%">
</p>

- 환율계산기를 구현하여 원을 입력하면 그에 해당하는 달러가, 달러를 입력하면 그에 해당하는 원이 나옵니다.
    - 한국 수출입 공사의 api를 `OkHttps`를 이용하여 사용해서 당일의 환율을 보여줍니다.
    - 만약 당일의 환율이 업데이트나 기타 오류로 보여지지 않는다면, 전날의 환율을 가져옵니다. 오류가 났을때 가져오는 데이터는 순차적으로 5일전까지입니다. 모두 오류가 뜬다면 “데이터 오류”라고 표시됩니다.
- **길이 변환** : 한국 길이 단위(cm, m) - 미국 길이 단위(ft, inch) 변환을 제공합니다.
- **무게 변환** : 한국 무게 단위(kg) - 미국 무게 단위(lbs(파운드)) 변환을 제공합니다.
- **온도 변환** : 섭씨-화씨 온도 변환을 제공합니다.

구현 방법

- `TextWatcher`& `addTextChangedListener` 를 이용해 실시간으로 단위 변환이 가능하도록 하였습니다.

## +

- `Viewpager2` 를 이용해 화면을 슬라이드 시켜서 다른 탭으로 넘어가게 할 수 있는 기능을 구현했습니다.

# 발전 가능성

- 원하는 주를 선택할 수 있도록 하여 더 구체적인 연락처 정보를 제공하도록 할 수 있습니다. 또한, 나라를 설정할 수 있도록 하여 나라별 연락처, 관광지, 단위 변환기를 할 수 있습니다.
- 여행 경보 단계를 API를 통해 직접 불러오도록 할 수 있습니다.
- 미국뿐만 아니라 일본, 중국등 다양한 국가로 발전시킬 수 있습니다.
