package com.vert.io;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Created by CHEN on 2016/9/13.
 * 不使用8080端口
 */
public class MyFirstVerticle1 extends AbstractVerticle{

    /**
     * verticle部署之后，会自动调用start方法，
     * start 方法接收Future对象 可以告诉用户执行完成还是报出错误
     * Vert.x是异步执行的，运行的时候，不会等到start方法完成
     * 所以，Future参数是非常重要的，可以通知方法是否已经执行完毕
     *
     * @param fut
     * @throws Exception
     */
    @Override
    public void start(Future<Void> fut) throws Exception {
        vertx
                .createHttpServer()
                .requestHandler(r->{
                    r.response().end("<h1>Hello from my first" +
                            "Vert.x 3 application</h1>");
                })
                .listen(
                        config().getInteger("http.port",8080)
                        ,result->{
                   if(result.succeeded()) {
                       fut.complete();
                   } else {
                       fut.fail(result.cause());//报告错误
                   }
                });
    }
}
