package com.vert.io;

import com.vert.entity.Whisky;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by CHEN on 2016/9/13.
 * 不使用8080端口
 */
public class MyFirstVerticle4 extends AbstractVerticle {

    /**
     * 不用数据库 仅仅用map存数据。
     * 存储产品。
     */
    private Map<Integer, Whisky> products = new LinkedHashMap<>();

    /**
     * 创建一些产品，假数据。
     * <p>创造一些假数据</p>
     *
     */
    private void createSomeData() {
        Whisky bowmore = new Whisky("Bowmore 15 years laimring", "Scotland,Islay");
        products.put(bowmore.getId(), bowmore);
        Whisky talisker = new Whisky("Talisker 57 North", "Scotland,Islay");
        products.put(talisker.getId(), talisker);
    }

    /**
     * @param fut
     * @throws Exception
     */
    @Override
    public void start(final Future<Void> fut) throws Exception {
        createSomeData();

        //创建路由对象
        Router router = Router.router(vertx);


        //获得产品（威士忌）
        router.get("/api/whiskies").handler(this::getAll);
        router.route("/api/whiskies*").handler(BodyHandler.create());
        router.post("/api/whiskies").handler(this::addOne);
        router.get("/api/whiskies/:id").handler(this::getOne);
        router.put("/api/whiskies/:id").handler(this::updateOne);
        router.delete("/api/whiskies/:id").handler(this::deleteOne);

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

    /**
     * 每一个handler都会接收一个RoutingContext参数，
     * 通过设置context和一些内容来填充response
     * 内容可能会碰到特殊的字符，所以强制使用UTF-8的格式
     * 创建内容的时候，并不需要自己去处理JSON格式的字符串
     * Vertx有处理JSON的API
     * 使用Json.encodePrettily(products.values()) 处理json字符串
     *
     * @param routingContext 路由信息
     */
    private void getAll(final RoutingContext routingContext) {
        routingContext.response()
                .putHeader("content-type", "application/json;charset=utf-8")
                .end(Json.encodePrettily(products.values()));
    }

    /**
     * 增加一个产品
     *
     * @param routingContext
     */
    private void addOne(RoutingContext routingContext) {
        final Whisky whisky = Json.decodeValue(routingContext.getBodyAsString(), Whisky.class);
        products.put(whisky.getId(), whisky);
        routingContext.response()
                .setStatusCode(201)
                .putHeader("content-type", "application/json;charset=utf-8")
                .end(Json.encodePrettily(whisky));
    }

    /**
     * 获得一个产品信息。
     * <p>通过用户的Id获得产品详情</p> </br>
     *
     * @param routingContext
     */
    private void getOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end(); //错误请求，比如说语法
        } else {
            final Integer idAsInteger=Integer.valueOf(id);
            Whisky whisky=products.get(idAsInteger);
            if(whisky == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                routingContext.response()
                        .putHeader("content-type","application/json;charset=utf-8")
                        .end(Json.encodePrettily(whisky));

            }
        }
    }


    /**
     * 根据产品Id 更新产品信息
     * <p>根据产品Id 更新产品信息</p> </br>
     * @param routingContext
     */
    private void updateOne(RoutingContext routingContext) {
        final String id = routingContext.request().getParam("id");
        JsonObject json=routingContext.getBodyAsJson();
        if (id == null || json == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            final Integer idAsInteger = Integer.valueOf(id);
            Whisky whisky = products.get(idAsInteger);
            if(whisky == null) {
                routingContext.response().setStatusCode(404).end();
            } else {
                whisky.setName(json.getString("name"));
                whisky.setOrigin(json.getString("origin"));
                routingContext.response()
                        .putHeader("content-type","application/json;charset=utf-8")
                        .end(Json.encodePrettily(whisky));
            }
        }
    }

    /**
     * 删除一个产品
     *
     * @param routingContext
     */
    private void deleteOne(final RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            routingContext.response().setStatusCode(400).end();
        } else {
            Integer idAsInteger = Integer.valueOf(id);
            products.remove(idAsInteger);
        }
        //204表示服务器成功处理客户端请求，但是不返回数据。是HTTP数据量最少的响应状态
        routingContext.response().setStatusCode(204);

    }
}
