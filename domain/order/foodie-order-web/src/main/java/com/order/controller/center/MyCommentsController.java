package com.order.controller.center;

import com.controller.BaseController;
import com.enums.YesOrNo;
import com.order.fallback.itemservice.ItemCommentsFeignClient;
import com.order.pojo.OrderItems;
import com.order.pojo.Orders;
import com.order.pojo.bo.center.OrderItemsCommentBO;
import com.order.service.center.MyCommentsService;
import com.order.service.center.MyOrdersService;
import com.pojo.IMOOCJSONResult;
import com.pojo.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Api(value = "用户中心评价模块", tags = {"用户中心评价模块相关接口"})
@RestController
@RequestMapping("mycomments")
public class MyCommentsController extends BaseController {

    @Autowired
    private MyCommentsService myCommentsService;

    @Autowired
    private ItemCommentsFeignClient itemCommentsService;

    @Autowired
    private MyOrdersService myOrdersService;

/*    @Autowired
    private LoadBalancerClient client;*/

    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "查询订单列表", notes = "查询订单列表", httpMethod = "POST")
    @PostMapping("/pending")
    public IMOOCJSONResult pending(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId) {

        // 判断用户和订单是否关联
        IMOOCJSONResult checkResult = myOrdersService.checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        // 判断该笔订单是否已经评价过，评价过了就不再继续
        Orders myOrder = (Orders)checkResult.getData();
        if (myOrder.getIsComment() == YesOrNo.YES.type) {
            return IMOOCJSONResult.errorMsg("该笔订单已经评价");
        }

        List<OrderItems> list = myCommentsService.queryPendingComment(orderId);

        return IMOOCJSONResult.ok(list);
    }


    @ApiOperation(value = "保存评论列表", notes = "保存评论列表", httpMethod = "POST")
    @PostMapping("/saveList")
    public IMOOCJSONResult saveList(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @RequestParam String orderId,
            @RequestBody List<OrderItemsCommentBO> commentList) {

        System.out.println(commentList);

        // 判断用户和订单是否关联
        IMOOCJSONResult checkResult = myOrdersService.checkUserOrder(userId, orderId);
        if (checkResult.getStatus() != HttpStatus.OK.value()) {
            return checkResult;
        }
        // 判断评论内容list不能为空
        if (commentList == null || commentList.isEmpty() || commentList.size() == 0) {
            return IMOOCJSONResult.errorMsg("评论内容不能为空！");
        }

        myCommentsService.saveComments(orderId, userId, commentList);
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "查询我的评价", notes = "查询我的评价", httpMethod = "POST")
    @PostMapping("/query")
    public IMOOCJSONResult query(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId,
            @ApiParam(name = "page", value = "查询下一页的第几页", required = false)
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "分页的每一页显示的条数", required = false)
            @RequestParam Integer pageSize) {

        if (StringUtils.isBlank(userId)) {
            return IMOOCJSONResult.errorMsg(null);
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        // TODO 前方施工，学完Feign再来改造
      /*  ServiceInstance instance = client.choose("FOODIE-ITEM-SERVICE");
        String target = String.format("http://%s:%s/item-comments-api/myComments" +
                        "?userId=%s&page=%s&pageSize=%s",
                instance.getHost(),
                instance.getPort(),
                userId,
                page,
                pageSize);
        // 偷个懒，不判断返回status，等下个章节用Feign重写
        PagedGridResult grid = restTemplate.getForObject(target, PagedGridResult.class);
        return IMOOCJSONResult.ok(grid);*/

        PagedGridResult grid = itemCommentsService.queryMyComments(userId,
                page,
                pageSize);

        return IMOOCJSONResult.ok(grid);
    }

}