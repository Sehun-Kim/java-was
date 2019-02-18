# Web Application Server
Http 통신 방식을 이해하기 위한 WAS 구현

### Environment
- JAVA 8
- Gradle

---

### Requirement
1. 테스트 주도 개발(TDD)
2. InputStream/OutputStream API를 활용하여 요청과 응답을 처리
3. Http Message를 클래스로 정의
4. 다형성을 사용하여 요청 url에 대한 분기 처리를 
5. 로그인/로그아웃 기능 (Cookie를 사용하여 상태값을 저장하고 로그인 상태를 로그아웃 상태로 변경함)

---

### 핵심 클래스

- **HttpRequest**

```java
public class HttpRequest {
    private RequestLine requestLine;
    private RequestHeaders requestHeaders;
    private RequestBody requestBody;

    public HttpRequest(RequestLine requestLine, RequestHeaders requestHeaders, RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public boolean matchMethod(HttpMethod method) {
        return this.requestLine.getMethod().equals(method);
    }

    public String getPath() {
        return this.requestLine.getUri();
    }

    public String getHeader(String key) {
        return this.requestHeaders.getHeader(key);
    }

    public String getParameter(String key) {
        String parameter = this.requestBody.getParameter(key);
        if (parameter == null)
            return this.requestLine.getQueryParameter(key);
        return parameter;
    }
}
```

- **HttpResponse**

```java
public class HttpResponse {
    private DataOutputStream response;
    private Map<String, String> headers;

    public HttpResponse(DataOutputStream response) {
        this.headers = new HashMap<>();
        this.response = response;
    }

    public void addHeader(String header, String field) {
        if (this.headers.containsKey(header)) {
            this.headers.remove(header);
        }
        this.headers.put(header, field);
    }

    public void forward(String path) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
        response200Header(body.length);
        processHeaders();
        responseBody(body);
        this.response.flush();
    }

    public void forwardBody(String bodyValue) throws IOException {
        byte[] body = bodyValue.getBytes();
        response200Header(body.length);
        processHeaders();
        responseBody(body);
        this.response.flush();
    }

    private void responseBody(byte[] body) throws IOException {
        this.response.write(body, 0, body.length);
    }

    private void response200Header(int contentLength) throws IOException {
        this.response.writeBytes("HTTP/1.1 200 OK \r\n");
        this.response.writeBytes("Content-Length: " + contentLength + "\r\n");
    }

    public void sendRedirect(String location) throws IOException {
        this.response.writeBytes("HTTP/1.1 302 Found \r\n");
        this.response.writeBytes("Location: " + location + "\r\n");
        processHeaders();

        this.response.flush();
    }

    private void processHeaders() throws IOException {
        for (String header : headers.keySet()) {
            this.response.writeBytes(header + ": " + headers.get(header) + "\r\n");
        }
        response.writeBytes("\r\n");
    }
}
```


- **Dispatcher**

```java
public class Dispatcher {
    public static void dispatch(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        DispatchPath.findController(path).service(request, response);
    }
}
```

- **DispatcherPath**

```java
public enum DispatchPath {
    CSS_PATH("/css", new StyleSheetController()),
    JS_PATH("/js", new JavaScriptController()),
    FONTS_PATH("/fonts", new FontsController()),
    INDEX_PATH("/index.html", new IndexController()),
    LOGIN_PATH("/user/login", new LoginController()),
    USER_LIST_PATH("/user/list", new ListUserController()),
    USER_PATH("/user", new CreateUserController());

    private String path;
    private Controller controller;

    DispatchPath(String path, Controller controller) {
        this.path = path;
        this.controller = controller;
    }

    public static Controller findController(String path) {
        for (DispatchPath dispatchPath : DispatchPath.values()) {
            if (path.startsWith(dispatchPath.path))
                return dispatchPath.controller;
        }
        return USER_PATH.controller;
    }

}
```
