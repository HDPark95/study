package project.was.httpserver;

import java.util.HashMap;
import java.util.Map;

public class ServletManager {

    private final Map<String, HttpServlet> servletMap = new HashMap<>();


    public void add(String path, HttpServlet servlet){
        servletMap.put(path, servlet);
    }

    public void execute(HttpRequest request, HttpResponse response){
        try{

        }catch (Exception e){

        }
    }
}
