package com.vert.io;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by CHEN on 2016/9/13.
 */
@RunWith(VertxUnitRunner.class)
public class MyFirstVertio1Test {
    private Vertx vertx;
    //申明全局变量
    private Integer port;


    /**
     * 在setUp方法里，创建了一个Vertx实例，
     * deploy了verticle ,你可能已经注意到了，
     * 这与传统JUnit的@Before不一样，
     * 它接收了一个TestContext参数，
     * TestContext对象让我们可以控制异步的测试，
     * 例如：当我们deploy了异步verticle，
     * 多个Vertx实例相互作用时，
     * 不能检测它的正确性，
     * deployVerticle方法的第二个参数返回一个handler：
     * context.asyncAssertSuccess()，
     * 如果verticle的状态是失败，
     * 那这个测试就是失败的，
     * 此外会等待verticle完成启动，
     * 还记得，在verticle里，
     * 我们调用了fut.complete()方法，
     * 直到等到调用fut.complete()方法才返回状态。
     * @param context
     */
    @Before
    public void setUp(TestContext context) {
        //设置端口号
        //port=8081;
        //我们把端口改为8081，但是问题没有解决，端口仍然可能会被占用
        ServerSocket socket=null;
        try {
            //new ServerSocket(0) 表示由操作系统为服务器随机分配一个端口
            socket = new ServerSocket(0);
            port=socket.getLocalPort();
        } catch (IOException e) {
            port=8080;
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        vertx=Vertx.vertx();
        //port=27017;
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", port)
                );
        vertx.deployVerticle(MyFirstVerticle.class.getName(), options, context.asyncAssertSuccess());
    }

    /**
     * 回收Vertx 对象
     * @param context
     */
    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    /**
     * 测试应用的testMyApplication方法，
     * 给应用发出请求并且拦截一个返回，
     * 发出请求和接收答复都是异步的，
     * 我们需要一种方法来控制，
     * setUp和tearDown方法接收一个TestContext对象，
     * 当这个测试完成的时候，
     * 通过我们这个对象创建的异步的处理器（async），
     * 通知测试框架（使用async.complete()）
     *
     * 我们使用getNow()方法（getNow()方法是get(...).end()的捷径），
     * 创建一个HTTP客户端和发一个HTTP请求给我们的应用，
     * 响应使用lambda处理，
     * 通过另一个lambda的handler方法接收一个response body，
     * 这个body参数是这个response body（如buffer对象），
     * 检测body是否等于“Hello”字符串，
     * 并且宣布测试完成（async.complete()）。
     * @param context
     */
    @Test
    public void testMyApplication(TestContext context) {
        final Async async=context.async();
        //port 改成8081
        vertx.createHttpClient().getNow(port,"localhost","/",
                response-> {
                   response.handler(body->{
                       context.assertTrue(body.toString().contains("Hello"));
                       async.complete();
                   }) ;
                });
    }
}
