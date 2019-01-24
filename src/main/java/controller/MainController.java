package controller;

import webserver.http.request.HttpRequest;
import webserver.http.response.HttpResponse;
import util.HttpResponseUtils;

import java.io.IOException;

public class MainController extends AbstractController{
    public static void index(HttpRequest request, HttpResponse response) throws IOException {
        String uri = "/index.html";
        byte[] body = HttpResponseUtils.generateBody(uri);
        HttpResponseUtils.response200Header(response.getResponse(), body.length);
        HttpResponseUtils.responseBody(response.getResponse(), body);
    }

}
