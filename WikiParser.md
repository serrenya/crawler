### 1. 나무 위키

* seed

  * https://namu.wiki/w/%EB%82%98%EB%AC%B4%EC%9C%84%ED%82%A4:%EB%8C%80%EB%AC%B8

* 위키경로

  * https://namu.wiki/w/

* 제목

  ```
  .wiki-document-title
  ```

* ~~날짜~~

* 본문 내 URL

  ```java
  Document document = Jsoup.connect(url).get(); //GET 요청을 보내고 Document 객체를 변수 Doc에 저장
  Elements elements = document.select(".wiki-link-internal");//본문 전체 링크
  for(Element element: elements){
      System.out.println(element.attr("href")); //링크추출
      System.out.println(element.text().trim());
  }
  ```

  * 크롤에서 제거할 URL
    * /w/%EA%B5%AC%EA%B0%9C%EC%88%98%EC%9D%8C`#s-2.4` 
    * /w/파일:
    * /w/사용자:
    * /w/템플릿:

* 본문 내 이미지 URL

  ```java
  Elements elements = document.select(".wiki-image");//링크 추출
  for(Element element: elements){
      System.out.println(element.attr("abs:src")); //링크추출
  }
  ```

  * 이미지 URL형태
    * https://namu.wiki/w/%ED%8C%8C%EC%9D%BC:external/invisioncommunity.co.uk/Game-Logo-Airship-Q.png
    * https://namu.wiki/w/%ED%8C%8C%EC%9D%BC:external/blogfiles.naver.net/%25BC%25AD%25C5%25C2%25C8%25C4_%25B9%25B0%25B3%25EE%25C0%25CC.jpg
    * https://w.namu.la/s/193f9ff14d6ef00649206b2efc3ece03d1493c25c4f95ded4df0b0b6a4f86014eed2dfbc3ef43179c9a2d066f0ec26d7cba179d32a16ab4b3d966378cd7af02b50f17cd891886454837a4ecd0eacabcf14b7f9053e6df199b916b6b0b659645e

* body

  ```java
  document.select(".wiki-inner-content").text()
  ```

  

  

### 2. 위키백과

* seed

  * https://ko.wikipedia.org/wiki/%EC%9C%84%ED%82%A4%EB%B0%B1%EA%B3%BC:%EB%8C%80%EB%AC%B8

* 위키경로

  * https://ko.wikipedia.org/wiki/

* 날짜

  ```java
  String date = document.select("#footer-info-lastmod").text();
  ```

  * 이 문서는 2018년 12월 10일 (월) 09:57에 마지막으로 편집되었습니다.

* URL

  ```java
  Elements links = document.select("a[href~=/wiki/]");//본문 전체 링크
  for(Element link: links){
    System.out.println(link.attr("abs:href")); //링크추출
  }
  ```

  * <body>에서 추출할 필요 있음
  * 크롤에서 제거할 URL
    * https://ko.wikipedia.org/wiki/%ED%81%AC%EB%88%84%ED%8A%B8_(%EB%B6%81%EA%B7%B9%EA%B3%B0)`#cite_note-48`
    * https://ko.wikipedia.org/wiki/%ED%81%B0%EA%B3%A0%EB%9E%98`#cite_ref-nmfs_6-6`

  * 파일:
  * 특수:
  * 틀:

* 이미지 URL

  ```java
  Elements links = document.select("img");//본문 전체 링크
  for(Element link: links){
    System.out.println(link.attr("abs:src")); //링크추출
  }
  ```

  * https://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/K%C3%B6lner_Dom_nachts_2013.jpg/285px-K%C3%B6lner_Dom_nachts_2013.jpg

* 제목

  ```java
   String title = document.select(".firstHeading").text();
  ```

* body

  ```java
  document.select(".mw-parser-output").text()
  ```

  

### 3. 리그베다 위키

* Seed

  * http://rigvedawiki.net/w/FrontPage

* 위키경로

  * http://www.rigvedawiki.net/w/

* 날짜

  ```java
  Elements links = document.body().select(".value-title");
  String time = links.attr("title");
  ```

* URL

  ```java
  Elements links =  document.body().select("a[href~=/w/]");
  for (Element link : links) {
    System.out.println(link.text().trim());
    System.out.println(link.attr("abs:href"));
  }
  ```

  * 크롤에서 제거할 URL
    * http://www.rigvedawiki.net/w/%ED%94%8C%EB%A0%88%EC%9D%B4%EA%B7%B8%20%EB%A7%88%EB%A6%B0`?action=scrap`
    * http://rigvedawiki.net/w/FrontPage`#s-1`

* 이미지 URL

  ```java
  Elements links =  document.body().select("div .info a");
  for (Element link : links) {
    System.out.println(link.attr("abs:href"));
  }
  ```

  

* 제목

  * ```java
    String title = document.body().select(".wikiTitle a span").text().trim();
    ```

* body

  ```java
  document.select("#wikiContent").text()
  ```

  

### 4. 리브레 위키

* Seed

* 위키 경로

  * https://librewiki.net/wiki/

* 날짜

  * 이 문서는 2018년 10월 13일 (토) 11:37에 마지막으로 편집되었습니다. 

  ```java
  String date = document.select(".footer-info-lastmod").text();
  ```

* URL

  ```java
  Elements links = document.body().select("a[href~=/wiki/]");//본문 전체 링크
  for (Element link : links) {
    System.out.println(link.attr("abs:href")); //링크추출
  }
  ```

  * 크롤에서 제거할 URL
    * 틀:
    * 리브레_위키:
    * 특수:
    * 도움말:
    * 토론:
    * 파일:

* 이미지 URL

  ```java
   Elements links = document.body().select("a img[src~=/images/]");//본문 전체 링크
   for (Element link : links) {
     System.out.println(link.attr("abs:src")); //링크추출
   }
  ```

  

* 제목

  ```java
     String time = document.select(".title").text().trim();
  ```

* body

  ```java
  document.select(".mw-parser-output").text()
  ```

  

### 5. 백괴사전

* Seed

  * https://uncyclopedia.kr/wiki/%EB%B0%B1%EA%B4%B4%EC%82%AC%EC%A0%84:%EB%8C%80%EB%AC%B8

* 위키 경로

  * https://uncyclopedia.kr/wiki/

* 날짜

  * 이 문서는 2018년 10월 13일 (토) 11:37에 마지막으로 편집되었습니다. 

  ```java
  String date = document.select(".footer-info-lastmod").text();
  ```

* URL

  ```java
   Elements links = document.select(".mw-parser-output").select("a[href~=/wiki/]");
   for (Element link : links) {
     System.out.println(link.attr("abs:href")); //링크추출
   }
  ```

  * 크롤에서 제거할 URL
    * 틀:
    * 사용자:
    * 도움말:
    * 특수:
    * 파일:

* 이미지 URL

  ```java
  Elements links = document.select(".mw-parser-output").select(".image img");
  for (Element link : links) {
    System.out.println(link.attr("abs:src"));
  }
  ```

  * 크롤에서 제거할 URL
    * https://i.uncyclopedia.kr/pedia/thumb/d/dd/Achtung`.svg`/30px-Achtung`.svg`.png

* 제목

  ```java
  String title = document.select(".firstHeading").text();
  ```

* body

  ```java
  document.select(".mw-parser-output").text();
  ```
