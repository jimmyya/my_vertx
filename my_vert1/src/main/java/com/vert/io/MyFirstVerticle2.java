package com.vert.io;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;

/**
 * Created by CHEN on 2016/9/13.
 * 不使用8080端口
 */
public class MyFirstVerticle2 extends AbstractVerticle {

    /**
     * verticle部署之后，会自动调用start方法。
     *
     * start 方法接收Future对象 可以告诉用户执行完成还是报出错误。
     *
     * Vert.x是异步执行的，运行的时候，不会等到start方法完成。
     *
     * 所以，Future参数是非常重要的，可以通知方法是否已经执行完毕。
     *
     * @param fut
     * @throws Exception
     */
    @Override
    public void start(final Future<Void> fut) throws Exception {
        //创建一个路由器 定义请求的分发
        Router router = Router.router(vertx);
        /**
         * 绑定"/" 到我们的hello 信息
         * 将访问"/"的请求“路由”请求到指定的handler
         * Handlers接收一个RoutingContext对象。
         *
         */
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>hello from my first vert.x 3 application</h1>");

        });

        //handler 这是实际处理请求并且返回结果的地方
        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        config().getInteger("http.port", 8080),
                        result -> {
                                if (result.succeeded()) {
                                    fut.complete();
                                } else {
                                    fut.fail(result.cause());
                                }
                        }

                );

    }
}
