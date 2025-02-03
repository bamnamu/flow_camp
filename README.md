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

![Screenshot_20250101-195407_[1].jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/6225e11f-ea58-47ba-af0f-00a110a0a76c/Screenshot_20250101-195407_1.jpg)

![Screenshot_20250101-195657_[1].jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/8c043472-cd69-4e86-834d-e7e54b92b942/Screenshot_20250101-195657_1.jpg)

![Screenshot_20250101-195710_Contacts[1].jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/0cf6e060-5896-417f-95ee-a0dce986d2e8/Screenshot_20250101-195710_Contacts1.jpg)

기능

- 미국에서 위급한 상황을 마주했을 때 쓸 수 있는 가장 대표적인 긴급전화 911부터, 여러 상황에서 도움을 받을 수 있는 곳의 전화번호와, 도움 받을 수 있는 상황을 보여줍니다.
- 연락처를 누르면 전화 연결할 수 있는 팝업이 나오고, 확인을 누르면 전화 앱으로 넘어갑니다.

구현 방법

- JSON 파일에 연락처 이름, 전화번호, 도움 받을 수 있는 상황을 저장하여 `RecyclerView`를 이용해 보여주었습니다.
- `Intent`로 연락처 리스트 중 하나를 클릭하면 전화 앱으로 연결되도록 하였습니다.

# Tab 2 : Attractions

![Screenshot_20250101-193255_[1].jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/5abc288e-ed5d-4fab-b082-dbef02ad6f98/Screenshot_20250101-193255_1.jpg)

![Screenshot_20250101-193309_ .jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/b4de762c-3ce5-4a98-98c9-d3a1fe367289/Screenshot_20250101-193309__.jpg)

![Screenshot_20250101-193323_ .jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/3e2fd86c-168b-4e4c-8edb-d459eac44be0/Screenshot_20250101-193323__.jpg)

- 각 주마다 사진, 이름, 여행 경보를 제공했습니다. 각 주의 항목은 `RecylcerView` 를 이용해 표현했습니다. 각 주의 테두리 색깔은 여행경보의 색깔로 했으며 여행경보가 없다면 회색을 사용했습니다.
    - 파랑 : 여행유의
    - 황색 : 여행자제
    - 적색 : 출국권고
    - 흑색 : 여행금지

![Screenshot_20250101-193455_ .jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/1f3b8c48-3d57-4714-b602-d9aa290aa7cd/Screenshot_20250101-193455__.jpg)

![Screenshot_20250101-193721_ .jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/f248fe78-e0e1-4167-b593-2ceb06fa5a9e/Screenshot_20250101-193721__.jpg)

![Screenshot_20250101-193726_ .jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/9e0c32a6-f2de-4e36-9d26-c53a7135ff11/Screenshot_20250101-193726__.jpg)

- 각 주를 클릭하면 그 주의 관광지들을 볼 수 있는 페이지가 나옵니다. 추가 버튼이 있습니다. 각 관광지 항목도 `RecyclerView` 를 이용해 구현했습니다.
    - 핸드폰 뒤로가기 버튼을 누르면 `OnBackPressedDispatcher`을 이용하여 항상 전 단계로 돌아갑니다. 맨 처음 주들만 있는 항목에서는 앱이 꺼집니다.
    - 추가버튼을 누르면 갤러리에서 사진을 업로드할 수 있습니다. 사진(필수), 설명(필수), 주소(선택)을 입력하면 관광지 페이지에 업로드 할 수 있습니다.

![Screenshot_20250101-201622_ .jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/8b5bdaf9-da4f-40e1-bcaa-e910f39378c5/Screenshot_20250101-201622__.jpg)

![Screenshot_20250101-201608_ .jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/4ac999e7-ed92-4252-8329-2fe099c2b609/Screenshot_20250101-201608__.jpg)

![Screenshot_20250101-201614_Android System.jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/2c1c88f6-39ff-4dfd-88f6-6988b5e2a4f6/Screenshot_20250101-201614_Android_System.jpg)

- 관광지 페이지에는 다음과 같은 기능 또한 있습니다.
    - 관광지 페이지의 항목을 누르면, 지도 검색을 하는지 묻는 창이 나옵니다. 취소를 누르면 돌아가고, 확인을 누르면 `Intent` 를 이용하여 구글 맵에 입력했던 주소로 검색이 됩니다.
    - 관광지 페이지의 항목을 꾹 누르면, 체크박스로 선택을 할 수 있습니다. 또한, 추가 버튼이 없어지고 삭제 버튼과 공유버튼이 나옵니다. 체크박스로 선택한 것 모두를 삭제/공유 할 수 있습니다.
    - 삭제와 추가는 앱을 껐다 켜도 유지됩니다. 이는 처음의  `Json` 파일을 핸드폰 내부에 저장 시켜서, 그 파일에서 데이터를 사용하기 때문입니다.

# Tab 3 : Utilities

![Screenshot_20250101-200653_[1].jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/7dac0e09-cccd-4565-aed2-17bd9cdd1824/Screenshot_20250101-200653_1.jpg)

![Screenshot_20250101-200927_[1].jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/2df37cb6-9864-4191-9e5c-acdcba885d6c/Screenshot_20250101-200927_1.jpg)

![Screenshot_20250101-200938_ .jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/8cedc645-b2f6-43a7-962f-8b490005ae43/Screenshot_20250101-200938__.jpg)

![Screenshot_20250101-201009_ .jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/f6cb388f-3934-47d6-9928-26d2e10eb0fc/3632a272-2240-4f90-9614-bcec124e036b/Screenshot_20250101-201009__.jpg)

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

- tab2의 뒤로가기, 추가, 공유, 삭제 버튼의 ui를 개선할 수 있습니다.
- 원하는 주를 선택할 수 있도록 하여 더 구체적인 연락처 정보를 제공하도록 할 수 있습니다. 또한, 나라를 설정할 수 있도록 하여 나라별 연락처, 관광지, 단위 변환기를 할 수 있습니다.
- 여행 경보 단계를 API를 통해 직접 불러오도록 할 수 있습니다.
- 미국뿐만 아니라 일본, 중국등 다양한 국가로 발전시킬 수 있습니다.
