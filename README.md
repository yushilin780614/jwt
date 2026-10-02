# jwt
作為Spring Boot的JWT測試用途

啟動專案後，可執行的測試步驟如下：

一、登入並從Response Body取得JWT字串：curl -i -X POST \
   -H "Content-Type:application/json" \
   -d \
'{
	"username": "user1",
  	"psd": "111"
}' \
 'http://localhost:8080/login'

二、 透過上一步取得的JWT，驗證其是否能通過測試：curl -i -X GET \
   -H "Authorization:Bearer {上一步回傳的JWT}" \
 'http://localhost:8080/test'



程式內容說明


1. 確認JWT是否成功的RESTful API

src\main\java\com\show\jwt\rest\TestRestController.java=>方法"login"讓人能取得JWT、方法"getMethodName"讓人能測試JWT


2. JWT相關設定

src\main\resources\application.properties=>設定JWT的加密金鑰與JWT的存活時間秒數

build.gradle=>引入JWT的套件

src\main\java\com\show\jwt\service\JwtService.java=>處理JWT的工具

src\main\java\com\show\jwt\filter\JwtFilter.java=>作為客製的Filter用來處理JWT，並把對應的使用者插入Spring Context中，以確保後續不會被Spring Security阻擋

src\main\java\com\show\jwt\SecurityConfig.java=>為了測試JWT，只做最簡單的設定；產生"JwtService"的Spring Bean；把JwtFilter.java作為Filter插入Filter Chain中
