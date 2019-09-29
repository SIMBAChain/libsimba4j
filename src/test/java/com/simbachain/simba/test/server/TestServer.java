/*
 * Copyright 2019 SIMBA Chain Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.simbachain.simba.test.server;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

/**
 *
 */
public class TestServer {
    
    private static Map<String, String> responses = new HashMap<>();
    private static ObjectMapper mapper = new ObjectMapper();
    
    public static MockServerClient mockServer(String propsLocation) throws Exception {
        
        System.out.println("Setup");
        Properties props = new Properties();
        InputStream in = Thread.currentThread()
                               .getContextClassLoader()
                               .getResourceAsStream(propsLocation);
        props.load(in);
        VelocityEngine engine = loadVelocity();
        StringResourceRepository repo = (StringResourceRepository) engine.getApplicationAttribute(
            StringResourceLoader.REPOSITORY_NAME_DEFAULT);
        int port = Integer.parseInt(props.getProperty("mock.port"));
        MockServerClient msc = org.mockserver.integration.ClientAndServer.startClientAndServer(port);
        in = Thread.currentThread()
                   .getContextClassLoader()
                   .getResourceAsStream(props.getProperty("req.json"));
        TestRequests requests = mapper.readValue(in, TestRequests.class);
        Map<String, RequestObject> requestObjects = requests.getRequests();
        for (String key : requestObjects.keySet()) {
            RequestObject obj = requestObjects.get(key);
            HttpRequest req = HttpRequest.request();
            
            req = req.withMethod(obj.getMethod())
                     .withPath(obj.getPath());
            if (obj.getParameters() != null && !obj.getParameters()
                                                   .isEmpty()) {
                req.withQueryStringParameters(obj.getParameters()); 
            }
            if (obj.getBody() != null && !obj.getBody().isEmpty()) {
                req = req.withBody(mapper.writeValueAsString(obj.getBody()));
            }
            ResponseObject resObj = obj.getResponse();
            HttpResponse resp = HttpResponse.response()
                                            .withStatusCode(resObj.getStatusCode());
            String responseString = "";
            if (resObj.getBody() != null && !resObj.getBody()
                                                .isEmpty()) {
                VelocityContext context = new VelocityContext();
                context.put("host", "http://localhost");
                context.put("port", port);
                
                responseString = writeResponseBody(key, resObj, repo, engine, context);
                resp = resp.withBody(responseString);
            }
            msc.when(req).respond(resp);
            responses.put(key, responseString);
        }
        
        return msc;
    }
    
    private static String writeResponseBody(String key, ResponseObject resObj,
        StringResourceRepository repo, VelocityEngine engine, VelocityContext context) throws Exception {
        String json = mapper.writeValueAsString(resObj.getBody());
        repo.putStringResource(key, json);

        Template template = engine.getTemplate(key);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        
        return writer.toString();
        
    }

    public static String getResponse(String key) {
        return responses.get(key);
    }
    
    private static VelocityEngine loadVelocity() {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(Velocity.RESOURCE_LOADER, "string");
        engine.addProperty("string.resource.loader.class", StringResourceLoader.class.getName());
        engine.addProperty("string.resource.loader.repository.static", "false");
        engine.init();
        return engine;
    }
    

    public static void main(String[] args) throws Exception {
        MockServerClient msc = TestServer.mockServer("./test.properties");
       
    }

}
